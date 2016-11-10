package com.example.gehrung.beerpongxtreme;

import android.os.Handler;
import android.view.MotionEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//Klasse für die Spiellogik
//Die Unterklassen enthalten lediglich levelspezifische Abfragen (Abprallen von den einzelnen Hindernissen in den Levels, Ventilator etc.)
class GameEngine implements Runnable {

    //onFling-Boolean Variable wird benötigt, um den Ball nur einmalig pro Versuch berühren zu können
    private boolean onFling;

    //Levelübergreifende Zählervariable für die Anzahl der Versuche
    private static int versuche = 0;

    static int getVersuche() {
        return versuche;
    }

    static void resetVersuche() {
        versuche = 0;
    }

    //Koordinaten des Balls
    float ballX, ballY;
    float ballVX, ballVY;

    //Koordinaten der 6-Becher
    private float hole1X, hole1Y;
    private float hole2X, hole2Y;
    private float hole3X, hole3Y;
    private float hole4X, hole4Y;
    private float hole5X, hole5Y;
    private float hole6X, hole6Y;
    //Begrenzung des Spielfeldes
    private float minX, minY, maxX, maxY;

    //Radien der 6-Becher
    private float holeRadius1;
    private float holeRadius2;
    private float holeRadius3;
    private float holeRadius4;
    private float holeRadius5;
    private float holeRadius6;

    //benötigte Konstanten:
    private static final long MS_PER_FRAME = 20;
    //Abprallfaktor am Rand
    static final float BOUNCE_FACTOR = 0.9f;
    //Verlangsamungsfaktor
    private static final float ACCELERATION_FACTOR = 0.985f;

    //Instanzobjekte
    GameView gameView;
    private ScheduledExecutorService service;
    Handler handler;
    OnGameEventListener onGameEventListener;

    //boolean-Abfragen, ob die Löcher getroffen wurden
    private boolean hole1 = false;
    private boolean hole2 = false;
    private boolean hole3 = false;
    private boolean hole4 = false;
    private boolean hole5 = false;
    private boolean hole6 = false;

    //setzt die Spielfeldbegrenzung
    void setRegion(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    //setzt die Postion des Balles
    void setBallPosition(float x, float y) {
        ballX = x;
        ballY = y;
    }

    //positioniert die Löcher
    void setHole1Position(float x, float y, float radius) {
        hole1X = x;
        hole1Y = y;
        holeRadius1 = radius;

    }

    void setHole2Position(float x, float y, float radius) {
        hole2X = x;
        hole2Y = y;
        holeRadius2 = radius;

    }

    void setHole3Position(float x, float y, float radius) {
        hole3X = x;
        hole3Y = y;
        holeRadius3 = radius;

    }

    void setHole4Position(float x, float y, float radius) {
        hole4X = x;
        hole4Y = y;
        holeRadius4 = radius;

    }

    void setHole5Position(float x, float y, float radius) {
        hole5X = x;
        hole5Y = y;
        holeRadius5 = radius;

    }

    void setHole6Position(float x, float y, float radius) {
        hole6X = x;
        hole6Y = y;
        holeRadius6 = radius;

    }

    //startet das Spiel
    void start() {
        //der SingleThreadScheduledExecutor sorgt dafür, dass alle 20 Millisekunden die run()-Funktion ausgeführt wird
        service = Executors.newSingleThreadScheduledExecutor();
        onFling = false;
        ballVX = 0;
        ballVY = 0;
        gameView.setBallPosition(ballX, ballY);
        service.scheduleAtFixedRate(this, MS_PER_FRAME, MS_PER_FRAME, TimeUnit.MILLISECONDS);
        gameView.updateVersuche(versuche);
    }

    //pausieren des Spiels
    void stop() {
        service.shutdownNow();
        handler.removeCallbacks(this);
    }

    //Weiterspielen nach einer Pause
    void resume() {
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this, MS_PER_FRAME, MS_PER_FRAME, TimeUnit.MILLISECONDS);
    }

    //eigentliche Spiellogik
    @Override
    public void run() {

        //alle 20ms wird die Geschwindigkeit des Balles, sowie die Koordinaten neu berechnet
        ballVX = ballVX * ACCELERATION_FACTOR;
        ballVY = ballVY * ACCELERATION_FACTOR;
        ballX += ballVX * MS_PER_FRAME / 1000;
        ballY += ballVY * MS_PER_FRAME / 1000;

        //Die Koordinaten werden der gameView übergeben, damit diese den Ball an der neuen Stelle zeichnen kann
        handler.post(new Runnable() {

            @Override
            public void run() {
                gameView.setBallPosition(ballX, ballY);
            }
        });

        //Wenn der Ball über den oberen bzw unteren Rand fliegt, ist der Versuch vorbei und ein neuer Versuch wird gestartet
        //Wenn der Ball die linke-bzw. rechte Spielbegrenzung überschreitet, prallt der Ball dort ab
        if (ballX < minX) {
            ballX = minX;
            ballVX = -ballVX * BOUNCE_FACTOR;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallBounce();
                }
            });
        }
        if (ballY < minY) {
            stop();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onVersuchFailed();
                }
            });
            return;
        }
        if (ballX > maxX) {
            ballX = maxX;
            ballVX = -ballVX * BOUNCE_FACTOR;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallBounce();
                }
            });
        }
        if (ballY > maxY) {
            stop();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onVersuchFailed();
                }
            });
            return;
        }

        //Abfragen, ob der Ball sich in einem der Löcher befindet
        //Satz des Pythagoras wird verwendet (Die Entfernung vom Ball zum Becher ist Wurzel(ballX-hole1X² + ballY-hole1Y²)
        //Diese Entfernung vom Ball zum Becher muss kleiner sein als der Radius
        if (!hole1 && (Math.sqrt((ballX - hole1X) * (ballX - hole1X) + (ballY - hole1Y) * (ballY - hole1Y)) < holeRadius1)) {
            stop();
            hole1 = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallInHole(1);
                }
            });
            return;
        }
        if (!hole2 && (Math.sqrt((ballX - hole2X) * (ballX - hole2X) + (ballY - hole2Y) * (ballY - hole2Y)) < holeRadius2)) {
            stop();
            hole2 = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallInHole(2);
                }
            });
            return;
        }
        if (!hole3 && (Math.sqrt((ballX - hole3X) * (ballX - hole3X) + (ballY - hole3Y) * (ballY - hole3Y)) < holeRadius3)) {
            stop();
            hole3 = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallInHole(3);
                }
            });
            return;
        }
        if (!hole4 && (Math.sqrt((ballX - hole4X) * (ballX - hole4X) + (ballY - hole4Y) * (ballY - hole4Y)) < holeRadius4)) {
            stop();
            hole4 = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallInHole(4);
                }
            });
            return;
        }
        if (!hole5 && (Math.sqrt((ballX - hole5X) * (ballX - hole5X) + (ballY - hole5Y) * (ballY - hole5Y)) < holeRadius5)) {
            stop();
            hole5 = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallInHole(5);
                }
            });
            return;
        }
        if (!hole6 && (Math.sqrt((ballX - hole6X) * (ballX - hole6X) + (ballY - hole6Y) * (ballY - hole6Y)) < holeRadius6)) {
            stop();
            hole6 = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onBallInHole(6);
                }
            });
            return;
        }

        //Wenn die Geschwindigkeit des Balles sehr niedrig ist, wird der Versuch abgebrochen
        if (getGeschwindigkeit() < 20 && getGeschwindigkeit() != 0) {
            stop();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onVersuchFailed();
                }
            });
            return;
        }

        //LevelSpezifische Abfrage
        levelSpecificQuery();

        handler.post(new Runnable() {

            @Override
            public void run() {
                gameView.setBallPosition(ballX, ballY);
            }
        });

    }

    //Konstruktor, der die jeweilige gameView definiert, sowie einen onGameEventListener
    GameEngine(GameView gameView, OnGameEventListener onGameEventListener) {
        this.gameView = gameView;
        this.onGameEventListener = onGameEventListener;
        handler = new Handler();
    }

    //Wird von der GameActivity aufgerufen, sobald eine Swipe-Bewegung erkannt wurde
    void onFling(MotionEvent e1, float velocityX, float velocityY) {

        // Wenn die Swipe-Bewegung den Ball berührt, wird die X- und Y-Geschwindigkeit des Balles gesetzt
        // Der Faktor 140 wurde durch Ausprobieren auf verschiedenen Geräten herausgefunden
        if (!onFling && Math.abs(e1.getX() - ballX) <= gameView.getBaseDimension() && Math.abs(e1.getY() - ballY) < gameView.getBaseDimension()) {

            onFling = true;

            versuche++;

            ballVX = velocityX * MS_PER_FRAME * 140;
            ballVY = velocityY * MS_PER_FRAME * 140;
            ballX += ballVX * MS_PER_FRAME / 1000;
            ballY += ballVY * MS_PER_FRAME / 1000;

        }
    }

    //Gibt die absolute Geschwindigkeit des Balles zurück (Vektorrechnung)
    private double getGeschwindigkeit() {
        return Math.sqrt((ballVX * ballVX) + (ballVY * ballVY));
    }

    //Setter um Löcher zu aktivieren / deaktivieren
    void setHole1(boolean hole1) {
        this.hole1 = hole1;
    }

    void setHole2(boolean hole2) {
        this.hole2 = hole2;
    }

    void setHole3(boolean hole3) {
        this.hole3 = hole3;
    }

    void setHole4(boolean hole4) {
        this.hole4 = hole4;
    }

    void setHole5(boolean hole5) {
        this.hole5 = hole5;
    }

    void setHole6(boolean hole6) {
        this.hole6 = hole6;
    }

    //LevelSpezifische Abfragen, die von den Unterklassen überschrieben werden
    void levelSpecificQuery() {
    }
}

//Interface für alle mögliche GameEvents. Dieses wird von der GameActivity implementiert
//Das Interface fungiert als Schnittstelle der GameEngine zum UserInterface der GameActivity
interface OnGameEventListener {
    void onBallInHole(int i);

    void onVersuchFailed();

    void onCatHit();

    void onBallBounce();

    void onFlaschenHit();
}



