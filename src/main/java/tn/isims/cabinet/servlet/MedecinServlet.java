package tn.isims.cabinet.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isims.cabinet.ejb.medecin.MedecinServiceLocal;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;

import java.io.IOException;
import java.util.List;

@WebServlet({"/medecins", "/medecins/*"})
public class MedecinServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(beanName = "MedecinService")
    private MedecinServiceLocal medecinService;

    // ── GET ───────────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        transferFlash(req);   // move session flash → request attributes

        String pathInfo = req.getPathInfo();
        String action   = (pathInfo != null && pathInfo.length() > 1)
                          ? pathInfo.substring(1) : "";

        switch (action) {
            case "add"      -> afficherFormulaireAjout(req, res);
            case "edit"     -> afficherFormulaireModification(req, res);
            case "search"   -> rechercherParSpecialite(req, res);
            case "patients" -> afficherPatientsDuMedecin(req, res);
            case "delete"   -> supprimerMedecin(req, res);
            default         -> listerMedecins(req, res);
        }
    }

    // ── POST ──────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String pathInfo    = req.getPathInfo();
        String pathAction  = (pathInfo != null && pathInfo.length() > 1)
                             ? pathInfo.substring(1) : "";
        String paramAction = req.getParameter("action");
        String action      = !pathAction.isEmpty() ? pathAction
                             : (paramAction != null ? paramAction : "");

        switch (action) {
            case "add", "save", "edit" -> sauvegarderMedecin(req, res);
            case "delete"              -> supprimerMedecin(req, res);
            default                    -> res.sendRedirect(
                                              req.getContextPath() + "/medecins");
        }
    }

    // ── LIST ──────────────────────────────────────────────────────────────────
    private void listerMedecins(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("medecins",    medecinService.listerTousLesMedecins());
        req.setAttribute("specialites", medecinService.listerSpecialites());
        req.getRequestDispatcher("/WEB-INF/views/medecins/liste.jsp").forward(req, res);
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    private void rechercherParSpecialite(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String specialite = req.getParameter("specialite");
        List<Medecin> medecins = (specialite != null && !specialite.trim().isEmpty())
            ? medecinService.rechercherParSpecialite(specialite)
            : medecinService.listerTousLesMedecins();
        req.setAttribute("medecins",              medecins);
        req.setAttribute("specialites",           medecinService.listerSpecialites());
        req.setAttribute("specialiteSelectionnee", specialite);
        req.getRequestDispatcher("/WEB-INF/views/medecins/liste.jsp").forward(req, res);
    }

    // ── PATIENTS DU MÉDECIN ───────────────────────────────────────────────────
    private void afficherPatientsDuMedecin(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) { res.sendRedirect(req.getContextPath() + "/medecins"); return; }
        try {
            Long    id      = Long.parseLong(idStr);
            Medecin medecin = medecinService.trouverMedecinParId(id);
            // Bug fix: use a dedicated JPA query instead of lazy collection
            List<Patient> patients = medecinService.obtenirPatientsDuMedecin(id);
            req.setAttribute("medecin",  medecin);
            req.setAttribute("patients", patients);
        } catch (Exception e) {
            req.setAttribute("erreur", "Erreur: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/medecins/patients.jsp").forward(req, res);
    }

    // ── FORMS ─────────────────────────────────────────────────────────────────
    private void afficherFormulaireAjout(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/medecins/ajouter.jsp").forward(req, res);
    }

    private void afficherFormulaireModification(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            Medecin m = medecinService.trouverMedecinParId(Long.parseLong(idStr));
            req.setAttribute("medecin", m);
        }
        req.getRequestDispatcher("/WEB-INF/views/medecins/modifier.jsp").forward(req, res);
    }

    // ── SAVE (add OR edit) ────────────────────────────────────────────────────
    private void sauvegarderMedecin(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        try {
            String idStr     = req.getParameter("id");
            String nom       = req.getParameter("nom");
            String prenom    = req.getParameter("prenom");
            String specialite = req.getParameter("specialite");
            String email     = req.getParameter("email");

            if (nom == null || nom.trim().isEmpty()) {
                flash(req, "error", "Le nom est obligatoire.");
                res.sendRedirect(req.getContextPath() + "/medecins/add");
                return;
            }

            Medecin m = new Medecin(nom.trim(), prenom.trim(), specialite.trim(), email.trim());

            if (idStr == null || idStr.trim().isEmpty()) {
                medecinService.ajouterMedecin(m);
                flash(req, "success", "Médecin Dr. " + prenom + " " + nom + " ajouté avec succès !");
            } else {
                medecinService.modifierMedecin(Long.parseLong(idStr), m);
                flash(req, "success", "Médecin modifié avec succès !");
            }
        } catch (Exception e) {
            flash(req, "error", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
        // POST → Redirect → GET  (never setAttribute on redirect)
        res.sendRedirect(req.getContextPath() + "/medecins");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    private void supprimerMedecin(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                boolean ok = medecinService.supprimerMedecin(Long.parseLong(idStr));
                if (ok) flash(req, "success", "Médecin supprimé avec succès.");
                else    flash(req, "error",   "Impossible de supprimer : ce médecin a des rendez-vous.");
            } catch (Exception e) {
                flash(req, "error", "Erreur : " + e.getMessage());
            }
        }
        res.sendRedirect(req.getContextPath() + "/medecins");
    }

    // ── Flash helpers ─────────────────────────────────────────────────────────
    private void flash(HttpServletRequest req, String type, String msg) {
        HttpSession s = req.getSession();
        s.setAttribute("flashMessage", msg);
        s.setAttribute("flashType",    type);
    }

    private void transferFlash(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return;
        String msg  = (String) s.getAttribute("flashMessage");
        String type = (String) s.getAttribute("flashType");
        if (msg != null) {
            if ("error".equals(type)) req.setAttribute("erreur",  msg);
            else                      req.setAttribute("message", msg);
            s.removeAttribute("flashMessage");
            s.removeAttribute("flashType");
        }
    }
}
