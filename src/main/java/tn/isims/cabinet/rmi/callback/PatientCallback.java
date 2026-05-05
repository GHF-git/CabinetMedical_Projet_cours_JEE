package tn.isims.cabinet.rmi.callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface Remote pour le callback du patient
 * Permet au serveur d'envoyer des notifications en temps réel au patient
 */
public interface PatientCallback extends Remote {

    /**
     * Méthode appelée par le serveur pour envoyer une notification au patient
     * @param notification Le message de notification
     * @throws RemoteException si une erreur RMI se produit
     */
    void recevoirNotification(String notification) throws RemoteException;

    /**
     * Méthode pour obtenir l'identifiant du patient connecté
     * @return L'ID du patient
     * @throws RemoteException si une erreur RMI se produit
     */
    Long getPatientId() throws RemoteException;
}
