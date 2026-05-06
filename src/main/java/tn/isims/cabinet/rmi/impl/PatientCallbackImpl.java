package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.rmi.callback.PatientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implémentation du callback patient - reçoit les notifications en temps réel
 * CopyOnWriteArrayList = thread-safe (notifications arrivent depuis thread RMI)
 */
public class PatientCallbackImpl extends UnicastRemoteObject implements PatientCallback {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Long patientId;

    // Thread-safe list — notifications arrive on a separate RMI thread
    private final List<String> notifications = new CopyOnWriteArrayList<>();
    private int      derniereNotificationVue = 0; // track unread count

    public PatientCallbackImpl(Long patientId) throws RemoteException {
        super();
        this.patientId = patientId;
    }

    // ── Called by the server in real time ─────────────────────────────────────
    @Override
    public void recevoirNotification(String message) throws RemoteException {
        String horodatage = LocalDateTime.now().format(FMT);
        String entree     = "[" + horodatage + "] " + message;

        notifications.add(entree);

        // Print immediately — interrupts the menu display intentionally
        // (this is the "real-time" behaviour required by the project)
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│  🔔  NOUVELLE NOTIFICATION                  │");
        System.out.println("│  " + padRight(message, 43) + "│");
        System.out.println("│  🕐  " + padRight(horodatage, 39) + "│");
        System.out.println("└─────────────────────────────────────────────┘");
        System.out.print("  ↩  Appuyez sur Entrée pour continuer...");
        System.out.flush();
    }

    @Override
    public Long getPatientId() throws RemoteException {
        return patientId;
    }

    // ── Getters used by RMIClientApplication ──────────────────────────────────

    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public int getNombreNonLues() {
        return notifications.size() - derniereNotificationVue;
    }

    public void marquerToutesLues() {
        derniereNotificationVue = notifications.size();
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) s = s.substring(0, n - 1) + "…";
        return String.format("%-" + n + "s", s);
    }
}
