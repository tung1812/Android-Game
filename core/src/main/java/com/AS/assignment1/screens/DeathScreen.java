package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class DeathScreen extends BaseScreen {

    public DeathScreen(Main game) {
        super(game);
    }

    @Override
    public void render(float delta) {
        if (!update()) {
            return;
        }

        Gdx.gl.glClearColor(0.08f, 0.02f, 0.02f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        drawBoldTextWithBox(titleFont, "You Died", screenHeight * 0.65f, 45, 22);
        drawBoldTextWithBox(smallFont, "Tap to restart", screenHeight * 0.45f, 35, 18);

        game.batch.end();
    }

    private boolean update() {
        if (Gdx.input.justTouched()) {
            game.showGameScreen();
            return false;
        }

        return true;
    }
}
