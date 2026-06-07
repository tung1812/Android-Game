package com.AS.assignment1.world;

import com.AS.assignment1.utils.IsoUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PortalVisualManager {
    private final TiledMap map;
    private final int tileWidth;
    private final int tileHeight;

    private Texture inactivePortalTexture;
    private Texture activePortalSheet;

    private TextureRegion inactivePortalFrame;
    private Animation<TextureRegion> activePortalAnimation;

    private float animationTime;

    public PortalVisualManager(TiledMap map) {
        this.map = map;

        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        animationTime = 0f;

        loadPortalAssets();
    }

    private void loadPortalAssets() {
        inactivePortalTexture = new Texture("Portals/portal_inactive.png");
        activePortalSheet = new Texture("Portals/portal_active_sheet.png");

        int frameCount = 6;
        int frameWidth = activePortalSheet.getWidth() / frameCount;
        int frameHeight = activePortalSheet.getHeight();

        inactivePortalFrame = new TextureRegion(
            inactivePortalTexture,
            0,
            0,
            Math.min(frameWidth, inactivePortalTexture.getWidth()),
            Math.min(frameHeight, inactivePortalTexture.getHeight())
        );

        Array<TextureRegion> activeFrames = new Array<>();

        for (int i = 0; i < frameCount; i++) {
            activeFrames.add(new TextureRegion(
                activePortalSheet,
                i * frameWidth,
                0,
                frameWidth,
                frameHeight
            ));
        }

        activePortalAnimation = new Animation<>(0.12f, activeFrames);
        activePortalAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float deltaTime) {
        animationTime += deltaTime;
    }

    public void draw(SpriteBatch batch, boolean hasKey) {
        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        for (MapObject object : interactablesLayer.getObjects()) {
            String targetMap = getStringProperty(object, "targetMap", "");

            if (targetMap.length() == 0) {
                continue;
            }

            boolean requiresKey = getBooleanProperty(object, "requiresKey", false);

            boolean portalIsActive = !requiresKey || hasKey || "WIN".equals(targetMap);

            TextureRegion frame;

            if (portalIsActive) {
                frame = activePortalAnimation.getKeyFrame(animationTime, true);
            } else {
                frame = inactivePortalFrame;
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

            Vector2 worldPosition = IsoUtils.tileToWorld(
                visualTileCol,
                visualTileRow,
                tileWidth,
                tileHeight
            );

            float scale = getFloatProperty(object, "portalScale", 1f);

            float drawWidth = frame.getRegionWidth() * scale;
            float drawHeight = frame.getRegionHeight() * scale;

            batch.draw(
                frame,
                worldPosition.x - drawWidth / 2f,
                worldPosition.y,
                drawWidth,
                drawHeight
            );
        }
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

        if (value instanceof Float) {
            return (Float) value;
        }

        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }

        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }

        return Float.parseFloat(value.toString());
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

    private String getStringProperty(MapObject object, String propertyName, String fallback) {
        Object value = object.getProperties().get(propertyName);

        if (value == null) {
            return fallback;
        }

        return value.toString();
    }

    public void dispose() {
        inactivePortalTexture.dispose();
        activePortalSheet.dispose();
    }
}
