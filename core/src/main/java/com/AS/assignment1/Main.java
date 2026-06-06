package com.AS.assignment1;

import com.AS.assignment1.screens.DeathScreen;
import com.AS.assignment1.screens.CreditScreen;
import com.AS.assignment1.screens.DeathScreen;
import com.AS.assignment1.screens.GameScreen;
import com.AS.assignment1.screens.HelpScreen;
import com.AS.assignment1.screens.LevelSelectScreen;
import com.AS.assignment1.screens.MenuScreen;
import com.AS.assignment1.screens.WinScreen;

import com.AS.assignment1.world.LevelManager;
import com.AS.assignment1.world.SoundManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.AS.assignment1.world.BGMManager;

public class Main extends Game {
    public SpriteBatch batch;

    private LevelManager levelManager;
    private SoundManager soundManager;
    private BGMManager bgmManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        soundManager = new SoundManager();
        levelManager = new LevelManager();
        bgmManager = new BGMManager();
        bgmManager.play();

        showMenuScreen();
    }

    public void changeScreen(Screen newScreen) {
        Screen oldScreen = getScreen();

        setScreen(newScreen);

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
        changeScreen(new MenuScreen(this));
    }

    public void showLevelSelectScreen() {
        changeScreen(new LevelSelectScreen(this));
    }

    public void showHelpScreen() {
        changeScreen(new HelpScreen(this));
    }

    public void showCreditScreen() {
        changeScreen(new CreditScreen(this));
    }

    public void showGameScreen() {
        changeScreen(new GameScreen(this));
    }

    public void showDeathScreen() {
        changeScreen(new DeathScreen(this));
    }

    public void showWinScreen() {
        changeScreen(new WinScreen(this));
    }
    public LevelManager getLevelManager() {
        return levelManager;
    }


    public SoundManager getSoundManager() {
        return soundManager;
    }
    public BGMManager getBgmManager() {
        return bgmManager;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }

        if (batch != null) {
            batch.dispose();
        }
        if (soundManager != null) {
            soundManager.dispose();
        }
        if (bgmManager != null) {
            bgmManager.dispose();
        }
    }
}
