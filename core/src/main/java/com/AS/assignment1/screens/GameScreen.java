package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.AS.assignment1.entities.Player;
import com.AS.assignment1.entities.EnemyManager;
import com.AS.assignment1.world.SpawnManager;
import com.AS.assignment1.world.CollisionManager;
import com.AS.assignment1.world.MapTransitionManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private Texture buttonSheetTexture;
    private Texture attackButtonTexture;

    private TextureRegion upButtonRegion;
    private TextureRegion downButtonRegion;
    private TextureRegion leftButtonRegion;
    private TextureRegion rightButtonRegion;

    private Rectangle menuButton;
    private Rectangle upButton;
    private Rectangle downButton;
    private Rectangle leftButton;
    private Rectangle rightButton;
    private Rectangle attackButton;

    private boolean attackSoundPlayed;

    private TiledMap tiledMap;
    private IsometricTiledMapRenderer mapRenderer;
    private OrthographicCamera mapCamera;

    private Player player;
    private EnemyManager enemyManager;
    private CollisionManager collisionManager;
    private MapTransitionManager mapTransitionManager;

    private String currentMapPath;

    public GameScreen(Main game) {
        super(game);

        backgroundTexture = new Texture("background.jpg");
        menuButtonTexture = new Texture("icon/home button.png");
        heartFullTexture = new Texture("xp bars/hearts/heart/heart full.png");

        buttonSheetTexture = new Texture("Buttons/Gray_Buttons_Pixel.png");
        attackButtonTexture = new Texture("Buttons/attack.png");

        loadButtonRegions();

        attackSoundPlayed = false;

        setupButtons();
        setupMapCamera();

        loadMap(game.getLevelManager().getCurrentMapPath(), "player");
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

    private void loadButtonRegions() {
        TextureRegion[][] buttons = TextureRegion.split(buttonSheetTexture, 16, 16);

        upButtonRegion = buttons[1][6];
        downButtonRegion = buttons[1][5];
        leftButtonRegion = buttons[0][5];
        rightButtonRegion = buttons[0][6];
    }

    private void setupMapCamera() {
        mapCamera = new OrthographicCamera();
        mapCamera.setToOrtho(false, screenWidth, screenHeight);
        mapCamera.update();
    }

    private void loadMap(String mapPath, String spawnName) {
        try {
            disposeCurrentLevel();

            currentMapPath = mapPath;

            tiledMap = new TmxMapLoader().load(mapPath);
            mapRenderer = new IsometricTiledMapRenderer(tiledMap);

            collisionManager = new CollisionManager(tiledMap);
            mapTransitionManager = new MapTransitionManager(tiledMap);

            MapProperties properties = tiledMap.getProperties();

            int mapWidth = properties.get("width", Integer.class);
            int mapHeight = properties.get("height", Integer.class);
            int tileHeight = properties.get("tileheight", Integer.class);

            float fallbackX = 0;
            float fallbackY = (mapWidth + mapHeight) * tileHeight / 4f;

            SpawnManager spawnManager = new SpawnManager(tiledMap);

            Vector2 playerSpawn = spawnManager.getSpawnPoint(spawnName, fallbackX, fallbackY);
            Vector2 enemySpawn = spawnManager.getSpawnPoint("enemy1", playerSpawn.x + 200f, playerSpawn.y);

            if (player == null) {
                player = new Player(playerSpawn.x, playerSpawn.y);
            } else {
                player.setPosition(playerSpawn.x, playerSpawn.y);
            }

            enemyManager = new EnemyManager();
            enemyManager.addEnemy(enemySpawn.x, enemySpawn.y);

            mapCamera.position.set(player.getX(), player.getY(), 0);
            mapCamera.zoom = 1.0f;
            mapCamera.update();

            Gdx.app.log("MAP", "Loaded map: " + mapPath);
            Gdx.app.log("SPAWN", "Player spawn: " + playerSpawn.x + ", " + playerSpawn.y);
            Gdx.app.log("SPAWN", "Enemy spawn: " + enemySpawn.x + ", " + enemySpawn.y);

        } catch (Exception e) {
            Gdx.app.error("MAP", "Failed to load map: " + mapPath, e);

            tiledMap = null;
            mapRenderer = null;
            collisionManager = null;
            mapTransitionManager = null;
            enemyManager = null;
            player = null;
        }
    }

    private void disposeCurrentLevel() {
        if (enemyManager != null) {
            enemyManager.dispose();
            enemyManager = null;
        }

        if (tiledMap != null) {
            tiledMap.dispose();
            tiledMap = null;
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
            mapRenderer = null;
        }

        collisionManager = null;
        mapTransitionManager = null;
    }

    private boolean update(float deltaTime) {
        boolean touching = Gdx.input.isTouched();
        float touchX = 0;
        float touchY = 0;

        if (touching) {
            touchX = Gdx.input.getX();
            touchY = screenHeight - Gdx.input.getY();

            if (menuButton.contains(touchX, touchY)) {
                game.getSoundManager().playClick();
                game.showMenuScreen();
                return false;
            }

            if (attackButton.contains(touchX, touchY)) {
                if (!attackSoundPlayed) {
                    game.getSoundManager().playAttack();
                    attackSoundPlayed = true;
                }
            } else {
                attackSoundPlayed = false;
            }
        } else {
            attackSoundPlayed = false;
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
        }

        if (enemyManager != null) {
            enemyManager.update(deltaTime, collisionManager, player);
        }

        if (player != null && player.isDead()) {
            game.showDeathScreen();
            return false;
        }

        if (mapTransitionManager != null && player != null) {
            MapTransitionManager.TransitionResult transition =
                mapTransitionManager.checkTransition(player.getBounds());

            if (transition != null) {
                if ("WIN".equals(transition.getTargetMap())) {
                    game.showWinScreen();
                    return false;
                }

                if ("maps/level2.tmx".equals(transition.getTargetMap())) {
                    game.getLevelManager().unlockLevel(2);
                }

                if ("maps/level3.tmx".equals(transition.getTargetMap())) {
                    game.getLevelManager().unlockLevel(3);
                }

                loadMap(transition.getTargetMap(), transition.getTargetSpawn());
                return true;
            }
        }

        if (mapCamera != null && player != null) {
            mapCamera.position.set(player.getX(), player.getY(), 0);
            mapCamera.update();
        }

        return true;
    }

    @Override
    public void render(float delta) {
        if (!update(delta)) {
            return;
        }

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

        game.batch.draw(
            menuButtonTexture,
            menuButton.x,
            menuButton.y,
            menuButton.width,
            menuButton.height
        );

        drawHealthBar();
        drawControlButtons();

        drawBoldTextWithBox(
            titleFont,
            "LEVEL " + game.getLevelManager().getSelectedLevel(),
            screenHeight * 0.92f,
            45,
            22
        );

        drawBoldTextWithBox(
            smallFont,
            "Use buttons to move Reiko",
            screenHeight * 0.08f,
            30,
            14
        );

        game.batch.end();
    }

    private void drawMapError() {
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        game.batch.draw(menuButtonTexture, menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        drawBoldTextWithBox(titleFont, "Map Error", screenHeight * 0.70f, 45, 22);

        if (currentMapPath != null) {
            drawBoldTextWithBox(smallFont, "Cannot load " + currentMapPath, screenHeight * 0.55f, 35, 18);
        } else {
            drawBoldTextWithBox(smallFont, "Cannot load selected map", screenHeight * 0.55f, 35, 18);
        }

        drawBoldTextWithBox(smallFont, "Check map, tileset, and spawn objects", screenHeight * 0.45f, 35, 18);
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
        drawImageButton(upButton, upButtonRegion);
        drawImageButton(downButton, downButtonRegion);
        drawImageButton(leftButton, leftButtonRegion);
        drawImageButton(rightButton, rightButtonRegion);

        drawTextureButton(attackButton, attackButtonTexture);
    }

    private void drawImageButton(Rectangle button, TextureRegion region) {
        if (region == null) {
            return;
        }

        game.batch.draw(
            region,
            button.x,
            button.y,
            button.width,
            button.height
        );
    }

    private void drawTextureButton(Rectangle button, Texture texture) {
        if (texture == null) {
            return;
        }

        game.batch.draw(
            texture,
            button.x,
            button.y,
            button.width,
            button.height
        );
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
        buttonSheetTexture.dispose();

        if (attackButtonTexture != null) {
            attackButtonTexture.dispose();
            attackButtonTexture = null;
        }

        disposeCurrentLevel();

        if (player != null) {
            player.dispose();
            player = null;
        }
    }
}
