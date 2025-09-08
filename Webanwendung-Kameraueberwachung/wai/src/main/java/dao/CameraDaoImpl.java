package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Camera;
import utils.JNDIFactory;
import utils.QueryStrategy.QueryBuilder;

public class CameraDaoImpl implements cameraDao {
    
	final JNDIFactory jndi = JNDIFactory.getInstance();

    @Override
	public List<Camera> getCameras(QueryBuilder builder){
        List<Camera> cameras = new ArrayList<>();
	    String query = "SELECT * FROM Kamera natural join Standort" + builder.getClause();


	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

            List<Object> params = builder.getParams();

            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

	        try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    Camera tempCam = new Camera();
                    tempCam.setKid(rs.getInt("kid"));
                    tempCam.setLocation(rs.getString("Adresse"));
                    tempCam.setName(rs.getString("name"));
                    tempCam.setStatus(rs.getString("status"));
                    tempCam.setDomain(rs.getString("url"));
                    cameras.add(tempCam);
                }
                return cameras;
	        }

	    } catch (Exception e) {
            return cameras;
	    }
	}
    
    @Override
	public int getCameraCount(QueryBuilder builder) {
	    String query = "SELECT count(distinct Kamera.kid) FROM Kamera natural join Standort natural left join Bilder" + builder.getClause() ;

  

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

            List<Object> params = builder.getParams();

            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

	        try (ResultSet rs = statement.executeQuery()) {
                if(rs.next()){
                    return rs.getInt("count");
                }
	        }

	    } catch (Exception e) {
            e.printStackTrace();
	    }

            return 0;
        
	}
    
    @Override
    public int insertDevice(String name, String location, String domain) {
         String query = "INSERT  INTO Kamera (name, status, url, date, standortid) values(?, ?, ? , NOW(), (SELECT standortid from Standort where adresse = ?))";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setString(2, "aktiv");
            statement.setString(3, domain);
            statement.setString(4, location);
	        statement.executeUpdate();
            return 1;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
        return 0;
    }
    
    
    @Override
	public List<String> getStandorte(){
        List<String> standorte = new ArrayList<String>();
        String query = "SELECT distinct adresse FROM Kamera natural join Standort";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

	        try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    standorte.add(rs.getString("adresse"));
                }

                return standorte;
	        }
        } catch (Exception e) {
	        e.printStackTrace();
	    }
        return null;
    }

    @Override
    public int deleteDevices(QueryBuilder builder) throws SQLException {
	    String query = "DELETE FROM Kamera " + builder.getClause();


	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

            List<Object> params = builder.getParams();

            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

	        int rs = statement.executeUpdate();
            return rs;

	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new SQLException();
	    }
    }

    @Override
    public int updateDevice(QueryBuilder setStrat, QueryBuilder whereStrat) {

	    String query = "UPDATE Kamera " + setStrat.getClause() + whereStrat.getClause();


	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

            List<Object> params = setStrat.getParams();
            params.addAll(whereStrat.getParams());

            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

	        int rs = statement.executeUpdate();
            return rs;

	    } catch (Exception e) {
	        e.printStackTrace();
            return -1;
	    }
    }

    @Override
    public List<Camera> getCamerasWithData(QueryBuilder builder) {
        List<Camera> cameras = new ArrayList<>();
	    String query = "SELECT distinct Kamera.*, Standort.* FROM Kamera natural join Standort natural join Bilder" + builder.getClause();


	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

            List<Object> params = builder.getParams();

            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

	        try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    Camera tempCam = new Camera();
                    tempCam.setKid(rs.getInt("kid"));
                    tempCam.setLocation(rs.getString("Adresse"));
                    tempCam.setName(rs.getString("name"));
                    tempCam.setStatus(rs.getString("status"));
                    tempCam.setDomain(rs.getString("url"));
                    cameras.add(tempCam);
                }
                return cameras;
	        }

	    } catch (Exception e) {
            return cameras;
	    }
    }
}
