package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.rmi.callback.PatientCallback;
import tn.isims.cabinet.rmi.impl.CabinetRMIServiceRemote;

import java.rmi.Naming;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Application cliente RMI pour les patients
 * Permet aux patients de se connecter et recevoir des notifications
 */
public class RMIClientApplication {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "CabinetRMIService";
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private CabinetRMIServiceRemote server;
    private PatientCallbackImpl callback;
    private Long patientId;

    public static void main(String[] args) {
        new RMIClientApplication().demarrer();
    }

    public void demarrer() {
        try (Scanner scanner = new Scanner(System.in)) {
            String serviceUrl = "rmi://" + SERVER_ADDRESS + ":" + RMI_PORT + "/" + SERVICE_NAME;
            System.out.println("Connexion à: " + serviceUrl);

            try {
                server = (CabinetRMIServiceRemote) Naming.lookup(serviceUrl);
            } catch (Exception e) {
                System.err.println("Impossible de se connecter au service RMI : " + e.getMessage());
                System.err.println("Assurez-vous que le serveur RMI est en cours d'exécution.");
                return;
            }

            boolean continueApp = true;
            while (continueApp) {
                System.out.println("\n=== Application Patient - Cabinet Médical ===");
                patientId = lireLong(scanner, "Entrez votre ID patient: ");

                try {
                    callback = new PatientCallbackImpl(patientId);

                    boolean registered = server.sEnregistrerPourNotifications(patientId, callback);
                    if (registered) {
                        System.out.println("Enregistré avec succès ! Vous recevrez les notifications.");
                    } else {
                        System.out.println("Impossible d'activer les notifications pour le moment.");
                    }

                    boolean running = true;
                    while (running) {
                        afficherMenu();
                        int choix = lireChoixMenu(scanner);

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
                            case 6:
                                running = false;
                                continueApp = gererQuitter(scanner);
                                break;
                            default:
                                System.out.println("Option invalide. Veuillez choisir un numéro entre 1 et 6.");
                        }

                        if (running) {
                            attendreRetourAuMenu(scanner);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la session patient : " + e.getMessage());
                    e.printStackTrace();
                    continueApp = false;
                }
            }
        } catch (Exception e) {
            System.err.println("Échec du client RMI : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean gererQuitter(Scanner scanner) {
        System.out.println("\n--- Fin de session ---");
        System.out.println("1. Quitter l'application");
        System.out.println("2. Choisir un autre ID patient");
        System.out.print("Votre choix: ");

        String choix = scanner.nextLine().trim();
        if (choix.equals("1")) {
            System.out.println("Au revoir !");
            return false; // Exit application
        } else if (choix.equals("2")) {
            System.out.println(""); // Blank line for readability
            return true; // Go back to patient ID input
        } else {
            System.out.println("Option invalide. Quitter l'application par défaut.");
            System.out.println("Au revoir !");
            return false;
        }
    }

    private void afficherMenu() {
        System.out.println("\n--- Menu Patient ---");
        System.out.println("1. Consulter mes rendez-vous");
        System.out.println("2. Créer un rendez-vous");
        System.out.println("3. Modifier un rendez-vous");
        System.out.println("4. Annuler un rendez-vous");
        System.out.println("5. Voir mes notifications");
        System.out.println("6. Quitter");
        System.out.print("Votre choix: ");
    }

    private int lireChoixMenu(Scanner scanner) {
        while (true) {
            String saisie = scanner.nextLine().trim();
            try {
                return Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.print("Veuillez saisir un nombre entre 1 et 6: ");
            }
        }
    }

    private Long lireLong(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String saisie = scanner.nextLine().trim();
            if (saisie.isEmpty()) {
                System.out.println("La valeur ne peut pas être vide.");
                continue;
            }
            try {
                return Long.parseLong(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un identifiant numérique valide.");
            }
        }
    }

    private LocalDateTime lireDateHeure(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String saisie = scanner.nextLine().trim();
            if (saisie.isEmpty()) {
                System.out.println("La date ne peut pas être vide. Format attendu : yyyy-MM-dd HH:mm");
                continue;
            }
            try {
                return LocalDateTime.parse(saisie, INPUT_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Format invalide. Utilisez le format yyyy-MM-dd HH:mm (ex: 2026-05-05 14:30).");
            }
        }
    }

    private String lireTexteObligatoire(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String saisie = scanner.nextLine().trim();
            if (!saisie.isEmpty()) {
                return saisie;
            }
            System.out.println("Cette valeur est obligatoire.");
        }
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
            System.err.println("Erreur lors de la consultation des rendez-vous : " + e.getMessage());
        }
    }

    private void creerRendezVous(Scanner scanner) {
        try {
            // Step 1: List all available doctors
            System.out.println("\n--- Sélectionner un médecin ---");
            var medecins = server.listerTousLesMedecins();

            if (medecins.isEmpty()) {
                System.out.println("❌ Aucun médecin disponible.");
                return;
            }

            // Display doctors with numbers for easy selection
            for (int i = 0; i < medecins.size(); i++) {
                System.out.printf("%d. Dr. %s (%s)\n", i + 1,
                    medecins.get(i).getNom(),
                    medecins.get(i).getSpecialite());
            }

            // Get doctor selection
            System.out.print("Sélectionnez un médecin (1-" + medecins.size() + "): ");
            int choice = lireChoixMenu(scanner);

            if (choice < 1 || choice > medecins.size()) {
                System.out.println("Selection invalide.");
                return;
            }

            Long medecinId = medecins.get(choice - 1).getId();

            // Step 2: Get date and reason
            LocalDateTime date = lireDateHeure(scanner, "Date du rendez-vous (yyyy-MM-dd HH:mm): ");
            String motif = lireTexteObligatoire(scanner, "Motif de la consultation: ");

            System.out.println("Création du rendez-vous en cours...");
            String result = server.creerRendezVousRMI(patientId, medecinId, date, motif);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Impossible de créer le rendez-vous : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void modifierRendezVous(Scanner scanner) {
        try {
            // Step 1: List all patient's rendez-vous
            System.out.println("\n--- Vos rendez-vous ---");
            var rendezVous = server.consulterRendezVousPassesEtFuturs(patientId);

            if (rendezVous.isEmpty()) {
                System.out.println("Aucun rendez-vous trouvé.");
                return;
            }

            // Display rendez-vous with numbers for easy selection
            for (int i = 0; i < rendezVous.size(); i++) {
                var rdv = rendezVous.get(i);
                System.out.printf("%d. RDV #%d - %s avec Dr. %s (Motif: %s)\n",
                    i + 1, rdv.getId(), rdv.getDateRendezVous(),
                    rdv.getMedecin().getNom(), rdv.getMotif());
            }

            // Get selection
            System.out.print("Sélectionnez un rendez-vous à modifier (1-" + rendezVous.size() + "): ");
            int choice = lireChoixMenu(scanner);

            if (choice < 1 || choice > rendezVous.size()) {
                System.out.println("Selection invalide.");
                return;
            }

            Long rdvId = rendezVous.get(choice - 1).getId();

            // Step 2: Get new date
            LocalDateTime nouvelleDate = lireDateHeure(scanner, "Nouvelle date (yyyy-MM-dd HH:mm): ");

            String result = server.modifierRendezVousRMI(rdvId, nouvelleDate);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Impossible de modifier le rendez-vous : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void annulerRendezVous(Scanner scanner) {
        try {
            // Step 1: List all patient's rendez-vous
            System.out.println("\n--- Vos rendez-vous ---");
            var rendezVous = server.consulterRendezVousPassesEtFuturs(patientId);

            if (rendezVous.isEmpty()) {
                System.out.println("Aucun rendez-vous trouvé.");
                return;
            }

            // Display rendez-vous with numbers for easy selection
            for (int i = 0; i < rendezVous.size(); i++) {
                var rdv = rendezVous.get(i);
                System.out.printf("%d. RDV #%d - %s avec Dr. %s (Motif: %s)\n",
                    i + 1, rdv.getId(), rdv.getDateRendezVous(),
                    rdv.getMedecin().getNom(), rdv.getMotif());
            }

            // Get selection
            System.out.print("Sélectionnez un rendez-vous à annuler (1-" + rendezVous.size() + "): ");
            int choice = lireChoixMenu(scanner);

            if (choice < 1 || choice > rendezVous.size()) {
                System.out.println("Selection invalide.");
                return;
            }

            Long rdvId = rendezVous.get(choice - 1).getId();

            String result = server.annulerRendezVousRMI(rdvId);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Impossible d'annuler le rendez-vous : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void voirNotifications() {
        System.out.println("\n=== Mes Notifications ===");
        if (callback == null) {
            System.out.println("Aucune session de notifications n'est disponible.");
            return;
        }

        var notifications = callback.getNotifications();
        if (notifications.isEmpty()) {
            System.out.println("Aucune notification.");
        } else {
            notifications.forEach(n -> System.out.println("- " + n));
            System.out.println("--- Fin des notifications ---");
        }
    }

    private void attendreRetourAuMenu(Scanner scanner) {
        System.out.print("\nAppuyez sur Entrée pour revenir au menu...");
        scanner.nextLine();
    }
}
