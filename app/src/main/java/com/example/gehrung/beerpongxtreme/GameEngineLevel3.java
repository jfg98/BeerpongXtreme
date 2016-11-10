package com.example.gehrung.beerpongxtreme;

class GameEngineLevel3 extends GameEngine {

    GameEngineLevel3(GameView gameView, OnGameEventListener onGameEventListener) {
        super(gameView, onGameEventListener);
    }

    //Abfrage, ob der Ball den oberen Rand des unteren Portals durchdringt
    //Die Funktionen getPortal1Position() oder getPortal2Position() der GameView geben ein Array mit den Koordinaten des Portals zurück (0->links, 1->oben, 2->rechts, 3->unten)
    @Override
    void levelSpecificQuery() {
        //Wenn der Ball in das untere Portal eindringt, wird er im oberen Portal wieder losgelassen
        if (ballY - gameView.getBaseDimension() < gameView.getPortal1Position()[1] && ballY > gameView.getPortal2Position()[3] + 100) {

            //Der Ball wird jeweils auf der anderen Seite des Displays wieder losgelassen
            if (ballX > gameView.getMiddleOfDisplay()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getMiddleOfDisplay() - (ballX - gameView.getMiddleOfDisplay());
                        ballY = gameView.getPortal2Position()[3] - gameView.getBaseDimension();
                    }
                });

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getMiddleOfDisplay() + (gameView.getMiddleOfDisplay() - ballX);
                        ballY = gameView.getPortal2Position()[3] - gameView.getBaseDimension();
                    }
                });

            }
        }
    }
}
