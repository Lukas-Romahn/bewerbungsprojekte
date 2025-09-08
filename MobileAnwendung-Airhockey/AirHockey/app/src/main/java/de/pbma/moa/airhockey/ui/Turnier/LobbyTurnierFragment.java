package de.pbma.moa.airhockey.ui.Turnier;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;
import org.json.JSONObject;

import de.pbma.moa.airhockey.ClientActivity;
import de.pbma.moa.airhockey.MqttMessaging;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentLobbyTurnierBinding;


public class LobbyTurnierFragment extends Fragment {
    FragmentLobbyTurnierBinding binding;
    MqttMessaging client;
    NavController navController;

    MqttConnectOptions options;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";

    String tournamentCode;
    String myUUID;
    String targetTopic;

    MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {

        @Override
        public void onMessage(String topic, String msg) throws JSONException {
            if (navController.getCurrentDestination().getId() == R.id.lobbyTurnierFragment) {
                if (getContext() != null) {
                    if (topic.equals(getString(R.string.MqTTTurnierMessagePlayers, tournamentCode))) {
                        String messageType = jsonEntangler(msg);
                        if (messageType.equals("gameStart")){
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showGamesStart();
                                }
                            });
                        }else if(messageType.equals("checkPresence")){
                           client.send(targetTopic, "ACK", 2);
                        }
                    }
                }
            }
        }
    };

    MqttMessaging.FailureListener failureListener = new MqttMessaging.FailureListener() {
        @Override
        public void onConnectionError(Throwable throwable) {
            Log.v("INFO", throwable.getMessage().toString());

        }

        @Override
        public void onMessageError(Throwable throwable, String msg) {
            Log.v("INFO", msg);
            Log.v("INFO", throwable.getMessage().toString());

        }

        @Override
        public void onSubscriptionError(Throwable throwable, String topic) {
            Log.v("INFO", topic);
            Log.v("INFO", throwable.getMessage().toString());
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLobbyTurnierBinding.inflate(inflater, container, false);
        tournamentCode = LobbyTurnierFragmentArgs.fromBundle(getArguments()).getTournamentCode();
        Log.v("INFO", getString(R.string.MqTTTurnierMessagePlayers, tournamentCode));
        client = new MqttMessaging(failureListener, messageListener);
        options = new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        client.connect(broker, options);
        if (myUUID == null) {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("PlayerInfo", Context.MODE_PRIVATE);
            myUUID = sharedPreferences.getString("UUID", null);
        }
        client.subscribe(getString(R.string.MqTTTurnierMessagePlayers, tournamentCode));

        client.send(getString(R.string.MqTTTurnierLobbyStarted,tournamentCode,myUUID),"Started",2);
        navController= NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    private String jsonEntangler(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            String player1 = jsonObject.getString("player1");
            String player2 = jsonObject.getString("player2");
            String messageType = jsonObject.getString("messageInfo");
            if (myUUID.equals(player1)) {
                targetTopic = getString(R.string.MqTTTurnierJoinPlayer1Ack, tournamentCode);
                return messageType;
            }
            if (myUUID.equals(player2)) {
                targetTopic = getString(R.string.MqTTTurnierJoinPlayer2Ack, tournamentCode);
                return messageType;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void showGamesStart() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Wollen sie am Spiel teilnehmen?");
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client.send(targetTopic,myUUID,2);
                Intent intent=new Intent(requireContext(), ClientActivity.class);
                intent.putExtra("LobbyCode",tournamentCode);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        builder.create();
        builder.show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }
}