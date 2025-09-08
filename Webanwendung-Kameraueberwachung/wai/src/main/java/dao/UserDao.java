package dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;
import utils.QueryStrategy.QueryBuilder;

public interface UserDao {
	
	public User getRoles(int id);
	
	public boolean checkLoginData(String username,String password) throws SQLException;
		
	public int getId(String username);

	public User getUserById(int Id);
	
	public ArrayList<User> getAllUser(QueryBuilder builder) throws SQLException;
	
	public void deleteUserbyId(int id) throws SQLException;
	
	public void addStandortToUser(int id,String adresse);
		
	public void addUser(String username, String password, String Rolle) throws SQLException;
	
	public ArrayList<String> getStandorte() throws SQLException;

	public void addUserSL(String username, String password, List<String> standorte, String rolle) throws SQLException;
	
	public void changeUser(int userid,User user) throws SQLException;
	
	public void deleteStandort(String name) throws SQLException;
	
	public void addStandort(String name) throws SQLException;
	
	public void updateStandort(String oldName,String newName) throws SQLException;
	
	public boolean updatePassword(String username, String newPassword);
	
	public boolean checkUserName(String username);


}
