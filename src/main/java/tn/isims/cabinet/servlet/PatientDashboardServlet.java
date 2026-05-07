package tn.isims.cabinet.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isims.cabinet.ejb.patient.PatientServiceLocal;
import tn.isims.cabinet.ejb.rendezvous.RendezVousServiceLocal;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;

import java.io.IOException;
import java.util.List;

@WebServlet("/mon-espace")
public class PatientDashboardServlet extends HttpServlet {

    @EJB(beanName = "RendezVousService")
    private RendezVousServiceLocal rendezVousService;

    @EJB(beanName = "PatientService")
    private PatientServiceLocal patientService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ROLE_PATIENT".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long patientId = (Long) session.getAttribute("patientId");
        Patient patient = patientService.trouverPatientParId(patientId);
        List<RendezVous> rdvList = rendezVousService.listerRendezVousParPatient(patientId);

        request.setAttribute("patient", patient);
        request.setAttribute("rendezVousList", rdvList);

        request.getRequestDispatcher("/WEB-INF/views/patients/dashboard.jsp").forward(request, response);
    }
}
