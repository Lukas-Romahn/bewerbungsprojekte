package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.DaoFactory;
import dao.UserDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Servlet implementation class LocationManager
 */
@WebServlet("/admin/locationManager")
public class LocationManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDao dao;
	private DaoFactory daofactory;
  
	private static Logger logger = LoggerFactory.getLogger(LocationManager.class);
	public LocationManager() {
		daofactory=DaoFactory.getInstance();
		dao=daofactory.getUserDao("psql");
	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String error= "null";
		if(request.getAttribute("error")!=null) {
			error=request.getAttribute("error").toString();
		}
		ArrayList<String> standorte=new ArrayList<>();
		try {
			standorte = dao.getStandorte();
		} catch (SQLException e) {
			if(error.equals("null")) {
			error="Fehler beim laden der Seite";
			}
		}
		if(!error.equals("null")) {
			request.setAttribute("error",error);

		}
		request.setAttribute("standorte", standorte);
		RequestDispatcher dispatcher= request.getRequestDispatcher("/view/locationManager.jsp");
		dispatcher.forward(request,response);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String methodOverride = request.getParameter("_method");
	    if ("DELETE".equalsIgnoreCase(methodOverride)) {
	        doDelete(request, response);
	        return;
	    }
		
		if(request.getParameter("aktion").equals("locationedit")) {
			String name=request.getParameter("name").toString();
			String oldName=request.getParameter("oldName").toString();
			System.out.println("bearbeiten von "+ name);
			try {
				dao.updateStandort(oldName, name);
				logger.info("Standort " + oldName + " wurde zu " + name + "geändert");
			} catch (SQLException e) {
				request.setAttribute("error","Sie können keinen Standort bearbeiten, sie sind nicht im Intranet");
				logger.error("keine Verbinung mit der Datenbank", e);
			}
			
		}
		if(request.getParameter("aktion").equals("locationadd")) {
			String name=request.getParameter("name").toString();

			try {
				dao.addStandort(name);
				logger.info("Standort + " + name + " wurde hinzugefügt");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				request.setAttribute("error","Sie können keinen Standort hinzufügen, sie sind nicht im Intranet");
				logger.error("keine Verbinung mit der Datenbank", e);
			}
		}
	doGet(request,response);
	}
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
			String name=request.getParameter("name").toString();
			System.out.println("löschen von "+ name);
		    try {
		    	try {
					dao.deleteStandort(name);
					logger.info("Standort " + name + " wurde gelöscht");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					request.setAttribute("error","Sie können keinen Standort löschen, sie sind nicht im Intranet");
					logger.error("keine Verbinung mit der Datenbank", e);
				}
		    	try {
					doGet(request,response);
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	

}
