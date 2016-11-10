package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.content.Intent;

public class ParcoursLevel03 extends GameActivity {

    //Implementierung der abstrakten Methoden der GameActivity:
    @Override
    Context getThislevel() {
        return ParcoursLevel03.this;
    }

    @Override
    Class getNextLevel() {
        return ParcoursLevel04.class;
    }

    @Override
    int getLevel() {
        return 3;
    }

    @Override
    int getImageResource() {
        return R.drawable.level3;
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
