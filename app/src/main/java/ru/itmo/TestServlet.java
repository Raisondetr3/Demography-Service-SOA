package ru.itmo;

import java.io.*;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

public class TestServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(TestServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.info("Servlet works!");
        resp.getWriter().write("Servlet works!");
    }
}