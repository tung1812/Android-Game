package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class WinScreen extends BaseScreen {

    //Full-screen image used for the win screen
    private Texture winTexture;

    //Large touch areas for retry and menu actions
    private Rectangle retryArea;
    private Rectangle menuArea;

    public WinScreen(Main game) {
        //Set up shared screen resources from BaseScreen
        super(game);

        //Load the win screen background image from android/assets
        winTexture = new Texture("win.jpg");

        //Create the touch areas
        setupTouchAreas();
    }

    private void setupTouchAreas() {
        //Left half of the screen is used to retry the game
        retryArea = new Rectangle(
            0,
            0,
            screenWidth / 2f,
            screenHeight
        );

        //Right half of the screen is used to return to the main menu
        menuArea = new Rectangle(
            screenWidth / 2f,
            0,
            screenWidth / 2f,
            screenHeight
        );
    }

    private void update() {
        //Only handle input when the player taps once
        if (!Gdx.input.justTouched()) {
            return;
        }

        //Get touch position
        float touchX = Gdx.input.getX();

        //Convert Y because LibGDX input Y starts from the top
        float touchY = screenHeight - Gdx.input.getY();

        //If player taps the left half, restart the current game screen
        if (retryArea.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showGameScreen();
            return;
        }

        //If player taps the right half, return to the main menu
        if (menuArea.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showMenuScreen();
        }
    }

    @Override
    public void render(float delta) {
        //Check touch input first
        update();

        //Clear screen before drawing
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw everything using the UI camera
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        //Draw the win image as a full-screen background
        game.batch.draw(
            winTexture,
            0,
            0,
            screenWidth,
            screenHeight
        );

        //Draw simple instructions for the player
        drawBoldTextWithBox(
            smallFont,
            "Tap left side to retry",
            screenHeight * 0.12f,
            30,
            14
        );

        drawBoldTextWithBox(
            smallFont,
            "Tap right side for menu",
            screenHeight * 0.05f,
            30,
            14
        );

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //Update screen size and UI camera
        super.resize(width, height);

        //Recalculate touch areas after resizing
        setupTouchAreas();
    }

    @Override
    public void dispose() {
        //Dispose BaseScreen resources
        super.dispose();

        //Dispose win image texture
        if (winTexture != null) {
            winTexture.dispose();
            winTexture = null;
        }
    }
}
