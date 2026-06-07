package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class HelpScreen extends BaseScreen {

    private Texture backgroundTexture;
    private Texture menuButtonTexture;
    private Rectangle menuButton;

    public HelpScreen(Main game) {
        super(game);

        backgroundTexture = new Texture("background.jpg");
        menuButtonTexture = new Texture("icon/home button.png");

        setupButtons();
    }

    private void setupButtons() {
        menuButton = new Rectangle(
            screenWidth * 0.04f,
            screenHeight * 0.82f,
            screenHeight * 0.12f,
            screenHeight * 0.12f
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

        drawBoldTextWithBox(titleFont, "Help", screenHeight * 0.78f, 45, 22);
        drawBoldTextWithBox(smallFont, "Use screen buttons to move", screenHeight * 0.60f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap ATK to attack", screenHeight * 0.50f, 35, 18);
        drawBoldTextWithBox(smallFont, "Avoid enemies and explore the map", screenHeight * 0.40f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap home button to go back", screenHeight * 0.30f, 35, 18);

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
        menuButtonTexture.dispose();
    }
}
