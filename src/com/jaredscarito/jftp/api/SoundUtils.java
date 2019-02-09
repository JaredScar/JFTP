package com.jaredscarito.jftp.api;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundUtils {
    private static SoundUtils soundUtils = new SoundUtils();
    public static SoundUtils getInstance() {
        return soundUtils;
    }

    public void playErrorSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("com/jaredscarito/jftp/resources/error_sound.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
