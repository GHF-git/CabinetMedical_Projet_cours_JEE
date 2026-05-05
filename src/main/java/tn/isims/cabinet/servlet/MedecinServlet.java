package tn.isims.cabinet.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String pathAction = (pathInfo != null && pathInfo.length() > 1) ? pathInfo.substring(1) : "";
        String paramAction = request.getParameter("action");
        String action = !pathAction.isEmpty() ? pathAction : (paramAction != null ? paramAction : "");

        switch (action) {
            case "add":
            case "save":
                sauvegarderMedecin(request, response);
                break;
            case "delete":
                supprimerMedecin(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/medecins");
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
        if (idStr != null) {
            Long id = Long.parseLong(idStr);
            Medecin medecin = medecinService.trouverMedecinParId(id);
            List<Patient> patients = medecinService.obtenirPatientsDuMedecin(id);

            request.setAttribute("medecin", medecin);
            request.setAttribute("patients", patients);
            request.getRequestDispatcher("/WEB-INF/views/medecins/patients.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/medecins");
        }
    }

    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/medecins/ajouter.jsp").forward(request, response);
    }

    private void afficherFormulaireModification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            Long id = Long.parseLong(idStr);
            Medecin medecin = medecinService.trouverMedecinParId(id);
            request.setAttribute("medecin", medecin);
        }
        request.getRequestDispatcher("/WEB-INF/views/medecins/modifier.jsp").forward(request, response);
    }

    private void sauvegarderMedecin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idStr = request.getParameter("id");
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String specialite = request.getParameter("specialite");
            String email = request.getParameter("email");

            if (idStr == null || idStr.isEmpty()) {
                // Ajout d'un nouveau médecin
                Medecin medecin = new Medecin(nom, prenom, specialite, email);
                medecinService.ajouterMedecin(medecin);
                request.setAttribute("message", "Médecin ajouté avec succès!");
            } else {
                // Modification
                Long id = Long.parseLong(idStr);
                Medecin medecinModifie = new Medecin(nom, prenom, specialite, email);
                medecinService.modifierMedecin(id, medecinModifie);
                request.setAttribute("message", "Médecin modifié avec succès!");
            }
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/medecins");
    }

    private void supprimerMedecin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            Long id = Long.parseLong(idStr);
            boolean success = medecinService.supprimerMedecin(id);

            if (success) {
                request.setAttribute("message", "Médecin supprimé avec succès!");
            } else {
                request.setAttribute("erreur", "Impossible de supprimer: médecin avec des rendez-vous");
            }
        }

        response.sendRedirect(request.getContextPath() + "/medecins");
    }
}
