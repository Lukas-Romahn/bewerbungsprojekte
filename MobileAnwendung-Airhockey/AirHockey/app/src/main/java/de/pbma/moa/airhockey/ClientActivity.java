package de.pbma.moa.airhockey;

import static android.content.Intent.getIntent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.RoomSQLiteQuery;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import de.pbma.moa.airhockey.databinding.MqttClientLayoutBinding;

public class ClientActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    Sensor accelerationSensor;
    Sensor gyroSensor;
    String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    String password = "34df9347";
    String userName = "24moagc";
    String topicMap = "24moagc/";
    String lobbyCode;
    MqttMessaging client1;
    MqttMessaging client2;
    String myUUID;
    MqttClientLayoutBinding binding;
    Intent intent;
    int playStyle;
    private long lastTimestamp = 0;
    private float posX = 0, posY = 0;
    private float velX = 0, velY = 0;
    private int counter;
    float[] FilterX = new float[4];
    float[] FilterY = new float[4];

    private static final int WINDOW_SIZE = 5;
    private LinkedList<Float> accelXBuffer = new LinkedList<>();
    private LinkedList<Float> accelYBuffer = new LinkedList<>();
    private static final float MOVEMENT_THRESHOLD = 0.1f;
    private static final float SMOOTH_FACTOR = 0.8f;
    private int sensorCounter;
    private Coordinates lastSendCoordinates = new Coordinates();
    private float lastMovedX = 0.2f;
    private float lastMovedY = 0.2f;

    boolean isTurnierFinished = false;
    float[] gravity = null;
    float[] geomagnetic = null;

    private boolean active;
    AlertDialog dialog;
    private ExecutorService mqttExecutor;
    private SharedPreferences sharedPrefernces;
    private int useTiltControl;
    private MediaPlayer mediaPlayer;
    //Change Sensor.TYPE_ACCELEROMETER with TYPE_LINEAR_ACCELERATION
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;
        sensorCounter++;
        String mqttTopic = getString(R.string.MqTTLobbyUserData, lobbyCode, myUUID);
        if (useTiltControl == 0 && event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            float[] rotationVector = event.values; //Die Quaternion-Daten des Sensors
            float[] rotationMatrix = new float[9]; //Platz für die Rotationsmatrix
            float[] orientationAngles = new float[3]; //Platz für dei Orientierung

            SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            float pitch = (float) Math.toDegrees(orientationAngles[1]); //Neigung nach vorne/hinten
            float roll = (float) Math.toDegrees(orientationAngles[2]); //Neigung nach links/rechts

            Coordinates tiltData = new Coordinates(0.15f * roll, 0.15f * pitch, event.timestamp); //X=Roll, Y=Pitch
            tiltData = smoothData(tiltData);

            if (isSignificantMovement(tiltData.x, tiltData.y)) {
                Coordinates sendData = PositionCalculate(tiltData);
                client1.send(mqttTopic, createJsonFromData(sendData));
            }
        }
        else if (useTiltControl == 1 && event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Coordinates motionData = new Coordinates(event.values[0] * 1.2f, event.values[1] * 1.4f, event.timestamp);
            motionData = smoothData(motionData);

            if (isSignificantMovement(motionData.x, motionData.y)) {
                Coordinates sendData = PositionCalculate(motionData);
                client1.send(mqttTopic, createJsonFromData(sendData));
            }
        }
        Log.d("Sensorchange", String.format("Counter: %d", sensorCounter));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {

        @Override
        public void onMessage(String topic, String msg) {
            Log.e("mqtt", topic + msg);
            if (topic.equals(getString(R.string.MqTTLobbyStart, lobbyCode))) {
                if (msg.equals("start")) {
                    registerListenter();

                }
            }
            if(topic.equals(getString(R.string.MqTTLobbyLeaveTable,lobbyCode))){
                if(active) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLobbyCloseDialog();

                        }
                    });
                }
            }
            if(topic.equals(getString(R.string.MqTTGameFinished,lobbyCode,myUUID))){
                if(msg.equals("Turnier")){
                    intent.putExtra("wasPlayed","Turnier");
                    intent.putExtra("tournamentCode",lobbyCode);
                    intent.putExtra("playerType", "Client");
                }
                isTurnierFinished = true;
                startActivity(intent);
                finish();
            }
            if(topic.equals(getString(R.string.MqTTGameInfo,lobbyCode,myUUID))){

                Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if(vibrator!=null){
                    if(jsonEntangler(msg)){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        // For Android Oreo and higher
                        VibrationEffect effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(effect);
                        mediaPlayer.start();
                    } else {
                        // For older Android versions
                        vibrator.vibrate(500);
                        mediaPlayer.start();
                    }
                }}



            }

            /*if(topic.equals(getString(R.string.MqTTLobbyLeavePlayerFromTable,lobbyCode))) { //TODO useless gerade das sendet niemand

                if (active) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showPlayerLeftDialog();

                        }
                    });
                }
            }*/
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MqttClientLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        counter = 0;
        Intent getintent = getIntent();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        myUUID = getSharedPreferences("PlayerInfo", MODE_PRIVATE).getString("UUID", "null");
        playStyle=getSharedPreferences("PlayerInfo",MODE_PRIVATE).getInt("PlayStyle",0);
        MqttConnectOptions options = new MqttConnectOptions();
        client1 = new MqttMessaging(failureListener, messageListener);
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        intent= new Intent(this,LobbyActivity.class);
        registerListenter();
        sharedPrefernces = getSharedPreferences("PlayerInfo", MODE_PRIVATE);
        useTiltControl = sharedPrefernces.getInt("PlayStyle", 0); //1 Accelo;0 Schwank
        mediaPlayer= MediaPlayer.create(this,R.raw.ding);
        mqttExecutor = Executors.newSingleThreadScheduledExecutor();
        lobbyCode = getintent.getExtras().getString("LobbyCode", null);

        if(lobbyCode == null){
            Intent intent = new Intent(this, LobbyActivity.class);
            Toast.makeText(this, "Es konnte keine Verbindung zum Spiel aufgebaut werden", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }


        client1.connect(broker, options);
        client1.send(getString(R.string.MqTTLobbyStartedACK, lobbyCode, myUUID), "ACK", 2);
        client1.subscribe(getString(R.string.MqTTLobbyStart, lobbyCode));
        client1.subscribe(getString(R.string.MqTTLobbyLeaveTable, lobbyCode));
        client1.subscribe(getString(R.string.MqTTLobbyLeavePlayer, lobbyCode, myUUID));
        client1.subscribe(getString(R.string.MqTTGameFinished,lobbyCode,myUUID));

        client1.subscribe(getString(R.string.MqTTLobbyStart,lobbyCode));
        client1.subscribe(getString(R.string.MqTTLobbyLeaveTable,lobbyCode));






    }


    private String createJsonFromData(Coordinates coordinates) {
        try {
            JSONObject json = new JSONObject();
            JSONObject positionJson = new JSONObject();
            positionJson.put("x", String.valueOf(coordinates.x));
            positionJson.put("y", String.valueOf(coordinates.y));
            positionJson.put("timestamp",String.valueOf(System.currentTimeMillis()));
            json.put("acceleration", positionJson);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not a json object";

    }

    private void registerListenter() {
       // if(useTiltControl){
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //}
        //else{
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //}


    }
    private Coordinates PositionCalculate(Coordinates data){


        float accelx = roundSensorData(data.x);
        float accely = roundSensorData(data.y);

        Log.v("SensorData", String.format("X: %f    Y: %f   Z: %.4f", accelx, accely, data.y));

        long currentTimestamp = data.timeStamp;

        return new Coordinates(accelx, -accely, currentTimestamp);
    }

    private float roundSensorData(float data){
        float result = 0;
        if (Math.abs(data) > 0.5) {
            result = (float) Math.round(data * 100f) / 100f;
        }
        return result;
    }

    private Coordinates removeRotation(float pitch, float roll) {

        pitch = roundSensorData(pitch);
        roll = roundSensorData(roll);
        float accelerationX= (float) (SensorManager.GRAVITY_EARTH * Math.cos(roll));
        float accelerationY = (float) (SensorManager.GRAVITY_EARTH * Math.cos(pitch));
        Log.v("removeRotation", String.format("X: %f, Y: %f", accelerationX, accelerationY));
        Log.v("rotation", String.format("X: %f, Y: %f", roll, pitch));
        return new Coordinates(accelerationX, accelerationY);
    }

    private float computeMovingAverage(LinkedList<Float> buffer, float newValue){
        if (buffer.size() >= WINDOW_SIZE){
            buffer.poll();
        }
        buffer.add(newValue);
        float factor = 0.4f;
        float sum = 0;
        int size = buffer.size();

        for(int i= 0; i < size; i++){
            sum += buffer.get(i) * factor;
            factor *= 1.2f;
        }

        return sum / buffer.size();

    }

    private Coordinates smoothData(Coordinates data){
//        float smoothedX = SMOOTH_FACTOR * computeMovingAverage(accelXBuffer, data.x) + (1-SMOOTH_FACTOR) * data.x;
//        float smoothedY = SMOOTH_FACTOR * computeMovingAverage(accelYBuffer, data.y) + (1-SMOOTH_FACTOR) * data.y;
        float smoothedX = 0.8f * computeMovingAverage(accelXBuffer, data.x) + 0.2f * lastMovedX;
        float smoothedY = 0.8f * computeMovingAverage(accelYBuffer, data.y) + 0.2f * lastMovedY;
        lastMovedX = smoothedX;
        lastMovedY = smoothedY;
        return new Coordinates(smoothedX, smoothedY);
    }

    private boolean isSignificantMovement(float deltaX, float deltaY){
        return Math.abs(deltaX) > MOVEMENT_THRESHOLD || Math.abs(deltaY) > MOVEMENT_THRESHOLD;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isTurnierFinished && !isChangingConfigurations()) {
            client1.send(getString(R.string.MqTTLobbyLeavePlayer, lobbyCode, myUUID), "Leave");
        }

        sensorManager.unregisterListener(this,gyroSensor);
        sensorManager.unregisterListener(this,accelerationSensor);
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
        client1.disconnect();
    }

    private void showLobbyCloseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lobby geschlossen")
                .setMessage("Die Lobby wurde vom Host geschlossen.")
                .setIcon(android.R.drawable.ic_dialog_alert) // Optional: Icon hinzufügen
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aktion bei Klick auf "OK"
                        dialog.dismiss();
                        finish(); // Optional: Activity schließen
                    }
                });

        // Dialog anzeigen
        dialog = builder.create();
        dialog.show();
    }
    private Boolean jsonEntangler(String json) {
        Boolean goal=false;
        try{
            JSONObject jsonObject= new JSONObject(json);
            goal=jsonObject.getBoolean("Goal");
        }catch(JSONException e){
            e.printStackTrace();
        }
        return goal;
    }

    private void showPlayerLeftDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Spieler hat die Lobby verlassen")
                .setMessage("Das Spiel wurde pausiert. Wollen sie das Spiel beenden?")
                .setIcon(android.R.drawable.ic_dialog_alert) // Optional: Icon hinzufügen
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aktion bei Klick auf "OK"
                        dialog.dismiss();
                        finish(); // Optional: Activity schließen


                    }

                });

        // Dialog anzeigen
        active=false;
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
