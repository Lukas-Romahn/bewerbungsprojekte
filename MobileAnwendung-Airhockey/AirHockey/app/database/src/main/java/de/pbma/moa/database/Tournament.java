package de.pbma.moa.database;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Entity(tableName = "tournamentTable")
public class Tournament implements  Comparable<Tournament> {
    @PrimaryKey @NonNull
    public String uid = "not a valid UUID";

    @ColumnInfo(name = "name")
    public String tournamentName;
    @ColumnInfo(name = "isFinished")
    public boolean isFinished;
    @ColumnInfo(name="currentGameIndex")
    public int currentGameIndex;

    @ColumnInfo(name="timestamp")
    public String timestamp;
    public String getTurnierId() {
        return uid;
    }

    public String getName() {
    return tournamentName;
    }
    public boolean getIsFinished(){
        return isFinished;
    }

    public Tournament() {
        isFinished = false;
        this.timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public Tournament(String UUID){
        this.uid =UUID;
        this.isFinished = false;
        this.timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    public int compareTo(Tournament o) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            return -(Objects.requireNonNull(format.parse(timestamp)).compareTo(format.parse(o.timestamp))) ;
        } catch (ParseException e) {
            return 0;
        }
    }
}
