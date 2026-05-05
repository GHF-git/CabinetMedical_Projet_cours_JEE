package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.rmi.callback.PatientCallback;
import tn.isims.cabinet.rmi.impl.CabinetRMIServiceRemote;

import java.net.InetAddress;
import java.rmi.Naming;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Application cliente RMI pour les patients
 * Permet aux patients de se connecter et recevoir des notifications
 */
public class RMIClientApplication {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "CabinetRMIService";

    private CabinetRMIServiceRemote server;
    private PatientCallbackImpl callback;
    private Long patientId;

    public static void main(String[] args) {
        new RMIClientApplication().demarrer();
    }

    public void demarrer() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Connexion au serveur
            System.out.println("=== Application Patient - Cabinet Médical ===");
            System.out.print("Entrez votre ID patient: ");
            patientId = scanner.nextLong();
            scanner.nextLine();

            // Créer le callback
            callback = new PatientCallbackImpl(patientId);

            // Rechercher le service sur le serveur
            String serviceUrl = "rmi://" + SERVER_ADDRESS + ":" + RMI_PORT + "/" + SERVICE_NAME;
            System.out.println("Connexion à: " + serviceUrl);

            server = (CabinetRMIServiceRemote) Naming.lookup(serviceUrl);

            // S'enregistrer pour les notifications
            boolean registered = server.sEnregistrerPourNotifications(patientId, callback);
            if (registered) {
                System.out.println("Enregistré avec succès! Vous recevrez les notifications.");
            }

            // Menu principal
            boolean running = true;
            while (running) {
                afficherMenu();
                int choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1:
                        consulterRendezVous();
                        break;
                    case 2:
                        creerRendezVous(scanner);
                        break;
                    case 3:
                        modifierRendezVous(scanner);
                        break;
                    case 4:
                        annulerRendezVous(scanner);
                        break;
                    case 5:
                        voirNotifications();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Au revoir!");
                        break;
                    default:
                        System.out.println("Option invalide");
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherMenu() {
        System.out.println("\n--- Menu Patient ---");
        System.out.println("1. Consulter mes rendez-vous");
        System.out.println("2. Créer un rendez-vous");
        System.out.println("3. Modifier un rendez-vous");
        System.out.println("4. Annuler un rendez-vous");
        System.out.println("5. Voir mes notifications");
        System.out.println("0. Quitter");
        System.out.print("Votre choix: ");
    }

    private void consulterRendezVous() {
        try {
            var rendezVous = server.consulterRendezVousPassesEtFuturs(patientId);
            if (rendezVous.isEmpty()) {
                System.out.println("Aucun rendez-vous trouvé.");
            } else {
                rendezVous.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void creerRendezVous(Scanner scanner) {
        try {
            System.out.print("ID Médecin: ");
            Long medecinId = scanner.nextLong();
            scanner.nextLine();

            System.out.print("Date (AAAA-MM-JJ HH:MM): ");
            String dateStr = scanner.nextLine();
            LocalDateTime date = LocalDateTime.parse(dateStr.replace(" ", "T"));

            System.out.print("Motif: ");
            String motif = scanner.nextLine();

            String result = server.creerRendezVousRMI(patientId, medecinId, date, motif);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void modifierRendezVous(Scanner scanner) {
        try {
            System.out.print("ID Rendez-vous: ");
            Long rdvId = scanner.nextLong();
            scanner.nextLine();

            System.out.print("Nouvelle date (AAAA-MM-JJ HH:MM): ");
            String dateStr = scanner.nextLine();
            LocalDateTime nouvelleDate = LocalDateTime.parse(dateStr.replace(" ", "T"));

            String result = server.modifierRendezVousRMI(rdvId, nouvelleDate);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void annulerRendezVous(Scanner scanner) {
        try {
            System.out.print("ID Rendez-vous à annuler: ");
            Long rdvId = scanner.nextLong();

            String result = server.annulerRendezVousRMI(rdvId);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void voirNotifications() {
        System.out.println("\n=== Mes Notifications ===");
        var notifications = callback.getNotifications();
        if (notifications.isEmpty()) {
            System.out.println("Aucune notification.");
        } else {
            notifications.forEach(n -> System.out.println("- " + n));
        }
    }
}
