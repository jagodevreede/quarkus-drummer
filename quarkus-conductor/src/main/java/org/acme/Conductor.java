package org.acme;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Conductor {
    private static final Logger LOGGER = Logger.getLogger("Conductor");

    private static final long MS_IN_MINUTE = 60_000;

    private final short tempo = 100;

    @Inject
    ConductorListener listener;

    @Scheduled(every = "15s")
    public void run() {
        short currentBeatNumber = 1;
        int playedBeats = 0;
        LOGGER.info("Starting conductor");
        long lastBeat = System.currentTimeMillis();
        long nextBeat = lastBeat;
        while (playedBeats <= 32) {
            playedBeats++;
            lastBeat = nextBeat;
            nextBeat = lastBeat + ((MS_IN_MINUTE / tempo / 2));
            sleepUntil(nextBeat);
            listener.onBeat(currentBeatNumber);
            if (currentBeatNumber == 8) {
                currentBeatNumber = 1;
            } else {
                currentBeatNumber++;
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
