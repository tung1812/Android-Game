package com.AS.assignment1;

import com.AS.assignment1.screens.CreditScreen;
import com.AS.assignment1.screens.DeathScreen;
import com.AS.assignment1.screens.GameScreen;
import com.AS.assignment1.screens.HelpScreen;
import com.AS.assignment1.screens.LevelSelectScreen;
import com.AS.assignment1.screens.MenuScreen;
import com.AS.assignment1.screens.WinScreen;

import com.AS.assignment1.world.LevelManager;
import com.AS.assignment1.world.SoundManager;
import com.AS.assignment1.world.BGMManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class Main extends Game {

    //SpriteBatch used to draw textures, fonts, and UI elements
    public SpriteBatch batch;

    //Manages selected levels and unlocked levels
    private LevelManager levelManager;

    //Manages sound effects such as click, attack, hurt, and movement sounds
    private SoundManager soundManager;

    //Manages background music
    private BGMManager bgmManager;

    @Override
    public void create() {
        //Create the SpriteBatch used by all screens
        batch = new SpriteBatch();

        //Create the sound effect manager
        soundManager = new SoundManager();

        //Create the level manager
        levelManager = new LevelManager();

        //Create and start the background music manager
        bgmManager = new BGMManager();
        bgmManager.play();

        //Show the main menu when the game starts
        showMenuScreen();
    }

    public void changeScreen(Screen newScreen) {
        //Store the current screen before changing to the new one
        Screen oldScreen = getScreen();

        //Set the new active screen
        setScreen(newScreen);

        //Dispose the old screen after the screen has changed
        if (oldScreen != null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    oldScreen.dispose();
                }
            });
        }
    }

    public void showMenuScreen() {
        //Change to the main menu screen
        changeScreen(new MenuScreen(this));
    }

    public void showLevelSelectScreen() {
        //Change to the level select screen
        changeScreen(new LevelSelectScreen(this));
    }

    public void showHelpScreen() {
        //Change to the help screen
        changeScreen(new HelpScreen(this));
    }

    public void showCreditScreen() {
        //Change to the credit screen
        changeScreen(new CreditScreen(this));
    }

    public void showGameScreen() {
        // hange to the main gameplay screen
        changeScreen(new GameScreen(this));
    }

    public void showDeathScreen() {
        //Change to the game over screen
        changeScreen(new DeathScreen(this));
    }

    public void showWinScreen() {
        //Change to the win screen
        changeScreen(new WinScreen(this));
    }

    public LevelManager getLevelManager() {
        //Return the level manager so screens can access level data
        return levelManager;
    }

    public SoundManager getSoundManager() {
        //Return the sound manager so screens and entities can play sound effects
        return soundManager;
    }

    public BGMManager getBgmManager() {
        //Return the background music manager
        return bgmManager;
    }

    @Override
    public void render() {
        //Let the current active screen render itself
        super.render();
    }

    @Override
    public void dispose() {
        //Dispose the current screen if it exists
        if (getScreen() != null) {
            getScreen().dispose();
        }

        //Dispose the shared SpriteBatch
        if (batch != null) {
            batch.dispose();
        }

        //Dispose all loaded sound effects
        if (soundManager != null) {
            soundManager.dispose();
        }

        //Dispose the background music
        if (bgmManager != null) {
            bgmManager.dispose();
        }
    }
}
