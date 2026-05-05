package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.rmi.callback.PatientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Implémentation du callback patient
 * Cette classe est instanciée côté client (patient) pour recevoir les notifications
 */
public class PatientCallbackImpl extends UnicastRemoteObject implements PatientCallback {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Long patientId;
    private final List<String> notifications = Collections.synchronizedList(new ArrayList<>());

    public PatientCallbackImpl(Long patientId) throws RemoteException {
        super();
        this.patientId = patientId;
    }

    @Override
    public void recevoirNotification(String notification) throws RemoteException {
        System.out.println("=== NOTIFICATION REÇUE ===");
        System.out.println("Pour le patient ID: " + patientId);
        System.out.println("Message: " + notification);
        System.out.println("=========================");

        // Stocker la notification
        notifications.add(notification);

        // Log dans un fichier pour audit
        logNotification(notification);
    }

    @Override
    public Long getPatientId() throws RemoteException {
        return patientId;
    }

    /**
     * Récupère toutes les notifications reçues
     * @return Liste des notifications
     */
    public List<String> getNotifications() {
        synchronized (notifications) {
            return new ArrayList<>(notifications);
        }
    }

    /**
     * Récupère les notifications non lues
     * @return Liste des notifications non lues
     */
    public List<String> getNotificationsNonLues() {
        synchronized (notifications) {
            List<String> copies = new ArrayList<>(notifications);
            notifications.clear();
            return copies;
        }
    }

    private void logNotification(String notification) {
        // Dans une vraie application, on écrirait dans un fichier de log
        // ou on enverrait vers une base de données
        System.out.println("[LOG] Notification enregistrée: " + notification);
    }
}
