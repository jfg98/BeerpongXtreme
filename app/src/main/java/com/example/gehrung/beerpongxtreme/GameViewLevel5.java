package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

class GameViewLevel5 extends GameView {

    //Hindernisse Level 5:
    private BitmapDrawable tequila;
    private RectF drawRectTequila = new RectF();
    private BitmapDrawable flaschen1;
    private RectF drawRectFlaschen1 = new RectF();

    GameViewLevel5(Context context) {
        super(context);

        //Bitmaps für Level 5 laden
        flaschen1 = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.bierflaschen6_farbig);

        tequila = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.tequila_set_farbe);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Hindernisse für Level 5 zeichnen
        drawRectFlaschen1.set(getResources().getDisplayMetrics().widthPixels / 9 - flaschen1.getBitmap().getWidth() / 10,
                17 * getResources().getDisplayMetrics().heightPixels / 60 - flaschen1.getBitmap().getHeight() / 10,
                getResources().getDisplayMetrics().widthPixels / 9 + flaschen1.getBitmap().getWidth() / 10,
                17 * getResources().getDisplayMetrics().heightPixels / 60 + flaschen1.getBitmap().getHeight() / 10);

        canvas.drawBitmap(flaschen1.getBitmap(), null, drawRectFlaschen1, paintBitmap);

        drawRectTequila.set(getResources().getDisplayMetrics().widthPixels - 22 * getResources().getDisplayMetrics().widthPixels / 80 - tequila.getBitmap().getWidth() / 4,
                getResources().getDisplayMetrics().heightPixels - 16 * getResources().getDisplayMetrics().heightPixels / 50 - tequila.getBitmap().getHeight() / 4,
                getResources().getDisplayMetrics().widthPixels - 22 * getResources().getDisplayMetrics().widthPixels / 80,
                getResources().getDisplayMetrics().heightPixels - 16 * getResources().getDisplayMetrics().heightPixels / 50);
        canvas.drawBitmap(tequila.getBitmap(), null, drawRectTequila, paintBitmap);

    }

    float[] getFlaschen1Position() {
        float[] test = {drawRectFlaschen1.left, drawRectFlaschen1.top, drawRectFlaschen1.right, drawRectFlaschen1.bottom};
        return test;
    }

    float[] getTequilaPosition() {
        float[] test = {drawRectTequila.left, drawRectTequila.top, drawRectTequila.right, drawRectTequila.bottom};
        return test;
    }


}
