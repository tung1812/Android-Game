package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class LevelSelectScreen extends BaseScreen {

    //Background image for the level select screen
    private Texture backgroundTexture;

    //Texture for the home/menu button
    private Texture menuButtonTexture;

    //Rectangle used to detect touch input on the home/menu button
    private Rectangle menuButton;

    //Rectangle areas used to detect touch input on each level button
    private Rectangle level1Button;
    private Rectangle level2Button;
    private Rectangle level3Button;

    public LevelSelectScreen(Main game) {
        //Call the BaseScreen constructor to set up shared UI resources
        super(game);

        //Load the background image
        backgroundTexture = new Texture("background.jpg");

        //Load the home/menu button image
        menuButtonTexture = new Texture("icon/home button.png");

        //Set up all button positions and sizes
        setupButtons();
    }

    private void setupButtons() {
        //Create the home/menu button near the top-left corner of the screen
        menuButton = new Rectangle(
            screenWidth * 0.04f,
            screenHeight * 0.82f,
            screenHeight * 0.12f,
            screenHeight * 0.12f
        );

        //Calculate the size and center position of the level buttons
        float buttonWidth = screenWidth * 0.30f;
        float buttonHeight = screenHeight * 0.12f;
        float buttonX = (screenWidth - buttonWidth) / 2f;

        //Create touch areas for the three level buttons
        level1Button = new Rectangle(buttonX, screenHeight * 0.55f, buttonWidth, buttonHeight);
        level2Button = new Rectangle(buttonX, screenHeight * 0.39f, buttonWidth, buttonHeight);
        level3Button = new Rectangle(buttonX, screenHeight * 0.23f, buttonWidth, buttonHeight);
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

        //Return to the main menu if the home/menu button is touched
        if (menuButton.contains(touchX, touchY)) {
            game.showMenuScreen();
            return;
        }

        //Start level 1 if the level 1 button is touched
        if (level1Button.contains(touchX, touchY)) {
            startLevel(1);
            return;
        }

        //Start level 2 if the level 2 button is touched
        if (level2Button.contains(touchX, touchY)) {
            startLevel(2);
            return;
        }

        //Start level 3 if the level 3 button is touched
        if (level3Button.contains(touchX, touchY)) {
            startLevel(3);
        }
    }

    private void startLevel(int level) {
        //Do not start the level if it is still locked
        if (!game.getLevelManager().isLevelUnlocked(level)) {
            return;
        }

        //Store the selected level in the level manager
        game.getLevelManager().setSelectedLevel(level);

        //Open the game screen with the selected level
        game.showGameScreen();
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

        //Draw the home/menu button
        game.batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        //Draw the screen title
        drawBoldTextWithBox(titleFont, "Select Level", screenHeight * 0.80f, 45, 22);

        //Draw each level button and show whether it is unlocked or locked
        drawLevelButton(level1Button, "LEVEL 1", game.getLevelManager().isLevelUnlocked(1));
        drawLevelButton(level2Button, "LEVEL 2", game.getLevelManager().isLevelUnlocked(2));
        drawLevelButton(level3Button, "LEVEL 3", game.getLevelManager().isLevelUnlocked(3));

        //Finish drawing
        game.batch.end();
    }

    private void drawLevelButton(Rectangle button, String text, boolean unlocked) {
        //Draw a dark rectangle as the button background
        game.batch.draw(darkBoxTexture, button.x, button.y, button.width, button.height);

        //Set the default button text
        String displayText = text;

        //Add LOCKED text if the level is not unlocked
        if (!unlocked) {
            displayText = text + " LOCKED";
        }

        //Measure the button text size
        layout.setText(smallFont, displayText);

        //Center the text inside the button rectangle
        float textX = button.x + (button.width - layout.width) / 2f;
        float textY = button.y + (button.height + layout.height) / 2f;

        //Use white text for unlocked levels and gray text for locked levels
        if (unlocked) {
            smallFont.setColor(Color.WHITE);
        } else {
            smallFont.setColor(Color.GRAY);
        }

        //Draw the level button text
        smallFont.draw(game.batch, displayText, textX, textY);

        //Reset the font color back to white for later text drawing
        smallFont.setColor(Color.WHITE);
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
        //Dispose textures used only by this screen
        backgroundTexture.dispose();
        menuButtonTexture.dispose();

        //Dispose shared resources from BaseScreen
        super.dispose();
    }
}
