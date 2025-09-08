package de.pbma.moa.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName= "gamesTable")
public class Game {

    @PrimaryKey @NonNull
    public String uid;

    @ColumnInfo(name = "playerOneId")
    public String playerOneUuid;

    @ColumnInfo(name = "playerOneName")
    public String playerOneName;

    @ColumnInfo(name = "playerTwoId")
    public String playerTwoUuid;

    @ColumnInfo(name = "playerTwoName")
    public String playerTwoName;

    @ColumnInfo(name = "tid")
    public String tid;

    @ColumnInfo(name = "playerOneGoals")
    public int playerOneGoals;

    @ColumnInfo(name = "palyerTwoGoals")
    public int playerTwoGoals;

    @ColumnInfo(name = "gameIndex")
    public int gameIndex;
    //true: fillGame false: keinFillGame
    @ColumnInfo(name="fillGame")
    public boolean fillGame;
    // true: open False: geschlossen
    @ColumnInfo(name="isFinished")
    public boolean isFinished;

    public Game(){
        this.uid = UUID.randomUUID().toString();
        this.isFinished = false;
        this.fillGame = false;
    }
    public void addNewPlayerToMatch(String playerUid, String playerName){
        if(playerOneUuid == null){
            playerOneUuid= playerUid;
            playerOneName = playerName;
        }else if(playerTwoUuid == null){
            playerTwoUuid = playerUid;
            playerTwoName = playerName;
        }
    }
}
