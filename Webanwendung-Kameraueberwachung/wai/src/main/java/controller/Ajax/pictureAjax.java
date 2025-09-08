package controller.Ajax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import dao.DaoFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bild;
import utils.QueryStrategy.EqualsStrategy;
import utils.QueryStrategy.GreaterEqualsStrategy;
import utils.QueryStrategy.LessStrategy;
import utils.QueryStrategy.OrderByStrategy;
import utils.QueryStrategy.PaginateStrategy;
import utils.QueryStrategy.QueryBuilder;
@WebServlet("/getPictures")
public class pictureAjax extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(pictureAjax.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Parameter aus der Anfrage holen
        String camera = req.getParameter("camera");
        String location = req.getParameter("location");
        String year = req.getParameter("year");
        String month = req.getParameter("month");
        String day = req.getParameter("day");
        String fromHour = req.getParameter("fromHour");
        String toHour = req.getParameter("toHour");

        String page = req.getParameter("page");
        String limit = req.getParameter("limit");
        // JSON-Antwort vorbereiten
        Map<String, Object> responseData = new HashMap<>();

        

        if(page == null){
            page = "1";
        }
        
        if(limit == null){
            limit = "20";
        }
        

        
        // Jahre laden (unabhängig von Filtern)
        QueryBuilder yearBuilder = new QueryBuilder();
        if(camera != null){
            yearBuilder.addStrategy(new EqualsStrategy<Object>("kid", Integer.valueOf(camera)));
        }
        try{
            List<String> years = DaoFactory.getInstance()
                    .getPictureDao("psql")
                    .getTimestamps(yearBuilder, "YYYY");
            responseData.put("years", years);
        }catch(Exception e){
            logger.error("Keine Verbindung zur Datenbank");
        }
        // Monate für gewähltes Jahr laden
        QueryBuilder monthBuilder = new QueryBuilder();
        if(camera != null){
            monthBuilder.addStrategy(new EqualsStrategy<Object>("kid", Integer.valueOf(camera)));
        }
        if (year != null && !year.isEmpty()) {
            monthBuilder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'YYYY')", year));
        }
        try{
            List<String> months = DaoFactory.getInstance()
                    .getPictureDao("psql")
                    .getTimestamps(monthBuilder, "MM");
            responseData.put("months", months);
        }catch(Exception e){
            logger.error("Keine Verbindung zur Datenbank");            
        }
        // Tage für gewähltes Jahr und Monat laden
        QueryBuilder dayBuilder = new QueryBuilder();
        if(camera != null){
            dayBuilder.addStrategy(new EqualsStrategy<Object>("kid", Integer.valueOf(camera)));
        }
        if (year != null && !year.isEmpty()) {
            dayBuilder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'YYYY')", year));
        }
        if (month != null && !month.isEmpty()) {
            dayBuilder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'MM')", month));
        }
        try{
            List<String> days = DaoFactory.getInstance()
                    .getPictureDao("psql")
                    .getTimestamps(dayBuilder, "DD");
            responseData.put("days", days);
        }catch(Exception e){
            logger.error("Keine Verbindung zur Datenbank");            
        }
        
        // Stunden für gewähltes Jahr, Monat und Tag laden
        QueryBuilder hourBuilder = new QueryBuilder();
        if(camera != null){
            hourBuilder.addStrategy(new EqualsStrategy<>("kid", Integer.valueOf(camera)));
        }
        if (year != null && !year.isEmpty()) {
            hourBuilder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'YYYY')", year));
        }
        if (month != null && !month.isEmpty()) {
            hourBuilder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'MM')", month));
        }
        if (day != null && !day.isEmpty()) {
            hourBuilder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'DD')", day));
        }
        
        try{

        
        List<String> hours = DaoFactory.getInstance()
                .getPictureDao("psql")
                .getTimestamps(hourBuilder, "HH24");
        responseData.put("hours", hours);
        
        }catch(Exception e){
            logger.error("Keine Verbindung zur Datenbank");            
        }


        // QueryBuilder für Bildersuche vorbereiten
        QueryBuilder builder = new QueryBuilder();
        if(camera != null){
            builder.addStrategy(new EqualsStrategy<>("kid", Integer.valueOf(camera)));
        }
        if (location != null && !location.isEmpty()) {
            builder.addStrategy(new EqualsStrategy<>("adresse", location));
        }
        if (year != null && !year.isEmpty()) {
            builder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'YYYY')", year));
        }
        if (month != null && !month.isEmpty()) {
            builder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'MM')", month));
        }
        if (day != null && !day.isEmpty()) {
            builder.addStrategy(new EqualsStrategy<>("TO_CHAR(timestamp, 'DD')", day));
        }
        
        if(fromHour != null && !fromHour.isEmpty()) {
            System.out.println(Integer.valueOf(fromHour));
        	builder.addStrategy(new GreaterEqualsStrategy<>("EXTRACT(HOUR FROM timestamp)", Integer.valueOf(fromHour)));
        }
        
        if (toHour != null && !toHour.isEmpty()) {
            builder.addStrategy(new LessStrategy<>("EXTRACT(HOUR FROM timestamp)", Integer.valueOf(toHour)));
        }
        
        builder.addStrategy(new OrderByStrategy("timestamp", "desc"));
        builder.addStrategy(new PaginateStrategy(Integer.parseInt(limit), Integer.parseInt(page)));
        // Bilder aus der Datenbank laden
        
        List<Bild> bilder = new ArrayList<>();
        try{

            bilder = DaoFactory.getInstance()
                    .getPictureDao("psql")
                    .getBilder(builder);

        }catch(Exception e){
            logger.error("Keine Verbindung zur Datenbank");            
        }
        // Bilder Base64 konvertieren
        List<Map<String, String>> images = new ArrayList<>();
        for (Bild bild : bilder) {
            Map<String, String> imageData = new HashMap<>();
            byte[] thumbBytes;
            byte[] fullBytes;

            // Thumbnail laden und Base64 encoden
            File thumbnailFile = new File(bild.thumbnailUrl());
            if(!thumbnailFile.exists()){
                ServletContext context = getServletContext();
                InputStream inputStream = context.getResourceAsStream("/assets/couldNotLoadImage.jpg");

                thumbBytes =  inputStream.readAllBytes(); // Java 9+
                inputStream.close();
            }else{
                thumbBytes = Files.readAllBytes(thumbnailFile.toPath());
            }
            
            String base64Thumb = Base64.getEncoder().encodeToString(thumbBytes);

            // Vollbild laden und Base64 encoden
            File fullFile = new File(bild.ablageUrl());
            if(!fullFile.exists()){
                ServletContext context = getServletContext();
                InputStream inputStream = context.getResourceAsStream("/assets/couldNotLoadImage.jpg");

                fullBytes =  inputStream.readAllBytes(); // Java 9+
                inputStream.close();
            }else{
                fullBytes = Files.readAllBytes(fullFile.toPath());
            }
            
            String base64Full = Base64.getEncoder().encodeToString(fullBytes);

            imageData.put("thumbnail", base64Thumb);
            imageData.put("full", base64Full);
            
            //Timestamp
            imageData.put("timestamp", bild.timestamp().toString());

            images.add(imageData);
        }

        QueryBuilder countBuilder = builder;
        countBuilder.popStrategy();
        countBuilder.popStrategy();
        int pictureCount = DaoFactory.getInstance()
            .getPictureDao("psql")
            .getPictureCount(countBuilder);
        
        if(pictureCount == 0){
            pictureCount = 1;
        }

        responseData.put("images", images);
        responseData.put("page", page);
        responseData.put("maxPages", (Math.ceil((((float)pictureCount))/Integer.parseInt(limit))));

        // Antwort zurück an den Client
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(new Gson().toJson(responseData));
    }
}