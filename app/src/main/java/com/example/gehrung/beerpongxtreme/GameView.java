package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

//Klasse für die Spieloberfläche
//Diese Spieloberfläche wird alle 20 ms neu gezeichnet
class GameView extends View {

    //Größe des Balles
    private static final float SIZE = 32;
    //Skalierungsfaktor des Bildschirms
    private float scale;
    //Koordinaten des Balles
    private float ballX, ballY;

    //Benötigte Objekte zum Zeichnen des Balles
    private BitmapDrawable ball;
    Paint paintBitmap = new Paint();
    private RectF drawRect = new RectF();

    //Konstruktor, der die benötigten Variablen zum Zeichnen des Balles initialisiert
    GameView(Context context) {
        super(context);

        scale = getResources().getDisplayMetrics().density;
        ball = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ball);
        paintBitmap.setAntiAlias(true);
    }

    //Zeichnet die GameView bei jedem Aufruf von setBallPosition() durch die gameActivity (alle 20ms) neu
    //Die Funktion wird von den Unterklassen überschrieben, da dort noch weitere Objekte gezeichnet werden müssen
    @Override
    protected void onDraw(Canvas canvas) {

        drawRect.set(ballX - SIZE * scale / 2, ballY - SIZE * scale / 2,
                ballX + SIZE * scale / 2, ballY + SIZE * scale / 2);

        canvas.drawBitmap(ball.getBitmap(), null, drawRect, paintBitmap);
    }

    //positioniert den Ball und ruft onDraw() auf
    void setBallPosition(float x, float y) {
        ballX = x;
        ballY = y;
        invalidate();
    }

    //Setzt die TextView für den Highscore auf der Oberfläche
    void updateVersuche(int x) {
        GameActivity.versuche.setText(Integer.toString(x));
    }

    //"Standard-Längeneinheit" -> eine Balllänge
    float getBaseDimension() {
        return scale * SIZE;
    }

    //gibt die Mitte des Displays zurück
    int getMiddleOfDisplay() {
        return getResources().getDisplayMetrics().widthPixels / 2;
    }

    //Koordinaten der Hindernisse etc.
    //Die Funktionen werden von den Unterklassen überschrieben
    float[] getCatPosition() {
        return null;
    }

    float getCatWidth() {
        return getCatPosition()[2] - getCatPosition()[0];
    }

    float[] getFlaschen1Position() {
        return null;
    }

    float getFlaschen1Width() {
        return getFlaschen1Position()[2] - getFlaschen1Position()[0];
    }

    float[] getFlaschen2Position() {
        return null;
    }

    float getFlaschen2Width() {
        return getFlaschen2Position()[2] - getFlaschen2Position()[0];
    }

    float[] getTequilaPosition() {
        return null;
    }

    float getTequilaWidth() {
        return getTequilaPosition()[2] - getTequilaPosition()[0];
    }

    float[] getPortal1Position() {
        return null;
    }

    float[] getPortal2Position() {
        return null;
    }

}
