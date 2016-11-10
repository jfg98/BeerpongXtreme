package com.example.gehrung.beerpongxtreme;

class GameEngineLevel5 extends GameEngine {

    GameEngineLevel5(GameView gameView, OnGameEventListener onGameEventListener) {
        super(gameView, onGameEventListener);
    }

    //Abfragen, ob die Flaschen getroffen wurden
    //Die Funktionen getFlaschen1Position() und getFlaschen2Position() geben ein Array mit den Koordinaten der Flaschen zurück(0->links, 1->oben, 2->rechts, 3->unten)
    //Abfragen, ob die Tequilaflasche getroffen wurden
    //Die Funktion getTequilaPosition() gibt ein Array mit den Koordinaten der Tequila zurück(0->links, 1->oben, 2->rechts, 3->unten)

    //Vorgehen der Algorithmen wie in der GameEngineLevel4, nur dass das Abprallen links nicht möglich ist
    @Override
    void levelSpecificQuery() {
        if (ballX > gameView.getFlaschen1Position()[0] && ballX < gameView.getFlaschen1Position()[2] && ballY > gameView.getFlaschen1Position()[1] && ballY < gameView.getFlaschen1Position()[3]) {

            final double abstandrechts = Math.abs(gameView.getFlaschen1Position()[2] - ballX);
            final double abstandunten = Math.abs(gameView.getFlaschen1Position()[3] - ballY) - gameView.getFlaschen1Width() / 10;

            if (ballVY < 0 && Math.min(abstandrechts, abstandunten) == abstandunten) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballY = gameView.getFlaschen1Position()[3];
                        ballVY = -ballVY * BOUNCE_FACTOR;
                    }
                });

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getFlaschen1Position()[2];
                        ballVX = -ballVX * BOUNCE_FACTOR;
                    }
                });
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onFlaschenHit();
                }
            });
        }


        if (ballX > gameView.getTequilaPosition()[0] && ballX < gameView.getTequilaPosition()[2] - 10 && ballY < gameView.getTequilaPosition()[3] && ballY > gameView.getTequilaPosition()[1]) {

            final double abstandlinks = Math.abs(gameView.getTequilaPosition()[0] - ballX);
            final double abstandrechts = Math.abs(gameView.getTequilaPosition()[2] - ballX);
            final double abstandunten = Math.abs(gameView.getTequilaPosition()[3] - ballY) - gameView.getTequilaWidth() / 7;

            if (ballVY < 0 && Math.min(Math.min(abstandlinks, abstandrechts), abstandunten) == abstandunten) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballY = gameView.getTequilaPosition()[3];
                        ballVY = -ballVY * BOUNCE_FACTOR;
                    }
                });

            } else if (ballVX < 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getTequilaPosition()[2];
                        ballVX = -ballVX * BOUNCE_FACTOR;
                    }
                });

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getTequilaPosition()[0];
                        ballVX = -ballVX * BOUNCE_FACTOR;
                    }
                });
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    onGameEventListener.onFlaschenHit();
                }
            });

        }
    }
}
