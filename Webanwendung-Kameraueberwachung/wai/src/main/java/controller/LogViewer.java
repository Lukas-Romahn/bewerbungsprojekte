package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JNDIFactory;

import java.io.*;

import javax.naming.NamingException;

@WebServlet("/admin/log-viewer")
public class LogViewer extends HttpServlet{
	
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 	String logPath = null;
	        String fileName = request.getParameter("file");
			try {
				logPath = JNDIFactory.getInstance().getEnvironmentAsString("logPath");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        File logFile = new File(logPath, fileName);

	        response.setContentType("text/plain");

	        if (logFile.exists()) {
	            try (BufferedReader reader = new BufferedReader(new FileReader(logFile));
	                 PrintWriter out = response.getWriter()) {
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    out.println(line);
	                }
	            }
	        } else {
	            response.getWriter().println("Datei nicht gefunden.");
	        }
	    }
}
