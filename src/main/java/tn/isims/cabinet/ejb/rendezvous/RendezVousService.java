package tn.isims.cabinet.ejb.rendezvous;

import jakarta.ejb.Stateful;
import jakarta.ejb.Remove;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;
import tn.isims.cabinet.rmi.callback.PatientCallback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EJB Stateful pour la gestion des rendez-vous
 * Maintient l'état de la session pour les opérations de création/modification de RDV
 */
@Stateful(mappedName = "RendezVousService")
public class RendezVousService implements RendezVousServiceRemote, RendezVousServiceLocal {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    private PatientCallback patientCallback;

    public void setPatientCallback(PatientCallback callback) {
        this.patientCallback = callback;
    }

    @Override
    public RendezVous creerRendezVous(Long patientId, Long medecinId,
                                       LocalDateTime dateRendezVous, String motif) {
        Patient patient = em.find(Patient.class, patientId);
        Medecin medecin = em.find(Medecin.class, medecinId);

        if (patient == null || medecin == null) {
            throw new IllegalArgumentException("Patient ou Médecin introuvable");
        }

        RendezVous rdv = new RendezVous(patient, medecin, dateRendezVous, motif);
        em.persist(rdv);

        // Envoyer notification via RMI Callback
        notifierPatient(patient, "CRÉATION",
            "Votre rendez-vous a été créé pour le " + dateRendezVous +
            " avec Dr. " + medecin.getNom() + " (" + medecin.getSpecialite() + ")");

        return rdv;
    }

    @Override
    public RendezVous modifierHoraire(Long rdvId, LocalDateTime nouvelleDate) {
        RendezVous rdv = em.find(RendezVous.class, rdvId);
        if (rdv != null && rdv.getStatut() == RendezVous.Statut.PLANIFIE) {
            LocalDateTime ancienneDate = rdv.getDateRendezVous();
            rdv.setDateRendezVous(nouvelleDate);
            em.merge(rdv);

            // Envoyer notification
            notifierPatient(rdv.getPatient(), "MODIFICATION",
                "Votre rendez-vous du " + ancienneDate +
                " a été déplacé au " + nouvelleDate);

            return rdv;
        }
        return null;
    }

    @Override
    public boolean annulerRendezVous(Long rdvId) {
        RendezVous rdv = em.find(RendezVous.class, rdvId);
        if (rdv != null && rdv.getStatut() == RendezVous.Statut.PLANIFIE) {
            rdv.setStatut(RendezVous.Statut.ANNULE);
            em.merge(rdv);

            // Envoyer notification
            notifierPatient(rdv.getPatient(), "ANNULATION",
                "Votre rendez-vous du " + rdv.getDateRendezVous() +
                " avec Dr. " + rdv.getMedecin().getNom() + " a été annulé.");

            return true;
        }
        return false;
    }

    @Override
    public List<RendezVous> listerRendezVousDuJour() {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(23, 59, 59);

        TypedQuery<RendezVous> query = em.createQuery(
            "SELECT r FROM RendezVous r WHERE r.dateRendezVous BETWEEN :debut AND :fin " +
            "AND r.statut = :statut ORDER BY r.dateRendezVous",
            RendezVous.class
        );
        query.setParameter("debut", debutJour);
        query.setParameter("fin", finJour);
        query.setParameter("statut", RendezVous.Statut.PLANIFIE);

        return query.getResultList();
    }

    @Override
    public List<RendezVous> listerRendezVousPasses() {
        TypedQuery<RendezVous> query = em.createQuery(
            "SELECT r FROM RendezVous r WHERE r.dateRendezVous < :maintenant " +
            "OR r.statut = :statutTermine ORDER BY r.dateRendezVous DESC",
            RendezVous.class
        );
        query.setParameter("maintenant", LocalDateTime.now());
        query.setParameter("statutTermine", RendezVous.Statut.TERMINE);

        return query.getResultList();
    }

    @Override
    public List<RendezVous> listerTousLesRendezVous() {
        TypedQuery<RendezVous> query = em.createQuery(
            "SELECT r FROM RendezVous r ORDER BY r.dateRendezVous DESC",
            RendezVous.class
        );
        return query.getResultList();
    }

    @Override
    public List<RendezVous> listerRendezVousParPatient(Long patientId) {
        TypedQuery<RendezVous> query = em.createQuery(
            "SELECT r FROM RendezVous r WHERE r.patient.id = :patientId ORDER BY r.dateRendezVous",
            RendezVous.class
        );
        query.setParameter("patientId", patientId);
        return query.getResultList();
    }

    @Override
    public List<RendezVous> listerRendezVousParMedecin(Long medecinId) {
        TypedQuery<RendezVous> query = em.createQuery(
            "SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId ORDER BY r.dateRendezVous",
            RendezVous.class
        );
        query.setParameter("medecinId", medecinId);
        return query.getResultList();
    }

    @Override
    public RendezVous trouverRendezVousParId(Long id) {
        return em.find(RendezVous.class, id);
    }

    @Override
    public boolean marquerCommeTermine(Long rdvId) {
        RendezVous rdv = em.find(RendezVous.class, rdvId);
        if (rdv != null && rdv.getStatut() == RendezVous.Statut.PLANIFIE) {
            rdv.setStatut(RendezVous.Statut.TERMINE);
            em.merge(rdv);
            return true;
        }
        return false;
    }

    private void notifierPatient(Patient patient, String type, String message) {
        try {
            // 1. Try the directly-set callback first (RMI client flow)
            PatientCallback cb = patientCallback;

            // 2. If not set, look up from the registry (web flow)
            if (cb == null) {
                cb = tn.isims.cabinet.rmi.impl.PatientNotificationRegistry
                        .getCallback(patient.getId());
            }

            if (cb != null) {
                String notification = type + ": " + message;
                cb.recevoirNotification(notification);
                System.out.println("[RMI] Notification envoyée au patient " 
                    + patient.getId() + " → " + notification);
            } else {
                System.out.println("[RMI] Aucun client connecté pour le patient " 
                    + patient.getId() + " - notification ignorée.");
            }
        } catch (Exception e) {
            System.err.println("[RMI] Erreur notification patient " 
                + patient.getId() + ": " + e.getMessage());
        }
    }
}
