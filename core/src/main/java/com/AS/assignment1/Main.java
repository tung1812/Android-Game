package com.AS.assignment1;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
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
    Texture heartFullTexture;

    Rectangle startButton;
    Rectangle optionButton;
    Rectangle creditButton;
    Rectangle quitButton;
    Rectangle menuButton;

    Rectangle upButton;
    Rectangle downButton;
    Rectangle leftButton;
    Rectangle rightButton;
    Rectangle attackButton;

    BitmapFont titleFont;
    BitmapFont smallFont;
    GlyphLayout layout;

    float screenWidth;
    float screenHeight;

    TiledMap tiledMap;
    IsometricTiledMapRenderer mapRenderer;
    OrthographicCamera mapCamera;
    OrthographicCamera uiCamera;

    Player player;
    Enemy enemy;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.jpg");
        startButtonTexture = new Texture("Menu/start.png");
        optionButtonTexture = new Texture("Menu/option.png");
        creditButtonTexture = new Texture("Menu/credit.png");
        quitButtonTexture = new Texture("Menu/quit.png");
        menuButtonTexture = new Texture("icon/home button.png");

        heartFullTexture = new Texture("xp bars/hearts/heart/heart full.png");

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

        setupButtons();
        setupCameras();
    }

    private void setupButtons() {
        float buttonWidth = screenWidth * 0.28f;
        float buttonHeight = screenHeight * 0.11f;
        float buttonX = (screenWidth - buttonWidth) / 2f;

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

        float controlSize = screenHeight * 0.10f;
        float gap = screenHeight * 0.02f;

        float controlX = screenWidth * 0.06f;
        float controlY = screenHeight * 0.08f;

        leftButton = new Rectangle(
            controlX,
            controlY + controlSize + gap,
            controlSize,
            controlSize
        );

        rightButton = new Rectangle(
            controlX + controlSize * 2f + gap * 2f,
            controlY + controlSize + gap,
            controlSize,
            controlSize
        );

        upButton = new Rectangle(
            controlX + controlSize + gap,
            controlY + controlSize * 2f + gap * 2f,
            controlSize,
            controlSize
        );

        downButton = new Rectangle(
            controlX + controlSize + gap,
            controlY,
            controlSize,
            controlSize
        );

        attackButton = new Rectangle(
            screenWidth * 0.84f,
            screenHeight * 0.10f,
            screenHeight * 0.13f,
            screenHeight * 0.13f
        );
    }

    private void setupCameras() {
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.update();

        mapCamera = new OrthographicCamera();
        mapCamera.setToOrtho(false, screenWidth, screenHeight);
        mapCamera.update();
    }

    private void loadMapIfNeeded() {
        if (tiledMap != null && mapRenderer != null) {
            return;
        }

        try {
            tiledMap = new TmxMapLoader().load("maps/level1.tmx");
            mapRenderer = new IsometricTiledMapRenderer(tiledMap);

            MapProperties properties = tiledMap.getProperties();

            int mapWidth = properties.get("width", Integer.class);
            int mapHeight = properties.get("height", Integer.class);
            int tileHeight = properties.get("tileheight", Integer.class);

            float centerX = 0;
            float centerY = (mapWidth + mapHeight) * tileHeight / 4f;

            player = new Player(centerX, centerY);
            enemy = new Enemy(centerX + 200f, centerY);

            mapCamera.position.set(player.getX(), player.getY(), 0);
            mapCamera.zoom = 1.0f;
            mapCamera.update();

            Gdx.app.log("MAP", "Map loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("MAP", "Failed to load map", e);

            tiledMap = null;
            mapRenderer = null;
            player = null;
            gameState = GameState.PLAYING;
        }
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
                loadMapIfNeeded();
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
    }

    private void updateHelp() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();

            if (menuButton.contains(touchX, touchY)) {
                gameState = GameState.MENU;
            }
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
    }

    private void updateGame() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        boolean touching = Gdx.input.isTouched();
        float touchX = 0;
        float touchY = 0;

        if (touching) {
            touchX = Gdx.input.getX();
            touchY = screenHeight - Gdx.input.getY();

            if (menuButton.contains(touchX, touchY)) {
                gameState = GameState.MENU;
                return;
            }
        }

        if (player != null) {
            player.update(
                deltaTime,
                leftButton,
                rightButton,
                upButton,
                downButton,
                attackButton,
                touchX,
                touchY,
                touching
            );
        }
        if (enemy != null) {
            enemy.update(deltaTime);
        }

        if (mapCamera != null && player != null) {
            mapCamera.position.set(player.getX(), player.getY(), 0);
            mapCamera.update();
        }
    }

    @Override
    public void render() {
        update();

        Gdx.gl.glClearColor(0.10f, 0.15f, 0.20f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameState == GameState.PLAYING) {
            drawGame();
            return;
        }

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        switch (gameState) {
            case MENU:
                drawMenu();
                break;

            case HELP:
                drawHelp();
                break;

            case CREDIT:
                drawCredit();
                break;

            default:
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

        drawBoldTextWithBox(smallFont, "Use screen buttons to move", screenHeight * 0.60f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap ATK to attack", screenHeight * 0.50f, 35, 18);
        drawBoldTextWithBox(smallFont, "Explore the isometric level", screenHeight * 0.40f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap home button to go back", screenHeight * 0.30f, 35, 18);
    }

    private void drawCredit() {
        batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawBoldTextWithBox(titleFont, "Credit", screenHeight * 0.78f, 45, 22);

        drawBoldTextWithBox(smallFont, "Game created by", screenHeight * 0.60f, 35, 18);
        drawBoldTextWithBox(smallFont, "Gia Minh Pham and Son Tung Nguyen", screenHeight * 0.50f, 35, 18);
        drawBoldTextWithBox(smallFont, "2D Isometric RPG Adventure Game", screenHeight * 0.40f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap home button to go back", screenHeight * 0.30f, 35, 18);
    }

    private void drawGame() {
        if (tiledMap == null || mapRenderer == null) {
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
            batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

            drawBoldTextWithBox(titleFont, "Map Error", screenHeight * 0.70f, 45, 22);
            drawBoldTextWithBox(smallFont, "Cannot load maps/level1.tmx", screenHeight * 0.55f, 35, 18);
            drawBoldTextWithBox(smallFont, "Check level1.tmx, tileset.tsx, and spritesheet.png", screenHeight * 0.45f, 35, 18);
            drawBoldTextWithBox(smallFont, "Tap home button to go back", screenHeight * 0.35f, 35, 18);

            batch.end();
            return;
        }

        mapRenderer.setView(mapCamera);
        mapRenderer.render();

        batch.setProjectionMatrix(mapCamera.combined);
        batch.begin();

        if (player != null) {
            player.draw(batch);
        }

        if (enemy != null) {
            enemy.draw(batch);
        }

        batch.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawHealthBar();
        drawControlButtons();

        drawBoldTextWithBox(titleFont, "LEVEL 1", screenHeight * 0.92f, 45, 22);
        drawBoldTextWithBox(smallFont, "Use buttons to move Reiko", screenHeight * 0.08f, 30, 14);

        batch.end();
    }

    private void drawHealthBar() {
        if (player == null) {
            return;
        }

        float heartSize = screenHeight * 0.065f;
        float gap = screenHeight * 0.010f;

        float startX = menuButton.x + menuButton.width + screenWidth * 0.03f;
        float startY = screenHeight * 0.855f;

        for (int i = 0; i < player.getHealth(); i++) {
            float heartX = startX + i * (heartSize + gap);

            batch.draw(
                heartFullTexture,
                heartX,
                startY,
                heartSize,
                heartSize
            );
        }
    }

    private void drawControlButtons() {
        drawButtonBox(upButton, "^");
        drawButtonBox(downButton, "v");
        drawButtonBox(leftButton, "<");
        drawButtonBox(rightButton, ">");

        drawButtonBox(attackButton, "ATK");
    }

    private void drawButtonBox(Rectangle button, String text) {
        batch.draw(darkBoxTexture, button.x, button.y, button.width, button.height);

        layout.setText(smallFont, text);

        float textX = button.x + (button.width - layout.width) / 2f;
        float textY = button.y + (button.height + layout.height) / 2f;

        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, text, textX, textY);
    }

    private void drawBoldTextWithBox(BitmapFont font, String text, float y, float paddingX, float paddingY) {
        layout.setText(font, text);

        float textX = (screenWidth - layout.width) / 2f;
        float boxX = textX - paddingX;
        float boxY = y - layout.height - paddingY;
        float boxWidth = layout.width + paddingX * 2f;
        float boxHeight = layout.height + paddingY * 2f;

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
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        setupButtons();

        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.update();

        if (mapCamera != null) {
            mapCamera.viewportWidth = screenWidth;
            mapCamera.viewportHeight = screenHeight;
            mapCamera.update();
        }
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
        heartFullTexture.dispose();

        titleFont.dispose();
        smallFont.dispose();

        if (player != null) {
            player.dispose();
        }

        if (enemy != null) {
            enemy.dispose();
        }

        if (tiledMap != null) {
            tiledMap.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
    }
}
