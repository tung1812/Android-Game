package com.AS.assignment1;

//import com.AS.assignment1.screens.CreditScreen;
import com.AS.assignment1.screens.DeathScreen;
import com.AS.assignment1.screens.GameScreen;
//import com.AS.assignment1.screens.HelpScreen;
import com.AS.assignment1.screens.MenuScreen;
import com.AS.assignment1.screens.WinScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
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

//    public void showHelpScreen() {
//        changeScreen(new HelpScreen(this));
//    }

//    public void showCreditScreen() {
//        changeScreen(new CreditScreen(this));
//    }

    public void showGameScreen() {
        changeScreen(new GameScreen(this));
    }

    public void showDeathScreen() {
        changeScreen(new DeathScreen(this));
    }

    public void showWinScreen() {
        changeScreen(new WinScreen(this));
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

        batch.dispose();
    }
}
