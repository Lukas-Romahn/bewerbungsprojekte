package model;

import java.util.ArrayList;

public class User {
	


	public User() {
		standorte= new ArrayList<>();
	}

	public int getId(){
		return Id;
	}

	public void setId(int i){
		this.Id = i;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRolle() {
		return rolle;
	}
	public void setRolle(String rolle) {
		this.rolle = rolle;
	}
	public ArrayList<String> getStandorte() {
		return standorte;
	}
	public void addStandorte(String standort) {
		standorte.add(standort);
	}
	
	public void transferStandorte(ArrayList<String> stands) {
		standorte=stands;
	}

	private int Id;
	private String password;
	private String username;
	private String rolle;
	private ArrayList<String> standorte;
	
	
	
}
