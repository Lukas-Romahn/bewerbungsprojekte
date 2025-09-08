package de.pbma.moa.airhockey;

import java.util.ArrayList;

import de.pbma.moa.database.Game;

public class TurnierInfo {
    private String title;
    private String description;
    private ArrayList<String> players;
    private ArrayList<Game> games;
    public TurnierInfo(String title, String description,ArrayList<String>players,ArrayList<Game>games) {
        this.title = title;
        this.description = description;
        this.players=players;
        this.games=games;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    public ArrayList<String> getPLayers(){
        return players;
    }
    public ArrayList<Game> getGames(){
        return games;
    }
}




