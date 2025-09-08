package de.pbma.moa.tournament2;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.UUID;

import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Game;
import de.pbma.moa.database.Tournament;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("de.pbma.moa.tournament2.test", appContext.getPackageName());
    }

    @Test
    public void useTournament(){
        ArrayList<Player> players = new ArrayList<Player>();

        players.add(new Player("1", "Anna"));
        players.add(new Player("2", "Berthold"));
        players.add(new Player("3", "Josie"));
        players.add(new Player("4", "Eymen"));
        players.add(new Player("5", "Lukas"));
        players.add(new Player("6", "Max"));
        TournamentLogicHandler tournament = TournamentLogicHandler.getInstance();
        tournament.initNewGame("Eymensturnier", players, UUID.randomUUID().toString());

        while(!tournament.isTournamentFinished()){
            tournament.endGame(null, 0,1);
        }
        tournament.printAllGames();
    }

    @Test
    public void initNewTournament(){
        ArrayList<Player> players = new ArrayList<Player>();

        players.add(new Player("1", "Person1"));
        players.add(new Player("2", "Person2"));
        players.add(new Player("3", "Person3"));
        players.add(new Player("4", "Person4"));
        players.add(new Player("5", "Person5"));
        players.add(new Player("6", "Person6"));
//        players.add(new Player("7", "Person7"));
//        players.add(new Player("8", "Person8"));
//        players.add(new Player("6", "Mika"));
//        players.add(new Player("7", "Person7"));
        TournamentLogicHandler tournament = TournamentLogicHandler.getInstance();
        tournament.initNewGame("Eymensturnier", players, UUID.randomUUID().toString());
        Game game;
        tournament.endGame(null, 0, 1);
        game = tournament.getCurrentGameInfo();
        tournament.endGame(null, 0, 1);
        game = tournament.getCurrentGameInfo();
        tournament.endGame(null, 0, 1);
        game = tournament.getCurrentGameInfo();
//        while(!tournament.isTournamentFinished()){
//            tournament.endGame(null, 0,1);
//        }
        tournament.printAllGames();
    }
    @Test
    public void reloadTournamentFromDatabase() {

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ArrayList<Player> players = new ArrayList<Player>();

        players.add(new Player("2", "Anna"));
        players.add(new Player("3", "Berthold"));
        players.add(new Player("4", "Josie"));
        players.add(new Player("5", "Eymen"));
        players.add(new Player("6", "Lukas"));
        players.add(new Player("7", "Max"));
        players.add(new Player("8", "Tobias"));

        Tournament t = new Tournament(UUID.randomUUID().toString());
        t.tournamentName = "Test";
        t.currentGameIndex = 7;
        t.isFinished = true;

        Thread writeToDataBaseThread = new Thread(() -> {
            AppDatabase.getInstance(context).tournamentDAO().insert(t);
            for(int i  = 0; i < 7; i++){
                Game game = new Game();
                game.tid = t.getTurnierId();
                game.playerOneName = players.get(i/2).Name;
                game.playerOneUuid = players.get(i/2).uid;
                game.playerTwoName = players.get(i/2 + 1).Name;
                game.playerTwoUuid = players.get(i/2 + 1).uid;
//                AppDatabase.getInstance(context).gameDAO().insert(game);
            }
        });
        writeToDataBaseThread.start();
        try {
            writeToDataBaseThread.join();
        }catch(Exception e){

        }
        TournamentLogicHandler tournament = TournamentLogicHandler.getInstance();
        tournament.loadTournamentById(context, t.getTurnierId());
        tournament.endGame(null, 1,2);
        tournament.printAllGames();
    }
}