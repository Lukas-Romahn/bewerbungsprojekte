package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JNDIFactory;

@WebServlet("/admin/fehlerLog")
public class LogFileList  extends HttpServlet{
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String logPath = null;
		try {
			logPath = JNDIFactory.getInstance().getEnvironmentAsString("logPath");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File logDir = new File(logPath);
        List<String> logFiles = new ArrayList<>();

        if (logDir.exists() && logDir.isDirectory()) {
            for (File file : logDir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".log")) {
                    logFiles.add(file.getName());
                }
            }
        }

        request.setAttribute("logFiles", logFiles);
        request.getRequestDispatcher("/view/Fehlerlog.jsp").forward(request, response);
    }
}
