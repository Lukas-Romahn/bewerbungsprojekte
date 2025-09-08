package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.google.gson.Gson;

import dao.DaoFactory;
import dao.UserDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Camera;
import utils.JNDIFactory;
import utils.QueryStrategy.EqualsStrategy;
import utils.QueryStrategy.InStrategy;
import utils.QueryStrategy.QueryBuilder;
import utils.QueryStrategy.SubQueryStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/cameraManager")
public class cameraManager extends HttpServlet {
	
	// ist in UserDao weil dort eine methode existiert die alles existierende standorte ausgibt
    private UserDao dao;
    private static Logger logger = LoggerFactory.getLogger(cameraManager.class);

	
	public cameraManager() {
		super();
		DaoFactory daofac =DaoFactory.getInstance();
		dao= daofac.getUserDao("psql");
	}
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
 		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/view/cameraManager.jsp");
 		resp.setContentType("text/html; charset=UTF-8");
 		resp.setCharacterEncoding("UTF-8");
		dispatcher.forward(req, resp);
    }
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String aktion=req.getParameter("aktion");
        resp.setContentType("text/html; charset=UTF-8");
 		resp.setCharacterEncoding("UTF-8");
 		if(aktion.equals("cancel")) {
 			
 			doGet(req,resp);
 			
 		}
    	if(aktion.equals("edit")) {
    		Gson gson = new Gson();
     	    String cameraJson = req.getParameter("camera");
     	   	Camera cam=gson.fromJson(cameraJson,Camera.class);

    		RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/view/cameraEdit.jsp");
    		List<String>stati=List.of("aktiv","passiv","wartung");
    		ArrayList<String> standorte=new ArrayList<>();
			try {
				standorte = dao.getStandorte();
			} catch (SQLException e) {
				logger.error("Es wurden keine Standorte gefunden", e);
			}
    		req.setAttribute("Standorte", standorte);
    		req.setAttribute("Stati",stati);
    		req.setAttribute("kid", cam.getKid());
    		req.setAttribute("name", cam.getName());
    		req.setAttribute("status", cam.getStatus());
    		req.setAttribute("location", cam.getLocation());
    		req.setAttribute("domain", cam.getDomain());
     	   	
     	   	dispatcher.forward(req, resp);
     	   	
    	}
    	
    	Map<String, Object> responseData = new HashMap<>();
        
        if(aktion.equals("addCamera")) {


        String test = req.getParameter("cameras");
        String domain = req.getParameter("domain");
        String name = req.getParameter("name");
        String location = req.getParameter("location");
        

        logger.info("Neue Kamera hinzufügen: Name={}, Domain={}, Location={}, Cameras={}", name, domain, location, test);
        
        int response = DaoFactory.getInstance().getCameraDao("psql").insertDevice(name, location, domain);

        if(response == 1){
        	logger.info("Kamera erfolgreich hinzufügen: {}", name);
            responseData.put("response", "success");
            RequestDispatcher rd=req.getRequestDispatcher("/view/cameraManager.jsp");
            rd.forward(req, resp);
        }else{
        	logger.error("Fehler beim hinzufügen der Kamera: {}", name);
            responseData.put("response", "Es ist etwas schieß gelaufen. Bitte versuche es erneut.");
            //fehler ausgabe einbauen
        }
        
        
        
      
        
        }
    	if(aktion.equals("cameraAdd")) {
           	RequestDispatcher rd=req.getRequestDispatcher("/view/cameraAdd.jsp");
           	try {
				req.setAttribute("standorte", dao.getStandorte());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				req.setAttribute("error", "sql");
			}
           	rd.forward(req, resp);
        }
    	if(aktion.equals("edited")) {
    		doPut(req,resp);
    	}
    	
    	}
    
    

  
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        

    	String jsonString = req.getParameter("camera"); // <- holt den String aus dem hidden input
    	Gson gson = new Gson();
    	Camera cam = gson.fromJson(jsonString, Camera.class);
    	    

            Map<String, String> camDetails = new HashMap<>();
            camDetails.put("name", cam.getName());
            camDetails.put("status", cam.getStatus());
            camDetails.put("url", cam.getDomain());
            camDetails.put("date", cam.getCreatedTimestamp());
            
            camDetails.entrySet().removeIf(entry -> entry.getValue() == null);

            QueryBuilder setStrat = new QueryBuilder("SET");
            QueryBuilder whereStrat = new QueryBuilder("WHERE");
            for(Map.Entry<String, String> set: camDetails.entrySet()){
                setStrat.addStrategy(new EqualsStrategy<Object>(set.getKey(), set.getValue()));
            }
            
            //location ist immer die extra wurst :(
            if(cam.getLocation() != null){

                QueryBuilder subQueryBuilder = new QueryBuilder("WHERE");
                subQueryBuilder.addStrategy(new EqualsStrategy<Object>("adresse", cam.getLocation()));

                setStrat.addStrategy(new SubQueryStrategy("standortID = (SELECT standortid FROM Standort", subQueryBuilder )); // Klammer wird automatisch geschlossen falls eine gebraucht wird
            }

            whereStrat.addStrategy(new EqualsStrategy<Object>("kid", cam.getKid()));
            int affected = DaoFactory.getInstance().getCameraDao("psql").updateDevice(setStrat, whereStrat);
            logger.info("Anzahl aktualisierter Kameradatensätze: {}", affected);
        
    		RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/view/cameraManager.jsp");
    		dispatcher.forward(req, resp);
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] cameraIdsParam = req.getParameterValues("cameras");
        List<Integer> ids = new ArrayList<>();
        boolean error = false;

        if (cameraIdsParam != null) {
            for (String idStr : cameraIdsParam) {
                try {
                    ids.add(Integer.parseInt(idStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        int affectedRows = 0;

        try {
            QueryBuilder builder = new QueryBuilder();
            builder.addStrategy(new InStrategy<>("kid", ids));
            affectedRows = DaoFactory.getInstance().getCameraDao("psql").deleteDevices(builder);
            logger.info("Anzahl gelöschter Kameras in DB: {}", affectedRows);
        } catch (SQLException e) {
            error = true;
            logger.error("SQL-Fehler beim löschen der Kameras", e);
        }
        
        if(!error)
        try {
			String imagePath = JNDIFactory.getInstance().getEnvironmentAsString("imageSavePath");
			String thumbnailPath = JNDIFactory.getInstance().getEnvironmentAsString("thumbnailSavePath");
			
			for(Integer id : ids) {
				File imageDir = new File(imagePath, String.valueOf(id));
				File thumbnailDir = new File(thumbnailPath, String.valueOf(id));
				
				deleteDirectory(imageDir);
				deleteDirectory(thumbnailDir);
				
				logger.info("Dateien und Thumbnails für Kamera-ID={} gelöscht", id);
			}
		} catch (NamingException e) {
			error = true;
			logger.error("NamingException beim Löschen der Verzeichnisse", e);
		}
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        if (error) {
            out.print("{\"response\": \"error\", \"message\": \"sql or filesystem\"}");
        } else {
            out.print("{\"response\": \"success\", \"deleted\": " + affectedRows + "}");
        }
        out.flush();
    }
    
    //Hilfsmethode zum rekursiven löschen eines Verzeichnisses
    private void deleteDirectory(File dir) throws IOException{
    	if(dir.exists()) {
    		Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>(){
    			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
    				Files.delete(file); //Datei löschen
    				return FileVisitResult.CONTINUE;
    			}
    			
    			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException{
    				Files.delete(dir); //Verzeichnis löschen, wenn leer
    				return FileVisitResult.CONTINUE;
    			}
    		});
    	}
    }

}