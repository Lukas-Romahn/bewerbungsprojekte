package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.User;
import utils.JNDIFactory;
import utils.QueryStrategy.QueryBuilder;

public class UserDaoImpl implements UserDao{
	
	final JNDIFactory jndi = JNDIFactory.getInstance();

	@Override
	public User getRoles(int id) {
	    User user = new User();

	    try (Connection con = jndi.getConnection("jdbc/waiDB")) {

	        // 1. Rolle holen
	        String query = "SELECT Rolle FROM Nutzer AS n LEFT JOIN Rollen AS r ON n.rollenid = r.Rollenid WHERE userid = ?";
	        try (PreparedStatement statement = con.prepareStatement(query)) {
	            statement.setInt(1, id);
	            try (ResultSet rs = statement.executeQuery()) {
	                if (rs.next()) {
	                    String rolle = rs.getString("Rolle"); // ❗ column name war falsch: "userid" → "Rolle"
	                    if (rolle != null) {
	                        user.setRolle(rolle);
	                    }
	                }
	            }
	        }

	        // 2. Standorte holen
	        query = "SELECT Adresse FROM Standortverteilung AS sv NATURAL JOIN Standort AS s WHERE userid = ?";
	        try (PreparedStatement stmt = con.prepareStatement(query)) {
	            stmt.setInt(1, id);
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    String adresse = rs.getString("Adresse");
	                    user.addStandorte(adresse);
	                }
	            }
	        }

	        return user;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	@Override
	public boolean checkLoginData(String username, String password) throws SQLException {
	    String query = "SELECT userid FROM Nutzer WHERE username = ? AND pwdhash = MD5(?)";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

	        statement.setString(1, username);
	        statement.setString(2, password);

	        try (ResultSet rs = statement.executeQuery()) {
	            if (rs.next()) {
	                // Optional: int id = rs.getInt("userid");
	                return true;
	            } else {
	                return false;
	            }
	        }

	    } catch (Exception e) {
	    	throw new SQLException();
	    }
	}


	@Override
	public int getId(String username) {
	    String query = "SELECT userid FROM Nutzer WHERE username = ?";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query)) {

	        statement.setString(1, username);

	        try (ResultSet rs = statement.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("userid");
	            } else {
	                return 0; // Benutzer nicht gefunden
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0; // Fehlerfall
	    }
	}

	@Override
	public User getUserById(int Id){
	    String query = "SELECT * FROM Nutzer as n LEFT JOIN rollen AS r ON n.rollenid = r.rollenid WHERE userId = ?";
		String queryStandOrte = "select adresse from Standort natural join standortverteilung where userid = ?";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query);
	         PreparedStatement standortStatment = con.prepareStatement(queryStandOrte)) {

	        statement.setInt(1, Id);

			User user = new User();
	        try (ResultSet rs = statement.executeQuery()) {
	            if (rs.next()) {
	                user.setId(rs.getInt("userId"));
					user.setUsername(rs.getString("username"));
					user.setRolle(rs.getString("rolle"));
	            } else {
	                return null ; // Benutzer nicht gefunden
	            }
	        }
			
			standortStatment.setInt(1, Id);
	        try (ResultSet rs = standortStatment.executeQuery()) {
	            while(rs.next()) {
					user.addStandorte(rs.getString("adresse"));
	            } 
	                return user; // Benutzer nicht gefunden
	            
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
	}


	@Override
	public ArrayList<User> getAllUser(QueryBuilder builder) throws SQLException {
	    String query = "SELECT " + 
				"n.userid, n.username, r.rolle, s.adresse " + 
				"FROM Nutzer AS n LEFT JOIN rollen AS r " +
				"ON n.rollenid = r.rollenid " +
				"LEFT JOIN standortverteilung AS sv ON n.userid = sv.userid " +
				"LEFT JOIN standort AS s ON sv.standortid = s.standortid " + builder.getClause();

	    ArrayList<User> userlist = new ArrayList<>();
	    Map<Integer, User> userMap = new LinkedHashMap<>();

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement statement = con.prepareStatement(query);) {

            List<Object> params = builder.getParams();
            for(int i = 0; i < params.size(); i++){
                statement.setObject(i+1, params.get(i));
            }

			ResultSet rs = statement.executeQuery();
	        while (rs.next()) {
	            int userId = rs.getInt("userid");

	            User user = userMap.get(userId);
	            if (user == null) {
	                user = new User();
	                user.setId(userId);
	                user.setUsername(rs.getString("username"));
	                user.setRolle(rs.getString("rolle"));
	                userMap.put(userId, user);
	            }

	            // Standort hinzufügen (immer)
	            String adresse = rs.getString("adresse");
	            if (adresse != null) {
	                user.addStandorte(adresse);
	            }
	        }

	        userlist.addAll(userMap.values());
	        return userlist;

	    } catch (Exception e) {
	    	throw new SQLException();
	    }
	}


	@Override
	public void deleteUserbyId(int id) throws SQLException {
		    String selectRollenId = "SELECT rollenid FROM nutzer WHERE userid = ?";
		    String deleteStandortVerteilung = "DELETE FROM standortverteilung WHERE userid = ?";
		    String deleteNutzer = "DELETE FROM nutzer WHERE userid = ?";
		    String deleteRolle = "DELETE FROM rollen WHERE rollenid = ?";

		    try (Connection con = jndi.getConnection("jdbc/waiDB")) {
		        con.setAutoCommit(false);  // Transaktion starten

		        try (
		            PreparedStatement psSelectRollenId = con.prepareStatement(selectRollenId);
		            PreparedStatement psStandort = con.prepareStatement(deleteStandortVerteilung);
		            PreparedStatement psDeleteNutzer = con.prepareStatement(deleteNutzer);
		            PreparedStatement psDeleteRolle = con.prepareStatement(deleteRolle)
		        ) {
		            // Schritt 1: rollenid holen
		            int rollenId = -1;
		            psSelectRollenId.setInt(1, id);
		            try (ResultSet rs = psSelectRollenId.executeQuery()) {
		                if (rs.next()) {
		                    rollenId = rs.getInt("rollenid");
		                } else {
		                    System.out.println("Kein Nutzer mit ID " + id + " gefunden.");
		                    con.rollback();
		                    return;
		                }
		            }

		            psStandort.setInt(1, id);
		            psStandort.executeUpdate();

		            psDeleteRolle.setInt(1, rollenId);
		            psDeleteRolle.executeUpdate();
		            
		            psDeleteNutzer.setInt(1, id);
		            psDeleteNutzer.executeUpdate();

		     
		            con.commit();
		            System.out.println("Nutzer und zugehörige Rolle erfolgreich gelöscht.");

		        } catch (Exception e) {
		            con.rollback();
		            throw new SQLException();
		        }
		    } catch (Exception e) {
	            throw new SQLException();

		    }
		}

	



	@Override
	public void addStandortToUser(int id, String adresse) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addUserSL(String username, String password, List<String> standorte, String rolle) throws SQLException {
		
		String rollenQuery = "insert into rollen(Rolle) values(?) returning rollenid";
		String nutzerQuery = "insert into nutzer(username,pwdhash,rollenid) values(?,md5(?),?) returning userid";
		String standortQuery = "insert into standortverteilung values(?,?)";
		int userid;
		int rollenid;
		List<Integer> standortid=new ArrayList<>();

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement rollenstmt = con.prepareStatement(rollenQuery)) {

	        rollenstmt.setString(1, rolle);

	        try (ResultSet rs = rollenstmt.executeQuery()) {
	            rs.next();
	            rollenid= rs.getInt("rollenid");
	            
	        }
	        try(PreparedStatement nutzerstmt = con.prepareStatement(nutzerQuery)){
	        	
	       	    nutzerstmt.setString(1, username);
	       	    nutzerstmt.setString(2, password);
	       	    nutzerstmt.setInt(3, rollenid);

	 	        try (ResultSet rs = nutzerstmt.executeQuery()) {
	 	            rs.next();
	 	            userid= rs.getInt("userid");}
	        }
	        
	        int adressCount=standorte.size();
	         
	        	//hier kriegen wir die Standortid die wir dann bei der standortstmt brauchen um die user auf die standorte zu mappen
	        
	        String adresseQuery=addAdressQuery(standorte);
	        
	        	try(PreparedStatement adressestmt = con.prepareStatement(adresseQuery)){
	        		for(int i=1;i<=adressCount;i++) {
	        		adressestmt.setString(i, standorte.get(i-1));
	        	
	        		}

	        		try (ResultSet rs = adressestmt.executeQuery()) {
	        			while(rs.next()) {
		        			standortid.add(rs.getInt("standortid"));
		        			}

	        			}
	        	}
	        
	        try(PreparedStatement standortstmt = con.prepareStatement(standortQuery)){
	        	
	       	    for(int i=0;i<standortid.size();i++) {
		       	    standortstmt.setInt(1, userid);
		       	    standortstmt.setInt(2, standortid.get(i));
		       	    standortstmt.addBatch();

	       	    }

	 	        standortstmt.executeBatch();
	 	        
	        }

	    } catch (Exception e) {
	    	throw new SQLException();

	    }
		
		
	}


	@Override
	public void addUser(String username, String password, String rolle) throws SQLException {
		// TODO Auto-generated method stub
		String rollenQuery = "insert into rollen(Rolle) values(?) returning rollenid";
		String nutzerQuery = "insert into nutzer(username,pwdhash,rollenid) values(?,md5(?),?)";
		int rollenid;
		int standortid;

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement rollenstmt = con.prepareStatement(rollenQuery)) {

	        rollenstmt.setString(1, rolle);

	        try (ResultSet rs = rollenstmt.executeQuery()) {
	            rs.next();
	            rollenid= rs.getInt("rollenid");
	            
	        }
	        try(PreparedStatement nutzerstmt = con.prepareStatement(nutzerQuery)){
	        	
	       	    nutzerstmt.setString(1, username);
	       	    nutzerstmt.setString(2, password);
	       	    nutzerstmt.setInt(3, rollenid);

	 	        nutzerstmt.executeUpdate();
	 	           
	        
	        }
	    

	    } catch (Exception e) {
	    	throw new SQLException();
	    }
		
	}


	@Override
	public ArrayList<String> getStandorte() throws SQLException {
	    String query = "SELECT adresse FROM standort";
	    ArrayList<String> standorte=new ArrayList<>();
		    try (Connection con = jndi.getConnection("jdbc/waiDB");
		         PreparedStatement statement = con.prepareStatement(query);) {


		        try (ResultSet rs = statement.executeQuery()) {
		            while (rs.next()) {
		            	standorte.add(rs.getString("adresse"));
		            } 
		        }
		        return standorte;
				
		     
		    } catch (Exception e) {
		        throw new SQLException();
		    }
		}
	@Override
	public void deleteStandort(String name) throws SQLException {
		String query= "DELETE From Standort where adresse=?";
		
		try(Connection con=jndi.getConnection("jdbc/waiDB");
				PreparedStatement statement= con.prepareStatement(query);)
			{
				statement.setString(1, name);
				statement.executeUpdate();
			
			
			
				
			
			
		}catch(Exception e) {
			throw new SQLException();
		}
		
		
	}

	
	
	
	public void changeUser(int userid,User user) throws SQLException {
		String rollenQuery="SELECT r.rolle, r.rollenid "
				+ "FROM nutzer n "
				+ "LEFT JOIN rollen r ON n.rollenid = r.rollenid "
				+ "WHERE n.userid = ?";
		String deleteSLorte="DELETE FROM standortverteilung where userid=?"; 
		String addStandorte="Insert into Standortverteilung values(?,?) ON CONFLICT (userid, standortid) DO NOTHING";
		String changeRolle="update rollen set rolle =? where rollenid=?";
		String oldRole="";
		Integer rollenid=null;
	
		        try(Connection con = jndi.getConnection("jdbc/waiDB");
		        		PreparedStatement rollennamestmt = con.prepareStatement(rollenQuery)){
		        	
		       	    rollennamestmt.setInt(1, userid);
		       	   
		 	        try (ResultSet rs = rollennamestmt.executeQuery()) {
		 	        	if(rs.next()) {
		 	        		oldRole=rs.getString("rolle");
		 	        		rollenid=rs.getInt("rollenid");
		 	        	}	
		 	        	//if(oldRole.equals("Standortleiter")&&(!user.getRolle().equals("Standortleiter"))){
		 	        		// wenn man davor standortleiter war also muss die rolle aus standortverteilung gelöscht werden
		 	        		
		 	        		try(PreparedStatement deleteSL=con.prepareStatement(deleteSLorte)){
		 	        			deleteSL.setInt(1, userid);
		 	        			deleteSL.executeUpdate();
		 	        			
		 	        		}
		 	        		try(PreparedStatement changerole=con.prepareStatement(changeRolle)){
		 	        			changerole.setString(1, user.getRolle());
		 	        			changerole.setInt(2, rollenid);
		 	        			changerole.executeUpdate();
		 	        			
		 	        		}
		 	        		
		 	        	//}
		 	        	ArrayList<String>standorte=user.getStandorte();
		 	        	ArrayList<Integer>standortid=new ArrayList<>();		
		 	        	int adressCount= standorte.size();
		 	        	if(user.getRolle().equals("Standortleiter")) {
		 	        	     String adresseQuery=addAdressQuery(standorte);
		 	    	        
		 		        	try(PreparedStatement adressestmt = con.prepareStatement(adresseQuery)){
		 		        		for(int i=1;i<=adressCount;i++) {
		 		        		adressestmt.setString(i, standorte.get(i-1));
		 		        	
		 		        		}

		 		        		try (ResultSet rs1 = adressestmt.executeQuery()) {
		 		        			while(rs1.next()) {
		 			        			standortid.add(rs1.getInt("standortid"));
		 			        			}

		 		        			}
		 		        	}
		 		        
		 		        try(PreparedStatement standortstmt = con.prepareStatement(addStandorte)){
		 		        	
		 		       	    for(int i=0;i<standortid.size();i++) {
		 			       	    standortstmt.setInt(1, userid);
		 			       	    standortstmt.setInt(2, standortid.get(i));
		 			       	    standortstmt.addBatch();

		 		       	    }

		 		 	        standortstmt.executeBatch();
		 		 	        
		 		        }
		 	        	
		 	        	}
		 	        }
		    

		    
		 }catch (Exception e) {
		        throw new SQLException();
		    }
			
		}
		
		
	
		
	private String addAdressQuery(List<String> standorte) {
		
		String adresseQuery= "Select standortid from Standort where adresse in (?";
		 int adressCount=standorte.size();
         
     	//hier kriegen wir die Standortid die wir dann bei der standortstmt brauchen um die user auf die standorte zu mappen
     
     	for(int i=1;i<adressCount;i++) {
     		adresseQuery=adresseQuery + ",?";
 		}
     	adresseQuery+= ")";
		
		return adresseQuery;
	}


	@Override
	public void addStandort(String name) throws SQLException {
		// TODO Auto-generated method stub
		String insertStandortQuery = "insert into Standort(adresse) values(?)";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement rollenstmt = con.prepareStatement(insertStandortQuery)) {

	        rollenstmt.setString(1, name);
	        rollenstmt.executeUpdate();
	            

	    } catch (Exception e) {
	    	throw new SQLException();
	    }
		
		
		
	}


	@Override
	public void updateStandort(String oldName, String newName) throws SQLException {
		String updateQuery="Update standort set adresse=? where adresse=?";
		try (Connection con = jndi.getConnection("jdbc/waiDB");
		         PreparedStatement rollenstmt = con.prepareStatement(updateQuery)) {

		        rollenstmt.setString(1, newName);
		        rollenstmt.setString(2, oldName);
		        rollenstmt.executeUpdate();
		            

		    } catch (Exception e) {
		    	throw new SQLException();
		    }
		
		
	}

	@Override
	public boolean updatePassword(String username, String newPassword) {
	    boolean result = false;
	    String sql = "UPDATE nutzer SET pwdhash = md5(?) WHERE username = ?";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement pstmt = con.prepareStatement(sql)) {
	        
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, username);

	        int affectedRows = pstmt.executeUpdate();
	        result = (affectedRows > 0);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}

	@Override
	public boolean checkUserName(String username) {
	    String sql = "SELECT * FROM nutzer WHERE username = ?";

	    try (Connection con = jndi.getConnection("jdbc/waiDB");
	         PreparedStatement pstmt = con.prepareStatement(sql)) {
	        
	        pstmt.setString(1, username);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return false;
	            } else {
	                return true;
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}


}
