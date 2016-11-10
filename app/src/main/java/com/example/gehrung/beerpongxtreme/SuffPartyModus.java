package com.example.gehrung.beerpongxtreme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SuffPartyModus extends GameActivity {

    //Welches Team ist gerade an der Reihe
    // --> startet mit Team rot
    private int team = 1;
    private ImageView teamImage;

    //Liste der bereits getroffenen Becher für beide Teams
    private List<Integer> dissapearedCupsTeam1 = new ArrayList<>();
    private List<Integer> dissapearedCupsTeam2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Unten links wird das aktuelle Team angezeigt
        //Dafür muss das ImageView der activity_main.xml erst sichtbar gestellt werden, da standardmäßig unsichtbar
        teamImage = (ImageView) findViewById(R.id.team);
        if (teamImage != null)
            teamImage.setVisibility(View.VISIBLE);
    }

    //Implementierte abstrakten Methoden der GameActivity:
    @Override
    Class getNextLevel() {
        return Suff.class;
    }

    @Override
    Context getThislevel() {
        return SuffPartyModus.this;
    }

    @Override
    int getLevel() {
        return 0;
    }

    @Override
    int getImageResource() {
        return 999;
    }

    //Das Untermenü Suff wird aufgerufen und der (unsichtbare) Spielstand zurückgesetzt
    @Override
    void levelFinishedAction() {
        Intent intent = new Intent(getThislevel(), getNextLevel());
        startActivity(intent);
        GameEngine.resetVersuche();
        finish();
    }

    //Wir nach jedem getroffenen Becher angezeigt
    private void saufenDialog() {
        dialog = new Dialog(SuffPartyModus.this);
        dialog.setContentView(R.layout.saufmodus_dialog);

        dialog.show();

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.saufmodus_imageView);
        imageView.setImageResource(R.drawable.treffer_suff);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (team == 1)
                    nextTeam(dissapearedCupsTeam1);
                if (team == 2)
                    nextTeam(dissapearedCupsTeam2);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (team == 1)
                    nextTeam(dissapearedCupsTeam1);
                if (team == 2)
                    nextTeam(dissapearedCupsTeam2);
            }
        });
    }

    //Wird angezeigt, sobald ein Team gewonnen hat
    //Ein Dialog öffnet sich, der signalisiert, welches Team gewonnen hat
    private void gameOverDialog() {
        dialog = new Dialog(SuffPartyModus.this);
        dialog.setContentView(R.layout.saufmodus_dialog);

        dialog.show();

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.saufmodus_imageView);

        if (imageView != null) {

            if (team == 1) {
                imageView.setImageResource(R.drawable.team_rot_gewonnen);
            }

            if (team == 2) {
                imageView.setImageResource(R.drawable.team_blau_gewonnen);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    levelFinishedAction();
                }
            });
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                levelFinishedAction();
            }
        });
    }

    //überschriebene Funktion der GameActivity
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

        gameEngine.setBallPosition(getResources().getDisplayMetrics().widthPixels / 2,
                getResources().getDisplayMetrics().heightPixels - 3 * gameView.getBaseDimension());
        cupDissapear(i);

        //Der getroffene Becher wird der Liste der getroffenen Bechern hinzugefügt
        if (team == 1) {
            dissapearedCupsTeam1.add(i);
        }
        if (team == 2) {
            dissapearedCupsTeam2.add(i);
        }

        //Wenn alle Becher getroffen wurden wird der gameOverDialog angezeigt, wenn nicht, wird das Spiel mit dem nächsten Team fortgesetzt
        if (cup1.getVisibility() == View.INVISIBLE
                && cup2.getVisibility() == View.INVISIBLE
                && cup3.getVisibility() == View.INVISIBLE
                && cup4.getVisibility() == View.INVISIBLE
                && cup5.getVisibility() == View.INVISIBLE
                && cup6.getVisibility() == View.INVISIBLE) {
            gameOverDialog();
        } else {
            if (team == 1) {
                team = 2;

            } else if (team == 2) {
                team = 1;
            }
            //Ein Dialog, der zum Trinken animieren soll, wird angezeigt
            saufenDialog();
            gameEngine.start();
        }
    }

    //Die Funktion färbt die Becher in der richtigen Farbe ein und lädt die noch nicht-getroffenen Becher eines Teams
    private void nextTeam(List<Integer> dissapearedCups) {

        cup1.setVisibility(View.VISIBLE);
        cup2.setVisibility(View.VISIBLE);
        cup3.setVisibility(View.VISIBLE);
        cup4.setVisibility(View.VISIBLE);
        cup5.setVisibility(View.VISIBLE);
        cup6.setVisibility(View.VISIBLE);

        //Alle Löcher standardmäßig aktivieren
        gameEngine.setHole1(false);
        gameEngine.setHole2(false);
        gameEngine.setHole3(false);
        gameEngine.setHole4(false);
        gameEngine.setHole5(false);
        gameEngine.setHole6(false);

        if (team == 1) {
            //rotes Teamimage und rote Becher
            teamImage.setImageResource(R.drawable.team_rot);
            cup1.setImageResource(R.drawable.redcup);
            cup2.setImageResource(R.drawable.redcup);
            cup3.setImageResource(R.drawable.redcup);
            cup4.setImageResource(R.drawable.redcup);
            cup5.setImageResource(R.drawable.redcup);
            cup6.setImageResource(R.drawable.redcup);
        }

        if (team == 2) {
            //blaues Teamimage und blaue Becher
            teamImage.setImageResource(R.drawable.team_blau);
            cup1.setImageResource(R.drawable.bluecup);
            cup2.setImageResource(R.drawable.bluecup);
            cup3.setImageResource(R.drawable.bluecup);
            cup4.setImageResource(R.drawable.bluecup);
            cup5.setImageResource(R.drawable.bluecup);
            cup6.setImageResource(R.drawable.bluecup);
        }

        for (int i = 0; i < dissapearedCups.size(); i++) {
            cupDissapear(dissapearedCups.get(i));

            //Bereits getroffene Becher deaktivieren
            switch (dissapearedCups.get(i)) {
                case 1:
                    gameEngine.setHole1(true);
                    break;
                case 2:
                    gameEngine.setHole2(true);
                    break;
                case 3:
                    gameEngine.setHole3(true);
                    break;
                case 4:
                    gameEngine.setHole4(true);
                    break;
                case 5:
                    gameEngine.setHole5(true);
                    break;
                case 6:
                    gameEngine.setHole6(true);
                    break;
            }
        }
    }

    //überschriebene Funktion der GameActivity
    @Override
    public void onVersuchFailed() {

        gameEngine.setBallPosition(getResources().getDisplayMetrics().widthPixels / 2,
                getResources().getDisplayMetrics().heightPixels - 3 * gameView.getBaseDimension());

        //Das nächste Team wird initialisiert und das Spiel fortgesetzt
        if (team == 1) {
            team = 2;
            nextTeam(dissapearedCupsTeam2);
        } else if (team == 2) {
            team = 1;
            nextTeam(dissapearedCupsTeam1);
        }
        gameEngine.start();
    }

}
