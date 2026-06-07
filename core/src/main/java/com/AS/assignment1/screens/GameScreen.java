package com.AS.assignment1.screens;

import com.AS.assignment1.Main;
import com.AS.assignment1.entities.EnemyManager;
import com.AS.assignment1.entities.Player;
import com.AS.assignment1.world.CollisionManager;
import com.AS.assignment1.world.MapTransitionManager;
import com.AS.assignment1.world.PuzzleManager;
import com.AS.assignment1.world.SpawnManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends BaseScreen {
    private Texture backgroundTexture;
    private Texture darkOverlayTexture;
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
    private PuzzleManager puzzleManager;

    private String currentMapPath;

    public GameScreen(Main game) {
        super(game);

        currentMapPath = game.getLevelManager().getCurrentMapPath();

        backgroundTexture = new Texture(getLevelBackgroundPath());
        menuButtonTexture = new Texture("icon/home button.png");
        heartFullTexture = new Texture("xp bars/hearts/heart/heart full.png");

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.85f);
        pixmap.fill();
        darkOverlayTexture = new Texture(pixmap);
        pixmap.dispose();

        buttonSheetTexture = new Texture("Buttons/Gray_Buttons_Pixel.png");
        attackButtonTexture = new Texture("Buttons/attack.png");
        loadButtonRegions();

        attackSoundPlayed = false;

        setupButtons();
        setupMapCamera();

        loadMap(currentMapPath, "player");
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
        mapCamera.zoom = 0.35f;
        mapCamera.update();
    }

    private void focusCameraOnPlayer() {
        if (mapCamera == null || player == null) {
            return;
        }

        mapCamera.position.set(player.getX(), player.getY(), 0);
        mapCamera.zoom = 0.35f;
        mapCamera.update();
    }

    private void loadMap(String mapPath, String spawnName) {
        try {
            disposeCurrentLevel();

            currentMapPath = mapPath;
            reloadLevelBackground();

            tiledMap = new TmxMapLoader().load(mapPath);
            mapRenderer = new IsometricTiledMapRenderer(tiledMap);
            collisionManager = new CollisionManager(tiledMap);
            mapTransitionManager = new MapTransitionManager(tiledMap);
            puzzleManager = new PuzzleManager(tiledMap);

            MapProperties properties = tiledMap.getProperties();

            int mapWidth = properties.get("width", Integer.class);
            int mapHeight = properties.get("height", Integer.class);
            int tileHeight = properties.get("tileheight", Integer.class);

            float fallbackX = 0;
            float fallbackY = (mapWidth + mapHeight) * tileHeight / 4f;

            SpawnManager spawnManager = new SpawnManager(tiledMap);

            Vector2 playerSpawn = spawnManager.getSpawnPoint(spawnName, fallbackX, fallbackY);

            if (player == null) {
                player = new Player(playerSpawn.x, playerSpawn.y);
            } else {
                player.setPosition(playerSpawn.x, playerSpawn.y);
            }

            enemyManager = new EnemyManager();
            Array<Vector2> enemySpawns = spawnManager.getSpawnPointsByPrefix("enemy");

            for (Vector2 enemySpawn : enemySpawns) {
                enemyManager.addEnemy(enemySpawn.x, enemySpawn.y);
                Gdx.app.log("SPAWN", "Enemy spawn: " + enemySpawn.x + ", " + enemySpawn.y);
            }

            focusCameraOnPlayer();

            Gdx.app.log("MAP", "Loaded map: " + mapPath);
            Gdx.app.log("SPAWN", "Player spawn: " + playerSpawn.x + ", " + playerSpawn.y);
            Gdx.app.log("SPAWN", "Total enemies: " + enemySpawns.size);

        } catch (Exception e) {
            Gdx.app.error("MAP", "Failed to load map: " + mapPath, e);

            tiledMap = null;
            mapRenderer = null;
            collisionManager = null;
            mapTransitionManager = null;
            puzzleManager = null;
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
        puzzleManager = null;
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
                game.getSoundManager().stopMoveLoop();
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

        updateMoveSound(touchX, touchY, touching);

        if (enemyManager != null) {
            boolean playerDamaged = enemyManager.update(deltaTime, collisionManager, player);

            if (playerDamaged) {
                game.getSoundManager().playHurt();
            }
        }

        if (player != null && player.isDead()) {
            game.getSoundManager().stopMoveLoop();
            game.showDeathScreen();
            return false;
        }

        if (puzzleManager != null && player != null) {
            puzzleManager.update(player.getX(), player.getY());
        }

        if (mapTransitionManager != null && player != null) {
            MapTransitionManager.TransitionResult transition =
                mapTransitionManager.checkTransition(player.getBounds());

            if (transition != null) {
                if (transition.requiresKey()
                    && (puzzleManager == null || !puzzleManager.hasKey())) {
                    Gdx.app.log("PUZZLE", "This exit requires a key.");
                    return true;
                }

                if ("WIN".equals(transition.getTargetMap())) {
                    game.getSoundManager().stopMoveLoop();
                    game.showWinScreen();
                    return false;
                }

                if ("maps/level2.tmx".equals(transition.getTargetMap())) {
                    game.getLevelManager().unlockLevel(2);
                    game.getLevelManager().setSelectedLevel(2);
                }

                if ("maps/level3.tmx".equals(transition.getTargetMap())) {
                    game.getLevelManager().unlockLevel(3);
                    game.getLevelManager().setSelectedLevel(3);
                }

                game.getSoundManager().stopMoveLoop();
                loadMap(transition.getTargetMap(), transition.getTargetSpawn());
                return true;
            }
        }

        focusCameraOnPlayer();

        return true;
    }

    private void updateMoveSound(float touchX, float touchY, boolean touching) {
        if (!touching) {
            game.getSoundManager().stopMoveLoop();
            return;
        }

        boolean moving =
            leftButton.contains(touchX, touchY)
                || rightButton.contains(touchX, touchY)
                || upButton.contains(touchX, touchY)
                || downButton.contains(touchX, touchY);

        boolean attacking = attackButton.contains(touchX, touchY);

        if (moving && !attacking) {
            game.getSoundManager().startMoveLoop();
        } else {
            game.getSoundManager().stopMoveLoop();
        }
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

        drawLevelBackground();

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
            "LEVEL " + getLevelNumberFromPath(),
            screenHeight * 0.92f,
            45,
            22
        );

        if (puzzleManager != null && puzzleManager.hasKeyObject()) {
            String keyText = puzzleManager.hasKey() ? "Key: Collected" : "Key: Missing";

            drawBoldTextWithBox(
                smallFont,
                keyText,
                screenHeight * 0.82f,
                30,
                14
            );
        }

        drawBoldTextWithBox(
            smallFont,
            "Use buttons to move Reiko",
            screenHeight * 0.08f,
            30,
            14
        );

        game.batch.end();
    }

    private int getLevelNumberFromPath() {
        if (currentMapPath == null) {
            return game.getLevelManager().getSelectedLevel();
        }

        if (currentMapPath.contains("level2")) {
            return 2;
        }

        if (currentMapPath.contains("level3")) {
            return 3;
        }

        return 1;
    }

    private void drawMapError() {
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        }

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

    private String getLevelBackgroundPath() {
        if (currentMapPath != null) {
            if (currentMapPath.contains("level1")) {
                return "level_1.jpg";
            }

            if (currentMapPath.contains("level2")) {
                return "level_2.jpg";
            }

            if (currentMapPath.contains("level3")) {
                return "level_3.png";
            }
        }

        int level = game.getLevelManager().getSelectedLevel();

        if (level == 1) {
            return "level_1.jpg";
        }

        if (level == 2) {
            return "level_2.jpg";
        }

        if (level == 3) {
            return "level_3.png";
        }

        return "level_1.jpg";
    }

    private void reloadLevelBackground() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        backgroundTexture = new Texture(getLevelBackgroundPath());
    }

    private void drawLevelBackground() {
        if (backgroundTexture == null || darkOverlayTexture == null) {
            return;
        }

        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        game.batch.setColor(1f, 1f, 1f, 0.45f);
        game.batch.draw(darkOverlayTexture, 0, 0, screenWidth, screenHeight);
        game.batch.setColor(Color.WHITE);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        setupButtons();

        if (mapCamera != null) {
            mapCamera.viewportWidth = screenWidth;
            mapCamera.viewportHeight = screenHeight;
            focusCameraOnPlayer();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        if (darkOverlayTexture != null) {
            darkOverlayTexture.dispose();
        }

        if (menuButtonTexture != null) {
            menuButtonTexture.dispose();
        }

        if (heartFullTexture != null) {
            heartFullTexture.dispose();
        }

        if (buttonSheetTexture != null) {
            buttonSheetTexture.dispose();
        }

        if (attackButtonTexture != null) {
            attackButtonTexture.dispose();
        }

        game.getSoundManager().stopMoveLoop();
        disposeCurrentLevel();

        if (player != null) {
            player.dispose();
            player = null;
        }
    }
}
