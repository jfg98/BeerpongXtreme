package com.example.gehrung.beerpongxtreme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SuffSingleModus extends GameActivity {

    //Komponenten für den Dialog am Anfang des Modus -> Eingabe von Werten für die Berechnung des Promillewertes
    private int anzahl = 0;
    private double geschlecht;
    private int gewicht;
    private double becherinhalt;
    private double alkoholgehalt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Oberflächenkomponenten der Promilleanzeige initialisiert und sichtbar gesetzt, da sie standardmäßig in activity_main.xml unsichbar sind
        final ProgressBar alkoholpegel = (ProgressBar) findViewById(R.id.alkoholpegel);
        final TextView promille = (TextView) findViewById(R.id.promille);
        if (promille != null)
            promille.setVisibility(View.VISIBLE);
        if (alkoholpegel != null) {
            alkoholpegel.setVisibility(View.VISIBLE);
            alkoholpegel.setProgress(1);
        }

        //Zu Beginn wird 0.0 Promille angezeigt
        refreshPromille(0, geschlecht, gewicht, becherinhalt, alkoholgehalt, promille, alkoholpegel);


        //Dialog zur Dateneingabe wird angezeigt
        final Dialog dateneingeben = new Dialog(getThislevel());
        dateneingeben.setContentView(R.layout.suff_einzelmodus_daten_eingeben_dialog);
        dateneingeben.show();
        dateneingeben.setCancelable(false);

        //Initialisierung der Dialogkomponenten
        final ImageButton play = (ImageButton) dateneingeben.findViewById(R.id.playbutton);
        final ImageButton zurueck = (ImageButton) dateneingeben.findViewById(R.id.zurueck);
        final Spinner geschlechtspinner = (Spinner) dateneingeben.findViewById(R.id.geschlechtspinner);
        final EditText gewichttext = (EditText) dateneingeben.findViewById(R.id.gewicht);
        final EditText becherinhalttext = (EditText) dateneingeben.findViewById(R.id.becherinhalt);
        final EditText alkoholgehalttext = (EditText) dateneingeben.findViewById(R.id.alkoholgehalt);

        //der Spinner für das Geschlecht wird mit Bildern gefüllt, dafür wird die Klasse ImageArrayAdapter verwendet
        ImageArrayAdapter adapter = new ImageArrayAdapter(SuffSingleModus.this,
                new Integer[]{R.drawable.rechner_alkoholiker, R.drawable.rechner_alkoholikerin});
        geschlechtspinner.setAdapter(adapter);

        //Wird der Play-Button gedrückt, wird erst einmal überprüft, ob alle Eingaben korrekt getätigt wurden (keine Exception)
        //Wenn ja, wird der Modus gestartet, wenn nicht, müssen die Eingaben erneut korrekt getätigt werden
        if (play != null) {

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (geschlechtspinner.getSelectedItemId() == 0)
                        geschlecht = 0.7;
                    else
                        geschlecht = 0.6;


                    try {
                        if (gewichttext.getText().toString().equals("") || becherinhalttext.getText().toString().equals("") || alkoholgehalttext.getText().toString().equals(""))
                            throw new Exception();

                        gewicht = Integer.parseInt(gewichttext.getText().toString());
                        becherinhalt = Double.parseDouble(becherinhalttext.getText().toString());
                        alkoholgehalt = Double.parseDouble(alkoholgehalttext.getText().toString());
                        dateneingeben.dismiss();
                        //Die angefangenen Spiele werden um eins erhöht
                        final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);
                        MainActivity.angefangeneSpieleZaehlen(sp);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.erneutEingeben), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (zurueck != null) {

            zurueck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(getThislevel());
                    dialog.setContentView(R.layout.back_dialog);
                    dialog.show();

                    final ImageButton zurueck = (ImageButton) dialog.findViewById(R.id.zurueck);
                    if (zurueck != null) {
                        zurueck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GameEngine.resetVersuche();
                                dialog.dismiss();
                                Intent intent = new Intent(getThislevel(), getNextLevel());
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            });
        }

    }

    //Implementierte abstrakten Methoden der GameActivity:
    @Override
    Class getNextLevel() {
        return Suff.class;
    }

    @Override
    Context getThislevel() {
        return SuffSingleModus.this;
    }

    @Override
    int getLevel() {
        return 0;
    }

    @Override
    int getImageResource() {
        return 999;
    }

    //Ein Dialog mit dem Promillestand wird angezeigt, wird dieser Weggeklickt wird das Untermenü Suff aufgerufen und der (unsichtbare) Spielstand zurückgesetzt
    @Override
    void levelFinishedAction() {
        dialog = new Dialog(getThislevel());
        dialog.setContentView(R.layout.alkoholpegel_dialog);
        final TextView pegel = (TextView) dialog.findViewById(R.id.promille);
        final ImageButton zurueck = (ImageButton) dialog.findViewById(R.id.zurueck);
        refreshPromille(anzahl, geschlecht, gewicht, becherinhalt, alkoholgehalt, pegel, null);
        dialog.show();
        zurueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getThislevel(), getNextLevel());
                startActivity(intent);
                GameEngine.resetVersuche();
                finish();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = new Intent(getThislevel(), getNextLevel());
                startActivity(intent);
                GameEngine.resetVersuche();
                finish();
            }
        });

    }

    //Überschriebene Funktion der GameActivity
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
        gameEngine.setBallPosition(getResources().getDisplayMetrics().widthPixels / 2,
                getResources().getDisplayMetrics().heightPixels - 3 * gameView.getBaseDimension());

        final ProgressBar alkoholpegel = (ProgressBar) findViewById(R.id.alkoholpegel);
        final TextView promille = (TextView) findViewById(R.id.promille);
        anzahl++;
        //Der Promillestand wird aktualisiert
        refreshPromille(anzahl, geschlecht, gewicht, becherinhalt, alkoholgehalt, promille, alkoholpegel);
        //Ein Dialog, der zum Trinken animieren soll, wird angezeigt
        dialog = new Dialog(getThislevel());
        dialog.setContentView(R.layout.saufmodus_dialog);
        dialog.show();

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.saufmodus_imageView);
        imageView.setImageResource(R.drawable.treffer_suff);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Wenn alle Becher getroffen wurden wird die levelFinishedAction() aufgerufen (Dialog mit dem Promillestand)
                if (cup1.getVisibility() == View.INVISIBLE
                        && cup2.getVisibility() == View.INVISIBLE
                        && cup3.getVisibility() == View.INVISIBLE
                        && cup4.getVisibility() == View.INVISIBLE
                        && cup5.getVisibility() == View.INVISIBLE
                        && cup6.getVisibility() == View.INVISIBLE) {
                    levelFinishedAction();
                } else {
                    gameEngine.start();
                }
            }
        });

        //Wenn alle Becher getroffen wurden wird die levelFinishedAction() aufgerufen (Dialog mit dem Promillestand)
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (cup1.getVisibility() == View.INVISIBLE
                        && cup2.getVisibility() == View.INVISIBLE
                        && cup3.getVisibility() == View.INVISIBLE
                        && cup4.getVisibility() == View.INVISIBLE
                        && cup5.getVisibility() == View.INVISIBLE
                        && cup6.getVisibility() == View.INVISIBLE) {
                    levelFinishedAction();
                } else {
                    gameEngine.start();
                }
            }
        });
    }

    //Funktion, die den aktuellen Promillestand berechnet
    //Die Formel dafür stammt aus dem Internet (http://www.perfectdrinks.de/promille.html)
    private void refreshPromille(int anzahl, double geschlecht, int gewicht, double becherinhalt, double alkoholgehalt, TextView promille, ProgressBar alkoholpegel) {
        final double maxPromille = ((becherinhalt * 100 * 6) * alkoholgehalt * 0.08) / (geschlecht * gewicht);
        double promilleZahl = ((becherinhalt * 100 * anzahl) * alkoholgehalt * 0.08) / (geschlecht * gewicht);
        promilleZahl = Math.round(100.0 * promilleZahl) / 100.0;
        promille.setText(promilleZahl + getResources().getString(R.string.promille));
        if (alkoholpegel != null) {
            double prozent = (promilleZahl * 100) / maxPromille;
            alkoholpegel.setProgress((int) prozent);
        }
    }

}
