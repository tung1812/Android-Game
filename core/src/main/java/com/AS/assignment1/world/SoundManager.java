package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private Sound clickSound;
    private Sound errorSound;

    private Preferences preferences;
    private float masterVolume;

    public SoundManager() {
        preferences = Gdx.app.getPreferences("sound_save");
        masterVolume = preferences.getFloat("masterVolume", 0.8f);

        clickSound = loadSound("Sound/UI/sci_fi_select.wav");
        errorSound = loadSound("Sound/UI/sci_fi_error.wav");
    }

    private Sound loadSound(String path) {
        if (Gdx.files.internal(path).exists()) {
            return Gdx.audio.newSound(Gdx.files.internal(path));
        }

        Gdx.app.error("SOUND", "Missing sound file: " + path);
        return null;
    }

    public void playClick() {
        play(clickSound);
    }

    public void playError() {
        play(errorSound);
    }

    private void play(Sound sound) {
        if (sound != null) {
            sound.play(masterVolume);
        }
    }

    public void increaseVolume() {
        masterVolume += 0.1f;

        if (masterVolume > 1.0f) {
            masterVolume = 1.0f;
        }

        saveVolume();
        playClick();
    }

    public void decreaseVolume() {
        masterVolume -= 0.1f;

        if (masterVolume < 0.0f) {
            masterVolume = 0.0f;
        }

        saveVolume();
        playClick();
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public int getVolumePercent() {
        return Math.round(masterVolume * 100);
    }

    private void saveVolume() {
        preferences.putFloat("masterVolume", masterVolume);
        preferences.flush();
    }

    public void dispose() {
        if (clickSound != null) {
            clickSound.dispose();
        }

        if (errorSound != null) {
            errorSound.dispose();
        }
    }
}
