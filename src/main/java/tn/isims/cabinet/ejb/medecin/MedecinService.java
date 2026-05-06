package tn.isims.cabinet.ejb.medecin;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;

import java.util.List;

@Stateless(mappedName = "MedecinService")
public class MedecinService implements MedecinServiceRemote, MedecinServiceLocal {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    @Override
    public List<Medecin> listerTousLesMedecins() {
        return em.createQuery("SELECT m FROM Medecin m ORDER BY m.nom", Medecin.class)
                 .getResultList();
    }

    @Override
    public Medecin trouverMedecinParId(Long id) {
        return em.find(Medecin.class, id);
    }

    @Override
    public Medecin ajouterMedecin(Medecin medecin) {
        em.persist(medecin);
        em.flush();   // force immediate INSERT so errors surface in the EJB
        return medecin;
    }

    @Override
    public Medecin modifierMedecin(Long id, Medecin medecinModifie) {
        Medecin m = em.find(Medecin.class, id);
        if (m != null) {
            m.setNom(medecinModifie.getNom());
            m.setPrenom(medecinModifie.getPrenom());
            m.setSpecialite(medecinModifie.getSpecialite());
            m.setEmail(medecinModifie.getEmail());
            em.merge(m);
            em.flush();
        }
        return m;
    }

    @Override
    public boolean supprimerMedecin(Long id) {
        Medecin m = em.find(Medecin.class, id);
        if (m == null) return false;

        // Count active appointments — do NOT touch lazy collection
        Long count = em.createQuery(
            "SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :id AND r.statut = 'PLANIFIE'",
            Long.class)
            .setParameter("id", id)
            .getSingleResult();

        if (count > 0) return false;

        em.remove(m);
        return true;
    }

    @Override
    public List<Medecin> rechercherParSpecialite(String specialite) {
        return em.createQuery(
            "SELECT m FROM Medecin m WHERE LOWER(m.specialite) LIKE :spec ORDER BY m.nom",
            Medecin.class)
            .setParameter("spec", "%" + specialite.toLowerCase() + "%")
            .getResultList();
    }

    @Override
    public List<Patient> obtenirPatientsDuMedecin(Long medecinId) {
        // Bug fix: direct JPQL query — never access lazy collections outside EJB transaction
        return em.createQuery(
            "SELECT DISTINCT r.patient FROM RendezVous r WHERE r.medecin.id = :id ORDER BY r.patient.nom",
            Patient.class)
            .setParameter("id", medecinId)
            .getResultList();
    }

    @Override
    public List<String> listerSpecialites() {
        return em.createQuery(
            "SELECT DISTINCT m.specialite FROM Medecin m ORDER BY m.specialite",
            String.class)
            .getResultList();
    }
}
