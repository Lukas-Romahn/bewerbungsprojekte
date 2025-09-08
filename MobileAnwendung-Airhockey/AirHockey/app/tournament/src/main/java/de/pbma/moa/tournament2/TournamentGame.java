package de.pbma.moa.tournament2;

public class TournamentGame {
    public String player1;
    public String player2;
    public int goalsPlayer1;
    public int goalsPlayer2;

    public boolean isFillerGame;
    public boolean isFinished;
    public TournamentGame(){
        player1 = null;
        player2 = null;
        goalsPlayer2 = 0;
        isFillerGame = false;
        isFinished = false;
    }

    public void addNewPlayerToMatch(String player){
        if(player1 == null){
            player1 = player;
        }else if(player2 == null){
            player2 = player;
        }
    }

}
