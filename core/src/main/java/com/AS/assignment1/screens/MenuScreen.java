package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends BaseScreen {
    private Texture backgroundTexture;
    private Texture startButtonTexture;
    private Texture optionButtonTexture;
    private Texture creditButtonTexture;
    private Texture quitButtonTexture;

    private Rectangle startButton;
    private Rectangle optionButton;
    private Rectangle creditButton;
    private Rectangle quitButton;

    public MenuScreen(Main game) {
        super(game);

        backgroundTexture = new Texture("background.jpg");
        startButtonTexture = new Texture("Menu/start.png");
        optionButtonTexture = new Texture("Menu/option.png");
        creditButtonTexture = new Texture("Menu/credit.png");
        quitButtonTexture = new Texture("Menu/quit.png");

        setupButtons();
    }

    private void setupButtons() {
        float buttonWidth = screenWidth * 0.28f;
        float buttonHeight = screenHeight * 0.11f;
        float buttonX = (screenWidth - buttonWidth) / 2f;

        startButton = new Rectangle(buttonX, screenHeight * 0.58f, buttonWidth, buttonHeight);
        optionButton = new Rectangle(buttonX, screenHeight * 0.44f, buttonWidth, buttonHeight);
        creditButton = new Rectangle(buttonX, screenHeight * 0.30f, buttonWidth, buttonHeight);
        quitButton = new Rectangle(buttonX, screenHeight * 0.16f, buttonWidth, buttonHeight);
    }

    private void update() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();

            if (startButton.contains(touchX, touchY)) {
                game.showGameScreen();
//            } else if (optionButton.contains(touchX, touchY)) {
//                game.showHelpScreen();
//            } else if (creditButton.contains(touchX, touchY)) {
//                game.showCreditScreen();
            } else if (quitButton.contains(touchX, touchY)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.10f, 0.15f, 0.20f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        drawBoldTextWithBox(titleFont, "Assignment 2 RPG Games", screenHeight * 0.86f, 45, 22);

        game.batch.draw(startButtonTexture, startButton.x, startButton.y, startButton.width, startButton.height);
        game.batch.draw(optionButtonTexture, optionButton.x, optionButton.y, optionButton.width, optionButton.height);
        game.batch.draw(creditButtonTexture, creditButton.x, creditButton.y, creditButton.width, creditButton.height);
        game.batch.draw(quitButtonTexture, quitButton.x, quitButton.y, quitButton.width, quitButton.height);

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

        backgroundTexture.dispose();
        startButtonTexture.dispose();
        optionButtonTexture.dispose();
        creditButtonTexture.dispose();
        quitButtonTexture.dispose();
    }
}
