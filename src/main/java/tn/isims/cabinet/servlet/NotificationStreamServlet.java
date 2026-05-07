package tn.isims.cabinet.servlet;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isims.cabinet.rmi.impl.WebNotificationRegistry;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Server-Sent Events endpoint for real-time web notifications.
 * Patient dashboard connects here and receives push notifications
 * whenever RMI actions occur (create/modify/cancel RDV).
 *
 * URL: GET /notifications/stream
 */
@WebServlet(urlPatterns = "/notifications/stream", asyncSupported = true)
public class NotificationStreamServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Only logged-in patients can subscribe
        HttpSession session = request.getSession(false);
        if (session == null || !"ROLE_PATIENT".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long patientId = (Long) session.getAttribute("patientId");
        String patientName = (String) session.getAttribute("username");

        // SSE headers
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");

        AsyncContext async = request.startAsync();
        async.setTimeout(0); // no timeout — long-lived connection

        PrintWriter writer = response.getWriter();

        // Send initial connection confirmation
        writer.write("data: {\"type\":\"connected\",\"message\":\"Connecté aux notifications en temps réel\"}\n\n");
        writer.flush();

        // Register this SSE client
        WebNotificationRegistry.register(patientId, patientName, async, writer);

        // Cleanup when client disconnects
        async.addListener(new jakarta.servlet.AsyncListener() {
            @Override public void onComplete(jakarta.servlet.AsyncEvent e) {
                WebNotificationRegistry.unregister(patientId, async);
            }
            @Override public void onTimeout(jakarta.servlet.AsyncEvent e) {
                WebNotificationRegistry.unregister(patientId, async);
            }
            @Override public void onError(jakarta.servlet.AsyncEvent e) {
                WebNotificationRegistry.unregister(patientId, async);
            }
            @Override public void onStartAsync(jakarta.servlet.AsyncEvent e) {}
        });
    }
}
