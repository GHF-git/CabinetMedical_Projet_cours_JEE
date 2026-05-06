package tn.isims.cabinet.ejb.patient;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tn.isims.cabinet.entity.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EJB Stateless — Gestion des Patients.
 *
 * SERIALIZATION FIX: Every Patient returned via a Remote interface
 * must be a plain POJO copy with no Hibernate proxy attachment.
 * We build new Patient instances with scalar fields only and
 * an empty ArrayList for rendezVous — never a PersistentBag.
 */
@Stateless(mappedName = "PatientService")
public class PatientService implements PatientServiceRemote, PatientServiceLocal {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    @Override
    public List<Patient> listerTousLesPatients() {
        return copyList(em.createQuery(
                "SELECT p FROM Patient p ORDER BY p.nom", Patient.class)
                .getResultList());
    }

    @Override
    public Patient trouverPatientParId(Long id) {
        return copy(em.find(Patient.class, id));
    }

    @Override
    public List<Patient> rechercherParNomOuEmail(String recherche) {
        return copyList(em.createQuery(
                "SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE :r OR LOWER(p.email) LIKE :r",
                Patient.class)
                .setParameter("r", "%" + recherche.toLowerCase() + "%")
                .getResultList());
    }

    @Override
    public Patient trouverPatientParEmail(String email) {
        List<Patient> r = em.createQuery(
                "SELECT p FROM Patient p WHERE p.email = :email", Patient.class)
                .setParameter("email", email).getResultList();
        return r.isEmpty() ? null : copy(r.get(0));
    }

    @Override
    public Patient ajouterPatient(Patient patient) {
        em.persist(patient);
        em.flush();
        return copy(patient);
    }

    @Override
    public Patient modifierPatient(Long id, Patient mod) {
        Patient p = em.find(Patient.class, id);
        if (p != null) {
            p.setNom(mod.getNom());
            p.setPrenom(mod.getPrenom());
            p.setEmail(mod.getEmail());
            p.setTelephone(mod.getTelephone());
            p.setDateNaissance(mod.getDateNaissance());
            em.merge(p);
            em.flush();
        }
        return copy(p);
    }

    @Override
    public boolean supprimerPatient(Long id) {
        Patient p = em.find(Patient.class, id);
        if (p == null) return false;
        // JPQL count — never touch the lazy collection
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM RendezVous r WHERE r.patient.id = :id AND r.statut = 'PLANIFIE'",
                Long.class).setParameter("id", id).getSingleResult();
        if (count > 0) return false;
        em.remove(em.merge(p));
        return true;
    }

    @Override
    public List<Patient> listerPatientsPourRMI() {
        return listerTousLesPatients();
    }

    // ── Deep-copy helpers ────────────────────────────────────────────────────
    private Patient copy(Patient p) {
        if (p == null) return null;
        Patient c = new Patient(p.getNom(), p.getPrenom(),
                p.getEmail(), p.getTelephone(), p.getDateNaissance());
        c.setId(p.getId());
        c.setRendezVous(new ArrayList<>());  // plain ArrayList — no Hibernate proxy
        return c;
    }

    private List<Patient> copyList(List<Patient> list) {
        return list.stream().map(this::copy).collect(Collectors.toList());
    }
}
