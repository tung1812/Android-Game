package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class CreditScreen extends BaseScreen {

    //Background image for the credit screen
    private Texture backgroundTexture;

    //Texture for the home/menu button
    private Texture menuButtonTexture;

    //Rectangle used to store the menu button position and size
    private Rectangle menuButton;

    public CreditScreen(Main game) {
        //Call the BaseScreen constructor to set up shared UI resources
        super(game);

        //Load the background image
        backgroundTexture = new Texture("background.jpg");

        //Load the home/menu button image
        menuButtonTexture = new Texture("icon/home button.png");

        //Set up the button position and size
        setupButtons();
    }

    private void setupButtons() {
        //Create the menu button near the top-left corner of the screen
        menuButton = new Rectangle(
            screenWidth * 0.04f,
            screenHeight * 0.82f,
            screenHeight * 0.12f,
            screenHeight * 0.12f
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

        //Return to the main menu if the menu button is touched
        if (menuButton.contains(touchX, touchY)) {
            game.showMenuScreen();
        }
    }

    @Override
    public void render(float delta) {
        //Update input before drawing the screen
        update();

        //Clear the screen with a dark background color
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
        drawBoldTextWithBox(titleFont, "CREDITS", screenHeight * 0.88f, 45, 22);

        //Draw the main developer and tool credits
        drawBoldTextWithBox(smallFont, "Game Developer: Gia Minh Pham and Son Tung Nguyen", screenHeight * 0.78f, 27, 12);
        drawBoldTextWithBox(smallFont, "Engine: LibGDX | Tools: Tiled, Android Studio", screenHeight * 0.71f, 27, 12);

        //Draw asset credits
        drawBoldTextWithBox(smallFont, "Player Character: Reiko by Gl Studio", screenHeight * 0.62f, 27, 12);
        drawBoldTextWithBox(smallFont, "Map Tileset: 32 x 32 Pixel Isometric Tiles by scrabling", screenHeight * 0.55f, 27, 12);
        drawBoldTextWithBox(smallFont, "Enemy Sprites: Someone", screenHeight * 0.39f, 27, 12);
        drawBoldTextWithBox(smallFont, "UI Assets: Pixel Explosive / @PixelExplosive. SlyFoxStudios", screenHeight * 0.32f, 27, 12);
        drawBoldTextWithBox(smallFont, "Background Images: suttersock", screenHeight * 0.25f, 27, 12);

        //Draw audio credits
        drawBoldTextWithBox(smallFont, "BGM: JDSherbert", screenHeight * 0.16f, 27, 12);
        drawBoldTextWithBox(smallFont, "SFX: Helton Yan | Beto Bezerra", screenHeight * 0.09f, 27, 12);

        //Finish drawing
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //Update the base screen size and UI camera
        super.resize(width, height);

        //Recalculate button position and size for the new screen size
        setupButtons();
    }

    @Override
    public void dispose() {
        //Dispose shared resources from BaseScreen
        super.dispose();

        //Dispose textures used only by this screen
        backgroundTexture.dispose();
        menuButtonTexture.dispose();
    }
}
