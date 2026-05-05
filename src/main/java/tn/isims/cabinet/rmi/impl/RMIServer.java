package tn.isims.cabinet.rmi.impl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe de démarrage du serveur RMI
 * À exécuter au démarrage de l'application pour exposer le service
 */
public class RMIServer {

    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "CabinetRMIService";

    public static void main(String[] args) {
        try {
            System.out.println("=== Démarrage du Serveur RMI ===");
            System.out.println("Port: " + RMI_PORT);

            // 1. Créer le registre RMI sur le port 1099
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            System.out.println("Registre RMI créé sur le port " + RMI_PORT);

            // 2. Créer et exposer le service
            CabinetRMIServiceRemote service = new CabinetRMIService();

            // 3. Lier le service au registre
            registry.rebind(SERVICE_NAME, service);

            System.out.println("Service '" + SERVICE_NAME + "' enregistré avec succès.");
            System.out.println("=== Serveur RMI prêt - En attente de clients... ===");

            // Garder le serveur actif indéfiniment
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur RMI:");
            e.printStackTrace();
        }
    }
}
