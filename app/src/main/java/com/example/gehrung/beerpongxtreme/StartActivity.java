package com.example.gehrung.beerpongxtreme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

//Startbildschirm der Application
public class StartActivity extends AppCompatActivity {

    private boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //Nach 2 Sekunden wird das Hauptmenü aufgerufen
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                if (!finished) {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                }

            }

        }, 2000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            finished = true;
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
        return super.onTouchEvent(event);
    }
}
