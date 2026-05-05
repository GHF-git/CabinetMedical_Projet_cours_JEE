package tn.isims.cabinet.ejb.patient;

import jakarta.ejb.Local;
import tn.isims.cabinet.entity.Patient;
import java.util.List;

@Local
public interface PatientServiceLocal {
    List<Patient> listerTousLesPatients();
    Patient trouverPatientParId(Long id);
    Patient ajouterPatient(Patient patient);
    Patient modifierPatient(Long id, Patient patientModifie);
    boolean supprimerPatient(Long id);
    List<Patient> rechercherParNomOuEmail(String recherche);
    Patient trouverPatientParEmail(String email);
}
