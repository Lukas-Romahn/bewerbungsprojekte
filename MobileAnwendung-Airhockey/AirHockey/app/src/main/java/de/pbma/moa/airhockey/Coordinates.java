package de.pbma.moa.airhockey;

public class Coordinates {

    public float x;
    public float y;
    public long timeStamp;

    public Coordinates(float x, float y){
        this.x=x;
        this.y=y;
        timeStamp = 0;
    }

    public Coordinates(float x, float y, long timeStamp){
        this.x=x;
        this.y=y;
        this.timeStamp = timeStamp;
    }

    public Coordinates(){}

}
