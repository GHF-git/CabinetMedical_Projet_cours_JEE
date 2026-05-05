package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.rmi.callback.PatientCallback;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Registre central pour gérer les callbacks des patients
 * Thread-safe pour supporter plusieurs connexions simultanées
 */
public class PatientNotificationRegistry {

    private static final Map<Long, PatientCallback> CALLBACKS = new ConcurrentHashMap<>();

    /**
     * Enregistre un callback pour un patient
     * @param patientId ID du patient
     * @param callback Interface de callback
     */
    public static void enregistrer(Long patientId, PatientCallback callback) {
        CALLBACKS.put(patientId, callback);
        System.out.println("[REGISTRY] Patient " + patientId + " enregistré pour notifications");
    }

    /**
     * Supprime l'enregistrement d'un patient
     * @param patientId ID du patient
     */
    public static void desinscrire(Long patientId) {
        CALLBACKS.remove(patientId);
        System.out.println("[REGISTRY] Patient " + patientId + " désinscrit des notifications");
    }

    /**
     * Récupère le callback d'un patient
     * @param patientId ID du patient
     * @return Le callback ou null si non enregistré
     */
    public static PatientCallback getCallback(Long patientId) {
        return CALLBACKS.get(patientId);
    }

    /**
     * Vérifie si un patient est enregistré
     * @param patientId ID du patient
     * @return true si enregistré
     */
    public static boolean estEnregistre(Long patientId) {
        return CALLBACKS.containsKey(patientId);
    }

    /**
     * Retourne le nombre de patients enregistrés
     * @return Nombre de callbacks actifs
     */
    public static int getNombreEnregistrements() {
        return CALLBACKS.size();
    }
}
