package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.AS.assignment1.entities.Player;
import com.AS.assignment1.world.SpawnManager;
import com.AS.assignment1.entities.EnemyManager;
import com.AS.assignment1.world.CollisionManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
public class GameScreen extends BaseScreen {
    private Texture backgroundTexture;
    private Texture menuButtonTexture;
    private Texture heartFullTexture;

    private Rectangle menuButton;
    private Rectangle upButton;
    private Rectangle downButton;
    private Rectangle leftButton;
    private Rectangle rightButton;
    private Rectangle attackButton;

    private TiledMap tiledMap;
    private IsometricTiledMapRenderer mapRenderer;
    private OrthographicCamera mapCamera;

    private Player player;
    private EnemyManager enemyManager;
    private CollisionManager collisionManager;

    public GameScreen(Main game) {
        super(game);

        backgroundTexture = new Texture("background.jpg");
        menuButtonTexture = new Texture("icon/home button.png");
        heartFullTexture = new Texture("xp bars/hearts/heart/heart full.png");

        setupButtons();
        setupMapCamera();

        enemyManager = new EnemyManager();

        loadMap();
    }

    private void setupButtons() {
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

    private void setupMapCamera() {
        mapCamera = new OrthographicCamera();
        mapCamera.setToOrtho(false, screenWidth, screenHeight);
        mapCamera.update();
    }

    private void loadMap() {
        try {
            tiledMap = new TmxMapLoader().load("maps/level1.tmx");
            mapRenderer = new IsometricTiledMapRenderer(tiledMap);
            collisionManager = new CollisionManager(tiledMap);

            MapProperties properties = tiledMap.getProperties();

            int mapWidth = properties.get("width", Integer.class);
            int mapHeight = properties.get("height", Integer.class);
            int tileHeight = properties.get("tileheight", Integer.class);

            float centerX = 0;
            float centerY = (mapWidth + mapHeight) * tileHeight / 4f;

            SpawnManager spawnManager = new SpawnManager(tiledMap);

            Vector2 playerSpawn = spawnManager.getSpawnPoint("player", centerX, centerY);
            Vector2 enemySpawn = spawnManager.getSpawnPoint("enemy1", playerSpawn.x + 200f, playerSpawn.y);

            player = new Player(playerSpawn.x, playerSpawn.y);

            if (enemyManager != null) {
                enemyManager.addEnemy(enemySpawn.x, enemySpawn.y);
            }

            mapCamera.position.set(player.getX(), player.getY(), 0);
            mapCamera.zoom = 1.0f;
            mapCamera.update();

            Gdx.app.log("MAP", "Map loaded successfully");
            Gdx.app.log("SPAWN", "Player spawn: " + playerSpawn.x + ", " + playerSpawn.y);
            Gdx.app.log("SPAWN", "Enemy spawn: " + enemySpawn.x + ", " + enemySpawn.y);

        } catch (Exception e) {
            Gdx.app.error("MAP", "Failed to load map", e);

            tiledMap = null;
            mapRenderer = null;
            player = null;
        }
    }

    private void update(float deltaTime) {
        boolean touching = Gdx.input.isTouched();
        float touchX = 0;
        float touchY = 0;

        if (touching) {
            touchX = Gdx.input.getX();
            touchY = screenHeight - Gdx.input.getY();

            if (menuButton.contains(touchX, touchY)) {
                game.showMenuScreen();
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
                touching,
                collisionManager
            );

//            if (player.isDead()) {
//                game.showDeathScreen();
//                return;
//            }
        }

        if (enemyManager != null) {
            enemyManager.update(deltaTime, collisionManager, player);
        }

        if (mapCamera != null && player != null) {
            mapCamera.position.set(player.getX(), player.getY(), 0);
            mapCamera.update();
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.10f, 0.15f, 0.20f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawGame();
    }

    private void drawGame() {
        if (tiledMap == null || mapRenderer == null) {
            drawMapError();
            return;
        }

        mapRenderer.setView(mapCamera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(mapCamera.combined);
        game.batch.begin();

        if (player != null) {
            player.draw(game.batch);
        }

        if (enemyManager != null) {
            enemyManager.draw(game.batch);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawHealthBar();
        drawControlButtons();

        drawBoldTextWithBox(titleFont, "LEVEL 1", screenHeight * 0.92f, 45, 22);
        drawBoldTextWithBox(smallFont, "Use buttons to move Reiko", screenHeight * 0.08f, 30, 14);

        game.batch.end();
    }

    private void drawMapError() {
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        game.batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawBoldTextWithBox(titleFont, "Map Error", screenHeight * 0.70f, 45, 22);
        drawBoldTextWithBox(smallFont, "Cannot load maps/level1.tmx", screenHeight * 0.55f, 35, 18);
        drawBoldTextWithBox(smallFont, "Check level1.tmx, tileset.tsx, and spritesheet.png", screenHeight * 0.45f, 35, 18);
        drawBoldTextWithBox(smallFont, "Tap home button to go back", screenHeight * 0.35f, 35, 18);

        game.batch.end();
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

            game.batch.draw(
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
        game.batch.draw(darkBoxTexture, button.x, button.y, button.width, button.height);

        layout.setText(smallFont, text);

        float textX = button.x + (button.width - layout.width) / 2f;
        float textY = button.y + (button.height + layout.height) / 2f;

        smallFont.setColor(Color.WHITE);
        smallFont.draw(game.batch, text, textX, textY);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        setupButtons();

        if (mapCamera != null) {
            mapCamera.viewportWidth = screenWidth;
            mapCamera.viewportHeight = screenHeight;
            mapCamera.update();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        backgroundTexture.dispose();
        menuButtonTexture.dispose();
        heartFullTexture.dispose();

        if (player != null) {
            player.dispose();
        }

        if (enemyManager != null) {
            enemyManager.dispose();
        }

        if (tiledMap != null) {
            tiledMap.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
    }
}
