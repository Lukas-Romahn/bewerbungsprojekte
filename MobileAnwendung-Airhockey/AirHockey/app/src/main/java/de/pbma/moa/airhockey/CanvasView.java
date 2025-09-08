package de.pbma.moa.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;
import android.os.Handler;
import android.view.Window;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.pbma.moa.airhockey.PositionPreprocess.PreProcess;

public class CanvasView extends View {
    private static final float PADDLE_RADIUS = 120f; // Radius der Schläger
    private static final float PUCK_RADIUS = 50f; // Radius des Pucks

    private float puckX, puckY; // Position des Pucks
    private float puckDX = 10f, puckDY = 10f; // Geschwindigkeit des Pucks
    private float playerX, playerY; // Position des Spieler-Schlägers
    private float baseX, baseY;
    private float opponentX, opponentY; // Position des Gegner-Schlägers
    private MutableLiveData<Integer> playerScore = new MutableLiveData<Integer>(0) {};
    private MutableLiveData<Integer> opponentScore = new MutableLiveData<Integer>(0) {}; // Score für die Spieler
    PreProcess preProcess;
    PreProcess preProcessPlayer2;
    private long lastTimeFrame;
    private Paint paint;
    private final Coordinates playerCoordinates = new Coordinates(500,1200,0);
    private final Coordinates player2Coordinates = new Coordinates(500,300,0);
    // Variablen für Bot-Movement
    private Random random;
    private float time;
    private boolean botLobby;
    private Paint darkOverlay;

    //Variablen für Countdown
    private boolean isCountingDown = false;
    private int countdownTime;
    private Handler handler = new Handler(Looper.getMainLooper()); // Handler der den Main Loop bekommt
    private boolean paused=false;

    private boolean isPuckPaused = false;

    public CanvasView(Context context, String lobbyID, String playerID, String player2ID) {
        super(context);

        //Initialisieren von random
        random = new Random();

        // Initialisieren der Positionen
        puckX = 0f;
        puckY = 0f;

        playerX = 500f;
        playerY = 1200f;
        synchronized (playerCoordinates){

            preProcess = new PreProcess(context, lobbyID, playerID, playerCoordinates, false);

        }

        opponentX = 500f;
        opponentY = 300f;
        synchronized (player2Coordinates){

            preProcessPlayer2 = new PreProcess(context, lobbyID, player2ID, player2Coordinates, true);

        }
        lastTimeFrame = System.currentTimeMillis();

        // Initialisieren des Paint-Objekts
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Additional debugging logs for onDraw
        Log.d("CanvasDraw", String.format("Drawing Player: X=%.2f, Y=%.2f, Opponent: X=%.2f, Y=%.2f",
                playerX, playerY, opponentX, opponentY));

        // Spielfeld zeichnen
        drawField(canvas);

        // Puck zeichnen
        paint.setColor(Color.RED);
        canvas.drawCircle(puckX, puckY, PUCK_RADIUS, paint);

        // Spieler-Schläger zeichnen
        paint.setColor(Color.BLUE);
        canvas.drawCircle(playerX, playerY, PADDLE_RADIUS, paint);

        // Gegner-Schläger zeichnen
        paint.setColor(Color.GREEN);
        canvas.drawCircle(opponentX, opponentY, PADDLE_RADIUS, paint);

        // Punktanzahl
        paint.setColor(Color.BLACK);
        paint.setTextSize(80);

        // Berechne die Breite des Textes für die Punktzahl
        float playerScoreWidth = paint.measureText("" + playerScore.getValue());
        float opponentScoreWidth = paint.measureText("" + opponentScore.getValue());

        // Setze den Text so, dass er nicht über den rechten Bildschirmrand hinausgeht
        float playerScoreX = getWidth() - playerScoreWidth - 20;  // 20px Abstand zum rechten Rand
        float opponentScoreX = getWidth() - opponentScoreWidth - 20;  // 20px Abstand zum rechten Rand

        // Zeichne die Punktzahlen
        canvas.drawText("" + playerScore.getValue(), playerScoreX, getHeight() / 2 + 80, paint);  // Punktzahl des Spielers
        canvas.drawText("" + opponentScore.getValue(), opponentScoreX, getHeight() / 2 - 20, paint);  // Punktzahl des Gegners

        // Countdown-Anzeige
        if (isCountingDown) {
            paint.setTextSize(500);
            paint.setColor(Color.BLACK);
            String countdownText = String.valueOf(countdownTime);
            canvas.drawText(countdownText, getWidth() / 2 - 150, getHeight() / 2 - 200, paint);  // Anzeige der verbleibenden Zeit
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Verhindern der Eingabe, während des Countdowns
        if(isCountingDown){
            return false;
        }

        // Berechne den Abstand zwischen dem Touchpunkt und dem Zentrum des Schlägers
        float touchX = event.getX();
        float touchY = event.getY();
        float paddleDistance = (float) Math.sqrt(Math.pow(touchX - playerX, 2) + Math.pow(touchY - playerY, 2));

        // Wenn der Touch innerhalb des Schlägers liegt (Radius des Schlägers)
        if (paddleDistance <= PADDLE_RADIUS) {
            // Bewege den Spieler-Schläger mit dem Touch
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                playerX = touchX;
                playerY = touchY;

                // Beschränkung des Schlägers von der Mittellinie
                if (playerY - PADDLE_RADIUS < getHeight() / 2) {
                    playerY = getHeight() / 2 + PADDLE_RADIUS;
                }

                invalidate(); // View neu zeichnen
            }
        }
        return true;
    }
    public void player1move(Coordinates coordinates){
        long currentTimeFrame = System.currentTimeMillis();
        float deltaTime = (currentTimeFrame - lastTimeFrame)/1000f;

            playerX= playerX +  deltaTime * coordinates.x;
            playerY= playerY + deltaTime * coordinates.y;

            if (playerX < 0) playerX = 0;
            if (playerX > getWidth()) playerX = getWidth();
            if (playerY - PADDLE_RADIUS < getHeight() / 2) playerY = getHeight() / 2 +PADDLE_RADIUS;
            if (playerY + PADDLE_RADIUS > getHeight()) playerY = getHeight() - PADDLE_RADIUS;

        // Log player movement for debugging
        Log.d("Player1Move", String.format("Player1 Pos: X=%.2f, Y=%.2f, Delta: X=%.2f, Y=%.2f",
                playerX, playerY, coordinates.x, coordinates.y));

        lastTimeFrame = currentTimeFrame;
        invalidate();
    }
    public void player2move(Coordinates coordinates){

        long currentTimeFrame = System.currentTimeMillis();
        float deltaTime = (currentTimeFrame - lastTimeFrame)/1000f;

        opponentX= opponentX +  deltaTime * coordinates.x;
        opponentY= opponentY + deltaTime *coordinates.y;

        if (opponentX < 0) opponentX = 0;
        if (opponentX > getWidth()) opponentX = getWidth();
        if (opponentY + PADDLE_RADIUS < getHeight() / 2) opponentY = getHeight() / 2 - PADDLE_RADIUS;
        if (opponentY < 0) opponentY = 0 + PADDLE_RADIUS;

    // Log opponent movement for debugging
        Log.d("Player2Move", String.format("Player2 Pos: X=%.2f, Y=%.2f, Delta: X=%.2f, Y=%.2f",
                opponentX, opponentY, coordinates.x, coordinates.y));
    lastTimeFrame = currentTimeFrame;

        invalidate();
    }
    public void showAlertAndHalt(){


        invalidate();
        paused=true;



    }
    public void closedAlertandContinue(){
        paused=false;
        invalidate();
    }

    public void update() {
        //Überprüfen ob Countdown läuft
        if(paused){
            return;
        }

        if(!isPuckPaused){
            //Bewege den Puck
//            puckX= (float) (puckX+(puckDX*0.70));
//            puckY=(float) (puckY+(puckDY*0.70));
            //puckX += puckDX;
            //puckY += puckDY;
             //Kollision mit den Schlägern
                    if (Math.pow(puckX - playerX, 2) + Math.pow(puckY - playerY, 2) < Math.pow(PADDLE_RADIUS + PUCK_RADIUS, 2)) {
                        puckDY = (puckY - playerY) / 4;
                        puckDX = (puckX - playerX) / 10;
                    }
                    if (Math.pow(puckX - opponentX, 2) + Math.pow(puckY - opponentY, 2) < Math.pow(PADDLE_RADIUS + PUCK_RADIUS, 2)) {
                        puckDY = (puckY - opponentY) / 4;
                        puckDX = (puckX - opponentX) / 10;
                    }
        }



        //PLayer bewegen
        synchronized (playerCoordinates){

            if(playerCoordinates.x + PADDLE_RADIUS> getWidth()){
                playerCoordinates.x= getWidth() - PADDLE_RADIUS;

            }else if(playerCoordinates.x - PADDLE_RADIUS < 0){
                playerCoordinates.x = 0 + PADDLE_RADIUS;
            }

            if(playerCoordinates.y - PADDLE_RADIUS < getHeight()/2){
                playerCoordinates.y = getHeight() / 2 + PADDLE_RADIUS;
            }else if(playerCoordinates.y + PADDLE_RADIUS > getHeight()){
                playerCoordinates.y = getHeight() - PADDLE_RADIUS;
            }

            playerX =  playerCoordinates.x;
            playerY = playerCoordinates.y;
        }

    //    Log.v("real player position", String.format("X: %f Y:%f", playerX, playerY));

        // Kollision mit den Wänden des Spielfelds
        if (puckX - PUCK_RADIUS < 0) {
            puckDX = -puckDX + 1;
        }
        if(puckX + PUCK_RADIUS > getWidth()){
            puckDX = -puckDX - 1;
        }
        if (puckY - PUCK_RADIUS < 0) {
            puckDY = -puckDY + 1 ;
        }
        if(puckY + PUCK_RADIUS > getHeight()){
            puckDY = -puckDY - 1;
        }

//

        // Überprüfe, ob der Puck das obere Tor berührt
        if (puckY - PUCK_RADIUS < 20 && puckX > getWidth() / 4 && puckX < getWidth() / 4 + 2 * (getWidth() / 4)) {
            // Tor erzielt oben
         //   Log.d("AirHockey", "Tor oben!");
            incrementPlayerScore();
            resetPuck();  // Setze den Puck zurück
        }

        // Überprüfe, ob der Puck das untere Tor berührt
        if (puckY + PUCK_RADIUS > getHeight() - 20 && puckX > getWidth() / 4 && puckX < getWidth() / 4 + 2 * (getWidth() / 4)) {
            // Tor erzielt unten
           // Log.d("AirHockey", "Tor unten!");
            incrementOpponentScore();
            resetPuck();  // Setze den Puck zurück
        }

        // Update der Position des Gegner-Schlägers (AI)
        if(botLobby) {
            //moveOpponent();
        }
        else{
            synchronized (player2Coordinates){

                if(player2Coordinates.x - PADDLE_RADIUS > getWidth()){
                    player2Coordinates.x= getWidth() + PADDLE_RADIUS;

                }else if(player2Coordinates.x - PADDLE_RADIUS< 0){
                    player2Coordinates.x = 0 + PADDLE_RADIUS;
                }

                if(player2Coordinates.y - PADDLE_RADIUS > getHeight() / 2){
                    player2Coordinates.y = getHeight() / 2 + PADDLE_RADIUS;
                }else if(player2Coordinates.y-PADDLE_RADIUS < 0){
                    player2Coordinates.y = 0 + PADDLE_RADIUS;
                }
//ToDO hier ist meine Kleine Änderung um das SPiel Langsamer zumachen
                opponentX =  (float) (player2Coordinates.x*0.80);
                opponentY = (float) (player2Coordinates.y*0.80);
            }
        }
        // Fordere ein Neuzeichnen der View an
        invalidate();
    }

    private void resetPuck() {
        // Setze den Puck zurück in die Mitte
        isPuckPaused = true;
        puckX = getWidth() / 2;
        puckY = getHeight() / 2;
        puckDX = 0f;
        puckDY = 0f;

        isCountingDown = true;
        countdownTime = 3;

        // Starte den Countdown mit einem Handler
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Countdown um eine Sekunde verringern
                countdownTime--;

                // Wenn der Countdown abgelaufen ist, stoppe ihn
                if (countdownTime <= 0) {
                    isCountingDown = false;
                    isPuckPaused = false;
                    startPuckMovement();  // Starte den Puck wieder
                } else {
                    // Andernfalls den Countdown weiter anzeigen
                    handler.postDelayed(this, 1000); // Wiederhole alle 1 Sekunde
                }

                invalidate();  // View neu zeichnen, um den Countdown anzuzeigen
            }
        }, 1000);  // Initiale Verzögerung von 1 Sekunde
    }

    private void startPuckMovement() {
        // Starte die Puck-Bewegung mit einer zufälligen Geschwindigkeit
        puckDX = getRandomStart();
        puckDY = getRandomStart();

        // Fordere ein Neuzeichnen der View an
        invalidate();
    }

    // Verwende die Sinusfunktion, um die vertikale Bewegung des Gegners zu berechnen
    private void moveOpponent() {
        time += 0.05f; // Zeit für die Sinusbewegung erhöhen, damit der Gegner sich bewegt

        // Berechne den neuen y-Wert des Gegners entlang der Sinuskurve
        float amplitude = 150f; // Amplitude der Bewegung (maximale vertikale Entfernung)
        float centerY = getHeight() / 7f; // Mittelpunkt der Bewegung
        opponentY = centerY + amplitude * (float) Math.sin(time); // Sinusbewegung
        // Bewege den Gegner in der x-Richtung, basierend auf der Puck-Position, aber nicht perfekt
        float randomOffset = getRandomFloat(-5f, 5f);  // Zufälliger Offset für die Bewegung in x-Richtung
        opponentX += (puckX - opponentX) * 0.05f + randomOffset;  // Bewege den Gegner
    }

    // Hilfsmethode, um eine zufällige Zahl zwischen min und max zu generieren
    private float getRandomFloat(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }

    //Hilfsmethode für den Start des Pucks
    private float getRandomStart() {
        boolean isNegativeRange = random.nextBoolean();

        float randomFloat;
        if (isNegativeRange) {
            // Zufallszahl im Bereich [-15, -10]
            randomFloat = -10 + (random.nextFloat() * 8);  // 8 ist der Bereich (max - min)
        } else {
            // Zufallszahl im Bereich [10, 15]
            randomFloat = 10 + (random.nextFloat() * 8);  // 8 ist der Bereich (max - min)
        }

        return randomFloat;
    }

    //LiveData Funktionen
    public LiveData<Integer> getPlayerScore() {
        return playerScore;
    }

    public LiveData<Integer> getOpponentScore() {
        return opponentScore;
    }
    public void incrementPlayerScore() {
        playerScore.setValue(playerScore.getValue() + 1);
    }

    public void incrementOpponentScore() {
        opponentScore.setValue(opponentScore.getValue() + 1);
    }

    private void drawField(Canvas canvas) {


        paint.setColor(Color.WHITE);
        // Spielfeld
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        // Mittellinie
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawLine(0, getHeight() / 2, getWidth() , getHeight() / 2, paint);

        // Tore
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(40);
        canvas.drawLine(getWidth() / 4, 20, getWidth() / 4 + (2*(getWidth()/4)), 20, paint); // Oberes Tor
        canvas.drawLine(getWidth() / 4, getHeight() - 20 , getWidth() / 4 + (2 * (getWidth()/4)), getHeight() - 20, paint); // Unteres Tor
    }

    // Diese Methode wird regelmäßig aufgerufen, um das Spiel zu aktualisieren
    public void startGame() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                update(); // Aktualisiere das Spiel
                startGame(); // Starte den nächsten Frame
            }
        }, 5); // FPS des Games
    }

    public void initializeGame(boolean botLobby){
        Log.v("initilazie", "Game wurde initialisiert!");
        this.botLobby=botLobby;
        puckX = getWidth()/2;
        puckY = getHeight()/2;

        playerX = getWidth()/ 2;
        playerY = getHeight() - 200f;

        synchronized (playerCoordinates){
            playerCoordinates.x = playerX;
            playerCoordinates.y = playerY;
            playerCoordinates.timeStamp = System.currentTimeMillis();
        }
        opponentX = getWidth()/2;
        opponentY = 200f;

        synchronized(player2Coordinates){
            player2Coordinates.x = opponentX;
            player2Coordinates.y = opponentY;
            player2Coordinates.timeStamp = System.currentTimeMillis();
        }

        preProcess.process();
        preProcessPlayer2.process();
    }

    private float interpolate(float value1, float value2, float t){
        return value1 + (value2 - value1) * t;
    }
}