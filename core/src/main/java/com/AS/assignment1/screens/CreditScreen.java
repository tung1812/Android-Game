package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class CreditScreen extends BaseScreen {

    private Texture backgroundTexture;
    private Texture menuButtonTexture;
    private Rectangle menuButton;

    public CreditScreen(Main game) {
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

        drawBoldTextWithBox(titleFont, "CREDITS", screenHeight * 0.88f, 45, 22);

        drawBoldTextWithBox(smallFont, "Game Developer: Gia Minh Pham and Son Tung Nguyen", screenHeight * 0.78f, 27, 12);
        drawBoldTextWithBox(smallFont, "Engine: LibGDX | Tools: Tiled, Android Studio", screenHeight * 0.71f, 27, 12);

        drawBoldTextWithBox(smallFont, "Player Character: Reiko by Gl Studio", screenHeight * 0.62f, 27, 12);
        drawBoldTextWithBox(smallFont, "Map Tileset: 32 x 32 Pixel Isometric Tiles by scrabling", screenHeight * 0.55f, 27, 12);
        drawBoldTextWithBox(smallFont, "Enemy Sprites: Someone", screenHeight * 0.39f, 27, 12);
        drawBoldTextWithBox(smallFont, "UI Assets: Pixel Explosive / @PixelExplosive. SlyFoxStudios", screenHeight * 0.32f, 27, 12);
        drawBoldTextWithBox(smallFont, "Background Images: suttersock", screenHeight * 0.25f, 27, 12);

        drawBoldTextWithBox(smallFont, "BGM: JDSherbert", screenHeight * 0.16f, 27, 12);
        drawBoldTextWithBox(smallFont, "SFX: Helton Yan | Beto Bezerra", screenHeight * 0.09f, 27, 12);
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
