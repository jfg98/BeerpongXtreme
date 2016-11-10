package com.example.gehrung.beerpongxtreme;

import android.content.Context;
import android.content.Intent;

public class ParcoursLevel05 extends GameActivity {

    //Implementierung der abstrakten Methoden der GameActivity:
    @Override
    Context getThislevel() {
        return ParcoursLevel05.this;
    }

    @Override
    Class getNextLevel() {
        return ParcoursLevel06.class;
    }

    @Override
    int getLevel() {
        return 5;
    }

    @Override
    int getImageResource() {
        return R.drawable.level5;
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
