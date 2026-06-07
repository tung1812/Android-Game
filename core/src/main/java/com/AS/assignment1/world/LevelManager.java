package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LevelManager {

    //Preferences object used to save and load level progress
    private Preferences preferences;

    //The level currently selected by the player
    private int selectedLevel;

    //The highest level unlocked by the player
    private int unlockedLevel;

    public LevelManager() {
        //Load saved game progress from local preferences
        preferences = Gdx.app.getPreferences("game_save");

        //Set level 1 as the default selected level
        selectedLevel = 1;

        //Load the unlocked level from saved data, or use level 1 as default
        unlockedLevel = preferences.getInteger("unlockedLevel", 1);
    }

    public void setSelectedLevel(int level) {
        //Store the level selected by the player
        selectedLevel = level;
    }

    public int getSelectedLevel() {
        //Return the currently selected level
        return selectedLevel;
    }

    public int getUnlockedLevel() {
        //Return the highest unlocked level
        return unlockedLevel;
    }

    public boolean isLevelUnlocked(int level) {
        //Check whether the requested level is unlocked
        return level <= unlockedLevel;
    }

    public void unlockLevel(int level) {
        //Only update saved progress if the new level is higher than the current unlocked level
        if (level > unlockedLevel) {
            unlockedLevel = level;

            //Save the new unlocked level
            preferences.putInteger("unlockedLevel", unlockedLevel);
            preferences.flush();
        }
    }

    public String getCurrentMapPath() {
        //Return the Tiled map path based on the selected level
        return "maps/level" + selectedLevel + ".tmx";
    }
}
