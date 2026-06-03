package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public abstract class BaseScreen extends ScreenAdapter {
    protected Main game;

    protected OrthographicCamera uiCamera;

    protected BitmapFont titleFont;
    protected BitmapFont smallFont;
    protected GlyphLayout layout;

    protected Texture darkBoxTexture;

    protected float screenWidth;
    protected float screenHeight;

    public BaseScreen(Main game) {
        this.game = game;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.update();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(5.5f);
        titleFont.setColor(Color.WHITE);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(2.6f);
        smallFont.setColor(Color.WHITE);

        layout = new GlyphLayout();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.78f);
        pixmap.fill();
        darkBoxTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    protected void drawBoldTextWithBox(BitmapFont font, String text, float y, float paddingX, float paddingY) {
        layout.setText(font, text);

        float textX = (screenWidth - layout.width) / 2f;
        float boxX = textX - paddingX;
        float boxY = y - layout.height - paddingY;
        float boxWidth = layout.width + paddingX * 2f;
        float boxHeight = layout.height + paddingY * 2f;

        game.batch.draw(darkBoxTexture, boxX, boxY, boxWidth, boxHeight);

        font.setColor(Color.BLACK);
        font.draw(game.batch, text, textX + 4, y - 4);

        font.setColor(Color.WHITE);
        font.draw(game.batch, text, textX, y);
        font.draw(game.batch, text, textX + 1, y);
        font.draw(game.batch, text, textX - 1, y);
        font.draw(game.batch, text, textX, y + 1);
        font.draw(game.batch, text, textX, y - 1);
    }

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.update();
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        smallFont.dispose();
        darkBoxTexture.dispose();
    }
}
