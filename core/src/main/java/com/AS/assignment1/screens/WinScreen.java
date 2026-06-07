package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class WinScreen extends BaseScreen {

    //Texture used for the full-screen win image
    private Texture winTexture;

    //Rectangle areas used to detect touch input for retry and menu actions
    private Rectangle retryButton;
    private Rectangle menuButton;

    public WinScreen(Main game) {
        //Call the BaseScreen constructor to set up shared UI resources
        super(game);

        //Load the win screen image
        winTexture = new Texture("win.jpg");

        //Set up button positions and sizes
        setupButtons();
    }

    private void setupButtons() {
        // Calculate button size based on the current screen size
        float buttonWidth = screenWidth * 0.25f;
        float buttonHeight = screenHeight * 0.10f;

        //Create the retry button touch area on the left side
        retryButton = new Rectangle(
            screenWidth * 0.22f,
            screenHeight * 0.12f,
            buttonWidth,
            buttonHeight
        );

        //Create the menu button touch area on the right side
        menuButton = new Rectangle(
            screenWidth * 0.53f,
            screenHeight * 0.12f,
            buttonWidth,
            buttonHeight
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

        //Restart the game if the retry area is touched
        if (retryButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showGameScreen();
            return;
        }

        //Return to the main menu if the menu area is touched
        if (menuButton.contains(touchX, touchY)) {
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

        //Draw the win image to cover the full screen
        game.batch.draw(winTexture, 0, 0, screenWidth, screenHeight);

        // Draw instruction text for retry and menu buttons
        drawBoldTextWithBox(smallFont, "Tap left side to retry", screenHeight * 0.12f, 30, 14);
        drawBoldTextWithBox(smallFont, "Tap right side for menu", screenHeight * 0.05f, 30, 14);

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
        //Dispose shared resources from BaseScreen
        super.dispose();

        //Dispose the win screen texture if it was loaded
        if (winTexture != null) {
            winTexture.dispose();
            winTexture = null;
        }
    }
}
