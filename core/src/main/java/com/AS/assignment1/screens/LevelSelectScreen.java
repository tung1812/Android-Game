package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class LevelSelectScreen extends BaseScreen {

    private Texture backgroundTexture;
    private Texture menuButtonTexture;
    private Texture darkButtonTexture;

    private Rectangle menuButton;
    private Rectangle level1Button;
    private Rectangle level2Button;
    private Rectangle level3Button;

    public LevelSelectScreen(Main game) {
        super(game);

        backgroundTexture = new Texture("background.jpg");
        menuButtonTexture = new Texture("icon/home button.png");

        darkButtonTexture = darkBoxTexture;

        setupButtons();
    }

    private void setupButtons() {
        menuButton = new Rectangle(
            screenWidth * 0.04f,
            screenHeight * 0.82f,
            screenHeight * 0.12f,
            screenHeight * 0.12f
        );

        float buttonWidth = screenWidth * 0.30f;
        float buttonHeight = screenHeight * 0.12f;
        float buttonX = (screenWidth - buttonWidth) / 2f;

        level1Button = new Rectangle(
            buttonX,
            screenHeight * 0.55f,
            buttonWidth,
            buttonHeight
        );

        level2Button = new Rectangle(
            buttonX,
            screenHeight * 0.39f,
            buttonWidth,
            buttonHeight
        );

        level3Button = new Rectangle(
            buttonX,
            screenHeight * 0.23f,
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

        if (menuButton.contains(touchX, touchY)) {
            game.showMenuScreen();
            return;
        }

        if (level1Button.contains(touchX, touchY)) {
            game.showGameScreen();
            return;
        }

        if (level2Button.contains(touchX, touchY)) {
            game.showGameScreen();
            return;
        }

        if (level3Button.contains(touchX, touchY)) {
            game.showGameScreen();
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
        game.batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawBoldTextWithBox(titleFont, "Select Level", screenHeight * 0.80f, 45, 22);

        drawLevelButton(level1Button, "LEVEL 1");
        drawLevelButton(level2Button, "LEVEL 2");
        drawLevelButton(level3Button, "LEVEL 3");

        game.batch.end();
    }

    private void drawLevelButton(Rectangle button, String text) {
        game.batch.draw(darkButtonTexture, button.x, button.y, button.width, button.height);

        layout.setText(smallFont, text);

        float textX = button.x + (button.width - layout.width) / 2f;
        float textY = button.y + (button.height + layout.height) / 2f;

        smallFont.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        smallFont.draw(game.batch, text, textX, textY);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setupButtons();
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        menuButtonTexture.dispose();

        super.dispose();
    }
}
