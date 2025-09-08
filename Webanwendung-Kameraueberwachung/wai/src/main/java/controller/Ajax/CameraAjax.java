package controller.Ajax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dao.DaoFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bild;
import model.Camera;
import model.User;
import utils.DateUtils;
import utils.QueryStrategy.BetweenStrategy;
import utils.QueryStrategy.EqualsStrategy;
import utils.QueryStrategy.InStrategy;
import utils.QueryStrategy.OrderByStrategy;
import utils.QueryStrategy.PaginateStrategy;
import utils.QueryStrategy.QueryBuilder;
import utils.QueryStrategy.likeStrategy;
@WebServlet("/getCameras")
public class CameraAjax extends HttpServlet{
    
    private static Logger logger = LoggerFactory.getLogger(CameraAjax.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name = req.getParameter("name");
        String selectedLocation = req.getParameter("location");
        String selectedStatus = req.getParameter("status");
        String page = req.getParameter("page");
        String limit = req.getParameter("limit");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        User user = (User) req.getSession().getAttribute("user");
        String referer =req.getHeader("Referer");
        boolean comingFromHomepage = referer != null ? referer.matches(".*\\/wai\\/homepage"): false;
        boolean includeTimeframe = false;
        QueryBuilder builder = new QueryBuilder();

        if(page == null){
            page = "1";
        }
        
        if(limit == null){
            limit = "3";
        }

        if(selectedLocation != null){
            builder.addStrategy(new EqualsStrategy<>("adresse", selectedLocation));
        }else{
            if(user != null && !user.getRolle().equals("admin")){
            builder.addStrategy(new InStrategy<>("adresse", user.getStandorte()));
        }
    }
        
        if(name != null){
            builder.addStrategy(new likeStrategy<>("name", name));
        }
        if(selectedStatus != null){
            builder.addStrategy(new EqualsStrategy<>("status", selectedStatus));
        }
        
        if(from != null && to != null){
            builder.addStrategy(new BetweenStrategy<>("timestamp", Timestamp.valueOf(DateUtils.dateBuilder(from)), Timestamp.valueOf(DateUtils.dateBuilder(to))));
            includeTimeframe = true;
        }

        
        builder.addStrategy(new OrderByStrategy("adresse", "desc"));
        builder.addStrategy(new PaginateStrategy(Integer.parseInt(limit), Integer.parseInt(page)));

        
        List<Camera> cameras;
        if(comingFromHomepage && includeTimeframe){
            cameras = DaoFactory.getInstance().getCameraDao("psql").getCamerasWithData(builder);
        }else{
            cameras = DaoFactory.getInstance().getCameraDao("psql").getCameras(builder);
        }

        Map<String, Object> responseData  = new HashMap<>();
        QueryBuilder countBuilder = builder; 
        countBuilder.popStrategy();
        countBuilder.popStrategy();
        int cameraCount = DaoFactory.getInstance()
            .getCameraDao("psql")
            .getCameraCount(countBuilder);
        
        if(cameraCount == 0){
            cameraCount = 1;
        }

        System.out.println("amount of Cameras: " + cameraCount);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        responseData.put("cameras", cameras);
        responseData.put("page", page);
        responseData.put("maxPages", (Math.ceil(((float)cameraCount)/Integer.parseInt(limit))));



        if(comingFromHomepage){

            QueryBuilder getLatestPictureBuilder = new QueryBuilder();
            
            if(selectedLocation != null){
                getLatestPictureBuilder.addStrategy(new EqualsStrategy<>("adresse", selectedLocation));
            }

            getLatestPictureBuilder.addStrategy(new OrderByStrategy("kid, timestamp", "desc"));
            
            List<Bild> pics = DaoFactory.getInstance().getPictureDao("psql").getLastestPictures(getLatestPictureBuilder);
            Map<Integer, String> ablageUrls = pics.stream()
            .collect(Collectors.toMap(
                p->p.kid(),
                p->p.ablageUrl()));

            JsonObject ImageData = new JsonObject();

            
                for(Camera cam: cameras){
                	try {
                        // File imageFile = new File(picture.thumbnailUrl());
                        String url = ablageUrls.get(cam.getKid());
                        
                        if(url == null){
                            url = "";
                        }

                        File imageFile = new File(url);

                        byte[] imageBytes;
                        if(!imageFile.exists()){
                            ServletContext context = getServletContext();
                            InputStream inputStream = context.getResourceAsStream("/assets/couldNotLoadImage.jpg");

                            imageBytes = inputStream.readAllBytes(); // Java 9+
                            inputStream.close();
                        }else{
                            imageBytes = Files.readAllBytes(imageFile.toPath());
                        }
                        

                        final String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                        ImageData.addProperty(String.valueOf(cam.getKid()), base64Image);
                   } catch (Exception e) {
                        logger.error("Fehler beim Laden der Bilder",e);
                }
            }
            
            responseData.put("Images", ImageData);
            System.out.println("TRUE");
        }
        resp.getWriter().write(new Gson().toJson(responseData));
    }
}
