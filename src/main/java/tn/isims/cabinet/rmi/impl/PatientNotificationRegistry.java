package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.rmi.callback.PatientCallback;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registre central thread-safe pour les callbacks RMI des patients.
 * Garde aussi la date de connexion pour affichage dans le tableau de bord.
 */
public class PatientNotificationRegistry {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Callback par patientId
    private static final Map<Long, PatientCallback> CALLBACKS  = new ConcurrentHashMap<>();
    // Date de connexion par patientId
    private static final Map<Long, String>          CONNEXIONS = new ConcurrentHashMap<>();
    // Nom affiché par patientId (optionnel)
    private static final Map<Long, String>          NOMS       = new ConcurrentHashMap<>();

    public static void enregistrer(Long patientId, PatientCallback callback) {
        enregistrer(patientId, callback, null);
    }

    public static void enregistrer(Long patientId, PatientCallback callback, String nom) {
        CALLBACKS.put(patientId,  callback);
        CONNEXIONS.put(patientId, LocalDateTime.now().format(FMT));
        if (nom != null) NOMS.put(patientId, nom);
    }

    public static void desinscrire(Long patientId) {
        CALLBACKS.remove(patientId);
        CONNEXIONS.remove(patientId);
        NOMS.remove(patientId);
    }

    public static PatientCallback getCallback(Long patientId) {
        return CALLBACKS.get(patientId);
    }

    public static boolean estEnregistre(Long patientId) {
        return CALLBACKS.containsKey(patientId);
    }

    public static int getNombreConnectes() {
        return CALLBACKS.size();
    }

    public static Map<Long, PatientCallback> getTousLesCallbacks() {
        return CALLBACKS;
    }

    public static String getDateConnexion(Long patientId) {
        return CONNEXIONS.getOrDefault(patientId, "?");
    }

    public static String getNom(Long patientId) {
        return NOMS.getOrDefault(patientId, "Patient #" + patientId);
    }
}
