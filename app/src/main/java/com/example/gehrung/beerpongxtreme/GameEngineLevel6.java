package com.example.gehrung.beerpongxtreme;

class GameEngineLevel6 extends GameEngine {

    GameEngineLevel6(GameView gameView, OnGameEventListener onGameEventListener) {
        super(gameView, onGameEventListener);
    }

    //Abfrage, ob der Ball den Ventilator passiert
    @Override
    void levelSpecificQuery() {

        //Ist der Ball auf der Höhe des Ventilators wird die x-Geschwindigkeit erhöht
        if (ballY > ParcoursLevel06.ventilatorImage.getTop() && ballY < ParcoursLevel06.ventilatorImage.getBottom()) {
            ballVX += 300;
        }
    }
}
