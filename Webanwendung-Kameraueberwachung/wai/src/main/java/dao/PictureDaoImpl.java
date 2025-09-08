package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Bild;
import utils.JNDIFactory;
import utils.QueryStrategy.QueryBuilder;

public class PictureDaoImpl implements PictureDao {


	final JNDIFactory jndi = JNDIFactory.getInstance();

    @Override
    public List<String> getLocationMetaData(QueryBuilder builder) {
        List<String> list = new ArrayList<>();

        String query = "SELECT distinct adresse FROM Bilder natural join Kamera natural join Standort " + builder.getClause();

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {
            
            List<Object> params = builder.getParams();
            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    list.add(rs.getString("adresse"));
                }

                return list;
            }
        } catch (Exception e) {
	        e.printStackTrace();
	    }
        return null;
    }

    @Override
    public List<Bild> getBilder(QueryBuilder builder) {
        List<Bild> list = new ArrayList<>();

        String query = "SELECT * FROM Bilder natural join Kamera natural join Standort " + builder.getClause();

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {
            
            List<Object> params = builder.getParams();
            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    list.add(new Bild(rs.getInt("bid"), rs.getInt("kid"),rs.getString("name"),rs.getString("adresse"), rs.getString("thumbnailurl"),rs.getString("ablageurl"), rs.getTimestamp("timestamp")));
                }

                return list;
            }
        } catch (Exception e) {
	        e.printStackTrace();
	    }
        return list;
    }

    @Override
    public List<String> getTimestamps(QueryBuilder builder, String format) throws SQLException {
        List<String> list = new ArrayList<>();

        String query = "SELECT Distinct TO_CHAR(timestamp, ?) as time  FROM Bilder natural join Kamera natural join Standort " + builder.getClause();

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {
            
            statement.setString(1, format);
            
            List<Object> params = builder.getParams();
            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+2, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    list.add(rs.getString("time"));
                }

                return list;
            }
        } catch (Exception e) {
	        throw new SQLException();
	    }
    } 

    @Override
    public List<Bild> getLastestPictures(QueryBuilder builder){
        List<Bild> list = new ArrayList<>();
        
        String query = "SELECT DISTINCT ON (kid) * FROM bilder natural join kamera natural join Standort " + builder.getClause(); 
 
	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	        PreparedStatement statement = con.prepareStatement(query)) {
            
            List<Object> params = builder.getParams();
            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    list.add(new Bild(rs.getInt("bid"), rs.getInt("kid"),rs.getString("name"),rs.getString("adresse"), rs.getString("thumbnailurl"),rs.getString("ablageurl"), rs.getTimestamp("timestamp")));
                }

                return list;
            }
        } catch (Exception e) {
	        e.printStackTrace();
	    }
        return null;
    }

	@Override
	public void insertBild(int kid, String thumbnailUrl, String ablageUrl, Timestamp timestamp) {
		String query = "INSERT INTO bilder (kid, thumbnailurl, ablageurl, timestamp) VALUES (?, ?, ?, ?)";
		
		try (Connection con = jndi.getConnection("jdbc/waiDB");
				PreparedStatement statement = con.prepareStatement(query)) {
				
				statement.setInt(1, kid);
				statement.setString(2, thumbnailUrl);
				statement.setString(3, ablageUrl);
				statement.setTimestamp(4, timestamp);
				statement.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
			}
	}

    @Override
    public int getPictureCount(QueryBuilder builder){
        String query = "SELECT count(distinct Bilder.bid) FROM Bilder natural join Kamera natural join Standort" + builder.getClause() ;

  

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
}
