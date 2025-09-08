package model;

public class Camera {
    
    public int kid;
    public String location;
    public String createdTimestamp;
    public String domain;
    public String name;
    public String status;
    
    public String getLocation(){
        return location;
    }
    public String getName(){
        return name;
    }
    public String getStatus(){
        return status;
    }
    
    public String getCreatedTimestamp(){
        return createdTimestamp;
    }
    
    public String getDomain(){
        return domain;
    }
    
    public int getKid(){
        return kid;
    }
    
    public void setLocation(String var){
        this.location = var;
    }

    public void setName(String var){
        this.name = var;
    }
    public void setDomain(String var) {
    	this.domain=var;
    }

    public void setStatus(String var){
        this.status = var;
    }
    public void setKid(int var){
        this.kid = var;
    }
}
