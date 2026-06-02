package com.AS.assignment1;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {

    public enum GameState {
        MENU,
        HELP,
        PLAYING
    }

    GameState gameState = GameState.MENU;

    SpriteBatch batch;

    BitmapFont titleFont;
    BitmapFont menuFont;
    BitmapFont smallFont;

    GlyphLayout layout;

    float screenWidth;
    float screenHeight;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        batch = new SpriteBatch();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(5);

        menuFont = new BitmapFont();
        menuFont.getData().setScale(3);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(2);

        layout = new GlyphLayout();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    }

    private void update() {
        switch (gameState) {
            case MENU:
                updateMenu();
                break;

            case HELP:
                updateHelp();
                break;

            case PLAYING:
                updateGame();
                break;
        }
    }

    private void updateMenu() {
        float startY = screenHeight * 0.62f;
        float helpY = screenHeight * 0.48f;
        float exitY = screenHeight * 0.34f;

        if (Gdx.input.justTouched()) {
            float touchY = screenHeight - Gdx.input.getY();

            if (touchY <= startY + 40 && touchY >= startY - 60) {
                gameState = GameState.PLAYING;
            }

            if (touchY <= helpY + 40 && touchY >= helpY - 60) {
                gameState = GameState.HELP;
            }

            if (touchY <= exitY + 40 && touchY >= exitY - 60) {
                Gdx.app.exit();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gameState = GameState.PLAYING;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            gameState = GameState.HELP;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.exit();
        }
    }

    private void updateHelp() {
        float backY = screenHeight * 0.12f;

        if (Gdx.input.justTouched()) {
            float touchY = screenHeight - Gdx.input.getY();

            if (touchY <= backY + 40 && touchY >= backY - 60) {
                gameState = GameState.MENU;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gameState = GameState.MENU;
        }
    }

    private void updateGame() {
        float menuY = screenHeight * 0.15f;

        if (Gdx.input.justTouched()) {
            float touchY = screenHeight - Gdx.input.getY();

            if (touchY <= menuY + 40 && touchY >= menuY - 60) {
                gameState = GameState.MENU;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            gameState = GameState.MENU;
        }
    }

    @Override
    public void render() {
        update();

        Gdx.gl.glClearColor(0.10f, 0.15f, 0.20f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        switch (gameState) {
            case MENU:
                drawMenu();
                break;

            case HELP:
                drawHelp();
                break;

            case PLAYING:
                drawGame();
                break;
        }

        batch.end();
    }

    private void drawMenu() {
        drawCenteredText(titleFont, "Assignment 2 adventure rpg game", screenHeight * 0.82f);

        drawCenteredText(menuFont, "START", screenHeight * 0.62f);
        drawCenteredText(menuFont, "HELP", screenHeight * 0.48f);
        drawCenteredText(menuFont, "EXIT", screenHeight * 0.34f);

        drawCenteredText(smallFont, "Touch START to start the game.", screenHeight * 0.18f);
        drawCenteredText(smallFont, "Need help? Right under.", screenHeight * 0.10f);
    }

    private void drawHelp() {
        drawCenteredText(titleFont, "HELP", screenHeight * 0.82f);

        drawCenteredText(smallFont, "Touch buttons to control the player", screenHeight * 0.54f);
        drawCenteredText(smallFont, "Attack enemies and reach the goal point", screenHeight * 0.44f);
        drawCenteredText(smallFont, "If your HP reaches 0, the player dies", screenHeight * 0.34f);

        drawCenteredText(menuFont, "BACK", screenHeight * 0.12f);
    }

    private void drawGame() {
        drawCenteredText(menuFont, "MENU", screenHeight * 0.15f);
    }

    private void drawCenteredText(BitmapFont font, String text, float y) {
        layout.setText(font, text);

        float x = (screenWidth - layout.width) / 2;

        font.draw(batch, text, x, y);
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        menuFont.dispose();
        smallFont.dispose();
    }
}
