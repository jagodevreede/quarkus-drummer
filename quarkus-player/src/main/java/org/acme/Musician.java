package org.acme;

import org.jboss.logging.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public class Musician {
    private static final Logger LOGGER = Logger.getLogger("Musician");
    private final Queue<Long> beatTimes = new CircularQueue<>(3);
    private final String patternToPlay;

    private long tempo = 100;

    private final List<File> wavs;


    public Musician(File folder, String kind, String patternToPlay) {
        this.patternToPlay = patternToPlay;
        wavs = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(f -> f.getName().contains(kind))
                .toList();
        if (wavs.isEmpty()) {
            throw new RuntimeException("No wavs found for " + kind);
        }
    }

    public void beat(short number) {
        beatTimes.add(System.currentTimeMillis());
        List<Long> list = beatTimes.stream().toList();
        if (list.size() > 2) {
            double averageDiff = 0;
            for (int i = 1; i < list.size(); i++) {
                final long first = list.get(i - 1);
                final long second = list.get(i);
                averageDiff += (second - first);
            }
            long newTempo = Math.round(60000 / (averageDiff / (list.size() - 1)));
            if (newTempo != tempo) {
                LOGGER.info("New tempo detected: " + newTempo);
                tempo = newTempo;
            }
        }
        if (tempo > 0) {
            play(number);
        }
    }

    public void play(short number) {
        char c = patternToPlay.charAt(number - 1);
        if (c != 'x') {
            return;
        }
        try {
            int randomElementIndex = ThreadLocalRandom.current().nextInt(wavs.size()) % wavs.size();
            AudioInputStream stream = AudioSystem.getAudioInputStream(wavs.get(randomElementIndex));
            var format = stream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(dataLineInfo);
            clip.open(stream);
            clip.setMicrosecondPosition(0);
            clip.start();
        } catch (LineUnavailableException | IOException | javax.sound.sampled.UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }
}
