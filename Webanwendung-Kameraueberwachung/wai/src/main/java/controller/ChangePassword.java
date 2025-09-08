package controller;

import dao.DaoFactory;
import dao.UserDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import org.apache.logging.log4j.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/changePassword")
public class ChangePassword extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger jlog = LogManager.getLogger(ChangePassword.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/view/ChangePassword.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        DaoFactory daofactory = DaoFactory.getInstance();
        UserDao userdao = daofactory.getUserDao("psql");

        String message;

        if (username == null || username.isEmpty()) {
            message = "Benutzername darf nicht leer sein.";
        } else if (!newPassword.equals(confirmPassword)) {
            message = "Neue Passwörter stimmen nicht überein.";
        } else
			try {
				if (!userdao.checkLoginData(username, oldPassword)) {
				    message = "Benutzername oder aktuelles Passwort ist falsch.";
				} else {
				    boolean updated = userdao.updatePassword(username, newPassword);
				    if (updated) {
				        message = "Erfolg: Passwort wurde geändert.";
				    } else {
				        message = "Fehler beim Aktualisieren des Passworts.";
				    }
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				message="Sie sind nicht im Intranet";
			}

        request.setAttribute("message", message);
        request.getRequestDispatcher("/view/ChangePassword.jsp").forward(request, response);
    }
}