package com.example.gehrung.beerpongxtreme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ParcoursLevel06 extends GameActivity {

    //Bild des Ventilators
    static ImageView ventilatorImage;
    //MediaPlayer für den Sound des Ventilators
    private MediaPlayer ventilatorEffect;

    //Daten, die wir speichern wollen
    private int punkte;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Deklarierung des Ventilators
        ventilatorImage = (ImageView) findViewById(R.id.ventilator);

        //Animation für das permanente Drehen des Ventilators/ Dieser wird durch die Animation automatisch sichtbar gesetzt (da standardmäßig in activity_main.xml unsichtbar)
        final Animation animation = AnimationUtils.loadAnimation(getThislevel(), R.anim.rotate);
        ventilatorImage.startAnimation(animation);
        //MediaPlayer wird mit dem Ventilator-Sound verknüpft und pausenlos abgespielt
        ventilatorEffect = MediaPlayer.create(getThislevel(), R.raw.wind);
        ventilatorEffect.setLooping(true);
        if (MainActivity.musikCheck) {
            ventilatorEffect.start();
        }
    }

    //Implementierung der abstrakten Methoden der GameActivity:
    @Override
    Context getThislevel() {
        return ParcoursLevel06.this;
    }

    @Override
    Class getNextLevel() {
        return Parcours.class;
    }

    @Override
    int getLevel() {
        return 6;
    }

    @Override
    int getImageResource() {
        return R.drawable.level6;
    }

    //Im Gegensatz zu allen anderen Levels wird nicht nur das nächste Level aufgerufen
    //Ein Dialog wird angezeigt, wo man den Spielernamen eingeben kann und den Spielstand speichern
    @Override
    void levelFinishedAction() {

        musicOff();

        final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);

        //Dialog --> Punktzahl & Name eintragen
        dialog = new Dialog(getThislevel());
        dialog.setContentView(R.layout.parcours_finished_dialog);

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.finishedImage);

        dialog.show();

        punkte = GameEngine.getVersuche();

        //Abhängig vom Ergebnis wird ein Bild oben im Dialog platziert
        switch (bewerteErgebnis(punkte)) {
            case 1:
                imageView.setImageResource(R.drawable.ende1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ende2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ende3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ende4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.ende5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.ende6);
                break;
        }

        //Der Dialog kann nicht weggeklickt werden
        dialog.setCancelable(false);

        final TextView punktzahl = (TextView) dialog.findViewById(R.id.punktzahl);

        punktzahl.setText(Integer.toString(GameEngine.getVersuche()));

        //Per Klick auf den saveHighscore-Button wird das erzielte Ergebnis mit der Bestenliste verglichen und ggf gespeichert
        //Die komplette Highscorelogik befindet sich in der Parcours-Klasse
        //In Jedem Fall wird der Highscore angezeigt
        final ImageButton saveHighscore = (ImageButton) dialog.findViewById(R.id.saveHighscore);
        saveHighscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText spielerName = (EditText) dialog.findViewById(R.id.spieler_name);

                punkte = GameEngine.getVersuche();
                String nameUntrimmed = spielerName.getText().toString();
                name = nameUntrimmed.trim();
                if (name.equals("")) {
                    name = getResources().getString(R.string.NoName);
                }

                if (Parcours.compareHighscore(punkte, sp) == -1) {
                    Toast.makeText(getThislevel(), getResources().getString(R.string.HighscoreZuSchlecht), Toast.LENGTH_SHORT).show();
                } else {
                    Parcours.saveHighscore(punkte, name, Parcours.compareHighscore(punkte, sp), sp);
                    Toast.makeText(getThislevel(), getResources().getString(R.string.HighscoreGespeichert), Toast.LENGTH_SHORT).show();
                }
                GameEngine.resetVersuche();
                saveHighscore.setVisibility(View.INVISIBLE);

                final Dialog highscore = new Dialog(getThislevel());
                highscore.setContentView(R.layout.highscore_dialog);

                Parcours.fillTextFieldsWithData(sp, highscore);
                highscore.setCancelable(false);
                highscore.show();

                //Der Highscore kann direkt nach dem Spiel zurückgesetzt werden
                //Das Vorgehen hierzu ist analog zu dem in der Parcours-Klasse
                final ImageButton resetHighscore = (ImageButton) highscore.findViewById(R.id.resetHighscore);
                final ImageButton zurueck = (ImageButton) highscore.findViewById(R.id.zurueck);
                if (resetHighscore != null) {
                    resetHighscore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog resetHighscore = new Dialog(getThislevel());
                            resetHighscore.setContentView(R.layout.highscore_wirklich_reset_dialog);
                            resetHighscore.show();
                            ImageButton reset = (ImageButton) resetHighscore.findViewById(R.id.reset);
                            ImageButton zurueck = (ImageButton) resetHighscore.findViewById(R.id.zurueck);

                            if (reset != null) {
                                reset.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Parcours.resetHighscore(sp);
                                        resetHighscore.dismiss();
                                        Parcours.fillTextFieldsWithData(sp, highscore);
                                    }
                                });
                            }

                            if (zurueck != null) {
                                zurueck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        resetHighscore.dismiss();
                                    }
                                });
                            }
                        }
                    });
                }

                //Wird der Zurück-Button im Highscore-Menü geklickt, gelangt der Benutzer zurück zum Parcours-Menü und die Activity wird beendet
                //Die Anzahl der Versuche wird zurückgesetzt
                if (zurueck != null) {
                    zurueck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            highscore.dismiss();
                            GameEngine.resetVersuche();
                            Intent intent = new Intent(getThislevel(), getNextLevel());
                            startActivity(intent);
                            container.removeAllViews();
                            finish();
                        }
                    });
                }

            }
        });

        //Neben dem Speichern des Spielstandes kann der Spieler direkt zurück zum Hauptmenü gelangen
        //Die Anzahl der Versuche wird zurückgesetzt und die Activity beendet
        final ImageButton home = (ImageButton) dialog.findViewById(R.id.homeButton);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameEngine.stop();
                final Dialog home = new Dialog(getThislevel());
                home.setContentView(R.layout.home_dialog);
                home.show();

                ImageButton zurueck = (ImageButton) home.findViewById(R.id.zurueck);
                if (zurueck != null) {
                    zurueck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            home.dismiss();
                            GameEngine.resetVersuche();
                            Intent intent = new Intent(getThislevel(), MainActivity.class);
                            startActivity(intent);
                            container.removeAllViews();
                            finish();
                        }
                    });
                }

                home.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        home.dismiss();
                    }
                });

            }
        });
    }

    //Überschreibung der onPause(), onDestroy() und onRestart() Methoden der GameActivity
    //-> der Ventilator-Sound wird zusätzlich gestoppt bzw. gestartet:
    @Override
    protected void onPause() {
        super.onPause();
        if (ventilatorEffect != null) {
            ventilatorEffect.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ventilatorEffect != null) {
            ventilatorEffect.stop();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (MainActivity.musikCheck) {
            if (ventilatorEffect.isPlaying()) {
                ventilatorEffect.pause();
            }
            ventilatorEffect.seekTo(0);
            ventilatorEffect.start();
        }
    }

    //Abhängig von der Anzahl der Versuche wird im finalen Parcours-Dialog ein Bild angezeigt
    //Die Funktion ordnet die Anzahl der Versuche den Bildern zu
    private int bewerteErgebnis(int punkte) {
        if (punkte >= 36 && punkte <= 56) {
            return 1;
        } else if (punkte >= 57 && punkte <= 66) {
            return 2;
        } else if (punkte >= 67 && punkte <= 76) {
            return 3;
        } else if (punkte >= 77 && punkte <= 86) {
            return 4;
        } else if (punkte >= 87 && punkte <= 96) {
            return 5;
        } else {
            return 6;
        }
    }

    //Überschreibung der musicOff() und musicOn()-Methoden der GameActivity
    //-> der Ventilator-Sound wird gestoppt bzw. gestartet
    @Override
    void musicOff() {
        if (ventilatorEffect.isPlaying()) {
            ventilatorEffect.pause();
            ventilatorEffect.seekTo(0);
        }
        super.musicOff();

    }

    @Override
    void musicOn() {
        ventilatorEffect.start();
    }
}
