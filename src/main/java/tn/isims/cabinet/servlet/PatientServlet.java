package tn.isims.cabinet.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isims.cabinet.ejb.patient.PatientServiceLocal;
import tn.isims.cabinet.entity.Patient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet({"/patients", "/patients/*"})
public class PatientServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(beanName = "PatientService")
    private PatientServiceLocal patientService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        transferFlash(req);
        String pathInfo = req.getPathInfo();
        String action = (pathInfo != null && pathInfo.length() > 1) ? pathInfo.substring(1) : "";

        switch (action) {
            case "add":    afficherFormulaireAjout(req, res); break;
            case "edit":   afficherFormulaireModification(req, res); break;
            case "search": rechercherPatients(req, res); break;
            default:       listerPatients(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String action = (pathInfo != null && pathInfo.length() > 1)
            ? pathInfo.substring(1)
            : req.getParameter("action") != null ? req.getParameter("action") : "";

        switch (action) {
            case "add":
            case "save":   sauvegarderPatient(req, res); break;
            case "edit":   sauvegarderPatient(req, res); break;
            case "delete": supprimerPatient(req, res); break;
            default:       res.sendRedirect(req.getContextPath() + "/patients");
        }
    }

    private void listerPatients(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("patients", patientService.listerTousLesPatients());
        req.getRequestDispatcher("/WEB-INF/views/patients/liste.jsp").forward(req, res);
    }

    private void rechercherPatients(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String recherche = req.getParameter("recherche");
        List<Patient> patients = (recherche != null && !recherche.trim().isEmpty())
            ? patientService.rechercherParNomOuEmail(recherche)
            : patientService.listerTousLesPatients();
        req.setAttribute("patients", patients);
        req.setAttribute("recherche", recherche);
        req.getRequestDispatcher("/WEB-INF/views/patients/liste.jsp").forward(req, res);
    }

    private void afficherFormulaireAjout(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/patients/ajouter.jsp").forward(req, res);
    }

    private void afficherFormulaireModification(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            Patient p = patientService.trouverPatientParId(Long.parseLong(idStr));
            req.setAttribute("patient", p);
        }
        req.getRequestDispatcher("/WEB-INF/views/patients/modifier.jsp").forward(req, res);
    }

    private void sauvegarderPatient(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        try {
            String idStr = req.getParameter("id");
            String nom = req.getParameter("nom");
            String prenom = req.getParameter("prenom");
            String email = req.getParameter("email");
            String telephone = req.getParameter("telephone");
            String dateStr = req.getParameter("dateNaissance");
            LocalDate dateNaissance = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;

            if (idStr == null || idStr.isEmpty()) {
                patientService.ajouterPatient(new Patient(nom, prenom, email, telephone, dateNaissance));
                flash(req, "success", "Patient ajouté avec succès !");
            } else {
                patientService.modifierPatient(Long.parseLong(idStr),
                    new Patient(nom, prenom, email, telephone, dateNaissance));
                flash(req, "success", "Patient modifié avec succès !");
            }
        } catch (Exception e) {
            flash(req, "error", "Erreur : " + e.getMessage());
        }
        res.sendRedirect(req.getContextPath() + "/patients");
    }

    private void supprimerPatient(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            boolean ok = patientService.supprimerPatient(Long.parseLong(idStr));
            if (ok) flash(req, "success", "Patient supprimé avec succès !");
            else flash(req, "error", "Impossible : ce patient a des rendez-vous actifs.");
        }
        res.sendRedirect(req.getContextPath() + "/patients");
    }

    private void flash(HttpServletRequest req, String type, String msg) {
        req.getSession().setAttribute("flashMessage", msg);
        req.getSession().setAttribute("flashType", type);
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
}
