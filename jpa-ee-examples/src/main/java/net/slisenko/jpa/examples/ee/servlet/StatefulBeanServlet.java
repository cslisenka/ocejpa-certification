package net.slisenko.jpa.examples.ee.servlet;

import net.slisenko.jpa.examples.ee.ejb.SessionbeanWithErrors;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/stateful"})
public class StatefulBeanServlet extends HttpServlet {

    @EJB
    private SessionbeanWithErrors bean;

    /**
     * Если подинжектать Stateful-бин напрямую, то он создаётся один на сессию. После выпадения эксепшена, бин уничтожается.
     * И далее обращения к сервлету вызывают ошибки.
     *
     * Если инжектать Stateful-бин прямо к Stateless, то он создаётся каждый раз новый.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("add") != null) {
            resp.getWriter().print("add number to = " + bean.addNumber());
        }

        if (req.getParameter("view") != null) {
            resp.getWriter().print("all numbers = " + bean.getNumbers());
        }

        if (req.getParameter("throw") != null) {
            bean.throwException();
        }

        // Even when we handle exception, session bean is discarded
        if (req.getParameter("throwAndHandle") != null) {
            try {
                bean.throwException();
            } catch (Exception e) {
                resp.getWriter().print("handled exception = " + e);
            }
        }
    }
}
