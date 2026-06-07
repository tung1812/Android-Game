package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BGMManager {

    //Music object used to store and control the background music
    private Music bgm;

    //Current background music volume, from 0.0 to 1.0
    private float volume;

    public BGMManager() {
        //Set the default background music volume
        volume = 0.45f;

        //Load the background music file
        bgm = loadMusic("BGM/bgm.m4a");
    }

    private Music loadMusic(String path) {
        //Check if the music file exists before loading it
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("BGM", "Missing BGM file: " + path);
            return null;
        }

        //Create a new Music object from the file path
        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));

        //Make the background music loop continuously
        music.setLooping(true);

        //Apply the current volume to the music
        music.setVolume(volume);

        //Return the loaded music object
        return music;
    }

    public void play() {
        //Do nothing if the background music was not loaded
        if (bgm == null) {
            return;
        }

        //Start playing the music only if it is not already playing
        if (!bgm.isPlaying()) {
            bgm.setLooping(true);
            bgm.setVolume(volume);
            bgm.play();
        }
    }

    public void stop() {
        //Stop the background music if it exists
        if (bgm != null) {
            bgm.stop();
        }
    }

    public void pause() {
        //Pause the background music if it exists
        if (bgm != null) {
            bgm.pause();
        }
    }

    public void increaseVolume() {
        //Increase the volume by 10%
        volume += 0.1f;

        //Prevent the volume from going above 100%
        if (volume > 1f) {
            volume = 1f;
        }

        //Apply the new volume to the background music
        updateVolume();
    }

    public void decreaseVolume() {
        //Decrease the volume by 10%
        volume -= 0.1f;

        //Prevent the volume from going below 0%
        if (volume < 0f) {
            volume = 0f;
        }

        //Apply the new volume to the background music
        updateVolume();
    }

    private void updateVolume() {
        //Update the music volume if the music object exists
        if (bgm != null) {
            bgm.setVolume(volume);
        }
    }

    public int getVolumePercent() {
        //Convert the volume value into a percentage
        return Math.round(volume * 100f);
    }

    public void dispose() {
        //Dispose the background music to free memory
        if (bgm != null) {
            bgm.dispose();
            bgm = null;
        }
    }
}
