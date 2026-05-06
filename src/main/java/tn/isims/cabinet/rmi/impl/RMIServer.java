package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.rmi.callback.PatientCallback;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           Serveur RMI — Cabinet Médical ISIMS               ║
 * ║  Améliorations :                                             ║
 * ║  • Console admin interactive (statut, clients, broadcast)    ║
 * ║  • Logs horodatés et filtrés (plus de flood WildFly)         ║
 * ║  • Arrêt propre sur commande "quit"                          ║
 * ║  • Tableau de bord des patients connectés                    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class RMIServer {

    private static final int    RMI_PORT     = 1099;
    private static final String SERVICE_NAME = "CabinetRMIService";

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ── Suppress WildFly / JBoss verbose startup logs ─────────────────────────
    static {
        silenceJBossLogs();
    }

    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            banniere();
            log("Démarrage du registre RMI sur le port " + RMI_PORT + "...");

            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            CabinetRMIServiceRemote service = new CabinetRMIService();
            registry.rebind(SERVICE_NAME, service);

            log("✅ Service '" + SERVICE_NAME + "' enregistré avec succès.");
            log("✅ Serveur RMI prêt — en attente de clients...");
            separateur();
            afficherAide();

            // ── Admin console loop ────────────────────────────────────────────
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("\n  [ADMIN] > ");
                if (!sc.hasNextLine()) break;
                String cmd = sc.nextLine().trim().toLowerCase();

                switch (cmd) {
                    case "status", "s"  -> afficherStatut();
                    case "clients", "c" -> afficherClients();
                    case "broadcast", "b" -> {
                        System.out.print("  Message à envoyer à tous les patients: ");
                        String msg = sc.nextLine().trim();
                        if (!msg.isEmpty()) broadcastMessage(msg);
                    }
                    case "kick" -> {
                        System.out.print("  ID patient à déconnecter: ");
                        String idStr = sc.nextLine().trim();
                        kickPatient(idStr);
                    }
                    case "help", "h", "?" -> afficherAide();
                    case "quit", "q", "exit" -> {
                        log("Arrêt du serveur RMI...");
                        try { registry.unbind(SERVICE_NAME); } catch (Exception ignored) {}
                        log("✅ Serveur arrêté proprement. Au revoir.");
                        System.exit(0);
                    }
                    case "" -> {}  // ignore empty lines
                    default -> System.out.println("  ⚠ Commande inconnue. Tapez 'help' pour la liste.");
                }
            }

        } catch (Exception e) {
            System.err.println("\n❌ Erreur fatale: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Status ────────────────────────────────────────────────────────────────
    private static void afficherStatut() {
        separateur();
        System.out.printf("  📊  STATUT DU SERVEUR RMI — %s%n", maintenant());
        System.out.printf("  %-25s %s%n", "Port RMI:",     RMI_PORT);
        System.out.printf("  %-25s %s%n", "Service:",      SERVICE_NAME);
        System.out.printf("  %-25s %s%n", "Hostname:",     "127.0.0.1");
        System.out.printf("  %-25s %d patient(s)%n",
            "Clients connectés:", PatientNotificationRegistry.getNombreConnectes());
        separateur();
    }

    // ── Clients ───────────────────────────────────────────────────────────────
    private static void afficherClients() {
        separateur();
        Map<Long, PatientCallback> clients = PatientNotificationRegistry.getTousLesCallbacks();
        if (clients.isEmpty()) {
            System.out.println("  Aucun patient connecté.");
        } else {
            System.out.printf("  %-6s  %-22s  %-22s  %s%n",
                "ID", "Nom", "Connecté depuis", "Joignable");
            System.out.println("  " + "─".repeat(70));
            for (Long id : clients.keySet()) {
                String nom    = PatientNotificationRegistry.getNom(id);
                String depuis = PatientNotificationRegistry.getDateConnexion(id);
                boolean ok    = testerCallback(id);
                System.out.printf("  %-6d  %-22s  %-22s  %s%n",
                    id, nom, depuis, ok ? "✅ OUI" : "❌ Déconnecté");
            }
        }
        separateur();
    }

    // ── Broadcast ─────────────────────────────────────────────────────────────
    private static void broadcastMessage(String message) {
        Map<Long, PatientCallback> clients = PatientNotificationRegistry.getTousLesCallbacks();
        if (clients.isEmpty()) {
            System.out.println("  ℹ Aucun patient connecté.");
            return;
        }
        int envoyes = 0;
        for (Map.Entry<Long, PatientCallback> e : clients.entrySet()) {
            try {
                e.getValue().recevoirNotification("📢 ANNONCE: " + message);
                envoyes++;
            } catch (RemoteException ex) {
                log("⚠ Patient " + e.getKey() + " injoignable — désinscrit.");
                PatientNotificationRegistry.desinscrire(e.getKey());
            }
        }
        log("📢 Broadcast envoyé à " + envoyes + " patient(s) : " + message);
    }

    // ── Kick ──────────────────────────────────────────────────────────────────
    private static void kickPatient(String idStr) {
        try {
            Long id = Long.parseLong(idStr.trim());
            if (!PatientNotificationRegistry.estEnregistre(id)) {
                System.out.println("  ⚠ Patient " + id + " non trouvé.");
                return;
            }
            try {
                PatientCallback cb = PatientNotificationRegistry.getCallback(id);
                cb.recevoirNotification("⚠ Vous avez été déconnecté par l'administrateur.");
            } catch (RemoteException ignored) {}
            PatientNotificationRegistry.desinscrire(id);
            log("🔌 Patient " + id + " déconnecté.");
        } catch (NumberFormatException e) {
            System.out.println("  ⚠ ID invalide.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Try pinging a callback to see if the client is still alive */
    private static boolean testerCallback(Long patientId) {
        PatientCallback cb = PatientNotificationRegistry.getCallback(patientId);
        if (cb == null) return false;
        try {
            cb.getPatientId(); // lightweight call
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    private static void afficherAide() {
        System.out.println("  Commandes disponibles :");
        System.out.println("    status   (s)  — Statut du serveur");
        System.out.println("    clients  (c)  — Liste des patients connectés");
        System.out.println("    broadcast(b)  — Envoyer un message à tous les patients");
        System.out.println("    kick          — Déconnecter un patient par ID");
        System.out.println("    help     (h)  — Afficher cette aide");
        System.out.println("    quit     (q)  — Arrêter le serveur proprement");
    }

    private static void banniere() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║        SERVEUR RMI — Cabinet Médical ISIMS           ║");
        System.out.println("║        2025-2026 · Technologies JEE · RMI Callback   ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void separateur() {
        System.out.println("  " + "─".repeat(70));
    }

    private static void log(String msg) {
        System.out.printf("  [%s] %s%n", maintenant(), msg);
    }

    private static String maintenant() {
        return LocalDateTime.now().format(FMT);
    }

    /**
     * Silences the verbose WildFly/JBoss/XNIO startup INFO logs that appear
     * when the first JNDI lookup initialises the remoting stack.
     * They are not errors — just library version announcements.
     */
    private static void silenceJBossLogs() {
        String[] noisyLoggers = {
            "org.wildfly.naming.client",
            "org.wildfly.security",
            "org.xnio",
            "org.xnio.nio",
            "org.jboss.threads",
            "org.jboss.remoting3",
            "org.jboss.ejb.client",
            "org.jboss.logging",
            "io.undertow",
            "org.jboss"
        };
        for (String name : noisyLoggers) {
            Logger logger = Logger.getLogger(name);
            logger.setLevel(Level.WARNING);  // only show warnings/errors
            logger.setUseParentHandlers(false);
        }
    }
}
