package tn.isims.cabinet.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/patients/*", "/medecins/*", "/rendezvous/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("authenticated") != null && (Boolean) session.getAttribute("authenticated"));

        if (loggedIn) {
            String role = (String) session.getAttribute("role");
            String path = req.getServletPath();
            
            // Si c'est un patient qui tente d'accéder aux routes d'admin
            if ("ROLE_PATIENT".equals(role) && (path.startsWith("/patients") || path.startsWith("/medecins") || path.startsWith("/rendezvous"))) {
                req.getSession().setAttribute("flashMessage", "Accès refusé. Réservé à l'administration.");
                req.getSession().setAttribute("flashType", "error");
                res.sendRedirect(req.getContextPath() + "/");
                return;
            }
            
            chain.doFilter(request, response);
        } else {
            req.getSession().setAttribute("flashMessage", "Veuillez vous connecter pour accéder à cette page.");
            req.getSession().setAttribute("flashType", "error");
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
