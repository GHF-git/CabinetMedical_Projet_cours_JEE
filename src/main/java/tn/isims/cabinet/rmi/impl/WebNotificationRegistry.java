package tn.isims.cabinet.rmi.impl;

import jakarta.servlet.AsyncContext;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for SSE (Server-Sent Events) web clients.
 * Bridges the EJB/RMI notification system to the patient browser dashboard.
 *
 * One patient can have MULTIPLE connections (multiple tabs) — all receive notifications.
 * The RMI server shows web-connected patients alongside terminal-connected ones.
 */
public class WebNotificationRegistry {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // patientId → list of active SSE connections (multiple tabs supported)
    private static final Map<Long, List<SseClient>> CLIENTS = new ConcurrentHashMap<>();
    // patientId → patient display name
    private static final Map<Long, String> NAMES = new ConcurrentHashMap<>();

    public static void register(Long patientId, String name,
                                 AsyncContext async, PrintWriter writer) {
        CLIENTS.computeIfAbsent(patientId, k -> new CopyOnWriteArrayList<>())
               .add(new SseClient(async, writer));
        if (name != null) NAMES.put(patientId, name);
        System.out.printf("  [%s] 🌐 WEB  %-20s (ID:%d) connecté — %d onglet(s) ouvert(s)%n",
            LocalDateTime.now().format(FMT),
            NAMES.getOrDefault(patientId, "Patient #" + patientId),
            patientId, getConnectionCount(patientId));
    }

    public static void unregister(Long patientId, AsyncContext async) {
        List<SseClient> list = CLIENTS.get(patientId);
        if (list != null) {
            list.removeIf(c -> c.async == async);
            if (list.isEmpty()) {
                CLIENTS.remove(patientId);
                System.out.printf("  [%s] 🌐 WEB  %-20s (ID:%d) déconnecté%n",
                    LocalDateTime.now().format(FMT),
                    NAMES.getOrDefault(patientId, "Patient #" + patientId), patientId);
            } else {
                System.out.printf("  [%s] 🌐 WEB  %-20s (ID:%d) onglet fermé — %d restant(s)%n",
                    LocalDateTime.now().format(FMT),
                    NAMES.getOrDefault(patientId, "Patient #" + patientId),
                    patientId, list.size());
            }
        }
    }

    /**
     * Send a notification to all browser tabs of a patient.
     * Called from RendezVousService (EJB) after each action.
     */
    public static void notify(Long patientId, String message) {
        List<SseClient> list = CLIENTS.get(patientId);
        if (list == null || list.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(FMT);
        // Escape message for JSON
        String safe = message.replace("\"", "\\\"").replace("\n", "\\n");
        String payload = String.format(
            "data: {\"type\":\"notification\",\"message\":\"%s\",\"time\":\"%s\"}\n\n",
            safe, timestamp);

        list.removeIf(c -> {
            try {
                c.writer.write(payload);
                c.writer.flush();
                return c.writer.checkError(); // remove dead connections
            } catch (Exception e) {
                return true;
            }
        });
    }

    public static boolean isConnected(Long patientId) {
        List<SseClient> list = CLIENTS.get(patientId);
        return list != null && !list.isEmpty();
    }

    public static int getConnectionCount(Long patientId) {
        List<SseClient> list = CLIENTS.get(patientId);
        return list == null ? 0 : list.size();
    }

    public static int getTotalWebClients() {
        return CLIENTS.values().stream().mapToInt(List::size).sum();
    }

    public static Map<Long, List<SseClient>> getAllClients() {
        return CLIENTS;
    }

    public static String getName(Long patientId) {
        return NAMES.getOrDefault(patientId, "Patient #" + patientId);
    }

    // ── Inner class ───────────────────────────────────────────────────────────
    public static class SseClient {
        public final AsyncContext async;
        public final PrintWriter  writer;
        public SseClient(AsyncContext async, PrintWriter writer) {
            this.async  = async;
            this.writer = writer;
        }
    }
}
