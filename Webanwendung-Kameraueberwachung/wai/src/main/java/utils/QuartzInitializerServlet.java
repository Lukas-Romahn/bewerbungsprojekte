package utils;

import java.io.IOException;

import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.SchedulerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzInitializerServlet extends HttpServlet{
	private static Logger logger = LoggerFactory.getLogger(QuartzInitializerServlet.class);
	private Scheduler scheduler = null;
	
	/*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public void init(ServletConfig cfg) throws ServletException {
        

        super.init(cfg);

        try {
            StdSchedulerFactory factory = new StdSchedulerFactory("quartz.properties");
            scheduler = factory.getScheduler();
            scheduler.start();
            logger.info("Quartz Scheduler gestartet: " + scheduler.getSchedulerName());
        } catch (Exception e) {
            logger.error("Fehler beim Initialisieren des Quartz Schedulers", e);
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (scheduler != null) scheduler.shutdown();
            logger.info("Quartz Scheduler wurde heruntergefahren.");
        } catch (Exception e) {
            logger.error("Fehler beim Shutdown", e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
