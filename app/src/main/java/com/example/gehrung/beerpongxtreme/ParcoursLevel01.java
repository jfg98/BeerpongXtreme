package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.content.Intent;

public class ParcoursLevel01 extends GameActivity {

    //Implementierung der abstrakten Methoden der GameActivity:
    @Override
    Context getThislevel() {
        return ParcoursLevel01.this;
    }

    @Override
    Class getNextLevel() {
        return ParcoursLevel02.class;
    }

    @Override
    int getLevel() {
        return 1;
    }

    @Override
    int getImageResource() {
        return R.drawable.level1;
    }

    //Das nächste Level wird aufgerufen und die Activity wird endgültig beendet
    @Override
    void levelFinishedAction() {
        Intent intent = new Intent(getThislevel(), getNextLevel());
        startActivity(intent);
        container.removeAllViews();
        finish();
    }
}
