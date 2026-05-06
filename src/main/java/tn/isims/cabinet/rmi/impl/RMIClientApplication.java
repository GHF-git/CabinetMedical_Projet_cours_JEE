package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;

import java.rmi.Naming;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Application cliente RMI — Cabinet Médical
 * Fixes:
 *  - Affiche la liste des patients au login (plus besoin de deviner l'ID)
 *  - Option 0 propose "changer de patient" ou "quitter"
 *  - Parsing d'heure tolérant (H:mm et HH:mm acceptés)
 *  - listerPatients() implémenté côté serveur
 *  - Notifications en temps réel via RMI Callback
 */
public class RMIClientApplication {

    private static final String SERVICE_URL = "rmi://localhost:1099/CabinetRMIService";

    private static final DateTimeFormatter DATE_DISPLAY =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_INPUT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CabinetRMIServiceRemote server;
    private PatientCallbackImpl     callback;
    private Long                    patientId;
    private String                  patientNom;
    private final Scanner           sc = new Scanner(System.in);

    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        new RMIClientApplication().demarrer();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void demarrer() {
        try {
            banniere();
            System.out.println("  Connexion au serveur RMI...");
            server = (CabinetRMIServiceRemote) Naming.lookup(SERVICE_URL);
            System.out.println("  ✅ Connecté au serveur Cabinet Médical.\n");

            // Outer loop: allows switching patient without restarting
            boolean continuer = true;
            while (continuer) {
                connecterPatient();
                boolean running = true;
                while (running) {
                    afficherMenu();
                    int choix = lireChoix(0, 5);
                    switch (choix) {
                        case 1 -> consulterRendezVous();
                        case 2 -> creerRendezVous();
                        case 3 -> modifierRendezVous();
                        case 4 -> annulerRendezVous();
                        case 5 -> voirNotifications();
                        case 0 -> running = false;
                    }
                    if (running) attendre();
                }

                // Option 0 was chosen — offer reconnect or exit
                continuer = menuQuitter();
            }

            System.out.println("\n  👋  Au revoir ! À bientôt au Cabinet Médical.\n");

        } catch (Exception e) {
            System.err.println("\n  ❌ Erreur de connexion: " + e.getMessage());
            System.err.println("  Vérifiez que le serveur RMI est démarré (RMIServer.java).");
        }
    }

    // ── Menu quitter — option 0 ───────────────────────────────────────────────
    private boolean menuQuitter() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           SESSION TERMINÉE           ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1 ─ Changer de patient              ║");
        System.out.println("║  0 ─ Quitter l'application           ║");
        System.out.println("╚══════════════════════════════════════╝");
        int choix = lireChoix(0, 1);
        return choix == 1;
    }

    // ── Connexion patient — affiche la liste ──────────────────────────────────
    private void connecterPatient() throws Exception {
        System.out.println("  ─────────────────────────────────────────────────────────");
        System.out.println("  LISTE DES PATIENTS");
        System.out.println("  ─────────────────────────────────────────────────────────");

        List<Patient> patients = null;
        try {
            patients = server.listerPatients();
        } catch (Exception e) {
            System.out.println("  ⚠ Impossible de charger la liste: " + e.getMessage());
        }

        if (patients != null && !patients.isEmpty()) {
            System.out.printf("  %-5s  %-20s  %s%n", "ID", "Nom Prénom", "Email");
            System.out.println("  " + "─".repeat(55));
            for (Patient p : patients) {
                System.out.printf("  %-5d  %-20s  %s%n",
                    p.getId(),
                    p.getPrenom() + " " + p.getNom(),
                    p.getEmail() != null ? p.getEmail() : "");
            }
            System.out.println("  " + "─".repeat(55));
        }

        // Ask for ID
        while (true) {
            System.out.print("\n  Entrez votre ID patient: ");
            String line = sc.nextLine().trim();
            try {
                patientId = Long.parseLong(line);
                break;
            } catch (NumberFormatException e) {
                System.out.println("  ⚠ Entrez un nombre valide.");
            }
        }

        // Find patient name for display
        patientNom = "";
        if (patients != null) {
            for (Patient p : patients) {
                if (p.getId().equals(patientId)) {
                    patientNom = p.getPrenom() + " " + p.getNom();
                    break;
                }
            }
        }

        // Register callback
        callback = new PatientCallbackImpl(patientId);
        server.sEnregistrerPourNotifications(patientId, callback);

        if (!patientNom.isEmpty()) {
            System.out.println("  👋  Bonjour, " + patientNom + " !");
        }
    }

    // ── Menu principal ────────────────────────────────────────────────────────
    private void afficherMenu() {
        int nonLues = callback.getNombreNonLues();
        String badge = nonLues > 0 ? " 🔔 (" + nonLues + " nouvelle(s))" : "";
        String titre = patientNom.isEmpty() ? "MENU PATIENT" : patientNom.toUpperCase();

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.printf( "║  %-36s║%n", titre);
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1 ─ Mes rendez-vous                 ║");
        System.out.println("║  2 ─ Créer un rendez-vous            ║");
        System.out.println("║  3 ─ Modifier un rendez-vous         ║");
        System.out.println("║  4 ─ Annuler un rendez-vous          ║");
        System.out.printf( "║  5 ─ Notifications%-19s║%n", badge);
        System.out.println("║  0 ─ Quitter / Changer de patient    ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ── 1. Consulter RDV ─────────────────────────────────────────────────────
    private List<RendezVous> consulterRendezVous() {
        System.out.println("\n  📋 MES RENDEZ-VOUS");
        ligne();
        try {
            List<RendezVous> liste = server.consulterRendezVousPassesEtFuturs(patientId);
            if (liste == null || liste.isEmpty()) {
                System.out.println("  Aucun rendez-vous trouvé.");
                return List.of();
            }
            System.out.printf("  %-4s  %-17s  %-20s  %-10s  %s%n",
                "N°", "Date", "Médecin", "Statut", "Motif");
            ligne();
            int i = 1;
            for (RendezVous r : liste) {
                String date    = r.getDateRendezVous() != null
                    ? r.getDateRendezVous().format(DATE_DISPLAY) : "—";
                String medecin = r.getMedecin() != null
                    ? "Dr. " + r.getMedecin().getNom() : "—";
                String statut  = r.getStatut() != null ? r.getStatut().name() : "—";
                String motif   = r.getMotif() != null ? r.getMotif() : "—";
                System.out.printf("  %-4d  %-17s  %-20s  %-10s  %s%n",
                    i++, date, medecin, statut, motif);
            }
            ligne();
            return liste;
        } catch (Exception e) {
            System.err.println("  ❌ Erreur: " + e.getMessage());
            return List.of();
        }
    }

    // ── 2. Créer RDV ─────────────────────────────────────────────────────────
    private void creerRendezVous() {
        System.out.println("\n  ➕ CRÉER UN RENDEZ-VOUS");
        ligne();
        try {
            List<Medecin> medecins = server.listerMedecins();
            if (medecins == null || medecins.isEmpty()) {
                System.out.println("  ❌ Aucun médecin disponible.");
                return;
            }
            System.out.println("  Choisissez un médecin :");
            for (int i = 0; i < medecins.size(); i++) {
                Medecin m = medecins.get(i);
                System.out.printf("    %d. Dr. %-20s (%s)%n",
                    i + 1, m.getNom() + " " + m.getPrenom(), m.getSpecialite());
            }
            int choixM = lireChoix(1, medecins.size()) - 1;
            Long medecinId = medecins.get(choixM).getId();

            // Date — today by default
            LocalDate aujourd_hui = LocalDate.now();
            System.out.printf("  Date (Entrée = aujourd'hui %s, ou JJ/MM/AAAA): ",
                aujourd_hui.format(DATE_INPUT));
            String dateStr = sc.nextLine().trim();
            LocalDate dateChoisie = dateStr.isEmpty()
                ? aujourd_hui
                : LocalDate.parse(dateStr, DATE_INPUT);

            // Time — lenient parser accepts H:MM and HH:MM
            System.out.print("  Heure (HH:MM, Entrée = heure actuelle): ");
            String heureStr = sc.nextLine().trim();
            LocalTime heureChoisie = heureStr.isEmpty()
                ? LocalTime.now().withSecond(0).withNano(0)
                : parseHeure(heureStr);

            LocalDateTime dateTime = LocalDateTime.of(dateChoisie, heureChoisie);

            System.out.print("  Motif (Entrée = Consultation): ");
            String motif = sc.nextLine().trim();
            if (motif.isEmpty()) motif = "Consultation";

            System.out.println("  ⏳ Création en cours...");
            String result = server.creerRendezVousRMI(patientId, medecinId, dateTime, motif);
            System.out.println("  " + result);

        } catch (DateTimeParseException e) {
            System.out.println("  ❌ Format de date/heure invalide. Utilisez JJ/MM/AAAA et HH:MM.");
        } catch (Exception e) {
            System.err.println("  ❌ Erreur: " + e.getMessage());
        }
    }

    // ── 3. Modifier RDV ──────────────────────────────────────────────────────
    private void modifierRendezVous() {
        System.out.println("\n  ✏️  MODIFIER UN RENDEZ-VOUS");
        ligne();
        try {
            List<RendezVous> liste = consulterRendezVous();
            if (liste.isEmpty()) return;

            List<RendezVous> modifiables = liste.stream()
                .filter(r -> r.getStatut() == RendezVous.Statut.PLANIFIE)
                .toList();
            if (modifiables.isEmpty()) {
                System.out.println("  ℹ Aucun rendez-vous modifiable (tous terminés/annulés).");
                return;
            }

            System.out.println("\n  Rendez-vous modifiables :");
            for (int i = 0; i < modifiables.size(); i++) {
                RendezVous r = modifiables.get(i);
                System.out.printf("    %d. %s — Dr. %s — %s%n",
                    i + 1,
                    r.getDateRendezVous().format(DATE_DISPLAY),
                    r.getMedecin() != null ? r.getMedecin().getNom() : "?",
                    r.getMotif() != null ? r.getMotif() : "");
            }
            int choix = lireChoix(1, modifiables.size()) - 1;
            RendezVous rdv = modifiables.get(choix);

            System.out.printf("  Nouvelle date (Entrée = garder %s, ou JJ/MM/AAAA): ",
                rdv.getDateRendezVous().format(DATE_INPUT));
            String dateStr = sc.nextLine().trim();
            LocalDate nouvelleDate = dateStr.isEmpty()
                ? rdv.getDateRendezVous().toLocalDate()
                : LocalDate.parse(dateStr, DATE_INPUT);

            System.out.printf("  Nouvelle heure (Entrée = garder %s, ou HH:MM): ",
                rdv.getDateRendezVous().format(DateTimeFormatter.ofPattern("HH:mm")));
            String heureStr = sc.nextLine().trim();
            LocalTime nouvelleHeure = heureStr.isEmpty()
                ? rdv.getDateRendezVous().toLocalTime()
                : parseHeure(heureStr);

            System.out.println("  ⏳ Modification en cours...");
            String result = server.modifierRendezVousRMI(rdv.getId(),
                LocalDateTime.of(nouvelleDate, nouvelleHeure));
            System.out.println("  " + result);

        } catch (DateTimeParseException e) {
            System.out.println("  ❌ Format invalide. Utilisez JJ/MM/AAAA et HH:MM.");
        } catch (Exception e) {
            System.err.println("  ❌ Erreur: " + e.getMessage());
        }
    }

    // ── 4. Annuler RDV ───────────────────────────────────────────────────────
    private void annulerRendezVous() {
        System.out.println("\n  ❌ ANNULER UN RENDEZ-VOUS");
        ligne();
        try {
            List<RendezVous> liste = consulterRendezVous();
            if (liste.isEmpty()) return;

            List<RendezVous> annulables = liste.stream()
                .filter(r -> r.getStatut() == RendezVous.Statut.PLANIFIE)
                .toList();
            if (annulables.isEmpty()) {
                System.out.println("  ℹ Aucun rendez-vous annulable.");
                return;
            }

            System.out.println("\n  Rendez-vous annulables :");
            for (int i = 0; i < annulables.size(); i++) {
                RendezVous r = annulables.get(i);
                System.out.printf("    %d. %s — Dr. %s — %s%n",
                    i + 1,
                    r.getDateRendezVous().format(DATE_DISPLAY),
                    r.getMedecin() != null ? r.getMedecin().getNom() : "?",
                    r.getMotif() != null ? r.getMotif() : "");
            }
            int choix = lireChoix(1, annulables.size()) - 1;
            RendezVous rdv = annulables.get(choix);

            System.out.printf("  Confirmer l'annulation du RDV du %s avec Dr. %s ? (o/n): ",
                rdv.getDateRendezVous().format(DATE_DISPLAY),
                rdv.getMedecin() != null ? rdv.getMedecin().getNom() : "?");
            String confirm = sc.nextLine().trim();
            if (!confirm.equalsIgnoreCase("o")) {
                System.out.println("  Annulation abandonnée.");
                return;
            }

            System.out.println("  ⏳ Annulation en cours...");
            String result = server.annulerRendezVousRMI(rdv.getId());
            System.out.println("  " + result);

        } catch (Exception e) {
            System.err.println("  ❌ Erreur: " + e.getMessage());
        }
    }

    // ── 5. Notifications ─────────────────────────────────────────────────────
    private void voirNotifications() {
        List<String> notifs = callback.getNotifications();
        int nonLues = callback.getNombreNonLues();

        System.out.println("\n  🔔 MES NOTIFICATIONS");
        ligne();

        if (notifs.isEmpty()) {
            System.out.println("  Aucune notification reçue.");
        } else {
            int debut = notifs.size() - nonLues;
            for (int i = 0; i < notifs.size(); i++) {
                String marqueur = (i >= debut) ? "● " : "  ";
                System.out.println("  " + marqueur + notifs.get(i));
            }
            ligne();
            System.out.printf("  Total: %d notification(s)  |  %d non lue(s)%n",
                notifs.size(), nonLues);
        }
        callback.marquerToutesLues();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Parse une heure en format H:MM ou HH:MM (tolère l'heure à un seul chiffre)
     */
    private LocalTime parseHeure(String s) throws DateTimeParseException {
        // Normalize: "9:30" → "09:30"
        if (s.matches("^\\d:\\d{2}$")) s = "0" + s;
        return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void banniere() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   Cabinet Médical — Application Patient ║");
        System.out.println("║   ISIMS 2025-2026 · JEE · RMI Callback ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private void ligne() {
        System.out.println("  " + "─".repeat(65));
    }

    private int lireChoix(int min, int max) {
        while (true) {
            System.out.printf("  Votre choix (%d-%d): ", min, max);
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
                System.out.printf("  ⚠ Entrez un nombre entre %d et %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  ⚠ Entrez un nombre valide.");
            }
        }
    }

    private void attendre() {
        System.out.print("\n  ↩  Appuyez sur Entrée pour revenir au menu...");
        sc.nextLine();
    }
}
