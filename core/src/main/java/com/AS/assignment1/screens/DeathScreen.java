package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class DeathScreen extends BaseScreen {

    //Texture used for the full-screen game over image
    private Texture gameOverTexture;

    //Rectangle areas used to detect touch input on the retry and exit buttons
    private Rectangle retryButton;
    private Rectangle exitButton;

    public DeathScreen(Main game) {
        //Call the BaseScreen constructor to set up shared UI resources
        super(game);

        //Load the game over background image
        gameOverTexture = new Texture("game_over.png");

        //Set up the button positions and sizes
        setupButtons();
    }

    private void setupButtons() {
        //Create the retry button area on the left side of the screen
        retryButton = new Rectangle(
            screenWidth * 0.18f,
            screenHeight * 0.28f,
            screenWidth * 0.30f,
            screenHeight * 0.18f
        );

        //Create the exit button area on the right side of the screen
        exitButton = new Rectangle(
            screenWidth * 0.52f,
            screenHeight * 0.28f,
            screenWidth * 0.30f,
            screenHeight * 0.18f
        );
    }

    private void update() {
        //Stop checking input if the screen was not just touched
        if (!Gdx.input.justTouched()) {
            return;
        }

        //Get the touch position
        float touchX = Gdx.input.getX();

        //Convert the touch Y position because LibGDX input Y starts from the top
        float touchY = screenHeight - Gdx.input.getY();

        //Restart the game if the retry button is touched
        if (retryButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showGameScreen();
            return;
        }

        //Return to the main menu if the exit button is touched
        if (exitButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showMenuScreen();
        }
    }

    @Override
    public void render(float delta) {
        //Update touch input before drawing the screen
        update();

        //Clear the screen with black color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Use the UI camera for drawing screen-space elements
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        //Draw the game over image to cover the full screen
        game.batch.draw(
            gameOverTexture,
            0,
            0,
            screenWidth,
            screenHeight
        );

        //Finish drawing
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //Update the base screen size and UI camera
        super.resize(width, height);

        //Recalculate button positions and sizes for the new screen size
        setupButtons();
    }

    @Override
    public void dispose() {
        //Dispose the game over texture if it was loaded
        if (gameOverTexture != null) {
            gameOverTexture.dispose();
        }

        //Dispose shared resources from BaseScreen
        super.dispose();
    }
}
