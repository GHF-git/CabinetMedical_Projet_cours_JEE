package tn.isims.cabinet.ejb.medecin;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EJB Stateless — Gestion des Médecins.
 *
 * SERIALIZATION FIX: Every Medecin/Patient returned via a Remote interface
 * must be a plain POJO copy — no Hibernate PersistentBag proxies.
 */
@Stateless(mappedName = "MedecinService")
public class MedecinService implements MedecinServiceRemote, MedecinServiceLocal {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    @Override
    public List<Medecin> listerTousLesMedecins() {
        return copyListM(em.createQuery(
                "SELECT m FROM Medecin m ORDER BY m.nom", Medecin.class)
                .getResultList());
    }

    @Override
    public Medecin trouverMedecinParId(Long id) {
        return copyM(em.find(Medecin.class, id));
    }

    @Override
    public List<Medecin> rechercherParSpecialite(String specialite) {
        return copyListM(em.createQuery(
                "SELECT m FROM Medecin m WHERE LOWER(m.specialite) LIKE :s ORDER BY m.nom",
                Medecin.class)
                .setParameter("s", "%" + specialite.toLowerCase() + "%")
                .getResultList());
    }

    @Override
    public List<Patient> obtenirPatientsDuMedecin(Long medecinId) {
        List<Patient> patients = em.createQuery(
                "SELECT DISTINCT r.patient FROM RendezVous r WHERE r.medecin.id = :id ORDER BY r.patient.nom",
                Patient.class)
                .setParameter("id", medecinId).getResultList();
        return copyListP(patients);
    }

    @Override
    public List<String> listerSpecialites() {
        return em.createQuery(
                "SELECT DISTINCT m.specialite FROM Medecin m ORDER BY m.specialite",
                String.class).getResultList();
    }

    @Override
    public Medecin ajouterMedecin(Medecin medecin) {
        em.persist(medecin);
        em.flush();
        return copyM(medecin);
    }

    @Override
    public Medecin modifierMedecin(Long id, Medecin mod) {
        Medecin m = em.find(Medecin.class, id);
        if (m != null) {
            m.setNom(mod.getNom()); m.setPrenom(mod.getPrenom());
            m.setSpecialite(mod.getSpecialite()); m.setEmail(mod.getEmail());
            em.merge(m); em.flush();
        }
        return copyM(m);
    }

    @Override
    public boolean supprimerMedecin(Long id) {
        Medecin m = em.find(Medecin.class, id);
        if (m == null) return false;
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :id AND r.statut = 'PLANIFIE'",
                Long.class).setParameter("id", id).getSingleResult();
        if (count > 0) return false;
        em.remove(em.merge(m));
        return true;
    }

    // ── Deep-copy helpers ────────────────────────────────────────────────────
    private Medecin copyM(Medecin m) {
        if (m == null) return null;
        Medecin c = new Medecin(m.getNom(), m.getPrenom(), m.getSpecialite(), m.getEmail());
        c.setId(m.getId());
        c.setRendezVous(new ArrayList<>());  // plain ArrayList — no Hibernate proxy
        return c;
    }

    private List<Medecin> copyListM(List<Medecin> list) {
        return list.stream().map(this::copyM).collect(Collectors.toList());
    }

    private Patient copyP(Patient p) {
        if (p == null) return null;
        Patient c = new Patient(p.getNom(), p.getPrenom(),
                p.getEmail(), p.getTelephone(), p.getDateNaissance());
        c.setId(p.getId());
        c.setRendezVous(new ArrayList<>());
        return c;
    }

    private List<Patient> copyListP(List<Patient> list) {
        return list.stream().map(this::copyP).collect(Collectors.toList());
    }
}
