package org.umlproject;

import javafx.animation.AnimationTimer;


public class FXDispatcher extends MainThreadDispatcher {

    private AnimationTimer timer;
    
    public void start() {
        timer = new AnimationTimer() {
            public void handle(long now) {
                Runnable r;
                while ((r = queue.poll()) != null) r.run();
            }
        };
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
