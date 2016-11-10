package com.example.gehrung.beerpongxtreme;

class GameEngineLevel2 extends GameEngine {

    GameEngineLevel2(GameView gameView, OnGameEventListener onGameEventListener) {
        super(gameView, onGameEventListener);
    }

    //Abfrage, ob die Katze getroffen wurde
    //Die Funktion getCatPosition() der GameView gibt ein Array mit den Koordinaten der Katze zurück (0->links, 1->oben, 2->rechts, 3->unten)
    //Die Funktion getCatWidth() der GameView gibt die Breite der Katze zurück
    @Override
    void levelSpecificQuery() {
        //Wird die Katze getroffen, endet der Versuch und der Katzensound wird durch die GameActivity abgespielt
        if (ballY < gameView.getCatPosition()[3] - gameView.getCatWidth() / 10 && ballY > gameView.getCatPosition()[1] + gameView.getCatWidth() / 10 && ballX > gameView.getCatPosition()[0] + gameView.getCatWidth() / 10 && ballX < gameView.getCatPosition()[2] - gameView.getCatWidth() / 5) {
            stop();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onVersuchFailed();
                    onGameEventListener.onCatHit();
                }
            });
        }
    }
}
