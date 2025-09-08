package de.pbma.moa.airhockey.ui.main;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.content.pm.ActivityInfo;
import android.content.pm.ModuleInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.OptionalModuleApi;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;

import com.google.android.gms.tflite.java.TfLite;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureActivity;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import de.pbma.moa.airhockey.ClientActivity;
import de.pbma.moa.airhockey.MqttMessaging;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentLobbyJoinBinding;

public class LobbyJoinFragment extends Fragment {

    FragmentLobbyJoinBinding binding;
    NavController navController;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";
    MqttMessaging client1;
    String userNamePlayer;
    String lobbyId;
    String myUUID;
    boolean joined=false;
    boolean blockLeaveSignal = false;

   ActivityResultLauncher<Intent> barcodeScanLauncher;
    boolean isInstalled;

    private MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) {

            if (getContext() != null) {
                if (topic.equals(getString(R.string.MqTTLobbyStart, lobbyId))) {
                    Intent intent = new Intent(requireContext(), ClientActivity.class);
                    intent.putExtra("LobbyCode", lobbyId);
                    blockLeaveSignal = true;
                    startActivity(intent);
                }
            }

        }
    };

    private MqttMessaging.FailureListener failureListener = new MqttMessaging.FailureListener() {
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



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("PlayerInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        isInstalled = true;
        if(preferences.getString("UUID","null").equals("null")){
            myUUID = UUID.randomUUID().toString();

            editor.putString("UUID", myUUID);
            editor.apply();

        }else{
            myUUID=preferences.getString("UUID","null");
        }
        userNamePlayer=preferences.getString("Username","0");



        MqttConnectOptions options = new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        client1 = new MqttMessaging(failureListener, messageListener);
        client1.connect(broker, options);
        userNamePlayer=preferences.getString("Username","0");
        binding=FragmentLobbyJoinBinding.inflate(getLayoutInflater());
        navController= Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        binding.btnQRCode.setOnClickListener(v -> {
            getLobbyCodeByQrCode();
        });

        binding.btnConfirmLobbyCode.setOnClickListener(v -> {

            if(binding.etLobbyCode.getText().toString().isEmpty()) {
                return;
            }

            lobbyId=binding.etLobbyCode.getText().toString();
            mqttshit();
        });

        return binding.getRoot();
    }

    private void getLobbyCodeByQrCode() {
        blockLeaveSignal = true;
        GmsBarcodeScannerOptions scannerOptionsoptions = new GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC)
                .enableAutoZoom()
                .build();

        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(requireContext(), scannerOptionsoptions);
        scanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            lobbyId = barcode.getRawValue();
                            binding.etLobbyCode.setText(String.valueOf(lobbyId));
                            blockLeaveSignal = false;
                        }
                )
                .addOnFailureListener(
                        e -> {
                            e.printStackTrace();
                        }
                );
    }

    private void mqttshit(){

        MqttConnectOptions options = new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        client1 = new MqttMessaging(failureListener, messageListener);

        client1.connect(broker, options);

        client1.send(getString(R.string.MqTTLobbyJoin,lobbyId),myUUID+ "/"+ userNamePlayer,2);
        client1.subscribe(getString(R.string.MqTTLobbyJoinAck,lobbyId,myUUID));
        client1.subscribe(getString(R.string.MqTTLobbyStart,lobbyId));
    }

    @Override
    public void onStop() {
        super.onStop();
        if(client1!=null) {
            if (client1.isConnected()) {
                if(!blockLeaveSignal){
                    client1.send(getString(R.string.MqTTLobbyLeavePlayerFromLobby, lobbyId, myUUID), "Leave", 2);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        joined=false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client1.disconnect();
    }
}