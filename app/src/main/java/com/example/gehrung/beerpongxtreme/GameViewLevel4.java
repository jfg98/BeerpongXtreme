package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

class GameViewLevel4 extends GameView {

    //Hindernisse Level 4:
    private BitmapDrawable flaschen1;
    private RectF drawRectFlaschen1 = new RectF();
    private BitmapDrawable flaschen2;
    private RectF drawRectFlaschen2 = new RectF();
    private BitmapDrawable cat;
    private RectF drawRectCat = new RectF();

    GameViewLevel4(Context context) {
        super(context);

        //Bitmaps für Level 4 laden
        flaschen1 = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.bierflaschen6_farbig);

        flaschen2 = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.bierflaschen3_farbig);

        cat = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.katze);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Hindernisse für Level 4 zeichnen
        drawRectFlaschen1.set(getResources().getDisplayMetrics().widthPixels / 2 - flaschen1.getBitmap().getWidth() / 10,
                23 * getResources().getDisplayMetrics().heightPixels / 60 - flaschen1.getBitmap().getHeight() / 10,
                getResources().getDisplayMetrics().widthPixels / 2 + flaschen1.getBitmap().getWidth() / 10,
                23 * getResources().getDisplayMetrics().heightPixels / 60 + flaschen1.getBitmap().getHeight() / 10);
        canvas.drawBitmap(flaschen1.getBitmap(), null, drawRectFlaschen1, paintBitmap);

        drawRectFlaschen2.set(getResources().getDisplayMetrics().widthPixels - getResources().getDisplayMetrics().widthPixels / 100 - flaschen2.getBitmap().getWidth() / 5,
                getResources().getDisplayMetrics().heightPixels - getResources().getDisplayMetrics().heightPixels / 6 - flaschen2.getBitmap().getHeight() / 5,
                getResources().getDisplayMetrics().widthPixels - getResources().getDisplayMetrics().widthPixels / 100,
                getResources().getDisplayMetrics().heightPixels - getResources().getDisplayMetrics().heightPixels / 6);
        canvas.drawBitmap(flaschen2.getBitmap(), null, drawRectFlaschen2, paintBitmap);

        drawRectCat.set(getResources().getDisplayMetrics().widthPixels / 2 - 2 * cat.getBitmap().getWidth() / 10,
                getResources().getDisplayMetrics().heightPixels - 11 * cat.getBitmap().getHeight() / 10,
                getResources().getDisplayMetrics().widthPixels / 2 + 2 * cat.getBitmap().getWidth() / 10,
                getResources().getDisplayMetrics().heightPixels - 7 * cat.getBitmap().getHeight() / 10);
        canvas.drawBitmap(cat.getBitmap(), null, drawRectCat, paintBitmap);

    }

    float[] getCatPosition() {
        float[] test = {drawRectCat.left, drawRectCat.top, drawRectCat.right, drawRectCat.bottom};
        return test;
    }

    float[] getFlaschen1Position() {
        float[] test = {drawRectFlaschen1.left, drawRectFlaschen1.top, drawRectFlaschen1.right, drawRectFlaschen1.bottom};
        return test;
    }

    float[] getFlaschen2Position() {
        float[] test = {drawRectFlaschen2.left, drawRectFlaschen2.top, drawRectFlaschen2.right, drawRectFlaschen2.bottom};
        return test;
    }
}
