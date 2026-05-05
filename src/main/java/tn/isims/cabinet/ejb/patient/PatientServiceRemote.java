package tn.isims.cabinet.ejb.patient;

import jakarta.ejb.Remote;
import tn.isims.cabinet.entity.Patient;

import java.util.List;

/**
 * Interface Remote pour le service Patient
 */
@Remote
public interface PatientServiceRemote {

    List<Patient> listerTousLesPatients();

    Patient trouverPatientParId(Long id);

    Patient ajouterPatient(Patient patient);

    Patient modifierPatient(Long id, Patient patientModifie);

    boolean supprimerPatient(Long id);

    List<Patient> rechercherParNomOuEmail(String recherche);

    Patient trouverPatientParEmail(String email);
}
