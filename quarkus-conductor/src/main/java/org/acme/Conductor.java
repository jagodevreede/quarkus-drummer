package org.acme;

import org.jboss.logging.Logger;

public class Conductor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("Conductor");

    private static final long MS_IN_MINUTE = 60_000;

    private short noOfBeats = 1;

    private short tempo = 100;

    private boolean running = true;

    private ConductorListener listener;

    public Conductor(ConductorListener listener) {
        this.listener = listener;
    }

    public void stop() {
        running = false;
        LOGGER.info("Conductor stopped");
    }

    @Override
    public void run() {
        LOGGER.info("Starting conductor");
        long lastBeat = System.currentTimeMillis();
        long nextBeat = lastBeat;
        while (running) {
            lastBeat = nextBeat;
            nextBeat = lastBeat + ((MS_IN_MINUTE / tempo / 2));
            sleepUntil(nextBeat);
            listener.onBeat(noOfBeats);
            if (noOfBeats >= 8) {
                noOfBeats = 1;
            } else {
                noOfBeats++;
            }
        }
        LOGGER.info("Conductor stopped");
    }

    private static void sleepUntil(long nextBeat) {
        try {
            final long sleepTime = nextBeat - System.currentTimeMillis() - 1;
            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            } else {
                LOGGER.warn("Conductor beat missed");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
