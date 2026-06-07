package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends BaseScreen {

    //Background image for the menu screen
    private Texture backgroundTexture;

    //Textures for the main menu buttons
    private Texture startButtonTexture;
    private Texture helpButtonTexture;
    private Texture creditButtonTexture;
    private Texture quitButtonTexture;

    //Textures for the volume button and volume control buttons
    private Texture volumeButtonTexture;
    private Texture increaseButtonTexture;
    private Texture decreaseButtonTexture;

    //Rectangle areas used to detect touch input on the main menu buttons
    private Rectangle startButton;
    private Rectangle helpButton;
    private Rectangle creditButton;
    private Rectangle quitButton;

    //Rectangle areas used to detect touch input on the volume controls
    private Rectangle volumeButton;
    private Rectangle increaseButton;
    private Rectangle decreaseButton;

    //Controls whether the volume setting panel is currently visible
    private boolean showVolumePanel;

    public MenuScreen(Main game) {
        //Call the BaseScreen constructor to set up shared UI resources
        super(game);

        //Load the background image
        backgroundTexture = new Texture("background.jpg");

        //Load main menu button images
        startButtonTexture = new Texture("Menu/start.png");
        helpButtonTexture = new Texture("Menu/help.png");
        creditButtonTexture = new Texture("Menu/credit.png");
        quitButtonTexture = new Texture("Menu/quit.png");

        //Load volume control images
        volumeButtonTexture = new Texture("icon/volume button.png");
        increaseButtonTexture = new Texture("Volumn/increase.png");
        decreaseButtonTexture = new Texture("Volumn/decrease.png");

        //Hide the volume panel by default
        showVolumePanel = false;

        //Set up all button positions and sizes
        setupButtons();
    }

    private void setupButtons() {
        //Calculate the size and position of the main menu buttons
        float buttonWidth = screenWidth * 0.28f;
        float buttonHeight = screenHeight * 0.11f;
        float buttonX = (screenWidth - buttonWidth) / 2f;

        //Create the main menu button touch areas
        startButton = new Rectangle(buttonX, screenHeight * 0.58f, buttonWidth, buttonHeight);
        helpButton = new Rectangle(buttonX, screenHeight * 0.44f, buttonWidth, buttonHeight);
        creditButton = new Rectangle(buttonX, screenHeight * 0.30f, buttonWidth, buttonHeight);
        quitButton = new Rectangle(buttonX, screenHeight * 0.16f, buttonWidth, buttonHeight);

        //Calculate the size and margin for the volume icon
        float iconSize = screenHeight * 0.12f;
        float margin = screenWidth * 0.04f;

        //Create the volume button touch area near the top-right corner
        volumeButton = new Rectangle(
            screenWidth - margin - iconSize,
            screenHeight * 0.82f,
            iconSize,
            iconSize
        );

        //Calculate the size of the increase and decrease buttons
        float soundButtonSize = screenHeight * 0.11f;

        //Create the decrease volume button touch area
        decreaseButton = new Rectangle(
            screenWidth - margin - iconSize * 2.4f,
            screenHeight * 0.66f,
            soundButtonSize,
            soundButtonSize
        );

        //Create the increase volume button touch area
        increaseButton = new Rectangle(
            screenWidth - margin - iconSize,
            screenHeight * 0.66f,
            soundButtonSize,
            soundButtonSize
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

        //Toggle the volume panel when the volume button is touched
        if (volumeButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            showVolumePanel = !showVolumePanel;
            return;
        }

        //Decrease the game volume if the decrease button is touched
        if (showVolumePanel && decreaseButton.contains(touchX, touchY)) {
            game.getSoundManager().decreaseVolume();
            return;
        }

        //Increase the game volume if the increase button is touched
        if (showVolumePanel && increaseButton.contains(touchX, touchY)) {
            game.getSoundManager().increaseVolume();
            return;
        }

        //Go to the level select screen when the start button is touched
        if (startButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showLevelSelectScreen();

            //o to the help screen when the help button is touched
        } else if (helpButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showHelpScreen();

            //Go to the credit screen when the credit button is touched
        } else if (creditButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            game.showCreditScreen();

            //Exit the application when the quit button is touched
        } else if (quitButton.contains(touchX, touchY)) {
            game.getSoundManager().playClick();
            Gdx.app.exit();
        }
    }

    @Override
    public void render(float delta) {
        //Update touch input before drawing the screen
        update();

        //Clear the screen with a dark blue background color
        Gdx.gl.glClearColor(0.10f, 0.15f, 0.20f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Use the UI camera for drawing screen-space elements
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        //Draw the background image to cover the full screen
        game.batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        //Draw the volume button
        drawVolumeButton();

        //If the volume panel is open, only draw the sound settings panel
        if (showVolumePanel) {
            drawVolumePanel();
            game.batch.end();
            return;
        }

        //Draw the game title
        drawBoldTextWithBox(titleFont, "Reiko Adventures Time", screenHeight * 0.86f, 45, 22);

        //Draw the main menu buttons
        game.batch.draw(startButtonTexture, startButton.x, startButton.y, startButton.width, startButton.height);
        game.batch.draw(helpButtonTexture, helpButton.x, helpButton.y, helpButton.width, helpButton.height);
        game.batch.draw(creditButtonTexture, creditButton.x, creditButton.y, creditButton.width, creditButton.height);
        game.batch.draw(quitButtonTexture, quitButton.x, quitButton.y, quitButton.width, quitButton.height);

        //Finish drawing
        game.batch.end();
    }

    private void drawVolumeButton() {
        //Draw the volume icon button
        game.batch.draw(
            volumeButtonTexture,
            volumeButton.x,
            volumeButton.y,
            volumeButton.width,
            volumeButton.height
        );
    }

    private void drawVolumePanel() {
        //Draw the volume setting title
        drawBoldTextWithBox(
            titleFont,
            "Sound Setting",
            screenHeight * 0.76f,
            45,
            22
        );

        //Draw the current volume percentage
        drawBoldTextWithBox(
            smallFont,
            "Volume: " + game.getSoundManager().getVolumePercent() + "%",
            screenHeight * 0.55f,
            30,
            14
        );

        //Draw the decrease volume button
        game.batch.draw(
            decreaseButtonTexture,
            decreaseButton.x,
            decreaseButton.y,
            decreaseButton.width,
            decreaseButton.height
        );

        //Draw the increase volume button
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
        //Update the base screen size and UI camera
        super.resize(width, height);

        //Recalculate button positions and sizes for the new screen size
        setupButtons();
    }

    @Override
    public void dispose() {
        //Dispose shared resources from BaseScreen
        super.dispose();

        //Dispose menu textures
        backgroundTexture.dispose();
        startButtonTexture.dispose();
        helpButtonTexture.dispose();
        creditButtonTexture.dispose();
        quitButtonTexture.dispose();

        //Dispose volume control textures
        volumeButtonTexture.dispose();
        increaseButtonTexture.dispose();
        decreaseButtonTexture.dispose();
    }
}
