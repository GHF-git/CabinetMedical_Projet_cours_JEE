package tn.isims.cabinet.ejb.medecin;

import jakarta.ejb.Local;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;
import java.util.List;

@Local
public interface MedecinServiceLocal {
    List<Medecin> listerTousLesMedecins();
    Medecin trouverMedecinParId(Long id);
    Medecin ajouterMedecin(Medecin medecin);
    Medecin modifierMedecin(Long id, Medecin medecinModifie);
    boolean supprimerMedecin(Long id);
    List<Medecin> rechercherParSpecialite(String specialite);
    List<Patient> obtenirPatientsDuMedecin(Long medecinId);
    List<String> listerSpecialites();
}
