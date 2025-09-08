package de.pbma.moa.airhockey.PositionPreprocess;


import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;
import org.json.JSONObject;


import de.pbma.moa.airhockey.Coordinates;
import de.pbma.moa.airhockey.MqttMessaging;
import de.pbma.moa.airhockey.R;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PreProcess {
    private Coordinates accelerationCoordinates;
    private String broker = "ssl://mqtt.inftech.hs-mannheim.de:8883";
    private MqttMessaging client;

    String password = "34df9347";
    String userName = "24moagc";
    Context context;
    String lobbyID;
    String playerID;
    private boolean invert = false;
    private float friction = 0.1f;
    private boolean motionDetected = false;
    private Coordinates speed;
    public Boolean playerLeft = false;
    private final Coordinates target;
    private long lastTimestamp;
    private Executor executor;
    private Coordinates[] detectionBuffer = new Coordinates[10];
    private Coordinates[] bufferSums = new Coordinates[detectionBuffer.length];
    private long lastNanoTime = 0;
    private float maxSpeedX = 1;
    private float maxSpeedY = 1;
    private MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String msg) throws JSONException {
            executor.execute(() -> {
                if (topic.equals(context.getString(R.string.MqTTLobbyUserData, lobbyID, playerID))) {

                    accelerationCoordinates = jsonEntangler(msg);

                    if (accelerationCoordinates.timeStamp == 0) {
                        return;
                    }
                    recalculatePosition();
                }
                if (topic.equals(context.getString(R.string.MqTTLobbyLeavePlayer, lobbyID, playerID))) {
                    playerLeft = true;
                }
            });

        }
    };

    private MqttMessaging.FailureListener failureListener = new MqttMessaging.FailureListener() {
        @Override
        public void onConnectionError(Throwable throwable) {

        }

        @Override
        public void onMessageError(Throwable throwable, String msg) {
            Log.v("MQTT ERROR", msg, throwable);
        }

        @Override
        public void onSubscriptionError(Throwable throwable, String topic) {

        }
    };

    public PreProcess(Context context, String lobbyID, String playerID, Coordinates targetCoordinates, boolean invert) {
        client = new MqttMessaging(failureListener, messageListener);
        this.context = context;
        this.lobbyID = lobbyID;
        this.playerID = playerID;
        this.invert = invert;
        target = targetCoordinates;
        speed = new Coordinates(0, 0, 0);
        executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < detectionBuffer.length; ++i) {
            detectionBuffer[i] = new Coordinates(0, 0);
            if (i < bufferSums.length) {
                bufferSums[i] = new Coordinates(0, 0);
            }
        }
    }

    public void process() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        client.connect(broker, options);
        client.subscribe(context.getString(R.string.MqTTLobbyUserData, lobbyID, playerID));

    }


    private static Coordinates jsonEntangler(String json) {
        Coordinates coordinates = new Coordinates();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObjectAccelerate = jsonObject.getJSONObject("acceleration");
            coordinates.x = (float) jsonObjectAccelerate.getDouble("x");
            coordinates.y = (float) jsonObjectAccelerate.getDouble("y");
            coordinates.timeStamp = Long.parseLong(jsonObjectAccelerate.optString("timestamp", "0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coordinates;

    }

    private void recalculatePosition() {
        long timestamp = accelerationCoordinates.timeStamp;
        float deltaTime = 0;
        if (timestamp == 1) {
            deltaTime = 0.02f;
        } else {
            deltaTime = (timestamp - lastTimestamp) / 1000f;
        }

        if(invert){
            accelerationCoordinates.x = - accelerationCoordinates.x;
            accelerationCoordinates.y = -accelerationCoordinates.y;
        }

//        float deltaTime = calculateDeltaTime();
        boolean frictionAllowed = bufferAcceleration();
        lastTimestamp = timestamp;
//        accelerationCoordinates = smoothAcceleration(accelerationCoordinates, detectionBuffer[0], 0.8f);
        speed.x = Math.min(speed.x + accelerationCoordinates.x * deltaTime, 500f);
        speed.y = Math.min(speed.y + accelerationCoordinates.y * deltaTime, 500f);

        if (frictionAllowed) {
            float speedMagnitude = (float) Math.sqrt(speed.x * speed.x + speed.y * speed.y);
            if (speedMagnitude > 0.01f) {
                float dynamicFriction = calculateDynamicFriction(speedMagnitude);
                speed.x *= (1 - dynamicFriction);
                speed.y *= (1 - dynamicFriction);
            }
        }
//        if (Math.abs(speed.x) > maxSpeedX) {
//            speed.x = maxSpeedX;
//        }
//        if (Math.abs(speed.y) > maxSpeedY) {
//            speed.y = maxSpeedY;
//        }
        if(speed.x>maxSpeedX){
            speed.x=maxSpeedX;
        }if(speed.y>maxSpeedY)
        {
            speed.y=maxSpeedY;
        }

        target.x = (target.x + 1000 * speed.x * deltaTime); //+ accelerationCoordinates.x * deltaTime * deltaTime);
        target.y = (target.y + 1000 * speed.y * deltaTime); //+ accelerationCoordinates.y * deltaTime * deltaTime);
//        target.x =( target.x + speed.x*deltaTime + 0.5f * accelerationCoordinates.x * deltaTime * deltaTime);
//        target.y =( target.y + speed.y*deltaTime + 0.5f * accelerationCoordinates.y * deltaTime * deltaTime);

//        Log.v("speedAndAcclerationY", String.format("target.Y: %f, target.X: %f, accleration.Y: %f, accleration.X: %f", speed.y,speed.x, accelerationCoordinates.y, accelerationCoordinates.x));
//        Log.v("speedAndAcclerationX", String.format("target.X: %f, accleration.X: %f", speed.x, accelerationCoordinates.x));
//        Log.v("targetSpeed", String.format("target.X: %f, target.Y: %f", speed.x, speed.y));
//        Log.v("targetPosition", String.format("target.X: %f, target.Y: %f", target.x, target.y));

        // Log.d("PreProcess", String.format("Acc: (%f, %f), Speed: (%f, %f), Pos: (%f, %f)",
        // accelerationCoordinates.x, accelerationCoordinates.y,
        //speed.x, speed.y, target.x, target.y));
    }

    private boolean bufferAcceleration() {
        Coordinates sum = new Coordinates(0, 0);
        detectionBuffer[0] = accelerationCoordinates;
        for (int i = 0; i < detectionBuffer.length; ++i) {
            sum.x += detectionBuffer[i].x;
            sum.y += detectionBuffer[i].y;

        }
//        accelerationCoordinates.x =accelerationCoordinates.x +  detectionBuffer[1].x * 0.5f + detectionBuffer[2].x * 0.25f + detectionBuffer[3].x * 0.1f;
//        accelerationCoordinates.y =accelerationCoordinates.y +  detectionBuffer[1].y * 0.5f + detectionBuffer[2].y * 0.25f + detectionBuffer[3].y * 0.1f;

//        detectionBuffer[0] = accelerationCoordinates;

        // Log.v("dotProduct", String.format("x: %f, y: %f", accelerationCoordinates.x, accelerationCoordinates.y));
        return Math.abs(sum.x) < 0.5 && Math.abs(sum.y) < 0.5;

    }

    private Coordinates smoothAcceleration(Coordinates rawAcceleration, Coordinates prevAcceleration, float alpha) {
        Coordinates smoothed = new Coordinates(0, 0);
        smoothed.x = alpha * prevAcceleration.x + (1 - alpha) * rawAcceleration.x;
        smoothed.y = alpha * prevAcceleration.y + (1 - alpha) * rawAcceleration.y;
        return smoothed;
    }

    private float calculateDynamicFriction(float speedMagnitude) {
        return Math.min(0.1f + 0.3f * (1 / (1 + speedMagnitude)), 0.4f);
    }

    private void clampSpeed(Coordinates speed, float maxSpeed) {
        float magnitude = (float) Math.sqrt(speed.x * speed.x + speed.y * speed.y);
        if (magnitude > maxSpeed) {
            float scale = maxSpeed / magnitude;
            speed.x *= scale;
            speed.y *= scale;
        }
    }

    private float calculateDeltaTime() {
        long currentNanoTime = System.nanoTime();
        float deltaTime = (currentNanoTime - lastNanoTime) / 1_000_000_000f;
        lastNanoTime = currentNanoTime;
        return deltaTime;
    }

    public void clearBuffer() {
        Coordinates emptycoordinate = new Coordinates(0, 0, 0);
        int counter = 0;
        for (Coordinates coordinates : detectionBuffer) {
            detectionBuffer[counter] = emptycoordinate;
            counter++;
        }
    }
}
