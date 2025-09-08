package controller.Ajax;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import dao.DaoFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Camera;
import model.User;
import utils.DateUtils;
import utils.QueryStrategy.BetweenStrategy;
import utils.QueryStrategy.EqualsStrategy;
import utils.QueryStrategy.QueryBuilder;


@WebServlet("/search")
public class SearchAjax  extends HttpServlet{
    
    // TODO GIVE DEVICES WITH DATA IN TIMEFRAME
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<String> standorte=new ArrayList<>();
		
		
        Map<String, Object> responseData = new HashMap<>();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<String> Locations = new ArrayList<>();

        User user =((User) req.getSession().getAttribute("user"));
        String selectedLocation = req.getParameter("location");
        String selectedStatus = req.getParameter("status");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String format = req.getParameter("format");
               
        // DEV
        if(user != null){
        	if(!user.getRolle().equals("Standortleiter")) {
        		try {
        			standorte = DaoFactory.getInstance().getUserDao("psql").getStandorte();
        		} catch (SQLException e) {
        			e.printStackTrace();
        		}
        		Locations=standorte;
        	}else {
        		Locations=user.getStandorte();
        	}
            if(Locations.size() == 1){
            	
                selectedLocation = Locations.get(0);
            }
            
        }else{
            Locations = Arrays.asList("Mannheim", "Heidelberg");
        }
        

        if(selectedLocation == null){
            responseData.put("locations", Locations);
            responseData.put("timestamps", List.of());
            responseData.put("devices", List.of());
            resp.getWriter().write(new Gson().toJson(responseData));
            return;
        }
        
        if(format == null){
            format = "yyyy-mm-dd\"T\"HH:mm:ss";
        }

        QueryBuilder builder = new QueryBuilder();
        builder.addStrategy(new EqualsStrategy<>("adresse", selectedLocation));

        if(from != null && to != null){
            builder.addStrategy(new BetweenStrategy<>("timestamp", Timestamp.valueOf(DateUtils.dateBuilder(from)), Timestamp.valueOf(DateUtils.dateBuilder(to)) ));
        }
        if(selectedStatus != null){
            builder.addStrategy(new EqualsStrategy<>("status", selectedStatus));
        }

        List<String> timestamps = new ArrayList<>();
		try {
			timestamps = DaoFactory.getInstance().getPictureDao("psql").getTimestamps(builder, format);
		} catch (SQLException e) {
			responseData.put("error", "Sie sind nicht im Intranet");
		}
        List<Camera> cameras = DaoFactory.getInstance().getCameraDao("psql").getCameras(builder);
        
        responseData.put("locations", Locations);
        responseData.put("timestamps", timestamps);

        responseData.put("devices", cameras);
        resp.getWriter().write(new Gson().toJson(responseData));
    }  
    
}
