package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private Sound clickSound;
    private Sound errorSound;
    private Sound attackSound;
    private Sound hurtSound;
    private Sound moveSound;

    private long moveSoundId;

    private Preferences preferences;
    private float masterVolume;

    public SoundManager() {
        preferences = Gdx.app.getPreferences("sound_save");
        masterVolume = preferences.getFloat("masterVolume", 0.8f);

        clickSound = loadSound("Sound/UI/sci_fi_select.wav");
        errorSound = loadSound("Sound/UI/sci_fi_error.wav");
        attackSound = loadSound("Sound/Weapons/sword_slice.wav");

        hurtSound = loadSound("Sound/Owie.mp3");
        moveSound = loadSound("Sound/Footsteps/grass.wav");

        moveSoundId = -1;
    }

    private Sound loadSound(String path) {
        if (Gdx.files.internal(path).exists()) {
            return Gdx.audio.newSound(Gdx.files.internal(path));
        }

        Gdx.app.error("SOUND", "Missing sound file: " + path);
        return null;
    }

    private void play(Sound sound) {
        if (sound != null) {
            sound.play(masterVolume);
        }
    }

    public void playClick() {
        play(clickSound);
    }

    public void playError() {
        play(errorSound);
    }

    public void playAttack() {
        play(attackSound);
    }

    public void playHurt() {
        play(hurtSound);
    }

    public void startMoveLoop() {
        if (moveSound == null) {
            return;
        }

        if (moveSoundId == -1) {
            moveSoundId = moveSound.loop(masterVolume * 0.45f);
        }
    }

    public void stopMoveLoop() {
        if (moveSound != null && moveSoundId != -1) {
            moveSound.stop(moveSoundId);
            moveSoundId = -1;
        }
    }

    public void increaseVolume() {
        masterVolume += 0.1f;

        if (masterVolume > 1.0f) {
            masterVolume = 1.0f;
        }

        if (moveSound != null && moveSoundId != -1) {
            moveSound.setVolume(moveSoundId, masterVolume * 0.45f);
        }

        saveVolume();
        playClick();
    }

    public void decreaseVolume() {
        masterVolume -= 0.1f;

        if (masterVolume < 0.0f) {
            masterVolume = 0.0f;
        }

        if (moveSound != null && moveSoundId != -1) {
            moveSound.setVolume(moveSoundId, masterVolume * 0.45f);
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
        stopMoveLoop();

        if (clickSound != null) {
            clickSound.dispose();
        }

        if (errorSound != null) {
            errorSound.dispose();
        }

        if (attackSound != null) {
            attackSound.dispose();
        }

        if (hurtSound != null) {
            hurtSound.dispose();
        }

        if (moveSound != null) {
            moveSound.dispose();
        }
    }
}
