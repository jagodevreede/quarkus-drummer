package org.acme;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Musician {
    private final String patternToPlay;

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
        play(number);
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
