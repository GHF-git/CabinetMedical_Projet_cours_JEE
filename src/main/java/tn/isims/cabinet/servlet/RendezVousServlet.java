package tn.isims.cabinet.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isims.cabinet.ejb.medecin.MedecinServiceLocal;
import tn.isims.cabinet.ejb.patient.PatientServiceLocal;
import tn.isims.cabinet.ejb.rendezvous.RendezVousServiceLocal;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet({"/rendezvous", "/rendezvous/*"})
public class RendezVousServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @EJB(beanName = "MedecinService")
    private MedecinServiceLocal medecinService;

    @EJB(beanName = "PatientService")
    private PatientServiceLocal patientService;

    @EJB(beanName = "RendezVousService")
    private RendezVousServiceLocal rendezVousService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Flush session flash messages into request
            transferFlash(request);

            String pathInfo = request.getPathInfo();
            String action = (pathInfo != null && pathInfo.length() > 1) ? pathInfo.substring(1) : "";

            switch (action) {
                case "add":
                    afficherFormulaireCreation(request, response);
                    break;
                case "edit":
                    afficherFormulaireModification(request, response);
                    break;
                case "du-jour":
                    listerRendezVousDuJour(request, response);
                    break;
                case "passes":
                    listerRendezVousPasses(request, response);
                    break;
                case "cancel":
                    // Support GET cancel via ?id=
                    doCancel(request, response);
                    break;
                case "terminate":
                    doTerminate(request, response);
                    break;
                default:
                    listerTousLesRendezVous(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, "Erreur lors du chargement des rendez-vous : " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            String pathAction = (pathInfo != null && pathInfo.length() > 1) ? pathInfo.substring(1) : "";
            String paramAction = request.getParameter("action");

            // Support both path-based and param-based action routing
            String action = !pathAction.isEmpty() ? pathAction : (paramAction != null ? paramAction : "");

            switch (action) {
                case "add":
                case "create":
                    creerRendezVous(request, response);
                    break;
                case "edit":
                case "update":
                    modifierRendezVous(request, response);
                    break;
                case "cancel":
                    doCancel(request, response);
                    break;
                case "terminate":
                    doTerminate(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/rendezvous");
            }
        } catch (Exception e) {
            handleError(request, response, "Erreur lors du traitement des rendez-vous : " + e.getMessage());
        }
    }

    // ── List views ───────────────────────────────────────────────

    private void listerTousLesRendezVous(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("rendezVous", rendezVousService.listerTousLesRendezVous());
        req.setAttribute("activeTab", "tous");
        req.getRequestDispatcher("/WEB-INF/views/rendezvous/liste.jsp").forward(req, res);
    }

    private void listerRendezVousDuJour(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("rendezVous", rendezVousService.listerRendezVousDuJour());
        req.setAttribute("activeTab", "jour");
        req.getRequestDispatcher("/WEB-INF/views/rendezvous/liste.jsp").forward(req, res);
    }

    private void listerRendezVousPasses(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("rendezVous", rendezVousService.listerRendezVousPasses());
        req.setAttribute("activeTab", "passes");
        req.getRequestDispatcher("/WEB-INF/views/rendezvous/liste.jsp").forward(req, res);
    }

    // ── Forms ────────────────────────────────────────────────────

    private void afficherFormulaireCreation(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("patients", patientService.listerTousLesPatients());
        req.setAttribute("medecins", medecinService.listerTousLesMedecins());
        req.getRequestDispatcher("/WEB-INF/views/rendezvous/creer.jsp").forward(req, res);
    }

    private void afficherFormulaireModification(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                RendezVous rdv = rendezVousService.trouverRendezVousParId(Long.parseLong(idStr));
                if (rdv != null) {
                    req.setAttribute("rendezVous", rdv);
                }
            } catch (NumberFormatException e) {
                req.setAttribute("erreur", "Identifiant invalide");
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/rendezvous/modifier.jsp").forward(req, res);
    }

    // ── Actions ──────────────────────────────────────────────────

    private void creerRendezVous(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        try {
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            Long medecinId = Long.parseLong(req.getParameter("medecinId"));
            LocalDateTime date = LocalDateTime.parse(req.getParameter("dateRendezVous"), FORMATTER);
            String motif = req.getParameter("motif");

            System.out.println("[RendezVousServlet] Creating rendez-vous patientId=" + patientId + ", medecinId=" + medecinId + ", date=" + date);
            RendezVous rdv = rendezVousService.creerRendezVous(patientId, medecinId, date, motif);
            flashSuccess(req, "Rendez-vous #" + rdv.getId() + " créé avec succès !");
        } catch (DateTimeParseException e) {
            flashError(req, "Format de date invalide.");
        } catch (Exception e) {
            flashError(req, "Erreur : " + e.getMessage());
        }
        res.sendRedirect(req.getContextPath() + "/rendezvous");
    }

    private void modifierRendezVous(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            LocalDateTime date = LocalDateTime.parse(req.getParameter("dateRendezVous"), FORMATTER);
            RendezVous rdv = rendezVousService.modifierHoraire(id, date);
            if (rdv != null) flashSuccess(req, "Rendez-vous modifié avec succès !");
            else flashError(req, "Rendez-vous introuvable ou déjà clôturé.");
        } catch (Exception e) {
            flashError(req, "Erreur : " + e.getMessage());
        }
        res.sendRedirect(req.getContextPath() + "/rendezvous");
    }

    private void doCancel(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                boolean ok = rendezVousService.annulerRendezVous(Long.parseLong(idStr));
                if (ok) flashSuccess(req, "Rendez-vous annulé avec succès.");
                else flashError(req, "Impossible d'annuler ce rendez-vous.");
            } catch (Exception e) {
                flashError(req, "Erreur : " + e.getMessage());
            }
        }
        res.sendRedirect(req.getContextPath() + "/rendezvous");
    }

    private void doTerminate(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                boolean ok = rendezVousService.marquerCommeTermine(Long.parseLong(idStr));
                if (ok) flashSuccess(req, "Rendez-vous marqué comme terminé.");
                else flashError(req, "Opération impossible.");
            } catch (Exception e) {
                flashError(req, "Erreur : " + e.getMessage());
            }
        }
        res.sendRedirect(req.getContextPath() + "/rendezvous");
    }

    // ── Flash message helpers ─────────────────────────────────────

    private void flashSuccess(HttpServletRequest req, String msg) {
        req.getSession().setAttribute("flashMessage", msg);
        req.getSession().setAttribute("flashType", "success");
    }

    private void flashError(HttpServletRequest req, String msg) {
        req.getSession().setAttribute("flashMessage", msg);
        req.getSession().setAttribute("flashType", "error");
    }

    private void transferFlash(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) {
            String msg = (String) s.getAttribute("flashMessage");
            String type = (String) s.getAttribute("flashType");
            if (msg != null) {
                if ("error".equals(type)) req.setAttribute("erreur", msg);
                else req.setAttribute("message", msg);
                s.removeAttribute("flashMessage");
                s.removeAttribute("flashType");
            }
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse res, String message)
            throws IOException {
        System.out.println("[RendezVousServlet] " + message);
        flashError(req, message);
        res.sendRedirect(req.getContextPath() + "/rendezvous");
    }
}
