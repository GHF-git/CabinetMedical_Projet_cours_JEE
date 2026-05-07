package tn.isims.cabinet.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.ejb.EJB;
import tn.isims.cabinet.ejb.patient.PatientServiceLocal;
import tn.isims.cabinet.entity.Patient;

import java.io.IOException;
@WebServlet({"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    @EJB(beanName = "PatientService")
    private PatientServiceLocal patientService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login?logout=success");
            return;
        }

        transferFlash(request);
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("admin".equals(username) && "admin".equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("authenticated", true);
            session.setAttribute("username", username);
            session.setAttribute("role", "ROLE_ADMIN");
            
            // Redirect to home page
            response.sendRedirect(request.getContextPath() + "/");
            return;
        } 
        
        // Check for patient login
        Patient patient = patientService.trouverPatientParEmail(username);
        if (patient != null && "patient".equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("authenticated", true);
            session.setAttribute("username", patient.getPrenom() + " " + patient.getNom());
            session.setAttribute("role", "ROLE_PATIENT");
            session.setAttribute("patientId", patient.getId());
            
            // Redirect to home page (which will act as patient dashboard)
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        flashError(request, "Identifiant ou mot de passe incorrect.");
        response.sendRedirect(request.getContextPath() + "/login");
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
}
