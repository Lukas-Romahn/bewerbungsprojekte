package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class MainMenuNavbar
 */
@WebServlet("/MainMenuNavbar")
public class MainMenuNavbar extends HttpServlet {
	private static final long serialVersionUID = 1L;

   
    public MainMenuNavbar() {
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsp= request.getParameter("action");
		if(jsp.equals("login")) {
			HttpSession session=request.getSession();
			session.invalidate();
			response.sendRedirect("/wai/login");

		}
		if(jsp.equals("homepage")) {
			response.sendRedirect("/wai/homepage");

		}
		
	}

}
