package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BGMManager {

    private Music bgm;
    private float volume;

    public BGMManager() {
        volume = 0.45f;

        bgm = loadMusic("BGM/bgm.m4a");
    }

    private Music loadMusic(String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("BGM", "Missing BGM file: " + path);
            return null;
        }

        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setLooping(true);
        music.setVolume(volume);

        return music;
    }

    public void play() {
        if (bgm == null) {
            return;
        }

        if (!bgm.isPlaying()) {
            bgm.setLooping(true);
            bgm.setVolume(volume);
            bgm.play();
        }
    }

    public void stop() {
        if (bgm != null) {
            bgm.stop();
        }
    }

    public void pause() {
        if (bgm != null) {
            bgm.pause();
        }
    }

    public void increaseVolume() {
        volume += 0.1f;

        if (volume > 1f) {
            volume = 1f;
        }

        updateVolume();
    }

    public void decreaseVolume() {
        volume -= 0.1f;

        if (volume < 0f) {
            volume = 0f;
        }

        updateVolume();
    }

    private void updateVolume() {
        if (bgm != null) {
            bgm.setVolume(volume);
        }
    }

    public int getVolumePercent() {
        return Math.round(volume * 100f);
    }

    public void dispose() {
        if (bgm != null) {
            bgm.dispose();
            bgm = null;
        }
    }
}
