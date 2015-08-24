package net.slisenko.web;

import net.slisenko.sharedlib.MyEntityClass;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@WebServlet(urlPatterns = {"/pu2"})
public class PU2Servlet extends HttpServlet {

    @EJB
    private PU2Ejb ejb;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("Persistence unit2, entity=" + ejb.save());
    }
}