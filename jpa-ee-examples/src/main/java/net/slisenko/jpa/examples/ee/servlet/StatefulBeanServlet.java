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

//    @EJB
//    private TestTransactionsAndExceptions bean2;

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
//            resp.getWriter().print("add number to = " + bean2.callAdd());
        }

        if (req.getParameter("view") != null) {
            resp.getWriter().print("all numbers = " + bean.getNumbers());
//            resp.getWriter().print("all numbers = " + bean2.callGetNumbers());
        }

        if (req.getParameter("throw") != null) {
            bean.throwException();
//            bean2.callSessionBeanWithError();
        }
    }
}
