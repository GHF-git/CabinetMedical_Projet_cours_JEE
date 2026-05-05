package tn.isims.cabinet.ejb.medecin;

import jakarta.ejb.Remote;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;

import java.util.List;

/**
 * Interface Remote pour le service Médecin
 */
@Remote
public interface MedecinServiceRemote {

    List<Medecin> listerTousLesMedecins();

    Medecin trouverMedecinParId(Long id);

    Medecin ajouterMedecin(Medecin medecin);

    Medecin modifierMedecin(Long id, Medecin medecinModifie);

    boolean supprimerMedecin(Long id);

    List<Medecin> rechercherParSpecialite(String specialite);

    List<Patient> obtenirPatientsDuMedecin(Long medecinId);

    List<String> listerSpecialites();
}
