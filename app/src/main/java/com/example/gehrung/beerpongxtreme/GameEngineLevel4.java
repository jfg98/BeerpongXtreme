package com.example.gehrung.beerpongxtreme;

class GameEngineLevel4 extends GameEngine {

    GameEngineLevel4(GameView gameView, OnGameEventListener onGameEventListener) {
        super(gameView, onGameEventListener);
    }

    //Abfrage, ob die Katze getroffen wurde
    //Die Funktion getCatPosition() der GameView gibt ein Array mit den Koordinaten der Katze zurück (0->links, 1->oben, 2->rechts, 3->unten)
    //Die Funktion getCatWidth() der GameView gibt die Breite der Katze zurück
    //Abfragen, ob die Flaschen getroffen wurden
    //Die Funktionen getFlaschen1Position() und getFlaschen2Position() geben ein Array mit den Koordinaten der Flaschen zurück(0->links, 1->oben, 2->rechts, 3->unten)
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

        //Wird eine der 6 Flaschen getroffen, wird zunächst der Abstand zum linken, rechten und unteren Rand der Flaschen berechnet
        //Aus dem minimalen Abstand ergibt sich, ob der Ball schließlich rechts, links oder unten abprallen soll
        if (ballX > gameView.getFlaschen1Position()[0] && ballX < gameView.getFlaschen1Position()[2] && ballY > gameView.getFlaschen1Position()[1] && ballY < gameView.getFlaschen1Position()[3]) {

            final double abstandlinks = Math.abs(gameView.getFlaschen1Position()[0] - ballX);
            final double abstandrechts = Math.abs(gameView.getFlaschen1Position()[2] - ballX);
            //Wenn der Ball in die linke oder rechte untere Ecke fliegt, kann der Abstand zum linken bzw. rechten Rand kleiner als der untere Abstand sein,
            //obwohl der Ball eigentlich unten abprallt -> der untere Abstand wird daher noch einmal verringert
            final double abstandunten = Math.abs(gameView.getFlaschen1Position()[3] - ballY) - gameView.getFlaschen1Width() / 10;

            //Abprallen unten: Y-Geschwindigkeit des Balles wird negiert
            if (ballVY < 0 && Math.min(Math.min(abstandlinks, abstandrechts), abstandunten) == abstandunten) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballY = gameView.getFlaschen1Position()[3];
                        ballVY = -ballVY * BOUNCE_FACTOR;
                    }
                });

                //Abprallen rechts: Bedingung(x-Geschwindigkeit ist kleiner 0) -> X-Geschwindigkeit wird negiert
            } else if (ballVX < 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getFlaschen1Position()[2];
                        ballVX = -ballVX * BOUNCE_FACTOR;
                    }
                });

                //Abprallen links: X-Geschwindigkeit wird negiert
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getFlaschen1Position()[0];
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


        //Gleiches Vorgehen wie bei den 6 Flaschen, diesmal wird der Abstand oben miteinbezogen, da ein Abprallen oben möglich ist
        //Außerdem gibt es die Möglichkeit, dass der Ball rechts abprallt nicht
        if (ballX > gameView.getFlaschen2Position()[0] && ballX < gameView.getFlaschen2Position()[2] && ballY < gameView.getFlaschen2Position()[3] && ballY > gameView.getFlaschen2Position()[1]) {

            final double abstandlinks = Math.abs(gameView.getFlaschen2Position()[0] - ballX);
            final double abstandunten = Math.abs(gameView.getFlaschen2Position()[3] - ballY) - gameView.getFlaschen2Width() / 10;
            final double abstandoben = Math.abs(gameView.getFlaschen2Position()[1] - ballY);

            if (ballVY < 0 && Math.min(Math.min(abstandlinks, abstandunten), abstandoben) == abstandunten) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballY = gameView.getFlaschen2Position()[3];
                        ballVY = -ballVY * BOUNCE_FACTOR;
                    }
                });
            } else if (ballVY > 0 && Math.min(Math.min(abstandlinks, abstandunten), abstandoben) == abstandoben) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballY = gameView.getFlaschen2Position()[1];
                        ballVY = -ballVY * BOUNCE_FACTOR;
                    }
                });

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ballX = gameView.getFlaschen2Position()[0];
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
