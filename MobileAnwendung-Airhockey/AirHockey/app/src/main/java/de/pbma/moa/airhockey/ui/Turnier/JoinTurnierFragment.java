package de.pbma.moa.airhockey.ui.Turnier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

import de.pbma.moa.airhockey.Coordinates;
import de.pbma.moa.airhockey.MqttMessaging;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentJoinTurnierBinding;
import de.pbma.moa.airhockey.databinding.FragmentLobbyJoinBinding;
import de.pbma.moa.airhockey.databinding.MqttClientLayoutBinding;


public class JoinTurnierFragment extends Fragment {
    MqttMessaging client;

    MqttConnectOptions options;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";

    String targetTopic;
    String myUUID;

    String userNamePlayer;
    String pseudotournamentCode;
    String tournamentCode;
    String inputBuffer;
    private NavController navController;
    MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {
        @SuppressLint("SuspiciousIndentation")
        @Override
        public void onMessage(String topic, String msg) {
            if (topic.equals(getString(R.string.MqTTTurnierJoinAck, pseudotournamentCode, myUUID)))
                tournamentCode = msg;
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (navController.getCurrentDestination().getId() == R.id.joinTurnierFragment) {
                        JoinTurnierFragmentDirections.ActionJoinTurnierFragmentToLobbyTurnierFragment action =
                                JoinTurnierFragmentDirections
                                        .actionJoinTurnierFragmentToLobbyTurnierFragment(tournamentCode);

                        navController.navigate(action);
                    }
                }
            });
        }
    };

//    private FragmentJoinTurnierBinding binding;
    private FragmentLobbyJoinBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


//        binding= FragmentJoinTurnierBinding.inflate(inflater,container,false);
        binding = FragmentLobbyJoinBinding.inflate(inflater, container, false);
        SharedPreferences preferences= requireActivity().getSharedPreferences("PlayerInfo", Context.MODE_PRIVATE);
        myUUID = preferences.getString("UUID", "");
        userNamePlayer = preferences.getString("Username", "Max");
        options = new MqttConnectOptions();
        options.setUserName(userName);
        options.setPassword(password.toCharArray());
        Log.v("info", myUUID);
        client = new MqttMessaging(null, messageListener);
        client.connect(broker, options);
        navController=NavHostFragment.findNavController(this);

        binding.btnConfirmLobbyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etLobbyCode.getText().toString().isEmpty()) {
                    return;
                }

                pseudotournamentCode = binding.etLobbyCode.getText().toString();
                mqttshit();

            }
        });

        binding.btnQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLobbyCodeByQrCode();

            }
        });

        return binding.getRoot();

    }

    private void getLobbyCodeByQrCode() {
        GmsBarcodeScannerOptions scannerOptionsoptions = new GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC)
                .enableAutoZoom()
                .build();

        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(requireContext(),scannerOptionsoptions);

        scanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {

                            pseudotournamentCode =barcode.getRawValue();
                            Log.v("Myinfo", pseudotournamentCode);
                            binding.etLobbyCode.setText(barcode.getRawValue());

                        }
                )
                .addOnFailureListener(
                        e -> {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Kameramodul kann nicht gefunden werden", Toast.LENGTH_SHORT).show();

                        }
                );
    }

    private void mqttshit(){
        // muss überprüft werden ob der Client verbunden ist (anderer MqTTClient benutzen)
        MqttConnectOptions options = new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        client = new MqttMessaging(null, messageListener);

        client.connect(broker, options);

        client.send(getString(R.string.MqTTTurnierJoin, pseudotournamentCode),myUUID+ "/"+ userNamePlayer, 2);
        client.subscribe(getString(R.string.MqTTTurnierJoinAck,pseudotournamentCode,myUUID));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }
}