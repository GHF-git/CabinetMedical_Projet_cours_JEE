package tn.isims.cabinet.ejb.medecin;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;

import java.util.List;

/**
 * EJB Stateless pour la gestion des médecins
 */
@Stateless(mappedName = "MedecinService")
public class MedecinService implements MedecinServiceRemote, MedecinServiceLocal {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    @Override
    public List<Medecin> listerTousLesMedecins() {
        TypedQuery<Medecin> query = em.createQuery("SELECT m FROM Medecin m ORDER BY m.nom", Medecin.class);
        return query.getResultList();
    }

    @Override
    public Medecin trouverMedecinParId(Long id) {
        return em.find(Medecin.class, id);
    }

    @Override
    public Medecin ajouterMedecin(Medecin medecin) {
        em.persist(medecin);
        return medecin;
    }

    @Override
    public Medecin modifierMedecin(Long id, Medecin medecinModifie) {
        Medecin medecin = em.find(Medecin.class, id);
        if (medecin != null) {
            medecin.setNom(medecinModifie.getNom());
            medecin.setPrenom(medecinModifie.getPrenom());
            medecin.setSpecialite(medecinModifie.getSpecialite());
            medecin.setEmail(medecinModifie.getEmail());
            em.merge(medecin);
        }
        return medecin;
    }

    @Override
    public boolean supprimerMedecin(Long id) {
        Medecin medecin = em.find(Medecin.class, id);
        if (medecin != null) {
            // Vérifier s'il y a des rendez-vous
            if (!medecin.getRendezVous().isEmpty()) {
                return false; // Ne peut pas supprimer
            }
            em.remove(medecin);
            return true;
        }
        return false;
    }

    @Override
    public List<Medecin> rechercherParSpecialite(String specialite) {
        TypedQuery<Medecin> query = em.createQuery(
            "SELECT m FROM Medecin m WHERE LOWER(m.specialite) LIKE :spec ORDER BY m.nom",
            Medecin.class
        );
        query.setParameter("spec", "%" + specialite.toLowerCase() + "%");
        return query.getResultList();
    }

    @Override
    public List<Patient> obtenirPatientsDuMedecin(Long medecinId) {
        Medecin medecin = em.find(Medecin.class, medecinId);
        if (medecin != null) {
            return medecin.getRendezVous().stream()
                    .map(rdv -> rdv.getPatient())
                    .distinct()
                    .toList();
        }
        return List.of();
    }

    @Override
    public List<String> listerSpecialites() {
        TypedQuery<String> query = em.createQuery(
            "SELECT DISTINCT m.specialite FROM Medecin m ORDER BY m.specialite",
            String.class
        );
        return query.getResultList();
    }
}
