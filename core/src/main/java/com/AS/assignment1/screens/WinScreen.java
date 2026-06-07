package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class WinScreen extends BaseScreen {

    private Texture winTexture;

    private Rectangle retryButton;
    private Rectangle menuButton;

    public WinScreen(Main game) {
        super(game);

        winTexture = new Texture("win.jpg");

        setupButtons();
    }

    private void setupButtons() {
        float buttonWidth = screenWidth * 0.25f;
        float buttonHeight = screenHeight * 0.10f;

        retryButton = new Rectangle(
            screenWidth * 0.22f,
            screenHeight * 0.12f,
            buttonWidth,
            buttonHeight
        );

        menuButton = new Rectangle(
            screenWidth * 0.53f,
            screenHeight * 0.12f,
            buttonWidth,
            buttonHeight
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

        if (menuButton.contains(touchX, touchY)) {
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

        game.batch.draw(winTexture, 0, 0, screenWidth, screenHeight);

        drawBoldTextWithBox(smallFont, "Tap left side to retry", screenHeight * 0.12f, 30, 14);
        drawBoldTextWithBox(smallFont, "Tap right side for menu", screenHeight * 0.05f, 30, 14);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setupButtons();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (winTexture != null) {
            winTexture.dispose();
            winTexture = null;
        }
    }
}
