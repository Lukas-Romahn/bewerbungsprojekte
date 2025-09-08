package controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/gallery")
public class Gallery extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getParameter("kid") == null || req.getParameter("kName") == null){
            resp.sendRedirect("/wai/homepage");
            return;
        }
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/view/Gallery.jsp");
        dispatcher.forward(req, resp);
    }
}