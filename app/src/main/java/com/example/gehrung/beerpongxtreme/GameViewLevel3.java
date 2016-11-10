package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

class GameViewLevel3 extends GameView {

    //Hindernisse Level 3:
    private BitmapDrawable alkoholleiche;
    private RectF drawRectLeiche = new RectF();
    private BitmapDrawable portal;
    private RectF drawRectPortal1 = new RectF();
    private RectF drawRectPortal2 = new RectF();

    GameViewLevel3(Context context) {
        super(context);

        //Bitmaps für Level 3 laden
        alkoholleiche = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.suffkopf2);
        portal = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.portal);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Hindernisse für Level 3 zeichnen
        drawRectPortal1.set(getResources().getDisplayMetrics().widthPixels / 2 - 9 * portal.getBitmap().getWidth() / 28,
                28 * getResources().getDisplayMetrics().heightPixels / 40 - 9 * portal.getBitmap().getHeight() / 28,
                getResources().getDisplayMetrics().widthPixels / 2 + 9 * portal.getBitmap().getWidth() / 28,
                28 * getResources().getDisplayMetrics().heightPixels / 40 + 9 * portal.getBitmap().getHeight() / 28);

        canvas.drawBitmap(portal.getBitmap(), null, drawRectPortal2, paintBitmap);

        drawRectPortal2.set(getResources().getDisplayMetrics().widthPixels / 2 - 9 * portal.getBitmap().getWidth() / 28,
                11 * getResources().getDisplayMetrics().heightPixels / 40 - 9 * portal.getBitmap().getHeight() / 28,
                getResources().getDisplayMetrics().widthPixels / 2 + 9 * portal.getBitmap().getWidth() / 28,
                11 * getResources().getDisplayMetrics().heightPixels / 40 + 9 * portal.getBitmap().getHeight() / 28);

        canvas.drawBitmap(portal.getBitmap(), null, drawRectPortal1, paintBitmap);

        drawRectLeiche.set(getResources().getDisplayMetrics().widthPixels / 2 - alkoholleiche.getBitmap().getWidth() / 3,
                getResources().getDisplayMetrics().heightPixels / 2 - alkoholleiche.getBitmap().getHeight() / 3,
                getResources().getDisplayMetrics().widthPixels / 2 + alkoholleiche.getBitmap().getWidth() / 3,
                getResources().getDisplayMetrics().heightPixels / 2 + alkoholleiche.getBitmap().getHeight() / 3);

        canvas.drawBitmap(alkoholleiche.getBitmap(), null, drawRectLeiche, paintBitmap);
    }

    float[] getPortal1Position() {
        float[] test = {drawRectPortal1.left, drawRectPortal1.top, drawRectPortal1.right, drawRectPortal1.bottom};
        return test;
    }

    float[] getPortal2Position() {
        float[] test = {drawRectPortal2.left, drawRectPortal2.top, drawRectPortal2.right, drawRectPortal2.bottom};
        return test;
    }
}
