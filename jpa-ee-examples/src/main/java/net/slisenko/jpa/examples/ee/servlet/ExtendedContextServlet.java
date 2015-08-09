package net.slisenko.jpa.examples.ee.servlet;

import net.slisenko.jpa.examples.ee.ejb.ExtendedContextBean;
import net.slisenko.jpa.examples.ee.ejb.SessionbeanWithErrors;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO добавить описание
 */
@WebServlet(urlPatterns = {"/extendedContext"})
public class ExtendedContextServlet extends HttpServlet {

    @EJB
    private ExtendedContextBean bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("add") != null) {
            resp.getWriter().print("add entity to = " + bean.addEntity());
        }

        if (req.getParameter("check") != null) {
            resp.getWriter().print("entity already in context = " + bean.checkEntityInContext());
        }
    }
}
