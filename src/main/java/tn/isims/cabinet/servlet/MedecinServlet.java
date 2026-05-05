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

/**
 * Servlet pour la gestion des médecins
 */
@WebServlet({"/medecins", "/medecins/*"})
public class MedecinServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(beanName = "MedecinService")
    private MedecinServiceLocal medecinService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            String action = pathInfo != null ? pathInfo.substring(1) : "";

            switch (action) {
                case "add":
                    afficherFormulaireAjout(request, response);
                    break;
                case "edit":
                    afficherFormulaireModification(request, response);
                    break;
                case "search":
                    rechercherParSpecialite(request, response);
                    break;
                case "patients":
                    afficherPatientsDuMedecin(request, response);
                    break;
                case "delete":
                    supprimerMedecin(request, response);
                    break;
                default:
                    listerMedecins(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, "Erreur lors du chargement des médecins : " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            String pathAction = (pathInfo != null && pathInfo.length() > 1) ? pathInfo.substring(1) : "";
            String paramAction = request.getParameter("action");
            String action = !pathAction.isEmpty() ? pathAction : (paramAction != null ? paramAction : "");

            switch (action) {
                case "add":
                case "save":
                case "edit":
                    sauvegarderMedecin(request, response);
                    break;
                case "delete":
                    supprimerMedecin(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/medecins");
            }
        } catch (Exception e) {
            handleError(request, response, "Erreur lors du traitement du médecin : " + e.getMessage());
        }
    }

    private void listerMedecins(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Medecin> medecins = medecinService.listerTousLesMedecins();
        List<String> specialites = medecinService.listerSpecialites();

        request.setAttribute("medecins", medecins);
        request.setAttribute("specialites", specialites);
        request.getRequestDispatcher("/WEB-INF/views/medecins/liste.jsp").forward(request, response);
    }

    private void rechercherParSpecialite(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String specialite = request.getParameter("specialite");
        List<Medecin> medecins;

        if (specialite != null && !specialite.trim().isEmpty()) {
            medecins = medecinService.rechercherParSpecialite(specialite);
        } else {
            medecins = medecinService.listerTousLesMedecins();
        }

        List<String> specialites = medecinService.listerSpecialites();

        request.setAttribute("medecins", medecins);
        request.setAttribute("specialites", specialites);
        request.setAttribute("specialiteSelectionnee", specialite);
        request.getRequestDispatcher("/WEB-INF/views/medecins/liste.jsp").forward(request, response);
    }

    private void afficherPatientsDuMedecin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            flash(request, "error", "Identifiant du médecin manquant.");
            response.sendRedirect(request.getContextPath() + "/medecins");
            return;
        }

        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            flash(request, "error", "Identifiant du médecin invalide.");
            response.sendRedirect(request.getContextPath() + "/medecins");
            return;
        }

        System.out.println("[MedecinServlet] Fetching patients for medecinId=" + id);
        Medecin medecin = medecinService.trouverMedecinParId(id);
        if (medecin == null) {
            flash(request, "error", "Médecin introuvable.");
            response.sendRedirect(request.getContextPath() + "/medecins");
            return;
        }

        List<Patient> patients = medecinService.obtenirPatientsDuMedecin(id);
        request.setAttribute("medecin", medecin);
        request.setAttribute("patients", patients);
        request.getRequestDispatcher("/WEB-INF/views/medecins/patients.jsp").forward(request, response);
    }

    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/medecins/ajouter.jsp").forward(request, response);
    }

    private void afficherFormulaireModification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            flash(request, "error", "Identifiant du médecin manquant.");
            response.sendRedirect(request.getContextPath() + "/medecins");
            return;
        }

        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            flash(request, "error", "Identifiant du médecin invalide.");
            response.sendRedirect(request.getContextPath() + "/medecins");
            return;
        }

        Medecin medecin = medecinService.trouverMedecinParId(id);
        if (medecin == null) {
            flash(request, "error", "Médecin introuvable.");
            response.sendRedirect(request.getContextPath() + "/medecins");
            return;
        }

        request.setAttribute("medecin", medecin);
        request.getRequestDispatcher("/WEB-INF/views/medecins/modifier.jsp").forward(request, response);
    }

    private void sauvegarderMedecin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        boolean editing = idStr != null && !idStr.isBlank();
        Medecin medecinForm = buildMedecinFromRequest(request);
        if (editing && idStr != null && !idStr.isBlank()) {
            try {
                medecinForm.setId(Long.parseLong(idStr));
            } catch (NumberFormatException ignored) {
                // Keep the form usable even if the id is invalid.
            }
        }

        try {
            if (!editing) {
                System.out.println("[MedecinServlet] Adding medecin: " + medecinForm.getPrenom() + " " + medecinForm.getNom());
                medecinService.ajouterMedecin(medecinForm);
                flash(request, "success", "Médecin ajouté avec succès !");
                response.sendRedirect(request.getContextPath() + "/medecins");
            } else {
                Long id = Long.parseLong(idStr);
                System.out.println("[MedecinServlet] Updating medecin id=" + id + " : " + medecinForm.getPrenom() + " " + medecinForm.getNom());
                Medecin medecin = medecinService.modifierMedecin(id, medecinForm);
                if (medecin == null) {
                    request.setAttribute("erreur", "Médecin introuvable.");
                    request.setAttribute("medecin", medecinForm);
                    request.getRequestDispatcher("/WEB-INF/views/medecins/modifier.jsp").forward(request, response);
                    return;
                }
                flash(request, "success", "Médecin modifié avec succès !");
                response.sendRedirect(request.getContextPath() + "/medecins");
            }
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur: " + e.getMessage());
            request.setAttribute("medecin", medecinForm);
            if (editing) {
                request.getRequestDispatcher("/WEB-INF/views/medecins/modifier.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/views/medecins/ajouter.jsp").forward(request, response);
            }
        }
    }

    private Medecin buildMedecinFromRequest(HttpServletRequest request) {
        return new Medecin(
            request.getParameter("nom"),
            request.getParameter("prenom"),
            request.getParameter("specialite"),
            request.getParameter("email")
        );
    }

    private void supprimerMedecin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            flash(request, "error", "Identifiant du médecin manquant.");
            response.sendRedirect(request.getContextPath() + "/medecins");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            boolean success = medecinService.supprimerMedecin(id);

            if (success) {
                flash(request, "success", "Médecin supprimé avec succès !");
            } else {
                flash(request, "error", "Impossible de supprimer: médecin avec des rendez-vous");
            }
            response.sendRedirect(request.getContextPath() + "/medecins");
        } catch (NumberFormatException e) {
            flash(request, "error", "Identifiant du médecin invalide.");
            response.sendRedirect(request.getContextPath() + "/medecins");
        }
    }

    private void flash(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        System.out.println("[MedecinServlet] " + message);
        flash(request, "error", message);
        response.sendRedirect(request.getContextPath() + "/medecins");
    }
}
