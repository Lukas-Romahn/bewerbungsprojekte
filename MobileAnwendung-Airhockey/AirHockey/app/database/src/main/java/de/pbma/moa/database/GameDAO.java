package de.pbma.moa.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class GameDAO {

    @Insert
    public abstract  long insert(Game game);

    @Update
    public abstract void update(Game game);

    @Delete
    public abstract  void delete(Game game);

    @Query("SELECT * FROM gamesTable WHERE uid= :uuid")
    public abstract Game getGame(String uuid);

    @Query("SELECT * FROM gamesTable WHERE tid= :tid AND gameIndex= :gameIndex")
    public abstract  Game getGameFromTidAndGameIndex(String tid, int gameIndex);
    @Query("SELECT * FROM GamesTable WHERE playerOneId = :playerId OR playerTwoId = :playerId")
    public abstract List<Game> getGamesByPlayerId(String playerId);

    @Query("SELECT * FROM GamesTable WHERE playerOneName = :playerName OR playerTwoName = :playerName")
    public abstract List<Game> getGamesByPlayerName(String playerName);

    @Query("SELECT * FROM gamesTable WHERE tid= :tid ORDER BY gameIndex ASC")
    public abstract List<Game> getGamesFromTournament(String tid);

    @Query("DELETE FROM GamesTable")
    public abstract void deleteAll();

}
