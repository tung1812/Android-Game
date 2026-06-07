package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class DeathScreen extends BaseScreen {

    private Texture gameOverTexture;

    private Rectangle retryButton;
    private Rectangle exitButton;

    public DeathScreen(Main game) {
        super(game);

        gameOverTexture = new Texture("game_over.png");

        setupButtons();
    }

    private void setupButtons() {
        retryButton = new Rectangle(
            screenWidth * 0.18f,
            screenHeight * 0.28f,
            screenWidth * 0.30f,
            screenHeight * 0.18f
        );

        exitButton = new Rectangle(
            screenWidth * 0.52f,
            screenHeight * 0.28f,
            screenWidth * 0.30f,
            screenHeight * 0.18f
        );
    }

    private void update() {
        if (!Gdx.input.justTouched()) {
            return;
        }

        float touchX = Gdx.input.getX();
        float touchY = screenHeight - Gdx.input.getY();

        if (retryButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showGameScreen();
            return;
        }

        if (exitButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showMenuScreen();
        }
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.batch.draw(
            gameOverTexture,
            0,
            0,
            screenWidth,
            screenHeight
        );

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setupButtons();
    }

    @Override
    public void dispose() {
        if (gameOverTexture != null) {
            gameOverTexture.dispose();
        }

        super.dispose();
    }
}
