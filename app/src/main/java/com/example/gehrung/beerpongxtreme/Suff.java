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

//Untermenü Suff
public class Suff extends AppCompatActivity {

    //Objekt zum Anzeigen spezieller Dialoge
    private Dialog dialog;
    private ImageButton musik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suff);

        //Initialisierung der Menübuttons
        final ImageButton foreveralone = (ImageButton) findViewById(R.id.foreveralone);
        final ImageButton partysaufen = (ImageButton) findViewById(R.id.partysaufen);
        musik = (ImageButton) findViewById(R.id.musik);
        final ImageButton hilfe = (ImageButton) findViewById(R.id.hilfe);
        final ImageButton home = (ImageButton) findViewById(R.id.home);

        //Per Klick auf den ForeverAlone-Button startet der SuffSingleModus
        if (foreveralone != null) {
            foreveralone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Suff.this, SuffSingleModus.class);
                    startActivity(intent);
                }
            });
        }

        //Per Klick auf den PartySaufen-Button startet der SuffPartyModus
        if (partysaufen != null) {
            partysaufen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Die angefangenen Spiele werden um eins erhöht
                    final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);
                    MainActivity.angefangeneSpieleZaehlen(sp);
                    Intent intent = new Intent(Suff.this, SuffPartyModus.class);
                    startActivity(intent);
                }
            });
        }

        //Per Klick auf den Home-Button gelangt man zurück ins Hauptmenü
        if (home != null) {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Suff.this, MainActivity.class);
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

        //Per Klick auf den Hilfe-Button öffnet sich ein Dialog mit einer Suffmodusspezifischen Spielanleitung
        if (hilfe != null) {
            hilfe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog = new Dialog(Suff.this);
                    dialog.setContentView(R.layout.hilfe_dialog);

                    final TextView erklaerung = (TextView) dialog.findViewById(R.id.erklärung);
                    erklaerung.setText(R.string.alkoholikerHilfeSuff);

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
        //Delegation der Volume-Buttons an die Medienlautstärke, somit wird nicht die Klingeltonlautstärke gesetzt
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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
