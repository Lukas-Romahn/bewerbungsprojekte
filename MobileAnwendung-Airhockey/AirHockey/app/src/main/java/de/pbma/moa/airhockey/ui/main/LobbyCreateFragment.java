package de.pbma.moa.airhockey.ui.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;

import java.util.Random;
import java.util.UUID;

import de.pbma.moa.airhockey.TableActivity;
import de.pbma.moa.airhockey.MqttMessaging;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentLobbyCreateBinding;

public class LobbyCreateFragment extends Fragment {

    NavController navController;
    FragmentLobbyCreateBinding binding;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";
    String lobbyId;
    int counter=0;
    String player1;
    String player2;
    String player1Name;
    String player2Name;
    MqttMessaging joinClient;
    MqttMessaging leaveClient;
    MqttMessaging ackClient;
    int lobbyStartedAckCnt;
    boolean botLobby=false;


    public static LobbyCreateFragment newInstance() {
        return new LobbyCreateFragment();
    }


    private MqttMessaging.MessageListener messageListenerJoin = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) {
            if(topic.equals(getString(R.string.MqTTLobbyJoin,lobbyId))){
                if(player1!=null){
                    if(msg.split("/")[0].equals(player1)){
                        return;
                    }else if(msg.split("/")[0].equals(player2)){
                        return;
                    }
                }

                if(counter==0){
                player1=msg.split("/")[0];
                player1Name=msg.split("/")[1];
                }
// hier muss geschaut werden ob die ID unterschiedlich sind  voneinander
                joinClient.send(getString(R.string.MqTTLobbyJoinAck,lobbyId,player1),"ACK",2);
                leaveClient.subscribe(getString(R.string.MqTTLobbyLeavePlayerFromLobby,lobbyId,player1));
                ackClient.subscribe(getString(R.string.MqTTLobbyStartedACK,lobbyId,player1));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvPlayer1.setText(player1Name);

                    }
                });



                if(counter==1){
                    player2=msg.split("/")[0];
                    player2Name=msg.split("/")[1];
                    joinClient.send(getString(R.string.MqTTLobbyJoinAck,lobbyId,player2),"ACK",2);
                    leaveClient.subscribe(getString(R.string.MqTTLobbyLeavePlayerFromLobby,lobbyId,player2));
                    ackClient.subscribe(getString(R.string.MqTTLobbyStartedACK,lobbyId,player2));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvPlayer2.setText(player2Name);


                        }
                    });
                }
                counter++;
            }


        }
    };

    private MqttMessaging.FailureListener failureListenerJoin = new MqttMessaging.FailureListener() {
        @Override
        public void onConnectionError(Throwable throwable) {
        }

        @Override
        public void onMessageError(Throwable throwable, String msg) {

        }

        @Override
        public void onSubscriptionError(Throwable throwable, String topic) {

        }
    };
    private MqttMessaging.MessageListener messageListenerLeave = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) {
            if (topic.equals(getString(R.string.MqTTLobbyLeavePlayerFromLobby, lobbyId, player1))) {

                counter--;
                player1 = null;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvPlayer1.setText("");
                    }
                });


            }
            if (topic.equals(getString(R.string.MqTTLobbyLeavePlayerFromLobby, lobbyId, player2))) {
                counter--;
                player2 = null;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvPlayer2.setText("");
                    }
                });
            }
        }
    };

    private MqttMessaging.FailureListener failureListenerLeave= new MqttMessaging.FailureListener() {
        @Override
        public void onConnectionError(Throwable throwable) {
        }

        @Override
        public void onMessageError(Throwable throwable, String msg) {

        }

        @Override
        public void onSubscriptionError(Throwable throwable, String topic) {

        }
    };

    private MqttMessaging.MessageListener messageListenerLobby = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) throws JSONException {
            if(topic.equals(getString(R.string.MqTTLobbyStartedACK,lobbyId,player1))){
                lobbyStartedAckCnt++;

            }
            if(topic.equals(getString(R.string.MqTTLobbyStartedACK,lobbyId,player2))){
                lobbyStartedAckCnt++;

            }
            if(botLobby) {

                if (lobbyStartedAckCnt == 1) {
                    startTable(botLobby);
                }

            }else{
                if (lobbyStartedAckCnt == 2) {
                    startTable(botLobby);
                }
            }
        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentLobbyCreateBinding.inflate(inflater,container,false);
        navController= Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);

        String lobbyId=createLobby();
        binding.ivQR.setImageBitmap(generateQR(lobbyId));
        binding.tvLobbyCode.setText(lobbyId);




        binding.btnBestaetigen.setOnClickListener(v -> {
            LobbyStartDialog();

        });

        return binding.getRoot();
    }





    private String createLobby(){
        Random random= new Random();
        lobbyId= String.valueOf(random.nextInt(1000));

         try{
             MqttConnectOptions options = new MqttConnectOptions();
             options.setPassword(password.toCharArray());
             options.setUserName(userName);
             joinClient = new MqttMessaging(failureListenerJoin,messageListenerJoin);
             joinClient.connect(broker,options);
             joinClient.subscribe(getString(R.string.MqTTLobbyJoin,lobbyId));
             leaveClient= new MqttMessaging(failureListenerLeave,messageListenerLeave);
             leaveClient.connect(broker,options);
             ackClient= new MqttMessaging(failureListenerJoin,messageListenerLobby);
             ackClient.connect(broker,options);
         }catch (Exception e){

         }
        return lobbyId;
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
        if(joinClient.isConnected()){
            joinClient.unsubscribe(getString(R.string.MqTTLobbyJoin,lobbyId));
            joinClient.disconnect();
        }
        if(leaveClient.isConnected()){
            leaveClient.unsubscribe(getString(R.string.MqTTLobbyLeavePlayerFromLobby,lobbyId,player1));
            leaveClient.unsubscribe(getString(R.string.MqTTLobbyLeavePlayerFromLobby,lobbyId,player2));
            leaveClient.disconnect();

        }if(ackClient.isConnected()){
            ackClient.unsubscribe(getString(R.string.MqTTLobbyJoinAck,lobbyId,player1));
            ackClient.unsubscribe(getString(R.string.MqTTLobbyJoinAck,lobbyId,player2));
            ackClient.unsubscribe(getString(R.string.MqTTLobbyStartedACK,lobbyId,player1));
            ackClient.unsubscribe(getString(R.string.MqTTLobbyStartedACK,lobbyId,player2));
            ackClient.disconnect();

        }




    }
    private void startTable(boolean botLobby) {

        leaveClient.unsubscribe(getString(R.string.MqTTLobbyLeavePlayerFromLobby, lobbyId, player1));
        leaveClient.unsubscribe(getString(R.string.MqTTLobbyLeavePlayerFromLobby, lobbyId, player2));
        Intent intent = new Intent(requireActivity(), TableActivity.class);
        intent.putExtra("LobbyCode", lobbyId);
        intent.putExtra("LobbyType", botLobby);
        intent.putExtra("player1", player1 + "/" +player1Name);
        intent.putExtra("player2", player2 + "/" + player2Name);
        startActivity(intent);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navController.navigateUp();

            }
        });

    }


    private void LobbyStartDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
        builder.setTitle("Wollen sie Fortfahren");
        builder.setNegativeButton("Abbrechen",new DialogInterface.OnClickListener(){


            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
                if(player1==null){
                    builder.setMessage("Die Lobby ist Leer sie Können erst starten wenn mindestens\n eine Person sich in der Lobby befindet");
                }else if(player2==null){
                    builder.setMessage("Die Lobby ist noch nicht voll falls sie Bestätigen\n werden sie gegen einen Bot spielen");
                    builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
                       public void onClick(DialogInterface dialog, int which){
                           dialog.dismiss();
                           // Lobby starten ohne zweiten Spieler
                            botLobby=true;
                           ackClient.send(getString(R.string.MqTTLobbyStart,lobbyId),"start", 2);
                       }
                    });
                }
                else {
                    builder.setMessage("Die Lobby ist voll wollen sie das Spiel starten");
                    builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            botLobby=false;
                            ackClient.send(getString(R.string.MqTTLobbyStart,lobbyId),"start",2);
                        }
                    });
                }
                builder.create();
        builder.show();


    }
}