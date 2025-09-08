package de.pbma.moa.airhockey;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import de.pbma.moa.database.Tournament;
import de.pbma.moa.tournament2.TournamentLogicHandler;

public class TableActivity extends AppCompatActivity {
    MqttMessaging clientTable;

    MqttMessaging clientReJoin;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";
    String player1;
    String player2;
    String player1Name;
    String player2Name;
    String lobbyID;
    CanvasView canvasView;
    Coordinates coordinates;

    SharedPreferences preferences;
    boolean gameClosed=false;
    PopupWindow popupWindow ;

    private boolean botLobby;
    private boolean turnierMode;
    AlertDialog dialog;
    int player1Score;
    int player2Score;
    Context context;

    private MqttMessaging.MessageListener messageListenerLeave = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) {

            if (topic.equals(getString(R.string.MqTTLobbyLeavePlayer, lobbyID, player1))) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!gameClosed) {// die abfrage wird gemacht um beim verlassen des zweiten spielers die Nachricht nicht merh anzuzeigen da die Activity sowieso beendet wird und das Dialog sonst keinen root hat.

                            showPlayerLeftDialog(player2Name, 1,player1);

                        }
                    }
                });
            }
            if (topic.equals(getString(R.string.MqTTLobbyLeavePlayer, lobbyID, player2))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!gameClosed) {
                            showPlayerLeftDialog(player1Name, 2,player2);
                        }
                    }
                });
            }


        }

    };




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        lobbyID=intent.getStringExtra("LobbyCode");
        botLobby=intent.getBooleanExtra("LobbyType",true);
        if(botLobby){
           player2 = "0000";
           player2Name= "Bot";
        }else{
            player2=intent.getStringExtra("player2").split("/")[0];
            player2Name=intent.getStringExtra("player2").split("/")[1];

        }
        player1=intent.getStringExtra("player1").split("/")[0];
        player1Name=intent.getStringExtra("player1").split("/")[1];
        turnierMode=intent.getBooleanExtra("Turnier",false);
        canvasView = new CanvasView(this,lobbyID,player1,player2);
        setContentView(canvasView);
        canvasView.startGame();
        coordinates= new Coordinates();
        context=this;


        preferences = this.getSharedPreferences("PlayerInfo", Context.MODE_PRIVATE);


        canvasView.getPlayerScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer score) {
               player1Score=score;
                checkScore(score,player1);
            }
        });

        canvasView.getOpponentScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer score) {
                player2Score=score;
                checkScore(score,player2);
            }
        });

        clientReJoin = new MqttMessaging(null, messageListenerLeave);
        clientTable= new MqttMessaging(null,messageListenerLeave);


        MqttConnectOptions options= new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);



        clientReJoin.connect(broker,options);
        clientTable.connect(broker,options);
        clientReJoin.subscribe(getString(R.string.MqTTLobbyLeavePlayer,lobbyID,player1));
        clientReJoin.subscribe(getString(R.string.MqTTLobbyLeavePlayer,lobbyID,player2));






        canvasView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                canvasView.initializeGame(botLobby);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(clientReJoin.isConnected()) {
            clientReJoin.send(getString(R.string.MqTTLobbyLeaveTable, lobbyID), "Leave", 2);
            clientReJoin.disconnect();
        }
    }

    private void showPlayerLeftDialog(String playerName, int leftPlayerNumber, String leftPlayerID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

       canvasView.showAlertAndHalt();



        builder.setTitle("Spieler " + leftPlayerNumber+ "hat das Spiel verlassen")
                .setMessage("Damit hat Spieler " +playerName +" Das Spiel gewonnen")
                .setIcon(android.R.drawable.ic_dialog_alert) // Optional: Icon hinzufügen
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(leftPlayerID.equals(player1)){
                            player1Score=0;
                            player2Score=1;
                        }
                        else{
                            player1Score=1;
                            player2Score=0;
                        }
                        startLobbyActivity();
                        gameClosed=true;
                        dialog.dismiss();
                        finish(); // Optional: Activity schließen

                    }

                });

        // Dialog anzeigen
        dialog = builder.create();
        dialog.show();

    }
    private void checkScore(int score,String playerID){
//Todo Hier müssen wir das aftergame Fragment starten in der LobbyActivity wenn turnier==true und dem die Daten geben vom Spiel. dann soll aftergame die Datenspeichern und so den TorunamentHandler aktuell halten
        if(score>0) {
            clientTable.send(getString(R.string.MqTTGameInfo, lobbyID, playerID), jsonTangler());
        }
        int targetGoals = preferences.getInt("targetGoals", 3);
        if(score>=targetGoals){
            startLobbyActivity();
        }
    }

    private void startLobbyActivity() {

            if (turnierMode) {

                TournamentLogicHandler.getInstance().endGame(this, player1Score, player2Score);
                Intent intent = new Intent(this, LobbyActivity.class);
                intent.putExtra("wasPlayed", "Turnier");
                intent.putExtra("playerType", "Table");
                intent.putExtra("tournamentCode", lobbyID);
                intent.putExtra("SpielErgebnis",  String.valueOf(player1Score) + ":" + String.valueOf(player2Score));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                clientTable.send(getString(R.string.MqTTGameFinished, lobbyID, player1), "Turnier");
                clientTable.send(getString(R.string.MqTTGameFinished, lobbyID, player2), "Turnier");
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, LobbyActivity.class);
                intent.putExtra("wasPlayed", "CasualGame");
                intent.putExtra("playerType", "Table");
                intent.putExtra("Player1", player1Name);
                intent.putExtra("Player2", player2Name);
                intent.putExtra("SpielErgebnis",  String.valueOf(player1Score) + ":" + String.valueOf(player2Score));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                clientTable.send(getString(R.string.MqTTGameFinished, lobbyID, player1), "Game");
                clientTable.send(getString(R.string.MqTTGameFinished, lobbyID, player2), "Game");

                startActivity(intent);
                finish();
            }



    }
    private String jsonTangler() {
        Boolean Goal=true;
        try {

            JSONObject jsonObjectGameInfo = new JSONObject();
            jsonObjectGameInfo.put("Goal",Goal);

            return jsonObjectGameInfo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return "not a json Object";
    }



}
