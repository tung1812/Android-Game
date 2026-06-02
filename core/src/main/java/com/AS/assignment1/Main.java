package com.AS.assignment1;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Main extends ApplicationAdapter {

    public enum GameState {
        MENU,
        HELP,
        PLAYING,
        CREDIT
    }

    GameState gameState = GameState.MENU;

    SpriteBatch batch;

    Texture backgroundTexture;
    Texture startButtonTexture;
    Texture optionButtonTexture;
    Texture creditButtonTexture;
    Texture quitButtonTexture;
    Texture menuButtonTexture;
    Texture darkBoxTexture;

    Rectangle startButton;
    Rectangle optionButton;
    Rectangle creditButton;
    Rectangle quitButton;
    Rectangle menuButton;

    BitmapFont titleFont;
    BitmapFont smallFont;
    GlyphLayout layout;

    float screenWidth;
    float screenHeight;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.jpg");
        startButtonTexture = new Texture("Menu/start.png");
        optionButtonTexture = new Texture("Menu/option.png");
        creditButtonTexture = new Texture("Menu/credit.png");
        quitButtonTexture = new Texture("Menu/quit.png");
        menuButtonTexture = new Texture("icon/home button.png");

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.78f);
        pixmap.fill();
        darkBoxTexture = new Texture(pixmap);
        pixmap.dispose();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(5.5f);
        titleFont.setColor(Color.WHITE);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(2.6f);
        smallFont.setColor(Color.WHITE);

        layout = new GlyphLayout();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        float buttonWidth = screenWidth * 0.28f;
        float buttonHeight = screenHeight * 0.11f;
        float buttonX = (screenWidth - buttonWidth) / 2;

        startButton = new Rectangle(buttonX, screenHeight * 0.58f, buttonWidth, buttonHeight);
        optionButton = new Rectangle(buttonX, screenHeight * 0.44f, buttonWidth, buttonHeight);
        creditButton = new Rectangle(buttonX, screenHeight * 0.30f, buttonWidth, buttonHeight);
        quitButton = new Rectangle(buttonX, screenHeight * 0.16f, buttonWidth, buttonHeight);

        menuButton = new Rectangle(
            screenWidth * 0.04f,
            screenHeight * 0.82f,
            screenHeight * 0.12f,
            screenHeight * 0.12f
        );
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

            case CREDIT:
                updateCredit();
                break;
        }
    }

    private void updateMenu() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();

            if (startButton.contains(touchX, touchY)) {
                gameState = GameState.PLAYING;
            }

            if (optionButton.contains(touchX, touchY)) {
                gameState = GameState.HELP;
            }

            if (creditButton.contains(touchX, touchY)) {
                gameState = GameState.CREDIT;
            }

            if (quitButton.contains(touchX, touchY)) {
                Gdx.app.exit();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gameState = GameState.PLAYING;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            gameState = GameState.HELP;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            gameState = GameState.CREDIT;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.exit();
        }
    }

    private void updateHelp() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();

            if (menuButton.contains(touchX, touchY)) {
                gameState = GameState.MENU;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            gameState = GameState.MENU;
        }
    }

    private void updateCredit() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();

            if (menuButton.contains(touchX, touchY)) {
                gameState = GameState.MENU;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            gameState = GameState.MENU;
        }
    }

    private void updateGame() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();

            if (menuButton.contains(touchX, touchY)) {
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

        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

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

            case CREDIT:
                drawCredit();
                break;
        }

        batch.end();
    }

    private void drawMenu() {
        drawBoldTextWithBox(titleFont, "Assignment 2 RPG Games", screenHeight * 0.86f, 45, 22);

        batch.draw(startButtonTexture, startButton.x, startButton.y, startButton.width, startButton.height);
        batch.draw(optionButtonTexture, optionButton.x, optionButton.y, optionButton.width, optionButton.height);
        batch.draw(creditButtonTexture, creditButton.x, creditButton.y, creditButton.width, creditButton.height);
        batch.draw(quitButtonTexture, quitButton.x, quitButton.y, quitButton.width, quitButton.height);
    }

    private void drawHelp() {
        batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawBoldTextWithBox(titleFont, "Option", screenHeight * 0.78f, 45, 22);

        drawBoldTextWithBox(smallFont, "Touch buttons to control the player", screenHeight * 0.60f, 35, 18);
        drawBoldTextWithBox(smallFont, "Attack enemies and reach the goal", screenHeight * 0.50f, 35, 18);
        drawBoldTextWithBox(smallFont, "If your HP reaches 0, the player dies", screenHeight * 0.40f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap the home button to go back", screenHeight * 0.30f, 35, 18);
    }

    private void drawCredit() {
        batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawBoldTextWithBox(titleFont, "Credit", screenHeight * 0.78f, 45, 22);

        drawBoldTextWithBox(smallFont, "Game created by", screenHeight * 0.60f, 35, 18);
        drawBoldTextWithBox(smallFont, "Gia Minh Pham and Son Tung Nguyen", screenHeight * 0.50f, 35, 18);
        drawBoldTextWithBox(smallFont, "2D RPG Adventure Game", screenHeight * 0.40f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap the home button to go back", screenHeight * 0.30f, 35, 18);
    }

    private void drawGame() {
        batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawBoldTextWithBox(titleFont, "LEVEL 1", screenHeight * 0.70f, 45, 22);
    }

    private void drawBoldTextWithBox(BitmapFont font, String text, float y, float paddingX, float paddingY) {
        layout.setText(font, text);

        float textX = (screenWidth - layout.width) / 2;
        float boxX = textX - paddingX;
        float boxY = y - layout.height - paddingY;
        float boxWidth = layout.width + paddingX * 2;
        float boxHeight = layout.height + paddingY * 2;

        batch.draw(darkBoxTexture, boxX, boxY, boxWidth, boxHeight);

        font.setColor(Color.BLACK);
        font.draw(batch, text, textX + 4, y - 4);

        font.setColor(Color.WHITE);
        font.draw(batch, text, textX, y);
        font.draw(batch, text, textX + 1, y);
        font.draw(batch, text, textX - 1, y);
        font.draw(batch, text, textX, y + 1);
        font.draw(batch, text, textX, y - 1);
    }

    @Override
    public void dispose() {
        batch.dispose();

        backgroundTexture.dispose();
        startButtonTexture.dispose();
        optionButtonTexture.dispose();
        creditButtonTexture.dispose();
        quitButtonTexture.dispose();
        menuButtonTexture.dispose();
        darkBoxTexture.dispose();

        titleFont.dispose();
        smallFont.dispose();
    }
}
