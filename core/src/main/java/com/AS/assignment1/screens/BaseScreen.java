package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public abstract class BaseScreen implements Screen {

    //Reference to the main game class
    protected Main game;

    //Camera used for drawing UI elements
    protected OrthographicCamera uiCamera;

    //Fonts used for title text and smaller text
    protected BitmapFont titleFont;
    protected BitmapFont smallFont;

    //Used to measure text size before drawing
    protected GlyphLayout layout;

    //Texture used as a dark background box behind text
    protected Texture darkBoxTexture;

    //Current screen width and height
    protected float screenWidth;
    protected float screenHeight;

    public BaseScreen(Main game) {
        //Store the main game instance
        this.game = game;

        //Get the current screen size
        screenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
        screenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();

        //Create and configure the UI camera
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.update();

        //Create the title font and increase its size
        titleFont = new BitmapFont();
        titleFont.getData().setScale(5.5f);
        titleFont.setColor(Color.WHITE);

        //Create the smaller font and increase its size
        smallFont = new BitmapFont();
        smallFont.getData().setScale(2.6f);
        smallFont.setColor(Color.WHITE);

        //Create a layout object for measuring text width and height
        layout = new GlyphLayout();

        //Create a 1x1 transparent black pixmap
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.78f);
        pixmap.fill();

        //Convert the pixmap into a texture so it can be drawn as a box
        darkBoxTexture = new Texture(pixmap);

        //Dispose of the pixmap because the texture has already been created
        pixmap.dispose();
    }

    //Draw centered bold text with a dark background box behind it
    protected void drawBoldTextWithBox(BitmapFont font, String text, float y, float paddingX, float paddingY) {
        //Measure the text size
        layout.setText(font, text);

        //Calculate the x position so the text is centered
        float textX = (screenWidth - layout.width) / 2f;

        //Calculate the background box position and size
        float boxX = textX - paddingX;
        float boxY = y - layout.height - paddingY;
        float boxWidth = layout.width + paddingX * 2f;
        float boxHeight = layout.height + paddingY * 2f;

        //Draw the dark background box behind the text
        game.batch.draw(darkBoxTexture, boxX, boxY, boxWidth, boxHeight);

        //Draw a black shadow behind the text
        font.setColor(Color.BLACK);
        font.draw(game.batch, text, textX + 4, y - 4);

        //Draw the main white text
        font.setColor(Color.WHITE);
        font.draw(game.batch, text, textX, y);

        //Draw the text multiple times with small offsets to make it look bold
        font.draw(game.batch, text, textX + 1, y);
        font.draw(game.batch, text, textX - 1, y);
        font.draw(game.batch, text, textX, y + 1);
        font.draw(game.batch, text, textX, y - 1);
    }

    @Override
    public void resize(int width, int height) {
        //Update the stored screen size when the window is resized
        screenWidth = width;
        screenHeight = height;

        //Update the UI camera to match the new screen size
        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.update();
    }

    @Override
    public void pause() {
        //Not used in this screen
    }

    @Override
    public void resume() {
        //Not used in this screen
    }

    @Override
    public void hide() {
        //Not used in this screen
    }

    @Override
    public void show() {
        //Not used in this screen
    }

    @Override
    public void dispose() {
        //Dispose fonts and textures to free memory
        titleFont.dispose();
        smallFont.dispose();
        darkBoxTexture.dispose();
    }
}
