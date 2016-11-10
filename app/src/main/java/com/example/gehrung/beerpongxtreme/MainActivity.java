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
import android.widget.Toast;

//Hauptmenü-Klasse
public class MainActivity extends AppCompatActivity {

    //Objekt zum Anzeigen spezieller Dialoge
    private Dialog dialog;
    //Klassenvariable Musik-an oder Musik-aus
    static boolean musikCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Menübuttons initialisieren
        final ImageButton parcours = (ImageButton) findViewById(R.id.parcours);
        final ImageButton suff = (ImageButton) findViewById(R.id.suff);
        final ImageButton information = (ImageButton) findViewById(R.id.information);
        final ImageButton hilfe = (ImageButton) findViewById(R.id.hilfe);
        final ImageButton home = (ImageButton) findViewById(R.id.home);

        //Per Klick auf den Parcours-Button wird die Parcours-Activity aufgerufen
        if (parcours != null) {
            parcours.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Parcours.class);
                    startActivity(intent);
                }
            });
        }

        //Per Klick auf den Suff-Button wird die Suff-Activity aufgerufen
        if (suff != null) {
            suff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Suff.class);
                    startActivity(intent);
                }
            });
        }

        //Wird auf den Home-Button geklickt, erscheint ein Toast
        if (home != null) {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.MainActivityHomebutton), Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Per Klick auf den Hilfe-Button öffnet sich ein Dialog mit einer Spielanleitung
        if (hilfe != null) {
            hilfe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.hilfe_dialog);

                    final TextView erklaerung = (TextView) dialog.findViewById(R.id.erklärung);
                    erklaerung.setText(R.string.alkoholikerHilfeStart);

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

        //Per Klick auf den Information-Button öffnet sich ein Dialog mit Allgemeinen Informationen über das Spiel
        if (information != null) {
            information.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.information_dialog);

                    TextView credits = (TextView) dialog.findViewById(R.id.credits);
                    credits.setMovementMethod(new ScrollingMovementMethod());

                    dialog.show();

                    final TextView angefangeneSpieleTextView = (TextView) dialog.findViewById(R.id.angefangeneSpiele);

                    final SharedPreferences sp = getSharedPreferences("highscoreData", Context.MODE_PRIVATE);
                    final int angefangeneSpiele = sp.getInt("angefangeneSpiele", 0);

                    angefangeneSpieleTextView.setText(Integer.toString(angefangeneSpiele));

                    final TextView abgebrocheneSpieleTextView = (TextView) dialog.findViewById(R.id.abgebrocheneSpiele);

                    final int abgebrocheneSpiele = sp.getInt("abgebrocheneSpiele", 0);

                    abgebrocheneSpieleTextView.setText(Integer.toString(abgebrocheneSpiele));


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

    //Wird im Hauptmenü der Back-Button auf dem Smartphone geklickt, wird die App minimiert
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    //Zähler für angefangene Spiele erhöhen
    static void angefangeneSpieleZaehlen(SharedPreferences sp) {
        final SharedPreferences.Editor spEditor = sp.edit();
        int angefangeneSpiele = sp.getInt("angefangeneSpiele", 0);
        angefangeneSpiele++;
        spEditor.putInt("angefangeneSpiele", angefangeneSpiele);
        spEditor.commit();
    }

    //Zähler für abgebrochene Spiele erhöhen
    static void abgebrocheneSpieleZaehlen(SharedPreferences sp) {
        final SharedPreferences.Editor spEditor = sp.edit();
        int abgebrocheneSpiele = sp.getInt("abgebrocheneSpiele", 0);
        abgebrocheneSpiele++;
        spEditor.putInt("abgebrocheneSpiele", abgebrocheneSpiele);
        spEditor.commit();
    }

}