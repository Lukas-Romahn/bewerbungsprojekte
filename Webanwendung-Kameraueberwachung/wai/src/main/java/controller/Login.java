package controller;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dao.DaoFactory;
import dao.UserDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger jlog = LogManager.getLogger(Login.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session= request.getSession();
		jlog.info("1");
		
		if(session!=null) {
			session.invalidate();
		}

		try {
			showUI(request,response);
			}catch(Exception e) {
				e.printStackTrace();
			}		
		
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		process(request,response);
        
        
		
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		
		DaoFactory daofactory= DaoFactory.getInstance();
        
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		User user= new User();
		user.setUsername(username);
		user.setPassword(password);
		HttpSession session=request.getSession();
		
		if(username != null && !username.isEmpty()) {
			
			UserDao userdao=daofactory.getUserDao("psql");
			boolean exist=false;
			try {
				exist = userdao.checkLoginData(username, password);
			} catch (SQLException e) {
				
					showUI(request,response, "Sie sind nicht im Intranet");
					return;

			}
			
			if(exist) {//Loginname existiert also jetzt checken ob Password richtig ist
				//Funktion für die Prüfung von dem Password
					
					User pull=userdao.getRoles(userdao.getId(username));
					String Rolle= pull.getRolle();
					user.setRolle(Rolle);
					try {
						if(!user.getRolle().equals("Standortleiter")){
							user.transferStandorte(userdao.getStandorte());
						}else{
							user.transferStandorte(pull.getStandorte());
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					session.setAttribute("user", user);
					if(user.getRolle().equals("Admin")) {

					response.sendRedirect("/wai/admin/cameraManager");
					}else {
						response.sendRedirect("/wai/homepage");
					}
					
				}
				else {
					// Password falsch 
					
					showUI(request,response, "Benutzername oder Passwort sind ungültig");
					
				}
			}
			
		}
		
		
        
        
	
	void showUI(HttpServletRequest request,HttpServletResponse response)throws Exception {
		HttpSession session=request.getSession();

		
		RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/view/Login.jsp");		
		dispatcher.forward(request,response);
		
	}

	void showUI(HttpServletRequest request,HttpServletResponse response, String message) {

		request.setAttribute("error", message);
	    RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/view/Login.jsp");		
	    try {
			dispatcher.forward(request,response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	


}
