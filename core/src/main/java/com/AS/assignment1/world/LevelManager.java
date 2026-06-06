package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LevelManager {
    private Preferences preferences;

    private int selectedLevel;
    private int unlockedLevel;

    public LevelManager() {
        preferences = Gdx.app.getPreferences("game_save");

        selectedLevel = 1;
        unlockedLevel = preferences.getInteger("unlockedLevel", 1);
    }

    public void setSelectedLevel(int level) {
        selectedLevel = level;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public int getUnlockedLevel() {
        return unlockedLevel;
    }

    public boolean isLevelUnlocked(int level) {
        return level <= unlockedLevel;
    }

    public void unlockLevel(int level) {
        if (level > unlockedLevel) {
            unlockedLevel = level;
            preferences.putInteger("unlockedLevel", unlockedLevel);
            preferences.flush();
        }
    }

    public String getCurrentMapPath() {
        return "maps/level" + selectedLevel + ".tmx";
    }
}
