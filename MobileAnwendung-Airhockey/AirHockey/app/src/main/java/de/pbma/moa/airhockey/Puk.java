package de.pbma.moa.airhockey;

public class Puk {

    private float x,y;
    private float radius;
    private float speedx,speedy;;

    public Puk(float x,float y,float radius){

        this.x=x;
        this.y=y;
        this.radius=radius;

    }

    public void setSpeed(float speedx,float speedy){
        this.speedx=speedx;
        this.speedy=speedy;
    }

    public void move(){

        x+=speedx;
        y+=speedy;}

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
}
