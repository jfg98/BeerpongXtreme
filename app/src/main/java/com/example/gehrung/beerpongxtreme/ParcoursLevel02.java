package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.content.Intent;

public class ParcoursLevel02 extends GameActivity {

    //Implementierung der abstrakten Methoden der GameActivity:
    @Override
    Context getThislevel() {
        return ParcoursLevel02.this;
    }

    @Override
    Class getNextLevel() {
        return ParcoursLevel03.class;
    }

    @Override
    int getLevel() {
        return 2;
    }

    @Override
    int getImageResource() {
        return R.drawable.level2;
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
