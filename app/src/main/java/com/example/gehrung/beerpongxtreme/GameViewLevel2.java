package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

class GameViewLevel2 extends GameView {

    //Hindernis Level 2:
    private BitmapDrawable cat;
    private RectF drawRectCat = new RectF();

    GameViewLevel2(Context context) {
        super(context);

        //Bitmap für Level 2 laden
        cat = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.katze);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Hindernis für Level 2 zeichnen
        drawRectCat.set(getResources().getDisplayMetrics().widthPixels / 2 - cat.getBitmap().getWidth() / 4,
                getResources().getDisplayMetrics().heightPixels / 2 - cat.getBitmap().getHeight() / 4,
                getResources().getDisplayMetrics().widthPixels / 2 + cat.getBitmap().getWidth() / 4,
                getResources().getDisplayMetrics().heightPixels / 2 + cat.getBitmap().getHeight() / 4);

        canvas.drawBitmap(cat.getBitmap(), null, drawRectCat, paintBitmap);
    }

    float[] getCatPosition() {
        float[] test = {drawRectCat.left, drawRectCat.top, drawRectCat.right, drawRectCat.bottom};
        return test;
    }
}
