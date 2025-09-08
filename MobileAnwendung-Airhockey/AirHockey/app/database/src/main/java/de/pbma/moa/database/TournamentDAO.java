package de.pbma.moa.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class TournamentDAO {

    @Insert
    public abstract long insert(Tournament tournament);

    @Update
    public abstract void update(Tournament tournament);

    @Delete
    public abstract  void delete(Tournament tournament);

    @Query("SELECT * FROM tournamentTable WHERE uid= :uuid")
    public abstract Tournament getTournamentById(String uuid);

    @Query("SELECT * FROM tournamentTable WHERE name= :name")
    public abstract Tournament getTournamentByName(String name);

    @Query("SELECT * FROM tournamentTable")
    public abstract List<Tournament> getAllTournaments();

    @Query("DELETE FROM tournamentTable")
    public abstract void deleteAll();

}
