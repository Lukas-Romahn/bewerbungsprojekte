package de.pbma.moa.airhockey.ui.Turnier;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.pbma.moa.airhockey.ListItem;
import de.pbma.moa.airhockey.MqttMessaging;
import de.pbma.moa.airhockey.PlayerListAdapter;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.TableActivity;
import de.pbma.moa.airhockey.databinding.FragmentCreateTurnierBinding;
import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Game;
import de.pbma.moa.database.Tournament;
import de.pbma.moa.tournament2.Player;
import de.pbma.moa.tournament2.TournamentGame;
import de.pbma.moa.tournament2.TournamentLogicHandler;


public class CreateTurnierFragment extends Fragment {
    private FragmentCreateTurnierBinding binding;
    String lobbycode;
    MqttMessaging client;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";
    TournamentLogicHandler turnierHandler;
    ArrayList<String> ackPlayers = new ArrayList<String>();
    ArrayList<String> players = new ArrayList<String>();
    MqttConnectOptions options;
    List<String> itemList;
    PlayerListAdapter adapter;
    String name;
    String pseudolobbycode;
    AppDatabase appDatabase;
    List<String>readPlayers;
    MqttMessaging.FailureListener failureListener = new MqttMessaging.FailureListener() {
        @Override
        public void onConnectionError(Throwable throwable) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Sie haben keine Internetverbindung", Toast.LENGTH_SHORT).show();
                }
            });
            Thread thread= new Thread(()->{
                client.connect(broker,options);
            });

            }



        @Override
        public void onMessageError(Throwable throwable, String msg) {

        }

        @Override
        public void onSubscriptionError(Throwable throwable, String topic) {

        }
    };
    MqttMessaging.ConnectionListener connectionListener= new MqttMessaging.ConnectionListener() {
        @Override
        public void onConnect() {
            client.subscribe(getString(R.string.MqTTTurnierJoin, pseudolobbycode));

        }

        @Override
        public void onDisconnect() {

        }
    };
    MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) throws JSONException {
            if (topic.equals(getString(R.string.MqTTTurnierJoinPlayer1Ack, lobbycode))) {
                if (!ackPlayers.contains(msg)) {
                    ackPlayers.add(msg);
                }
            }
            if (topic.equals(getString(R.string.MqTTTurnierJoinPlayer2Ack, lobbycode))) {
                if (!ackPlayers.contains(msg)) {
                    ackPlayers.add(msg);
                }
            }
            if(ackPlayers.size() == 2){
                Game game = turnierHandler.getCurrentGameInfo();
                if(ackPlayers.contains(game.playerOneUuid) && ackPlayers.contains(game.playerTwoUuid)){
                    Intent newIntent = new Intent(requireContext(), TableActivity.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    newIntent.putExtra("LobbyCode", lobbycode);
                    newIntent.putExtra("Rejoin", false);
                    newIntent.putExtra("Turnier",true);
                    newIntent.putExtra("LobbyType",false);
                    newIntent.putExtra("player1", game.playerOneUuid + "/" +  game.playerOneName);
                    newIntent.putExtra("player2", game.playerTwoUuid + "/" +  game.playerTwoName);

                    new Thread(() -> {
                        appDatabase.tournamentDAO().insert(turnierHandler.getTournament());
                        Game[] games = turnierHandler.getGames();
                        for (int i = 1; i < games.length; ++i) {
                            games[i].tid = turnierHandler.getTournament().getTurnierId();
                            games[i].gameIndex = i;
                            appDatabase.gameDAO().insert(games[i]);
                        }
                    }).start();

                    startActivity(newIntent);
                }
            }

            if (topic.equals(getString(R.string.MqTTTurnierJoin, pseudolobbycode))) {
                client.subscribe(getString(R.string.MqTTTurnierLobbyStarted, lobbycode,msg.split("/")[0]));

                client.send(getString(R.string.MqTTTurnierJoinAck, pseudolobbycode, msg.split("/")[0]), lobbycode, 2);
                if (players.contains(msg)) {
                    return;
                }
                itemList.add(msg.split("/")[1]);
                players.add(msg);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
            for (String player : players) {
                if (topic.equals(getString(R.string.MqTTTurnierLobbyStarted, lobbycode, player.split("/")[0]))) {
                    if (!readPlayers.contains(player)) {
                        readPlayers.add(player);
                    }
                }
            }
        }

    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String>saveList= new ArrayList<>();
        for(String player :readPlayers){
            saveList.add(player+"/"+itemList.get(readPlayers.indexOf(player)));
        }
        outState.putString("tournamentCode",lobbycode);

        outState.putStringArrayList("joinedPlayers",saveList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateTurnierBinding.inflate(inflater, container, false);
        itemList = new ArrayList<>();
//        new Thread(() -> {
//            AppDatabase.getInstance(requireContext()).gameDAO().deleteAll();
//            AppDatabase.getInstance(requireContext()).tournamentDAO().deleteAll();
//        }).start();
        adapter = new PlayerListAdapter(requireContext(), itemList);
        binding.lvPlayers.setAdapter(adapter);
        client = new MqttMessaging(failureListener, messageListener,connectionListener);
        options = new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        pseudolobbycode = String.valueOf(new Random().nextInt(1000));

        client.connect(broker, options);

        turnierHandler = TournamentLogicHandler.getInstance();
        readPlayers = new ArrayList<>();

        lobbycode=UUID.randomUUID().toString();

        if (savedInstanceState != null) {
            ArrayList<String> combineList = savedInstanceState.getStringArrayList("joinedPlayers");
            for (String combined : combineList) {
                readPlayers.add(combined.split("/")[0]);
                itemList.add(combined.split("/")[1]);
                players.add(combined.split("/")[0]);

            }
            lobbycode = savedInstanceState.getString("tournamentCode");
            adapter.notifyDataSetChanged();
        }

        //hier kiregen wir von der TurnierLogik den LobbyCode für das Turnier
        appDatabase = AppDatabase.getInstance(requireContext());


        binding.ivQR.setImageBitmap(generateQR(pseudolobbycode));
        binding.tvLobbyCode.setText(String.valueOf(pseudolobbycode));


        binding.btnTurnierBestaetigen.setOnClickListener(v -> {

            //Julius Funktion die Tournament erstellt: gameIndex
            name = binding.etTurnierName.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(requireContext(), "Das Turnier muss einen Namen haben", Toast.LENGTH_SHORT).show();
                return;
            }
            if (players.size() >= 2 && players.size() < 6) {
                if (readPlayers.size() == players.size()) {
                    ArrayList<Player> newPlayers = new ArrayList<Player>();
                    for (String player : players) {
                        newPlayers.add(new Player(player.split("/")[0], player.split("/")[1]));
                    }

                        turnierHandler.initNewGame(name, newPlayers,lobbycode);
                        turnierHandler.getTournament().tournamentName = name;

                        createGame(turnierHandler);
                }
            }
        });
        return binding.getRoot();
    }

    private void createGame(TournamentLogicHandler handler){

        client.subscribe(getString(R.string.MqTTTurnierJoinPlayer1Ack, lobbycode));
        client.subscribe(getString(R.string.MqTTTurnierJoinPlayer2Ack, lobbycode));
        Log.v("myInfo", getString(R.string.MqTTTurnierJoin, lobbycode));

        Game game = handler.getCurrentGameInfo();
        client.send(getString(R.string.MqTTTurnierMessagePlayers, lobbycode), createPlayerNotifyJSON(game), 2) ;
    }

    private String createPlayerNotifyJSON(Game game){
        JSONObject playerConfirmJson = new JSONObject();
        try{
            playerConfirmJson.put("messageInfo", "gameStart");
            playerConfirmJson.put("player1", game.playerOneUuid);
            playerConfirmJson.put("player2", game.playerTwoUuid);
        }catch(Exception e){

        }
        return playerConfirmJson.toString();
    }
    private Bitmap generateQR(String qrText){
        MultiFormatWriter writer= new MultiFormatWriter();
        try {
            BitMatrix matrix= writer.encode(qrText, BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder encoder= new BarcodeEncoder();
            Bitmap bitmap= encoder.createBitmap(matrix);
            return bitmap;
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }
}