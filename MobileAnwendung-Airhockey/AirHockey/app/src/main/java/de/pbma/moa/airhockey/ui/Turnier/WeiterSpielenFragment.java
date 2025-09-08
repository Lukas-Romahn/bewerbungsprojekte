package de.pbma.moa.airhockey.ui.Turnier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Random;
import java.util.UUID;

import de.pbma.moa.airhockey.MqttMessaging;
import de.pbma.moa.airhockey.PlayerScoreAdapter;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.TableActivity;
import de.pbma.moa.airhockey.databinding.FragmentWeiterSpielenBinding;
import de.pbma.moa.database.Game;
import de.pbma.moa.tournament2.TournamentLogicHandler;

public class WeiterSpielenFragment extends Fragment {
    FragmentWeiterSpielenBinding binding;
    MqttMessaging client;
    String turnierId;
    ArrayList<String> ackPlayers = new ArrayList<String>();

    TournamentLogicHandler tournamentLogicHandler;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";
    String player1;
    boolean playerOneJoined = false;
    String player2;
    boolean playerTwoJoined = false;
    String pseudoLobbyCode;

    MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) throws JSONException {
            Game game = tournamentLogicHandler.getCurrentGameInfo();
            if (topic.equals(getString(R.string.MqTTTurnierJoinPlayer1Ack, turnierId))) {

                if(msg.equals("ACK")){
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.iVCheckPlayer1.setVisibility(View.VISIBLE);
                        }
                    });

                    playerOneJoined = true;

                }else if(msg.equals(game.playerOneUuid)){
                    if(!ackPlayers.contains(player1)){
                        ackPlayers.add(player1);
                    }
                }
            }
            if (topic.equals(getString(R.string.MqTTTurnierJoinPlayer2Ack, turnierId))) {
                if(msg.equals("ACK")) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.iVCheckPlayer2.setVisibility(View.VISIBLE);
                        }
                    });
                }else if(msg.equals(game.playerTwoUuid)){
                    if(!ackPlayers.contains(player2)){
                        ackPlayers.add(player2);
                    }

                }

                playerTwoJoined = true;
            }
            if(ackPlayers.size() == 2) {
                if (ackPlayers.contains(game.playerOneUuid) && ackPlayers.contains(game.playerTwoUuid)) {
                    Intent newIntent = new Intent(requireContext(), TableActivity.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    newIntent.putExtra("LobbyCode", turnierId);
                    newIntent.putExtra("Rejoin", false);
                    newIntent.putExtra("Turnier", true);
                    newIntent.putExtra("LobbyType", false);
                    newIntent.putExtra("player1", game.playerOneUuid + "/" + game.playerOneName);
                    newIntent.putExtra("player2", game.playerTwoUuid + "/" + game.playerTwoName);

                    startActivity(newIntent);
                } else {
                    Toast.makeText(requireContext(), "nicht alle Spieler sind bereit", Toast.LENGTH_SHORT).show();
                }
            }
            if (topic.equals(getString(R.string.MqTTTurnierJoin, pseudoLobbyCode))) {
                if (msg.split("/")[0].equals(player1)) {

                    client.send(getString(R.string.MqTTTurnierJoinAck, pseudoLobbyCode, msg.split("/")[0]), turnierId,2);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.iVCheckPlayer1.setVisibility(View.VISIBLE);
                        }
                    });
                    playerOneJoined = true;
                } else if (msg.split("/")[0].equals(player2)) {
                    client.send(getString(R.string.MqTTTurnierJoinAck, pseudoLobbyCode, msg.split("/")[0]), turnierId,2);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.iVCheckPlayer2.setVisibility(View.VISIBLE);
                        }
                    });
                    playerTwoJoined = true;
                }
            }
            if(topic.equals(getString(R.string.MqTTLobbyJoin, turnierId))){
               if(msg.split("/")[0].equals(game.playerOneUuid)){
                   if(!ackPlayers.contains(msg)){
                       ackPlayers.add(msg);
                   }
                }
                if(msg.split("/")[0].equals(game.playerTwoUuid)){
                    if(!ackPlayers.contains(msg)){
                        ackPlayers.add(msg);
                    }
                }
            }


            }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeiterSpielenBinding.inflate(inflater, container, false);
        tournamentLogicHandler = TournamentLogicHandler.getInstance();
        binding.tvTournamentName.setText(tournamentLogicHandler.getName());
        binding.tvPlayer1NextMatch.setText(tournamentLogicHandler.getCurrentGameInfo().playerOneName);
        binding.tvPlayer2NextMatch.setText(tournamentLogicHandler.getCurrentGameInfo().playerTwoName);

        NavController controller = NavHostFragment.findNavController(this);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                controller.navigate(R.id.turnierFragment);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                controller.navigate(R.id.turnierFragment);
            }
        };


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        player1 = tournamentLogicHandler.getCurrentGameInfo().playerOneUuid;
        player2 = tournamentLogicHandler.getCurrentGameInfo().playerTwoUuid;
        PlayerScoreAdapter adapter = new PlayerScoreAdapter(requireContext(), tournamentLogicHandler.getPlayers());
        binding.lvCurrentScore.setAdapter(adapter);
        if (tournamentLogicHandler.getTournament().currentGameIndex == tournamentLogicHandler.getGames().length - 1) {
            binding.gLLastGameInfo.setVisibility(View.GONE);
        } else {
            binding.gLLastGameInfo.setVisibility(View.VISIBLE);
        }

        if (tournamentLogicHandler.getTournament().currentGameIndex < 1) {
            binding.llNextMatchWeiterSpielen.setVisibility(View.GONE);
            binding.btnStartNextGame.setText("View TurnierResult");
            binding.btnStartNextGame.setVisibility(View.GONE);
            binding.gLLastGameInfo.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.GONE);
            binding.tvQrCodeWeiterSpielen.setVisibility(View.GONE);
        }

        Game lastGame = tournamentLogicHandler.getLastGameInfo();
        if (lastGame != null) {
            binding.tvPlayer1WeiterSpielen.setText(lastGame.playerOneName);
            binding.tvPlayer2WeiterSpielen.setText(lastGame.playerTwoName);
            binding.tvPlayer1GoalsWeiterSpielen.setText(String.valueOf(lastGame.playerOneGoals));
            binding.tvPlayer2GoalsWeiterSpielen.setText(String.valueOf(lastGame.playerTwoGoals));
        }

        turnierId = tournamentLogicHandler.getTournament().getTurnierId();
        tournamentLogicHandler.printAllGames();
        binding.btnStartNextGame.setOnClickListener(v -> {
            createGame();
        });

        pseudoLobbyCode = String.valueOf(new Random().nextInt(1000));
        client = new MqttMessaging(null, messageListener);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(userName);
        options.setPassword(password.toCharArray());
        client.connect(broker, options);
        Bitmap qrCode = generateQR(pseudoLobbyCode);

        qrCode = Bitmap.createScaledBitmap(qrCode, 400, 400,  true);
        binding.imageView.setImageBitmap(qrCode);
        binding.tvQrCodeWeiterSpielen.setText(pseudoLobbyCode);
        client.subscribe(getString(R.string.MqTTTurnierJoin, pseudoLobbyCode));
        client.subscribe(getString(R.string.MqTTTurnierJoinPlayer1Ack, turnierId));
        client.subscribe(getString(R.string.MqTTTurnierJoinPlayer2Ack, turnierId));
        client.send(getString(R.string.MqTTTurnierMessagePlayers, turnierId), createPlayerNotifyJSON(tournamentLogicHandler.getCurrentGameInfo(), "checkPresence"), 2);
        Log.v("INFO", turnierId);
        return binding.getRoot();
    }

    private void createGame() {
        if(playerOneJoined && playerTwoJoined){
            Game game = tournamentLogicHandler.getCurrentGameInfo();
            client.send(getString(R.string.MqTTTurnierMessagePlayers, turnierId), createPlayerNotifyJSON(game, "gameStart"), 2) ;
        }else{
            Toast.makeText(requireContext(), "nicht alle Spieler sind bereit", Toast.LENGTH_SHORT).show();
        }
    }
    private String createPlayerNotifyJSON(Game game, String info){
        JSONObject playerConfirmJson = new JSONObject();
        if(info == null){
            info = "";
        }
        try{
            playerConfirmJson.put("messageInfo", info);
            playerConfirmJson.put("player1", game.playerOneUuid);
            playerConfirmJson.put("player2", game.playerTwoUuid);
        }catch(Exception e){

        }
        return playerConfirmJson.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }

    private Bitmap generateQR(String qrText){
        MultiFormatWriter writer= new MultiFormatWriter();
        try {
            BitMatrix matrix= writer.encode(qrText, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder encoder= new BarcodeEncoder();
            Bitmap bitmap= encoder.createBitmap(matrix);
            return bitmap;
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}