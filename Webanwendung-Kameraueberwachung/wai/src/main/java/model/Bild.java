package model;
import java.sql.Timestamp;

public record Bild(int id, int kid,  String kameraname, String standort, String thumbnailUrl, String ablageUrl, Timestamp timestamp){}

/* public class Bild  { 
    int id;
    String kameraName;
    String Standort;
    String thumbnailURL;
    String ablageURL;
    Timestamp timestamp;

    public Bild(int id, String kameraname, String standort, String thumbnailUrl, String ablageUrl, Timestamp timestamp){
        
        this.id = id;
        this.kameraName = kameraname;
        this.Standort = standort;
        this.thumbnailURL = thumbnailUrl;
        this.ablageURL = ablageUrl;
        this.timestamp = timestamp;
    }


    public int id(){
        return id;
    }
    public String kameraName(){
        return kameraName;
    }
    public String Standort(){
        return Standort;
    }
    public String thumbnailURL(){
        return thumbnailURL;
    }
    public String ablageURL(){
        return ablageURL;
    }
    public Timestamp timestamp(){
        return timestamp;
    }
    
    public void setThumbnailURL(String url) {
        this.thumbnailURL = url;
    }
    public void setAblageURL(String url) {
        this.ablageURL = url;
    }
} */


