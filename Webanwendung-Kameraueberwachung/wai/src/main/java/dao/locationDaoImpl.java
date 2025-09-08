package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import utils.JNDIFactory;
public class locationDaoImpl implements locationDao {
    
	final JNDIFactory jndi = JNDIFactory.getInstance();

    @Override
    public List<String> getStandorte() {
        List<String> standorte = new ArrayList<>();
        String query = "SELECT distinct adresse FROM Standort";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

	        try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()){
                    standorte.add(rs.getString("adresse"));
                }

	        }
        } catch (Exception e) {
	        e.printStackTrace();
	    }
        return standorte;
    }
}
