package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends BaseScreen {
    private Texture backgroundTexture;
    private Texture startButtonTexture;
    private Texture helpButtonTexture;
    private Texture creditButtonTexture;
    private Texture quitButtonTexture;

    private Texture volumeButtonTexture;
    private Texture increaseButtonTexture;
    private Texture decreaseButtonTexture;

    private Rectangle startButton;
    private Rectangle helpButton;
    private Rectangle creditButton;
    private Rectangle quitButton;

    private Rectangle volumeButton;
    private Rectangle increaseButton;
    private Rectangle decreaseButton;

    private boolean showVolumePanel;

    public MenuScreen(Main game) {
        super(game);

        backgroundTexture = new Texture("background.jpg");
        startButtonTexture = new Texture("Menu/start.png");
        helpButtonTexture = new Texture("Menu/help.png");
        creditButtonTexture = new Texture("Menu/credit.png");
        quitButtonTexture = new Texture("Menu/quit.png");

        volumeButtonTexture = new Texture("icon/volume button.png");
        increaseButtonTexture = new Texture("Volumn/increase.png");
        decreaseButtonTexture = new Texture("Volumn/decrease.png");

        showVolumePanel = false;

        setupButtons();
    }

    private void setupButtons() {
        float buttonWidth = screenWidth * 0.28f;
        float buttonHeight = screenHeight * 0.11f;
        float buttonX = (screenWidth - buttonWidth) / 2f;

        startButton = new Rectangle(buttonX, screenHeight * 0.58f, buttonWidth, buttonHeight);
        helpButton = new Rectangle(buttonX, screenHeight * 0.44f, buttonWidth, buttonHeight);
        creditButton = new Rectangle(buttonX, screenHeight * 0.30f, buttonWidth, buttonHeight);
        quitButton = new Rectangle(buttonX, screenHeight * 0.16f, buttonWidth, buttonHeight);

        float iconSize = screenHeight * 0.12f;
        float margin = screenWidth * 0.04f;

        volumeButton = new Rectangle(
            screenWidth - margin - iconSize,
            screenHeight * 0.82f,
            iconSize,
            iconSize
        );

        float soundButtonSize = screenHeight * 0.11f;

        decreaseButton = new Rectangle(
            screenWidth - margin - iconSize * 2.4f,
            screenHeight * 0.66f,
            soundButtonSize,
            soundButtonSize
        );

        increaseButton = new Rectangle(
            screenWidth - margin - iconSize,
            screenHeight * 0.66f,
            soundButtonSize,
            soundButtonSize
        );
    }

    private void update() {
        if (!Gdx.input.justTouched()) {
            return;
        }

        float touchX = Gdx.input.getX();
        float touchY = screenHeight - Gdx.input.getY();

        if (volumeButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            showVolumePanel = !showVolumePanel;
            return;
        }

        if (showVolumePanel && decreaseButton.contains(touchX, touchY)) {
            game.getSoundManager().decreaseVolume();
            return;
        }

        if (showVolumePanel && increaseButton.contains(touchX, touchY)) {
            game.getSoundManager().increaseVolume();
            return;
        }

        if (startButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showLevelSelectScreen();
        } else if (helpButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showHelpScreen();
        } else if (creditButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showCreditScreen();
        } else if (quitButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            Gdx.app.exit();
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

        drawVolumeButton();

        if (showVolumePanel) {
            drawVolumePanel();
            game.batch.end();
            return;
        }

        drawBoldTextWithBox(titleFont, "Reiko Adventures Time", screenHeight * 0.86f, 45, 22);

        game.batch.draw(startButtonTexture, startButton.x, startButton.y, startButton.width, startButton.height);
        game.batch.draw(helpButtonTexture, helpButton.x, helpButton.y, helpButton.width, helpButton.height);
        game.batch.draw(creditButtonTexture, creditButton.x, creditButton.y, creditButton.width, creditButton.height);
        game.batch.draw(quitButtonTexture, quitButton.x, quitButton.y, quitButton.width, quitButton.height);

        game.batch.end();
    }

    private void drawVolumeButton() {
        game.batch.draw(
            volumeButtonTexture,
            volumeButton.x,
            volumeButton.y,
            volumeButton.width,
            volumeButton.height
        );
    }

    private void drawVolumePanel() {
        drawBoldTextWithBox(
            titleFont,
            "Sound Setting",
            screenHeight * 0.76f,
            45,
            22
        );

        drawBoldTextWithBox(
            smallFont,
            "Volume: " + game.getSoundManager().getVolumePercent() + "%",
            screenHeight * 0.55f,
            30,
            14
        );

        game.batch.draw(
            decreaseButtonTexture,
            decreaseButton.x,
            decreaseButton.y,
            decreaseButton.width,
            decreaseButton.height
        );

        game.batch.draw(
            increaseButtonTexture,
            increaseButton.x,
            increaseButton.y,
            increaseButton.width,
            increaseButton.height
        );
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
        helpButtonTexture.dispose();
        creditButtonTexture.dispose();
        quitButtonTexture.dispose();

        volumeButtonTexture.dispose();
        increaseButtonTexture.dispose();
        decreaseButtonTexture.dispose();
    }
}
