package de.pbma.moa.tournament2;
public class Player implements Comparable<Player> {
    public String uid;
    public String Name;

    public int score;

    //test
    public Player(String uid, String Name){
       this.uid = uid;
       this.Name = Name;
       score = 0;
    }

    @Override
    public int compareTo(Player o) {
        return Integer.compare(o.score, this.score);
    }
}
