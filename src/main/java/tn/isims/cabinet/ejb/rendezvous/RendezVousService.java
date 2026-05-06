package tn.isims.cabinet.ejb.rendezvous;

import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;
import tn.isims.cabinet.rmi.callback.PatientCallback;
import tn.isims.cabinet.rmi.impl.PatientNotificationRegistry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EJB Stateful — Gestion des Rendez-vous.
 *
 * SERIALIZATION FIX:
 *   RendezVous holds Patient and Medecin via @ManyToOne(EAGER).
 *   Patient/Medecin each have @OneToMany(LAZY) List<RendezVous>.
 *   When WildFly marshals the return value over the Remote interface,
 *   it walks the full object graph and hits the uninitialized
 *   Hibernate PersistentBag on those lists → EJBException / Failed to read response.
 *
 *   Solution: build a plain DTO-style copy of every entity we return,
 *   using only scalar fields and empty ArrayLists for the collections.
 *   The copy is a real POJO with no Hibernate attachment whatsoever.
 */
@Stateful(mappedName = "RendezVousService")
public class RendezVousService implements RendezVousServiceRemote, RendezVousServiceLocal {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    private PatientCallback patientCallback;

    public void setPatientCallback(PatientCallback callback) {
        this.patientCallback = callback;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public RendezVous creerRendezVous(Long patientId, Long medecinId,
                                      LocalDateTime dateRendezVous, String motif) {
        Patient patient = em.find(Patient.class, patientId);
        Medecin medecin = em.find(Medecin.class, medecinId);
        if (patient == null || medecin == null)
            throw new IllegalArgumentException("Patient ou Médecin introuvable");

        RendezVous rdv = new RendezVous(patient, medecin, dateRendezVous, motif);
        em.persist(rdv);
        em.flush();

        notifier(patient.getId(), "CRÉATION RDV #" + rdv.getId()
                + " : confirmé pour le " + rdv.getDateFormatted());

        return copy(rdv);
    }

    // ── MODIFY ────────────────────────────────────────────────────────────────
    @Override
    public RendezVous modifierHoraire(Long rdvId, LocalDateTime nouvelleDate) {
        RendezVous rdv = em.find(RendezVous.class, rdvId);
        if (rdv == null || rdv.getStatut() != RendezVous.Statut.PLANIFIE) return null;

        String ancienne = rdv.getDateFormatted();
        rdv.setDateRendezVous(nouvelleDate);
        em.merge(rdv);
        em.flush();

        notifier(rdv.getPatient().getId(),
                "MODIFICATION RDV #" + rdvId + " : déplacé du " + ancienne
                + " au " + rdv.getDateFormatted());

        return copy(rdv);
    }

    // ── CANCEL ────────────────────────────────────────────────────────────────
    @Override
    public boolean annulerRendezVous(Long rdvId) {
        RendezVous rdv = em.find(RendezVous.class, rdvId);
        if (rdv == null || rdv.getStatut() != RendezVous.Statut.PLANIFIE) return false;

        rdv.setStatut(RendezVous.Statut.ANNULE);
        em.merge(rdv);
        em.flush();

        notifier(rdv.getPatient().getId(),
                "ANNULATION RDV #" + rdvId + " du " + rdv.getDateFormatted()
                + " avec Dr. " + rdv.getMedecin().getNom() + " — annulé.");
        return true;
    }

    // ── TERMINATE ─────────────────────────────────────────────────────────────
    @Override
    public boolean marquerCommeTermine(Long rdvId) {
        RendezVous rdv = em.find(RendezVous.class, rdvId);
        if (rdv == null || rdv.getStatut() != RendezVous.Statut.PLANIFIE) return false;
        rdv.setStatut(RendezVous.Statut.TERMINE);
        em.merge(rdv);
        em.flush();
        return true;
    }

    // ── FIND ──────────────────────────────────────────────────────────────────
    @Override
    public RendezVous trouverRendezVousParId(Long id) {
        return copy(em.find(RendezVous.class, id));
    }

    // ── LIST QUERIES ──────────────────────────────────────────────────────────
    @Override
    public List<RendezVous> listerRendezVousParPatient(Long patientId) {
        return copyList(em.createQuery(
                "SELECT r FROM RendezVous r WHERE r.patient.id = :pid ORDER BY r.dateRendezVous",
                RendezVous.class)
                .setParameter("pid", patientId).getResultList());
    }

    @Override
    public List<RendezVous> listerTousLesRendezVous() {
        return copyList(em.createQuery(
                "SELECT r FROM RendezVous r ORDER BY r.dateRendezVous DESC",
                RendezVous.class).getResultList());
    }

    @Override
    public List<RendezVous> listerRendezVousDuJour() {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin   = LocalDate.now().atTime(23, 59, 59);
        return copyList(em.createQuery(
                "SELECT r FROM RendezVous r WHERE r.dateRendezVous BETWEEN :d AND :f " +
                "AND r.statut = :s ORDER BY r.dateRendezVous", RendezVous.class)
                .setParameter("d", debut).setParameter("f", fin)
                .setParameter("s", RendezVous.Statut.PLANIFIE).getResultList());
    }

    @Override
    public List<RendezVous> listerRendezVousPasses() {
        return copyList(em.createQuery(
                "SELECT r FROM RendezVous r WHERE r.dateRendezVous < :now " +
                "OR r.statut = :t ORDER BY r.dateRendezVous DESC", RendezVous.class)
                .setParameter("now", LocalDateTime.now())
                .setParameter("t",   RendezVous.Statut.TERMINE).getResultList());
    }

    @Override
    public List<RendezVous> listerRendezVousParMedecin(Long medecinId) {
        return copyList(em.createQuery(
                "SELECT r FROM RendezVous r WHERE r.medecin.id = :mid ORDER BY r.dateRendezVous",
                RendezVous.class)
                .setParameter("mid", medecinId).getResultList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DEEP COPY — the only reliable fix for Hibernate proxy serialization
    //
    //  We build brand-new POJO instances (no JPA attachment, no Hibernate proxy)
    //  containing only the scalar values we need.  The lazy List<RendezVous>
    //  on Patient and Medecin is set to an empty plain ArrayList.
    // ══════════════════════════════════════════════════════════════════════════

    private Patient copyPatient(Patient p) {
        if (p == null) return null;
        Patient c = new Patient(p.getNom(), p.getPrenom(),
                p.getEmail(), p.getTelephone(), p.getDateNaissance());
        c.setId(p.getId());
        c.setRendezVous(new ArrayList<>());   // ← plain ArrayList, no proxy
        return c;
    }

    private Medecin copyMedecin(Medecin m) {
        if (m == null) return null;
        Medecin c = new Medecin(m.getNom(), m.getPrenom(), m.getSpecialite(), m.getEmail());
        c.setId(m.getId());
        c.setRendezVous(new ArrayList<>());   // ← plain ArrayList, no proxy
        return c;
    }

    private RendezVous copy(RendezVous r) {
        if (r == null) return null;
        RendezVous c = new RendezVous();
        c.setId(r.getId());
        c.setDateRendezVous(r.getDateRendezVous());
        c.setMotif(r.getMotif());
        c.setStatut(r.getStatut());
        c.setPatient(copyPatient(r.getPatient()));   // deep-copy patient
        c.setMedecin(copyMedecin(r.getMedecin()));   // deep-copy medecin
        return c;
    }

    private List<RendezVous> copyList(List<RendezVous> list) {
        return list.stream().map(this::copy).collect(Collectors.toList());
    }

    // ── NOTIFICATION ──────────────────────────────────────────────────────────
    private void notifier(Long patientId, String message) {
        try {
            PatientCallback cb = patientCallback;
            if (cb == null) cb = PatientNotificationRegistry.getCallback(patientId);
            if (cb != null) {
                cb.recevoirNotification(message);
                System.out.println("[RMI] ✅ Notification → patient " + patientId + ": " + message);
            } else {
                System.out.println("[RMI] ℹ Patient " + patientId + " non connecté — notification ignorée.");
            }
        } catch (Exception e) {
            System.err.println("[RMI] ⚠ Erreur notification: " + e.getMessage());
        }
    }
}
