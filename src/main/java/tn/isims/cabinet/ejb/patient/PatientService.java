package tn.isims.cabinet.ejb.patient;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import tn.isims.cabinet.entity.Patient;

import java.util.List;

/**
 * EJB Stateless pour la gestion des patients
 */
@Stateless(mappedName = "PatientService")
public class PatientService implements PatientServiceRemote, PatientServiceLocal {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    @Override
    public List<Patient> listerTousLesPatients() {
        TypedQuery<Patient> query = em.createQuery("SELECT p FROM Patient p ORDER BY p.nom", Patient.class);
        return query.getResultList();
    }

    @Override
    public Patient trouverPatientParId(Long id) {
        return em.find(Patient.class, id);
    }

    @Override
    public Patient ajouterPatient(Patient patient) {
        em.persist(patient);
        return patient;
    }

    @Override
    public Patient modifierPatient(Long id, Patient patientModifie) {
        Patient patient = em.find(Patient.class, id);
        if (patient != null) {
            patient.setNom(patientModifie.getNom());
            patient.setPrenom(patientModifie.getPrenom());
            patient.setEmail(patientModifie.getEmail());
            patient.setTelephone(patientModifie.getTelephone());
            patient.setDateNaissance(patientModifie.getDateNaissance());
            em.merge(patient);
        }
        return patient;
    }

    @Override
    public boolean supprimerPatient(Long id) {
        Patient patient = em.find(Patient.class, id);
        if (patient != null) {
            // Vérifier s'il y a des rendez-vous actifs
            boolean aDesRendezVousActifs = patient.getRendezVous().stream()
                    .anyMatch(rdv -> rdv.getStatut() == tn.isims.cabinet.entity.RendezVous.Statut.PLANIFIE);

            if (aDesRendezVousActifs) {
                return false; // Ne peut pas supprimer
            }

            em.remove(patient);
            return true;
        }
        return false;
    }

    @Override
    public List<Patient> rechercherParNomOuEmail(String recherche) {
        TypedQuery<Patient> query = em.createQuery(
            "SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE :rech OR LOWER(p.email) LIKE :rech",
            Patient.class
        );
        query.setParameter("rech", "%" + recherche.toLowerCase() + "%");
        return query.getResultList();
    }

    @Override
    public Patient trouverPatientParEmail(String email) {
        TypedQuery<Patient> query = em.createQuery(
            "SELECT p FROM Patient p WHERE p.email = :email",
            Patient.class
        );
        query.setParameter("email", email);
        List<Patient> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
