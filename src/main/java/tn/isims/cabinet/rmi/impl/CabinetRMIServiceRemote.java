package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;
import tn.isims.cabinet.rmi.callback.PatientCallback;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface Remote pour le service RMI du cabinet médical
 */
public interface CabinetRMIServiceRemote extends Remote {

    /**
     * Permet à un patient de consulter ses rendez-vous
     * @param patientId L'ID du patient
     * @return Liste des rendez-vous
     * @throws RemoteException
     */
    List<RendezVous> consulterRendezVous(Long patientId) throws RemoteException;

    /**
     * Permet à un patient de s'enregistrer pour recevoir des notifications
     * @param patientId L'ID du patient
     * @param callback L'interface de callback
     * @return true si l'enregistrement a réussi
     * @throws RemoteException
     */
    boolean sEnregistrerPourNotifications(Long patientId, PatientCallback callback)
            throws RemoteException;

    /**
     * Consulte tous les rendez-vous (passés et futurs)
     * @param patientId L'ID du patient
     * @return Liste des rendez-vous
     * @throws RemoteException
     */
    List<RendezVous> consulterRendezVousPassesEtFuturs(Long patientId) throws RemoteException;

    /**
     * Crée un rendez-vous via RMI
     * @param patientId ID du patient
     * @param medecinId ID du médecin
     * @param dateRendezVous Date et heure du RDV
     * @param motif Motif du rendez-vous
     * @return Message de confirmation
     * @throws RemoteException
     */
    String creerRendezVousRMI(Long patientId, Long medecinId,
                               LocalDateTime dateRendezVous, String motif) throws RemoteException;

    /**
     * Modifie l'horaire d'un rendez-vous via RMI
     * @param rdvId ID du rendez-vous
     * @param nouvelleDate Nouvelle date et heure
     * @return Message de confirmation
     * @throws RemoteException
     */
    String modifierRendezVousRMI(Long rdvId, LocalDateTime nouvelleDate) throws RemoteException;

    /**
     * Annule un rendez-vous via RMI
     * @param rdvId ID du rendez-vous
     * @return Message de confirmation
     * @throws RemoteException
     */
    String annulerRendezVousRMI(Long rdvId) throws RemoteException;

    /** Retourne la liste de tous les médecins disponibles */
    List<Medecin> listerMedecins() throws RemoteException;

    /** Retourne la liste de tous les patients */
    List<Patient> listerPatients() throws RemoteException;
}
