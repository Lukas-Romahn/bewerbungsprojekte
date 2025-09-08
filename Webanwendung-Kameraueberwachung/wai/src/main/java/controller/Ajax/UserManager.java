package controller.Ajax;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import dao.DaoFactory;
import dao.UserDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import utils.QueryStrategy.QueryBuilder;
import utils.QueryStrategy.likeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Servlet implementation class BenutzerVerwaltung
 */
@WebServlet("/admin/userManager")
public class UserManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private UserDao dao;
    private final Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(UserManager.class);    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserManager() {
       super();
		DaoFactory daofac =DaoFactory.getInstance();
		dao= daofac.getUserDao("psql");
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String info = request.getParameter("info");
		String name = request.getParameter("name");
		RequestDispatcher dispatcher=getServletContext().getRequestDispatcher("/view/userManager.jsp");
		String editError= request.getParameter("edit");
		if(editError==null) {
			editError="null";
		}
		if(info==null) {
			info="null";
		}
		
		if(info.equals("ajax")) {
			QueryBuilder builder = new QueryBuilder();
			if(name != null){
				builder.addStrategy(new likeStrategy<>("n.username", name));
			}
			response.setContentType("application/json");
	    	response.setCharacterEncoding("UTF-8");
			ArrayList<User> userlist=new ArrayList<>();
			Map<String, Object> responseMap = new HashMap<>();
		try {
			userlist = dao.getAllUser(builder);
			responseMap.put("success", true);
	        responseMap.put("users", userlist);
		} catch (SQLException e) {
			responseMap.put("success", false);
	        responseMap.put("error", "sql");
	        logger.error("Der Server konnte keine Daten für die Benutzerverwaltung holen");


		}
		/*if(userlist!=null) {
		for (User user1 : userlist) {
		    System.out.println(user1.getUsername());
		    System.out.println(user1.getId());
		    System.out.println(user1.getRolle());

		    for (String adresse : user1.getStandorte()) {
		        System.out.println(adresse);
		    }
		}}*/
		
		String json=gson.toJson(responseMap);
		System.out.println(json);
		response.getWriter().write(json);
		
		
		}else {
			dispatcher=getServletContext().getRequestDispatcher("/view/userManager.jsp");
			request.setAttribute("error", editError);
			dispatcher.forward(request, response);

		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String userIdStr= request.getParameter("userId");
        try {
			dao.deleteUserbyId(Integer.parseInt(userIdStr));
			logger.info("Nutzer wurde gelöscht");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Nutzer konnte nich gelöscht werden");
		}
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
       
        String aktion = request.getParameter("aktion");
        
        if(aktion.equals("cancel")) {
        	
        	try {
				doGet(request,response);
			} catch (ServletException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        
        if ("userAdd".equals(aktion)) {
    		RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/view/userAdd.jsp");
    		List<String> rollen= List.of("Standortleiter","Admin","Chef");
    		request.setAttribute("rollen", rollen);
    		try {
				request.setAttribute("standorte", dao.getStandorte());
			} catch (SQLException e) {
				request.setAttribute("error","Sie können keinen Benutzer hinzufügen, sie sind nicht im Intranet");
        		dispatcher= getServletContext().getRequestDispatcher("/view/userManager.jsp");
    			logger.error("keine Verbindung zur Datenbank");

			}
    		try {
    			
				dispatcher.forward(request, response);		
				return;
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
        }
        if(aktion.equals("userAdded")) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/view/userManager.jsp");

        	String username=request.getParameter("username").toString();
        	String password=request.getParameter("password").toString();
        	String rolle=request.getParameter("rollen").toString();
        	if(!dao.checkUserName(username)) {
	            request.setAttribute("error", "Der Nutzer konnte nicht hinzugefügt werden dieser Nutzername exisiert bereits");
	            try {
					dispatcher.forward(request, response);
				} catch (ServletException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            return;

        	}
        	if(rolle.equals("Standortleiter")) {
        		if(request.getParameterValues("standorte")!=null) {
            	List<String> standorte =Arrays.asList(request.getParameterValues("standorte"));
            	
            	try {
					dao.addUserSL(username, password, standorte, rolle);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error("Nutzer: Name={} konnte nicht hinzugefügt werden",username);
				}
        		}
        		else {
            		try {
						dao.addUser(username, password, rolle);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						logger.error("Nutzer: Name={} konnte nicht hinzugefügt werden",username);
					}
            	}
        	}

        	else {
        		try {
					dao.addUser(username, password, rolle);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error("Nutzer: Name={} konnte nicht hinzugefügt werden",username);

				}
        	
        	}
			try {
				dispatcher.forward(request, response);
			} catch (ServletException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

        	
        }
        if(aktion.equals("edit")) {
        	
        	requestEditUser(request,response);
        	
        	
        }
        if(aktion.equals("userEdited")) {
        	editUser(request,response);
        }
        
       

       // dao.addUser(user);

}
	
	private void editUser(HttpServletRequest request, HttpServletResponse response) {
	    int userid = Integer.parseInt(request.getParameter("userid"));
	    User user = new User();
	    user.setId(userid);
	    String rolle = request.getParameter("rollen");
	    user.setRolle(rolle);

	    if (rolle.equals("Standortleiter")) {
	        String[] standortArray = request.getParameterValues("standorte");
	        ArrayList<String> standorte = new ArrayList<>();
	        if (standortArray != null) {
	            standorte = new ArrayList<>(List.of(standortArray));
	        }
	        user.transferStandorte(standorte);
	    }

	    try {
	        dao.changeUser(userid, user);
	        logger.info("Nutzer: Name={} wurde bearbeitet", user.getUsername());
	        try {
	            response.sendRedirect("/wai/admin/userManager");
	            return;
	        } catch (IOException e) {
	            logger.error("Fehler beim Redirect", e);
	        }
	    } catch (Exception e) {
	        logger.error("Nutzer konnte nicht bearbeitet werden", e);
	        try {
	            request.setAttribute("error", "Der Nutzer konnte nicht bearbeitet werden");
	            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/view/userManager.jsp");
	            dispatcher.forward(request, response);
	            return;
	        } catch (Exception e1) {
	            logger.error("Fehler beim Weiterleiten zur Fehlerseite", e1);
	        }
	    }
	}
	
	
	private void requestEditUser(HttpServletRequest request, HttpServletResponse response) {
	    int userid = Integer.parseInt(request.getParameter("userId"));
	    RequestDispatcher dispatcher;

	    try {
	        User user = dao.getUserById(userid);
	        ArrayList<String> standorte = dao.getStandorte();

	        request.setAttribute("userid", userid);
	        request.setAttribute("username", user.getUsername());
	        request.setAttribute("selectedStandorte", user.getStandorte());
	        request.setAttribute("standorte", standorte);
	        request.setAttribute("selectedRolle", user.getRolle());
	        List<String> rollen = List.of("Standortleiter", "Admin", "Chef");
	        request.setAttribute("rollen", rollen);
	        try {
				dispatcher = getServletContext().getRequestDispatcher("/view/userChange.jsp");
				dispatcher.forward(request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    } catch (SQLException e) {
			try {
				
				request.setAttribute("error", "Sie sind nicht im Intranet,\n sie können keine Nutzer bearbeiten");
				try {
					dispatcher=getServletContext().getRequestDispatcher("/view/userManager.jsp");
					dispatcher.forward(request, response);

				} catch (ServletException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }

	    
	}

	
}
