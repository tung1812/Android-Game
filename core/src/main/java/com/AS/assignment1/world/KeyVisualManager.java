package com.AS.assignment1.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyVisualManager {
    private final TiledMap map;
    private final int tileWidth;
    private final int tileHeight;

    private Texture keySheet;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> disappearAnimation;

    private float idleAnimationTime;

    private Map<MapObject, Float> disappearTimers;
    private Set<MapObject> finishedDisappearAnimations;

    public KeyVisualManager(TiledMap map) {
        this.map = map;

        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        idleAnimationTime = 0f;

        disappearTimers = new HashMap<>();
        finishedDisappearAnimations = new HashSet<>();

        loadKeyAnimations();
    }

    private void loadKeyAnimations() {
        keySheet = new Texture("Items/blue_coin_sheet.png");

        int columns = 8;
        int rows = 2;

        int frameWidth = keySheet.getWidth() / columns;
        int frameHeight = keySheet.getHeight() / rows;

        TextureRegion[][] regions = TextureRegion.split(
            keySheet,
            frameWidth,
            frameHeight
        );

        Array<TextureRegion> idleFrames = new Array<>();
        Array<TextureRegion> disappearFrames = new Array<>();

        for (int col = 0; col < columns; col++) {
            idleFrames.add(regions[0][col]);
        }

        for (int col = 0; col < columns; col++) {
            disappearFrames.add(regions[1][col]);
        }

        idleAnimation = new Animation<>(0.12f, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        disappearAnimation = new Animation<>(0.08f, disappearFrames);
        disappearAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void update(float deltaTime) {
        idleAnimationTime += deltaTime;

        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        for (MapObject object : interactablesLayer.getObjects()) {
            if (!isKeyObject(object)) {
                continue;
            }

            if (!isCollected(object)) {
                continue;
            }

            if (finishedDisappearAnimations.contains(object)) {
                continue;
            }

            float currentTimer = disappearTimers.containsKey(object)
                ? disappearTimers.get(object)
                : 0f;

            currentTimer += deltaTime;
            disappearTimers.put(object, currentTimer);

            if (disappearAnimation.isAnimationFinished(currentTimer)) {
                finishedDisappearAnimations.add(object);
            }
        }
    }

    public void draw(SpriteBatch batch, boolean enemiesCleared) {
        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        for (MapObject object : interactablesLayer.getObjects()) {
            if (!isKeyObject(object)) {
                continue;
            }

            boolean requiresEnemiesCleared = getBooleanProperty(
                object,
                "requiresEnemiesCleared",
                false
            );

            if (requiresEnemiesCleared && !enemiesCleared) {
                continue;
            }

            if (finishedDisappearAnimations.contains(object)) {
                continue;
            }

            TextureRegion frame;

            if (isCollected(object)) {
                float disappearTime = disappearTimers.containsKey(object)
                    ? disappearTimers.get(object)
                    : 0f;

                frame = disappearAnimation.getKeyFrame(disappearTime, false);
            } else {
                frame = idleAnimation.getKeyFrame(idleAnimationTime, true);
            }

            int visualTileCol = getIntProperty(
                object,
                "visualTileCol",
                getIntProperty(object, "tileCol", 0)
            );

            int visualTileRow = getIntProperty(
                object,
                "visualTileRow",
                getIntProperty(object, "tileRow", 0)
            );

            Vector2 worldPosition = tileToWorld(visualTileCol, visualTileRow);

            float scale = getFloatProperty(object, "keyScale", 1.5f);

            float drawWidth = frame.getRegionWidth() * scale;
            float drawHeight = frame.getRegionHeight() * scale;

            batch.draw(
                frame,
                worldPosition.x - drawWidth / 2f,
                worldPosition.y + 8f,
                drawWidth,
                drawHeight
            );
        }
    }

    private boolean getBooleanProperty(MapObject object, String propertyName, boolean fallback) {
        Object value = object.getProperties().get(propertyName);

        if (value == null) {
            return fallback;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return Boolean.parseBoolean(value.toString());
    }

    private boolean isKeyObject(MapObject object) {
        String type = getStringProperty(object, "type", "");
        String objectName = object.getName();

        return "key".equalsIgnoreCase(type)
            || (objectName != null && objectName.toLowerCase().startsWith("key"));
    }

    private boolean isCollected(MapObject object) {
        Object value = object.getProperties().get("collected");

        if (value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return Boolean.parseBoolean(value.toString());
    }

    private Vector2 tileToWorld(int tileCol, int tileRow) {
        float worldX = (tileCol + tileRow) * tileWidth / 2f;
        float worldY = (tileRow - tileCol) * tileHeight / 2f;

        return new Vector2(worldX, worldY);
    }

    private int getIntProperty(MapObject object, String propertyName, int fallback) {
        Object value = object.getProperties().get(propertyName);

        if (value == null) {
            return fallback;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Float) {
            return Math.round((Float) value);
        }

        if (value instanceof Double) {
            return (int) Math.round((Double) value);
        }

        return Math.round(Float.parseFloat(value.toString()));
    }

    private float getFloatProperty(MapObject object, String propertyName, float fallback) {
        Object value = object.getProperties().get(propertyName);

        if (value == null) {
            return fallback;
        }

        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }

        if (value instanceof Float) {
            return (Float) value;
        }

        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }

        return Float.parseFloat(value.toString());
    }

    private String getStringProperty(MapObject object, String propertyName, String fallback) {
        Object value = object.getProperties().get(propertyName);

        if (value == null) {
            return fallback;
        }

        return value.toString();
    }

    public void dispose() {
        keySheet.dispose();
    }
}
