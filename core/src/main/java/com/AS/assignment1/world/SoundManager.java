package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    //Sound effect used when clicking UI buttons
    private Sound clickSound;

    //Sound effect used for error actions
    private Sound errorSound;

    //Sound effect used when the player attacks
    private Sound attackSound;

    //Sound effect used when the player gets hurt
    private Sound hurtSound;

    //Sound effect used for player movement footsteps
    private Sound moveSound;

    //Stores the ID of the looping movement sound
    private long moveSoundId;

    //Preferences object used to save and load volume settings
    private Preferences preferences;

    //Main volume value for all sound effects, from 0.0 to 1.0
    private float masterVolume;

    public SoundManager() {
        //Load saved sound preferences
        preferences = Gdx.app.getPreferences("sound_save");

        //Load the saved master volume, or use 0.8 as the default value
        masterVolume = preferences.getFloat("masterVolume", 0.8f);

        //Load UI sound effects
        clickSound = loadSound("Sound/UI/sci_fi_select.wav");
        errorSound = loadSound("Sound/UI/sci_fi_error.wav");

        //Load player action sound effects
        attackSound = loadSound("Sound/Weapons/sword_slice.wav");
        hurtSound = loadSound("Sound/Owie.mp3");

        //Load movement sound effect
        moveSound = loadSound("Sound/Footsteps/grass.wav");

        //Set the movement sound ID to -1 to show that it is not playing yet
        moveSoundId = -1;
    }

    private Sound loadSound(String path) {
        //Check if the sound file exists before loading it
        if (Gdx.files.internal(path).exists()) {
            return Gdx.audio.newSound(Gdx.files.internal(path));
        }

        //Print an error message if the sound file is missing
        Gdx.app.error("SOUND", "Missing sound file: " + path);
        return null;
    }

    private void play(Sound sound) {
        //Play the sound only if it was loaded successfully
        if (sound != null) {
            sound.play(masterVolume);
        }
    }

    public void playClick() {
        //Play the UI click sound
        play(clickSound);
    }

    public void playError() {
        //Play the error sound
        play(errorSound);
    }

    public void playAttack() {
        //Play the attack sound
        play(attackSound);
    }

    public void playHurt() {
        //Play the hurt sound
        play(hurtSound);
    }

    public void startMoveLoop() {
        //Do nothing if the movement sound was not loaded
        if (moveSound == null) {
            return;
        }

        // Start looping the movement sound only if it is not already playing
        if (moveSoundId == -1) {
            moveSoundId = moveSound.loop(masterVolume * 0.45f);
        }
    }

    public void stopMoveLoop() {
        //Stop the movement loop if it is currently playing
        if (moveSound != null && moveSoundId != -1) {
            moveSound.stop(moveSoundId);
            moveSoundId = -1;
        }
    }

    public void increaseVolume() {
        //Increase the master volume by 10%
        masterVolume += 0.1f;

        //Prevent the volume from going above 100%
        if (masterVolume > 1.0f) {
            masterVolume = 1.0f;
        }

        //Update the movement loop volume if it is currently playing
        if (moveSound != null && moveSoundId != -1) {
            moveSound.setVolume(moveSoundId, masterVolume * 0.45f);
        }

        //Save the new volume value
        saveVolume();

        //Play a click sound to give feedback to the player
        playClick();
    }

    public void decreaseVolume() {
        //Decrease the master volume by 10%
        masterVolume -= 0.1f;

        //Prevent the volume from going below 0%
        if (masterVolume < 0.0f) {
            masterVolume = 0.0f;
        }

        //Update the movement loop volume if it is currently playing
        if (moveSound != null && moveSoundId != -1) {
            moveSound.setVolume(moveSoundId, masterVolume * 0.45f);
        }

        //Save the new volume value
        saveVolume();

        //Play a click sound to give feedback to the player
        playClick();
    }

    public float getMasterVolume() {
        //Return the current master volume value
        return masterVolume;
    }

    public int getVolumePercent() {
        //Convert the master volume into a percentage
        return Math.round(masterVolume * 100);
    }

    private void saveVolume() {
        //Save the master volume value to preferences
        preferences.putFloat("masterVolume", masterVolume);

        //Write the preference changes to storage
        preferences.flush();
    }

    public void dispose() {
        //Stop the movement sound before disposing it
        stopMoveLoop();

        //Dispose the click sound to free memory
        if (clickSound != null) {
            clickSound.dispose();
        }

        //Dispose the error sound to free memory
        if (errorSound != null) {
            errorSound.dispose();
        }

        //Dispose the attack sound to free memory
        if (attackSound != null) {
            attackSound.dispose();
        }

        //Dispose the hurt sound to free memory
        if (hurtSound != null) {
            hurtSound.dispose();
        }

        //Dispose the movement sound to free memory
        if (moveSound != null) {
            moveSound.dispose();
        }
    }
}
