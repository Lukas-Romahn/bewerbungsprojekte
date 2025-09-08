package de.pbma.moa.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Game.class, Tournament.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GameDAO gameDAO();
    public abstract TournamentDAO tournamentDAO();
    private static AppDatabase Instance = null;
    private static AppDatabase createInstance(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "database")
                .enableMultiInstanceInvalidation()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized AppDatabase getInstance(Context context){
        if(Instance == null){
            Instance = createInstance(context);
        }
        return Instance;
   }
}