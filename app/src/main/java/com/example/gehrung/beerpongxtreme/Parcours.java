package com.example.gehrung.beerpongxtreme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

//Untermenü Parcours
//Hier befindet sich die komplette Highscore-Logik
public class Parcours extends AppCompatActivity {

    //Objekt zum Anzeigen spezieller Dialoge
    private Dialog dialog;
    private ImageButton musik;

    private static int punktePlatz1;
    private static String namePlatz1;
    private static int punktePlatz2;
    private static String namePlatz2;
    private static int punktePlatz3;
    private static String namePlatz3;
    private static int punktePlatz4;
    private static String namePlatz4;
    private static int punktePlatz5;
    private static String namePlatz5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours);

        //Initialisierung der Menübuttons
        final ImageButton play = (ImageButton) findViewById(R.id.play);
        final ImageButton highscore = (ImageButton) findViewById(R.id.highscore);
        musik = (ImageButton) findViewById(R.id.musik);
        final ImageButton hilfe = (ImageButton) findViewById(R.id.hilfe);
        final ImageButton home = (ImageButton) findViewById(R.id.home);

        //Per Klick auf den Home-Button gelangt man zurück ins Hauptmenü
        if (home != null) {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Parcours.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }

        //Das Bild des Musik-buttons wird je nach ein-oder ausgeschaltetem Musik-Modus gesetzt
        if (musik != null) {
            musik.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.musikCheck) {
                        musik.setImageResource(R.drawable.musik_off);
                        MainActivity.musikCheck = false;
                    } else {
                        musik.setImageResource(R.drawable.musik_on);
                        MainActivity.musikCheck = true;
                    }
                }
            });
        }

        //Per Klick auf den Hilfe-Button öffnet sich ein Dialog mit einer Parcoursspezifischen Spielanleitung
        if (hilfe != null) {
            hilfe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog = new Dialog(Parcours.this);
                    dialog.setContentView(R.layout.hilfe_dialog);

                    final TextView erklaerung = (TextView) dialog.findViewById(R.id.erklärung);
                    erklaerung.setText(R.string.alkoholikerHilfeParcours);

                    erklaerung.setMovementMethod(new ScrollingMovementMethod());

                    dialog.show();

                    final ImageButton zurueck = (ImageButton) dialog.findViewById(R.id.zurueck);
                    if (zurueck != null) {
                        zurueck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
        }

        //Per Klick auf den Highscore öffnet sich ein Dialog mit den 5 Besten Spielständen
        if (highscore != null) {
            highscore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Highscoredaten werden lokal vom Handy geladen
                    final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);
                    dialog = new Dialog(Parcours.this);
                    dialog.setContentView(R.layout.highscore_dialog);

                    //Die TextFelder im Dialog werden mit den Spieldaten befüllt
                    fillTextFieldsWithData(sp, dialog);

                    //Der Highscore kann zurückgesetzt werden. Es öffnet sich ein Dialog, der verifiziert, dass der Highscore tatsächlich zurückgesetzt werden soll
                    final ImageButton resetHighscore = (ImageButton) dialog.findViewById(R.id.resetHighscore);
                    if (resetHighscore != null) {
                        resetHighscore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog resetHighscore = new Dialog(Parcours.this);
                                resetHighscore.setContentView(R.layout.highscore_wirklich_reset_dialog);
                                resetHighscore.show();
                                final ImageButton reset = (ImageButton) resetHighscore.findViewById(R.id.reset);
                                final ImageButton zurueck = (ImageButton) resetHighscore.findViewById(R.id.zurueck);

                                if (reset != null) {
                                    reset.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            resetHighscore(sp);
                                            resetHighscore.dismiss();
                                            fillTextFieldsWithData(sp, dialog);
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

                    final ImageButton zurueck = (ImageButton) dialog.findViewById(R.id.zurueck);
                    if (zurueck != null) {
                        zurueck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }

                    dialog.show();
                }
            });
        }

        //Per Klick auf den Play-button wird das Spiel mit Level 1 gestartet
        if (play != null) {
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Parcours.this, ParcoursLevel01.class);
                    startActivity(intent);

                    //Die angefangenen Spiele werden um eins erhöht
                    final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);
                    MainActivity.angefangeneSpieleZaehlen(sp);
                }
            });
        }
        //Delegation der Volume-Buttons an die Medienlautstärke, somit wird nicht die Klingeltonlautstärke gesetzt
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    //Funktion zum Füllen der TextFelder im Highscore-Dialog mit den Spielständen
    static void fillTextFieldsWithData(SharedPreferences sp, Dialog dialog) {
        //Deklarierung der Textfelder
        final TextView name1 = (TextView) dialog.findViewById(R.id.name1);
        final TextView punkte1 = (TextView) dialog.findViewById(R.id.punkte1);
        final TextView name2 = (TextView) dialog.findViewById(R.id.name2);
        final TextView punkte2 = (TextView) dialog.findViewById(R.id.punkte2);
        final TextView name3 = (TextView) dialog.findViewById(R.id.name3);
        final TextView punkte3 = (TextView) dialog.findViewById(R.id.punkte3);
        final TextView name4 = (TextView) dialog.findViewById(R.id.name4);
        final TextView punkte4 = (TextView) dialog.findViewById(R.id.punkte4);
        final TextView name5 = (TextView) dialog.findViewById(R.id.name5);
        final TextView punkte5 = (TextView) dialog.findViewById(R.id.punkte5);

        //Aktueller Highscore wird geladen
        loadHighscore(sp);

        //Schreibe Platz1 in TextView
        name1.setText(namePlatz1);
        punkte1.setText(Integer.toString(punktePlatz1));

        //Schreibe Platz2 in TextView
        name2.setText(namePlatz2);
        punkte2.setText(Integer.toString(punktePlatz2));

        //Schreibe Platz3 in TextView
        name3.setText(namePlatz3);
        punkte3.setText(Integer.toString(punktePlatz3));

        //Schreibe Platz4 in TextView
        name4.setText(namePlatz4);
        punkte4.setText(Integer.toString(punktePlatz4));

        //Schreibe Platz5 in TextView
        name5.setText(namePlatz5);
        punkte5.setText(Integer.toString(punktePlatz5));
    }

    //Highscore zurücksetzen (Standard: Punkte 99999, Name xxxxxx)
    static void resetHighscore(SharedPreferences sp) {

        final SharedPreferences.Editor spEditor = sp.edit();

        spEditor.putInt("punktePlatz1", 99999);
        spEditor.putString("namePlatz1", "xxxxx");

        spEditor.putInt("punktePlatz2", 99999);
        spEditor.putString("namePlatz2", "xxxxx");

        spEditor.putInt("punktePlatz3", 99999);
        spEditor.putString("namePlatz3", "xxxxx");

        spEditor.putInt("punktePlatz4", 99999);
        spEditor.putString("namePlatz4", "xxxxx");

        spEditor.putInt("punktePlatz5", 99999);
        spEditor.putString("namePlatz5", "xxxxx");

        spEditor.commit();
    }

    //Aktueller Highscore wird lokal vom Smartphone geladen
    //Klassenvariablen aktualisieren
    private static void loadHighscore(SharedPreferences sp) {

        punktePlatz1 = sp.getInt("punktePlatz1", 99999);
        namePlatz1 = sp.getString("namePlatz1", "xxxxx");

        punktePlatz2 = sp.getInt("punktePlatz2", 99999);
        namePlatz2 = sp.getString("namePlatz2", "xxxxx");

        punktePlatz3 = sp.getInt("punktePlatz3", 99999);
        namePlatz3 = sp.getString("namePlatz3", "xxxxx");

        punktePlatz4 = sp.getInt("punktePlatz4", 99999);
        namePlatz4 = sp.getString("namePlatz4", "xxxxx");

        punktePlatz5 = sp.getInt("punktePlatz5", 99999);
        namePlatz5 = sp.getString("namePlatz5", "xxxxx");

    }

    //Speichere den Highscore
    //--> Verschiebung der alten Platzierungen
    static void saveHighscore(int punkte, String name, int platzierung, SharedPreferences sp) {

        final SharedPreferences.Editor spEditor = sp.edit();

        switch (platzierung) {
            case 1:
                spEditor.putInt("punktePlatz1", punkte);
                spEditor.putString("namePlatz1", name);

                spEditor.putInt("punktePlatz2", punktePlatz1);
                spEditor.putString("namePlatz2", namePlatz1);

                spEditor.putInt("punktePlatz3", punktePlatz2);
                spEditor.putString("namePlatz3", namePlatz2);

                spEditor.putInt("punktePlatz4", punktePlatz3);
                spEditor.putString("namePlatz4", namePlatz3);

                spEditor.putInt("punktePlatz5", punktePlatz4);
                spEditor.putString("namePlatz5", namePlatz4);

                spEditor.commit();
                break;
            case 2:
                spEditor.putInt("punktePlatz2", punkte);
                spEditor.putString("namePlatz2", name);

                spEditor.putInt("punktePlatz3", punktePlatz2);
                spEditor.putString("namePlatz3", namePlatz2);

                spEditor.putInt("punktePlatz4", punktePlatz3);
                spEditor.putString("namePlatz4", namePlatz3);

                spEditor.putInt("punktePlatz5", punktePlatz4);
                spEditor.putString("namePlatz5", namePlatz4);

                spEditor.commit();
                break;
            case 3:
                spEditor.putInt("punktePlatz3", punkte);
                spEditor.putString("namePlatz3", name);

                spEditor.putInt("punktePlatz4", punktePlatz3);
                spEditor.putString("namePlatz4", namePlatz3);

                spEditor.putInt("punktePlatz5", punktePlatz4);
                spEditor.putString("namePlatz5", namePlatz4);

                spEditor.commit();
                break;
            case 4:
                spEditor.putInt("punktePlatz4", punkte);
                spEditor.putString("namePlatz4", name);

                spEditor.putInt("punktePlatz5", punktePlatz4);
                spEditor.putString("namePlatz5", namePlatz4);

                spEditor.commit();
                break;
            case 5:
                spEditor.putInt("punktePlatz5", punkte);
                spEditor.putString("namePlatz5", name);

                spEditor.commit();
                break;
        }
    }

    //neue Highscore-Daten werden mit der Bestenliste verglichen
    //Die Funktion gibt den Platz in der Highscore-Liste zurück
    static int compareHighscore(int punkte, SharedPreferences sp) {
        loadHighscore(sp);

        int platzierung;

        if (punkte < punktePlatz1) {
            platzierung = 1;
        } else if (punkte < punktePlatz2) {
            platzierung = 2;
        } else if (punkte < punktePlatz3) {
            platzierung = 3;
        } else if (punkte < punktePlatz4) {
            platzierung = 4;
        } else if (punkte < punktePlatz5) {
            platzierung = 5;
        } else {
            //Wenn nicht unter Top5 -> platzierung = -1
            platzierung = -1;
        }

        return platzierung;

    }

    //Die Funktion onResume() wird immer aufgerufen, wenn die Activity wieder in den Vordergrund rückt
    //Der Musik-button wird neu gesetzt
    protected void onResume() {
        super.onResume();
        musik = (ImageButton) findViewById(R.id.musik);
        if (MainActivity.musikCheck && musik != null)
            musik.setImageResource(R.drawable.musik_on);
        else if (musik != null)
            musik.setImageResource(R.drawable.musik_off);

    }

}
