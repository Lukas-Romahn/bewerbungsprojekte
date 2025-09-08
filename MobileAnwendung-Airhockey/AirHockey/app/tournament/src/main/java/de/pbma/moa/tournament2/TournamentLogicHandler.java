package de.pbma.moa.tournament2;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Game;
import de.pbma.moa.database.Tournament;

public class TournamentLogicHandler {
    private ArrayList<Player> players;
    Game[] games;
    Tournament tournament;


    private static TournamentLogicHandler handler;

    public static TournamentLogicHandler getInstance(){
        if(handler == null){
            handler = new TournamentLogicHandler();
        }
        return handler;
    }

    private TournamentLogicHandler(){
        players = new ArrayList<Player>();
    }

    public void initNewGame(String name, ArrayList<Player> players,String UUID) {
        this.tournament = new Tournament(UUID);
        this.tournament.tournamentName= name;
        this.players = players;
        int amountOfGames= (int)Math.pow(2,Math.ceil(Math.log(players.size())/Math.log(2)));
        Log.v("Info", String.valueOf(amountOfGames));
        games = new Game[amountOfGames];
        Collections.shuffle(players);
        initTournamentTree(amountOfGames/2);
        cleanUpUnEvenDistribution();

        tournament.currentGameIndex = games.length-1;
    }
    public void endGame(Context context, int goals1 ,int goals2){
        if(tournament.currentGameIndex < 1){
            return;
        }
        if(games[tournament.currentGameIndex].fillGame){
            while(games[tournament.currentGameIndex].fillGame){
                tournament.currentGameIndex--;
            }
        }
        int safeGameIndex = tournament.currentGameIndex;
        games[tournament.currentGameIndex].isFinished = true;
        games[tournament.currentGameIndex].playerOneGoals = goals1;
        games[tournament.currentGameIndex].playerTwoGoals = goals2;
        addScoreToPlayers(games[tournament.currentGameIndex]);
        //finale
        if(tournament.currentGameIndex == 1){
            tournament.isFinished = true;
            //save to Database
            new Thread(() ->{
                if(context != null){
                    tournament.currentGameIndex--;
                    Log.v("Info", String.valueOf(games[safeGameIndex].uid));
                    AppDatabase.getInstance(context).tournamentDAO().update(tournament);
                    AppDatabase.getInstance(context).tournamentDAO().update(tournament);
                    AppDatabase.getInstance(context).gameDAO().update(games[safeGameIndex]);
                }
            }).start();
            return;
        }

        if(goals1 < goals2){
            games[tournament.currentGameIndex/2].addNewPlayerToMatch(games[tournament.currentGameIndex].playerTwoUuid, games[tournament.currentGameIndex].playerTwoName);
        }else{
            games[tournament.currentGameIndex/2].addNewPlayerToMatch(games[tournament.currentGameIndex].playerOneUuid,  games[tournament.currentGameIndex].playerOneName);
        }
        //save to Database
        new Thread(() ->{
            if(context != null){
                tournament.currentGameIndex--;
                AppDatabase.getInstance(context).tournamentDAO().update(tournament);
                AppDatabase.getInstance(context).gameDAO().update(games[safeGameIndex]);
                AppDatabase.getInstance(context).gameDAO().update(games[safeGameIndex/2]);
            }
        }).start();

    }
    private void initTournamentTree(int maxPlayerCountBeforeAddingByeTickets) {
        int playerCounter = 0;
        int log2PlayerCount =(int) Math.floor(Math.log(players.size())/Math.log(2));

        if((players.size()&(players.size()-1)) == 0){
           log2PlayerCount--;
        }
        int amountOfGamesToFillThePlayers = (int) Math.pow(2,log2PlayerCount);
        fillPlayersIntoTree(amountOfGamesToFillThePlayers);
        for(int i = 1; i < games.length - amountOfGamesToFillThePlayers ; i++){
            Game game = new Game();
            game.playerOneName = null;
            game.playerOneUuid = null;
            game.playerTwoName = null;
            game.playerTwoUuid= null;
            games[i] = game;
        }
    }

    private void fillPlayersIntoTree(int amountOfGamesToFillThePlayers){
        if(games.length < amountOfGamesToFillThePlayers){
            Log.v("Info", "GamearrayLength < amount of Gmaes to Fill all Players");
            return;
        }
        int playerCount = 0;
        for(int i = games.length-1; i >= games.length - amountOfGamesToFillThePlayers; i--){
            Game game = new Game();
            int remainingPlayers = players.size() - playerCount;
            int remainingGames = (games.length -  amountOfGamesToFillThePlayers) - (games.length - (i + 1));
            if(remainingPlayers <= remainingGames){
                game.playerOneName = players.get(playerCount).Name;
                game.playerOneUuid= players.get(playerCount++).uid;
                game.playerTwoName = null;
                game.playerTwoUuid = null;
                game.fillGame = true;
            }else{
                game.playerOneName = players.get(playerCount).Name;
                game.playerOneUuid= players.get(playerCount++).uid;
                game.playerTwoName = players.get(playerCount).Name;
                game.playerTwoUuid= players.get(playerCount++).uid;
            }
            game.gameIndex = i;
            games[i] = game;
        }
    }

    private void fillRestOfPlayers(int counter){
      int anzahlZufüllenderSpiele= (int)Math.ceil(Math.log(counter)/Math.log(2));

    }
    public boolean isTournamentFinished(){
        return games[1].isFinished;
    }
    public void printNotFinishedGames(){
        for(int i = 1; i < games.length; i++){
            if(games[i].isFinished && !games[i].fillGame){
                Log.v("Testing",String.format("game %d: %s : %s \n", i, games[i].playerOneName, games[i].playerTwoName));
            }
        }
    }

    public void printAllGames(){
        for(int i = 1; i < games.length; i++){
            if(!games[i].fillGame){
                Log.v("Testing", String.format("game %d: %s : %s mit %d : %d\n", i, games[i].playerOneName, games[i].playerTwoName, games[i].playerOneGoals,games[i].playerTwoGoals));
            }
        }
    }

    public Game getCurrentGameInfo(){
        if(tournament.currentGameIndex <= 0){
            return games[1];
        }
        if(games[tournament.currentGameIndex].fillGame){
            while(games[tournament.currentGameIndex].fillGame){
                tournament.currentGameIndex--;
            }
        }

        Log.v("hallo", String.format("game %d: %s : %s mit %d : %d \n", tournament.currentGameIndex, games[tournament.currentGameIndex].playerOneName, games[tournament.currentGameIndex].playerTwoName, games[tournament.currentGameIndex].playerOneGoals,games[tournament.currentGameIndex].playerTwoGoals));
        return games[tournament.currentGameIndex];
    }

    public Game getLastGameInfo() {
        if (tournament.currentGameIndex == games.length - 1) {
            return null;
        } else if (tournament.currentGameIndex < 1) {
            return games[1];
        } else {
            if (games[tournament.currentGameIndex + 1].fillGame) {
                int localGameIndex = tournament.currentGameIndex;
                while (localGameIndex < games.length-1 && games[localGameIndex].fillGame) {
                    localGameIndex++;
                }
                if(games[localGameIndex].fillGame){
                    return null;
                }
                return games[localGameIndex];
            }
            return games[tournament.currentGameIndex + 1];
        }
    }

    private boolean isLeaf(int index){
        return 2 * index >= games.length;
    }

    private void cleanUpUnEvenDistribution(){
        for(int i = games.length-1; i >= 1;  i--){
            if(games[i].playerTwoUuid== null && isLeaf(i) && games[i].fillGame){
                games[i/2].addNewPlayerToMatch(games[i].playerOneUuid, games[i].playerOneName);
            }
        }
    }

    public String getName(){
        return tournament.getName();
    }

    public void loadTournamentById(Context context, String id) {
        AppDatabase database = AppDatabase.getInstance(context);
        Thread myTreah = new Thread(() -> {
            this.tournament = database.tournamentDAO().getTournamentById(String.valueOf(id));
            List<Game> databaseGames = database.gameDAO().getGamesFromTournament(id);

            this.players = new ArrayList<Player>();
            this.games = new Game[databaseGames.size() + 1];
            for (int i = 0; i < databaseGames.size(); i++) {
                this.games[i + 1] = databaseGames.get(i);
                if (i >= databaseGames.size() / 2) {
                    if(databaseGames.get(i).playerOneUuid != null){
                        this.players.add(new Player(databaseGames.get(i).playerOneUuid, databaseGames.get(i).playerOneName));
                    }
                    if(databaseGames.get(i).playerTwoUuid != null) {
                        this.players.add(new Player(databaseGames.get(i).playerTwoUuid, databaseGames.get(i).playerTwoName));
                    }
                }
            }
            calculateAbsoluteScoreForEachPlayer();
        });
        myTreah.start();
        try {
            myTreah.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Game[] getGames(){
        return this.games;
    }

    public Tournament getTournament(){
        return this.tournament;
    }
    public ArrayList<Player> getPlayers(){
        return this.players;
    }

    private Player getPlayerByID(String id){
        for(Player player: players){
            if(player.uid.equals(id)){
                return player;
            }
        }
        return null;
    }
    private void calculateAbsoluteScoreForEachPlayer(){
       for(int i = 1; i < games.length; i++){
           if(games[i].isFinished && !games[i].fillGame){
               addScoreToPlayers(games[i]);
           }
       }
    }

    private void addScoreToPlayers(Game game){
        if(game.playerOneGoals > game.playerTwoGoals){
            Player player = getPlayerByID(game.playerOneUuid);
            if(player != null){
                player.score += 3;
            }
        }else if(game.playerTwoGoals > game.playerOneGoals){
            Player player = getPlayerByID(game.playerTwoUuid);
            if(player != null){
                player.score += 3;
            }
        }else{
            Player player1 = getPlayerByID(game.playerOneUuid);
            Player player2 = getPlayerByID(game.playerTwoUuid);
            if(player1 != null && player2 != null){
                player1.score += 1;
                player2.score += 1;
            }
        }

    }
}

