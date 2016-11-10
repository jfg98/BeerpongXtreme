package com.example.gehrung.beerpongxtreme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// Oberklasse aller Level, sowie der Suff-Modi
// deckt grundlegende Funktionalität ab, die einzelnen Level/ Modi erweitern teilweise lediglich zusätzliche Funktionen
abstract class GameActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, OnGameEventListener {

    //Deklarierung der Instanzvariablen
    GameView gameView;
    ViewGroup container;
    GameEngine gameEngine;
    private GestureDetector gDetector;

    //Variable, ob das Spiel gestartet oder gestoppt ist
    private boolean game = false;

    //Deklarierung der auf der Oberfläche benötigten Komponenten
    Dialog dialog;
    private ImageButton musik;
    static TextView versuche;
    private ImageView versucheTextView;
    ImageView cup1;
    ImageView cup2;
    ImageView cup3;
    ImageView cup4;
    ImageView cup5;
    ImageView cup6;

    //Initialisierung der Zählervariable für den Level-Hack
    private int pressUpperLeftCorner = 0;

    //Mediaplayer zum Abpspielen der GameEvent-Sounds
    private MediaPlayer catEffect;
    private MediaPlayer bounceEffect;
    private MediaPlayer bottleEffect;
    MediaPlayer trefferEffect;

    //Koordinaten der Becher werden für die spätere Löcherpositionierung benötigt
    private float hole1X, hole1Y;
    private float hole2X, hole2Y;
    private float hole3X, hole3Y;
    private float hole4X, hole4Y;
    private float hole5X, hole5Y;
    private float hole6X, hole6Y;

    //Deklarierung einzelner Instanzvariablen beim Aufruf der Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Für alle Spielmodi ist die Oberfläche activity_game.xml
        //Unbenötigte Oberflächenkomponenten, wie Highscoreanzeigen; Team-Bilder oder Ventilatorenetc. werden zur Laufzeit bei Bedarf sichtbar/unsichtbar gestellt
        setContentView(R.layout.activity_game);

        //Level-Anzeige zu Beginn der Levels
        //Suff-Modi haben als "ImageResource" die Zahl 999
        if (getImageResource() != 999)
            showToast(getImageResource());

        //Initialisierung der Menüführungs-Buttons
        final ImageButton pause = (ImageButton) findViewById(R.id.pause);
        final ImageButton home = (ImageButton) findViewById(R.id.home);
        musik = (ImageButton) findViewById(R.id.musik);

        //Deklarierung der Oberflächenkomponenten
        //Der Container ist die Spieloberfläche, hier werden zur Laufzeit der Ball/Hindernisse hinzugefügt
        container = (ViewGroup) findViewById(R.id.container);
        versuche = (TextView) findViewById(R.id.versucheAnzahl);
        versucheTextView = (ImageView) findViewById(R.id.versuche);
        cup1 = (ImageView) findViewById(R.id.cup1);
        cup2 = (ImageView) findViewById(R.id.cup2);
        cup3 = (ImageView) findViewById(R.id.cup3);
        cup4 = (ImageView) findViewById(R.id.cup4);
        cup5 = (ImageView) findViewById(R.id.cup5);
        cup6 = (ImageView) findViewById(R.id.cup6);

        //Deklarierung des GestureDetectors, der später die Swipe-Bewegung registriert
        this.gDetector = new GestureDetector(this, this);

        //Delegation der Volume-Buttons an die Medienlautstärke, somit wird nicht die Klingeltonlautstärke gesetzt
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Mediaplayer werden mit den jeweiligen Sounds verknüpft
        catEffect = MediaPlayer.create(getThislevel(), R.raw.katze2);
        bounceEffect = MediaPlayer.create(getThislevel(), R.raw.tisch);
        bottleEffect = MediaPlayer.create(getThislevel(), R.raw.bottle);
        trefferEffect = MediaPlayer.create(getThislevel(), R.raw.splash);

        //Bei Klick auf den Pause-Button wird das Spiel gestoppt und ein Dialog mit den Möglichkeiten retry und resume angezeigt
        if (pause != null) {
            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameEngine.stop();
                    game = false;
                    dialog = new Dialog(getThislevel());
                    dialog.setContentView(R.layout.pause_dialog);
                    dialog.show();

                    final ImageButton resume = (ImageButton) dialog.findViewById(R.id.resume);
                    final ImageButton retry = (ImageButton) dialog.findViewById(R.id.retry);

                    if (retry != null) {
                        retry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                game=true;
                                dialog.dismiss();
                                onVersuchFailed();
                            }
                        });
                    }

                    if (resume != null) {
                        resume.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                game=true;
                                dialog.dismiss();
                                gameEngine.resume();
                            }
                        });
                    }

                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            game=true;
                            gameEngine.resume();
                        }
                    });
                }
            });
        }

        //Bei Klick auf den Home-Button wird das Spiel gestoppt und ein Dialog angezeigt, der abfrägt, ob wirklich zurück zum Hauptmenü navigiert werden soll
        //Der aktuelle Punktestand muss dabei zurückgesetzt werden
        //Damit die Activity nicht per Back-Button auf dem Smartphone wiedererreicht werden kann, wird sie mit finish() endgültig beendet und vom Activity-Stack geworfen
        if (home != null) {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameEngine.stop();
                    game = false;
                    dialog = new Dialog(getThislevel());
                    dialog.setContentView(R.layout.home_dialog);
                    dialog.show();

                    final ImageButton zurueck = (ImageButton) dialog.findViewById(R.id.zurueck);
                    if (zurueck != null) {
                        zurueck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GameEngine.resetVersuche();

                                //abgebrochene Spiele werden um eins erhöht
                                final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);
                                MainActivity.abgebrocheneSpieleZaehlen(sp);

                                Intent intent = new Intent(getThislevel(), MainActivity.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                    }

                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            game=true;
                            gameEngine.resume();
                        }
                    });
                }
            });
        }

        //Je nachdem, ob Musik ein-oder ausgeschaltet ist, wird der ImageButton musik gesetzt
        if (MainActivity.musikCheck && musik != null)
            musik.setImageResource(R.drawable.musik_on);
        else if (musik != null)
            musik.setImageResource(R.drawable.musik_off);

        //Bei Klick auf den Musik-Button wird während der Laufzeit die Funktion musicOff()/musicOn() aufgerufen, die alle gebrauchten MediaPlayer stoppt
        if (musik != null) {
            musik.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.musikCheck) {
                        musik.setImageResource(R.drawable.musik_off);
                        MainActivity.musikCheck = false;
                        musicOff();
                    } else {
                        musik.setImageResource(R.drawable.musik_on);
                        MainActivity.musikCheck = true;
                        musicOn();
                    }
                }
            });
        }

        //schließlich wird die Start-Methode aufgerufen, die letztendlich der GameEngine und GameView alle benötigten Parameter übergibt
        // Die komplette Spiellogik, sowie die Oberfläche des Spiels befinden sich in der GameEngine und GameView
        start(getLevel());
    }

    //initialisiert das Spiel
    private void start(int level) {

        //Abhängig vom aktuellen Level wird die GameEngine und GameView initialisiert
        //Suff-Modi haben das Level 0
        switch (level) {
            case 0:
                gameView = new GameView(this);
                gameEngine = new GameEngine(gameView, this);
                //Die Punkteanzeige wird in den Suff-Modi nicht benötigt und daher unsichtbar gesetzt
                versuche.setVisibility(View.INVISIBLE);
                versucheTextView.setVisibility(View.INVISIBLE);
                break;
            case 1:
                gameView = new GameView(this);
                gameEngine = new GameEngine(gameView, this);
                break;
            case 2:
                gameView = new GameViewLevel2(this);
                gameEngine = new GameEngineLevel2(gameView, this);
                break;
            case 3:
                gameView = new GameViewLevel3(this);
                gameEngine = new GameEngineLevel3(gameView, this);
                break;
            case 4:
                gameView = new GameViewLevel4(this);
                gameEngine = new GameEngineLevel4(gameView, this);
                break;
            case 5:
                gameView = new GameViewLevel5(this);
                gameEngine = new GameEngineLevel5(gameView, this);
                break;
            case 6:
                gameView = new GameView(this);
                gameEngine = new GameEngineLevel6(gameView, this);
                break;
        }

        //Dem Container wird die Spieloberfläche hinzugefügt
        container.addView(gameView,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        //die Funktion getBaseDimension() returniert genau eine Balllänge
        final float bd = gameView.getBaseDimension();

        //Ballposition sowie Becherpositionen werden der GameEngine übergeben und das Spiel gestartet
        gameEngine.setBallPosition(getResources().getDisplayMetrics().widthPixels / 2,
                getResources().getDisplayMetrics().heightPixels - 3 * bd);
        gameEngine.setRegion(bd / 2, bd / 2, getResources().getDisplayMetrics().widthPixels - bd / 2, getResources().getDisplayMetrics().heightPixels - 24 * gameView.getBaseDimension() / 10);
        gameEngine.setHole1Position(hole1X, hole1Y, bd / 2);
        gameEngine.setHole2Position(hole2X, hole2Y, bd / 2);
        gameEngine.setHole3Position(hole3X, hole3Y, bd / 2);
        gameEngine.setHole4Position(hole4X, hole4Y, bd / 2);
        gameEngine.setHole5Position(hole5X, hole5Y, bd / 2);
        gameEngine.setHole6Position(hole6X, hole6Y, bd / 2);

        gameEngine.start();
        game = true;

    }

    //Zeigt zu Beginn der Level einen Toast in der Bildschirmmitte mit dem jeweiligen Level
    private void showToast(int imageResource) {
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        ImageView levelImage = new ImageView(this);
        levelImage.setImageResource(imageResource);
        toast.setView(levelImage);
        toast.show();
    }

    //Touch-Events müssen in der GameActivity behandelt werden
    //Der GestureDetector übernimmt die TouchEvents. Dieser erkennt bestimmte Swipe-Bewegungen
    //Touch-Events werden somit an die nachfolgenden 6 Interface-Funktionen delegiert
    //Ein Level-Hack überspringt das aktuelle Level (10-maliger Klick in die linke obere Ecke)
    public synchronized boolean onTouchEvent(MotionEvent ev) {
        this.gDetector.onTouchEvent(ev);
        if (getLevel() != 0) {
            //Level Hack
            if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getX() < getResources().getDisplayMetrics().widthPixels / 10 && ev.getY() < getResources().getDisplayMetrics().heightPixels / 10) {
                pressUpperLeftCorner++;
            }
            if (pressUpperLeftCorner == 10)
                levelFinishedAction();
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    //Lediglich die onFling-Bewegung wird behandelt -> Swiping-Bewegung mit dem Finger
    //Bei der Bewegung erkennt der GestureDetector den Anfangspunkt (e1), den Endpunkt (e2), sowie die x- und y-Geschwindigkeiten
    //Die Stärke Swipe-Bewegung wird abhängig vom Smartphone prozentural zur maximalen Swipe-Geschwindigkeit berechnet
    //Schließlich werden der GameEngine die benötigten Parameter übergeben. Alle weiteren Aktionen übernimmt die GameEngine
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final float maxFlingVelocity = ViewConfiguration.get(getThislevel()).getScaledMaximumFlingVelocity();
        final float velocityPercentX = velocityX / maxFlingVelocity;
        final float velocityPercentY = velocityY / maxFlingVelocity;
        gameEngine.onFling(e1, velocityPercentX, velocityPercentY);
        return true;
    }

    //Wird immer aufgerufen, wenn die Activity wieder in den Vordergrund kommt und zuvor noch nicht durch finish() beendet wurde
    //Wenn aktuell kein Dialog angezeigt wird, wird das Spiel fortgesetzt
    protected void onRestart() {
        super.onRestart();
        if (gameEngine != null && dialog == null || gameEngine != null && !dialog.isShowing()) {
            game=true;
            gameEngine.resume();
        }
    }

    //Die Funktion wird immer aufgerufen, wenn die Activity in den Hintergrund gerät
    //Das Spiel wird gestoppt, sowie alle MediaPlayer angehalten
    protected void onPause() {
        super.onPause();
        if (gameEngine != null) gameEngine.stop(); game = false;
        if (catEffect != null) {
            catEffect.stop();
        }
        if (bounceEffect != null) {
            bounceEffect.stop();
        }
        if (bottleEffect != null) {
            bottleEffect.stop();
        }
        if (trefferEffect != null) {
            trefferEffect.stop();
        }
    }

    //Aufgerufen, wenn die Activity durch finish() beendet wurde oder vom System zerstört
    //Dem Container werden alle Komponenten entzogen, sowie das Spiel gestoppt und alle MediaPlayer freigegeben
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameEngine != null) {
            gameEngine.stop();
            game= false;
            container.removeAllViews();
        }
        if (catEffect != null) {
            catEffect.release();
        }
        if (bounceEffect != null) {
            bounceEffect.release();
        }
        if (bottleEffect != null) {
            bottleEffect.stop();
        }
        if (trefferEffect != null) {
            trefferEffect.stop();
        }
    }

    //Bei Klick auf den Back-Button des Smartphones wird das Spiel gestoppt und ein Dialog angezeigt, der abfrägt, ob wirklich zurück navigiert werden soll
    //Der aktuelle Punktestand muss dabei zurückgesetzt werden
    //Damit die Activity nicht per Back-Button auf dem Smartphone wiedererreicht werden kann, wird sie mit finish() endgültig beendet und vom Activity-Stack geworfen
    @Override
    public void onBackPressed() {
        gameEngine.stop();
        game = false;
        dialog = new Dialog(getThislevel());
        dialog.setContentView(R.layout.back_dialog);
        dialog.show();
        ImageButton zurueck = (ImageButton) dialog.findViewById(R.id.zurueck);
        if (zurueck != null) {
            zurueck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameEngine.resetVersuche();

                    //abgebrochene Spiele werden um eins erhöht
                    final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);
                    MainActivity.abgebrocheneSpieleZaehlen(sp);

                    GameActivity.super.onBackPressed();
                    finish();
                    dialog.dismiss();
                }
            });
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                game=true;
                gameEngine.resume();
            }
        });
    }

    //In der onCreate()-Methode ist die Oberfläche noch nicht korrekt skaliert, weshalb dort keine Koordinaten gesetzt werden können
    //Die Funktion wird immer aufgerufen, wenn alle Layouts erfolgreich geladen und skaliert wurden
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final float bd = gameView.getBaseDimension();

        //Die Koordinaten der Becher werden displayunabhängig gesetzt und der GameEngine übergeben
        hole1X = cup1.getRight() - (cup1.getRight() - cup1.getLeft()) / 2;
        hole1Y = (cup1.getBottom() - 2 * (cup1.getBottom() - cup1.getTop()) / 3);
        hole2X = cup2.getRight() - (cup2.getRight() - cup2.getLeft()) / 2;
        hole2Y = (cup2.getBottom() - 2 * (cup2.getBottom() - cup2.getTop()) / 3);
        hole3X = cup3.getRight() - (cup3.getRight() - cup3.getLeft()) / 2;
        hole3Y = (cup3.getBottom() - 2 * (cup3.getBottom() - cup3.getTop()) / 3);
        hole4X = cup4.getRight() - (cup4.getRight() - cup4.getLeft()) / 2;
        hole4Y = (cup4.getBottom() - 2 * (cup4.getBottom() - cup4.getTop()) / 3);
        hole5X = cup5.getRight() - (cup5.getRight() - cup5.getLeft()) / 2;
        hole5Y = (cup5.getBottom() - 2 * (cup5.getBottom() - cup5.getTop()) / 3);
        hole6X = cup6.getRight() - (cup6.getRight() - cup6.getLeft()) / 2;
        hole6Y = (cup6.getBottom() - 2 * (cup6.getBottom() - cup6.getTop()) / 3);

        if (gameEngine != null) {
            gameEngine.setHole1Position(hole1X, hole1Y, bd / 2);
            gameEngine.setHole2Position(hole2X, hole2Y, bd / 2);
            gameEngine.setHole3Position(hole3X, hole3Y, bd / 2);
            gameEngine.setHole4Position(hole4X, hole4Y, bd / 2);
            gameEngine.setHole5Position(hole5X, hole5Y, bd / 2);
            gameEngine.setHole6Position(hole6X, hole6Y, bd / 2);

            if(hasFocus && !game){
                gameEngine.resume();
                game=true;
            }
        }
    }

    //Funktion zum Unsichtbar-Stellen getroffener Becher
    void cupDissapear(int i) {
        switch (i) {
            case 1:
                cup1.setVisibility(View.INVISIBLE);
                break;
            case 2:
                cup2.setVisibility(View.INVISIBLE);
                break;
            case 3:
                cup3.setVisibility(View.INVISIBLE);
                break;
            case 4:
                cup4.setVisibility(View.INVISIBLE);
                break;
            case 5:
                cup5.setVisibility(View.INVISIBLE);
                break;
            case 6:
                cup6.setVisibility(View.INVISIBLE);
                break;
        }
    }

    //Wird immer von der GameEngine aufgerufen, wenn ein Treffer erzielt wurde
    @Override
    public void onBallInHole(int i) {
        //Zunächst wird der Treffer-Sound abgespielt
        if (MainActivity.musikCheck) {
            if (trefferEffect.isPlaying()) {
                trefferEffect.pause();
            }
            trefferEffect.seekTo(0);
            trefferEffect.start();
        }

        cupDissapear(i);

        //Wenn alle Becher getroffen wurden wird das nächste Level aufgerufen, wenn nicht, wird das Spiel fortgesetzt
        if (cup1.getVisibility() == View.INVISIBLE
                && cup2.getVisibility() == View.INVISIBLE
                && cup3.getVisibility() == View.INVISIBLE
                && cup4.getVisibility() == View.INVISIBLE
                && cup5.getVisibility() == View.INVISIBLE
                && cup6.getVisibility() == View.INVISIBLE) {
            if (getLevel() != 0 && getLevel() != 6)
                levelFinishedDialog();
            else
                levelFinishedAction();
        } else {
            gameEngine.setBallPosition(getResources().getDisplayMetrics().widthPixels / 2,
                    getResources().getDisplayMetrics().heightPixels - 3 * gameView.getBaseDimension());
            gameEngine.start();
        }
    }

    //Wird immer von der GameEngine aufgerufen, wenn kein Treffer erzielt wurde
    @Override
    public void onVersuchFailed() {

        gameEngine.setBallPosition(getResources().getDisplayMetrics().widthPixels / 2,
                getResources().getDisplayMetrics().heightPixels - 3 * gameView.getBaseDimension());
        gameEngine.start();
    }

    //von der GameEngine ausgelöst, wenn die Katze getroffen wurde
    public void onCatHit() {
        if (MainActivity.musikCheck) {
            if (catEffect.isPlaying()) {
                catEffect.pause();
            }
            catEffect.seekTo(0);
            catEffect.start();
        }
    }

    //von der GameEngine ausgelöst, wenn die Bande getroffen wurde
    public void onBallBounce() {
        if (MainActivity.musikCheck) {
            if (bounceEffect.isPlaying()) {
                bounceEffect.pause();
            }
            bounceEffect.seekTo(0);
            bounceEffect.start();
        }
    }

    //von der GameEngine ausgelöst, wenn eine Flaschen getroffen wurde
    public void onFlaschenHit() {
        if (MainActivity.musikCheck) {
            if (bottleEffect.isPlaying()) {
                bottleEffect.pause();
            }
            bottleEffect.seekTo(0);
            bottleEffect.start();
        }
    }

    //Ausgelöst, wenn die Musik ausgeschaltet wurde
    //Alle MediaPlayer werden gestoppt
    void musicOff() {
        if (trefferEffect.isPlaying()) {
            trefferEffect.pause();
            trefferEffect.seekTo(0);
        }
        if (catEffect.isPlaying()) {
            catEffect.pause();
            catEffect.seekTo(0);
        }
        if (bounceEffect.isPlaying()) {
            bounceEffect.pause();
            bounceEffect.seekTo(0);
        }
        if (bottleEffect.isPlaying()) {
            bottleEffect.pause();
            bottleEffect.seekTo(0);
        }
    }

    //Ausgelöst, wenn die Musik eingeschaltet wird
    //Wird nur im 6.Level benötigt. Dort wird die Funktion überschrieben
    void musicOn() {
    }

    //Abstrakte Klassen, die von den jeweiligen Unterklassen implementiert werden müssen:

    //Returniert das nächste Level, das bei Intents benötigt wird
    abstract Class getNextLevel();

    //Returniert das aktuelle Level. Immer benötigt, wenn der aktuelle Kontext gebraucht wird (z.B. bei Intents, Toasts oder Dialogen)
    abstract Context getThislevel();

    //Returniert das aktuelle Level
    abstract int getLevel();

    //Returniert das Bild zur Anzeige am Anfang des Levels
    abstract int getImageResource();

    //Aktion beim Beenden eines Levels
    abstract void levelFinishedAction();

    //Wird beim Beenden eines Levels aufgerufen -> Dialog, der bei Button-Klick das nächste Level startet
    private void levelFinishedDialog() {
        gameEngine.stop();
        game = false;
        dialog = new Dialog(getThislevel());
        dialog.setContentView(R.layout.level_finished_dialog);
        dialog.setCancelable(false);
        dialog.show();
        final ImageButton nextLevel = (ImageButton) dialog.findViewById(R.id.nextLevel);
        if (nextLevel != null) {
            nextLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    levelFinishedAction();
                }
            });
        }
    }
}
