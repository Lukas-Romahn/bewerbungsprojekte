package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.DaoFactory;
import jakarta.servlet.http.HttpSession;
import model.User;


public class Validator {
    final Map<String, List<Permission>> rolePermissions;
    private static Validator instance;

    private class Permission {
        String endPoint;
        List<String> operationAccess;
        
        public Permission(String endpoint, List<String> access){
            this.endPoint = endpoint;
            this.operationAccess = access;
        }
        
        public String getEndPoint(){
            return endPoint;
        }
        
        public boolean checkIfAccessAllowed(String type){
            for(String element : operationAccess){
                if(element.equals(type)){
                    return true;
                }
            }
            return false;
        }
    }

    public static Validator getInstance(){
        if(instance == null){
            instance = new Validator();
        }
        return instance;
    }

    private Validator(){

        // Database?
        rolePermissions= new HashMap<>();

        List<Permission> AdminPermissions = Arrays.asList(
            new Permission("/admin/adminMainMenu", Arrays.asList("GET", "POST")),
            new Permission("/admin/cameraManager", Arrays.asList("GET", "POST", "PUT", "DELETE")),
            new Permission("/admin/locationManager", Arrays.asList("GET", "POST", "DELETE")),
            new Permission("/admin/fehlerLog", Arrays.asList("GET")),
            new Permission("/admin/log-viewer", Arrays.asList("GET")),
            new Permission("/admin/userManager", Arrays.asList("GET", "POST", "DELETE")),
            new Permission("/getCameras", Arrays.asList("GET")),
            new Permission("/search", Arrays.asList("GET"))
        );
            
        List<Permission> ChefPermissions = Arrays.asList(
            new Permission("/homepage", Arrays.asList("GET")),
            new Permission("/getCameras", Arrays.asList("GET")),
            new Permission("/getPictures", Arrays.asList("GET")),
            new Permission("/changePassword", Arrays.asList("GET")),
            new Permission("/gallery", Arrays.asList("GET")),
            new Permission("/search", Arrays.asList("GET"))

            
        );

        List<Permission> standortLeiterPermissions = Arrays.asList(
            new Permission("/homepage", Arrays.asList("GET")),
            new Permission("/getCameras", Arrays.asList("GET")),
            new Permission("/getPictures", Arrays.asList("GET")),
            new Permission("/changePassword", Arrays.asList("GET")),
            new Permission("/gallery", Arrays.asList("GET")),
            new Permission("/search", Arrays.asList("GET"))
        );

        rolePermissions.put("Admin", AdminPermissions);
        rolePermissions.put("Chef", ChefPermissions);
        rolePermissions.put("Standortleiter", standortLeiterPermissions);       
    }


    public boolean authenticate(HttpSession session){
        if(session == null || (User) session.getAttribute("user") == null){
            return false;
        }
        
        boolean userExists;
        
        try{

            userExists = DaoFactory.getInstance().getUserDao("psql").checkLoginData(((User)session.getAttribute("user")).getUsername(),((User)session.getAttribute("user")).getPassword());
        }catch(Exception e){
            return false;
        }
        return userExists;
    }
    
    public boolean authorize(User user, String Endpoint, String Method){
        
        List<Permission> permissions = rolePermissions.getOrDefault(user.getRolle(), null );
        
        if(permissions == null){
            return false;
        }
        for(Permission element : permissions){
            if(element.getEndPoint().equals(Endpoint)){
                return element.checkIfAccessAllowed(Method);
            }
        }
        return false;
     }
}
