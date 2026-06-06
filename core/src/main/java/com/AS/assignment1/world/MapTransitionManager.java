package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MapTransitionManager {
    private final TiledMap map;
    private final int tileWidth;
    private final int tileHeight;

    public MapTransitionManager(TiledMap map) {
        this.map = map;
        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    public TransitionResult checkTransition(Rectangle playerBounds) {
        if (map == null) {
            Gdx.app.log("LEVEL", "Map is null");
            return null;
        }

        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        if (interactablesLayer == null) {
            Gdx.app.log("LEVEL", "No Interactables layer found");
            return null;
        }

        for (MapObject object : interactablesLayer.getObjects()) {
            if (!object.getProperties().containsKey("tileCol") ||
                !object.getProperties().containsKey("tileRow")) {
                Gdx.app.log("LEVEL", "Missing tileCol/tileRow on object: " + object.getName());
                continue;
            }

            int tileCol = getIntProperty(object, "tileCol");
            int tileRow = getIntProperty(object, "tileRow");

            Vector2 worldPosition = tileToWorld(tileCol, tileRow);

            Rectangle triggerBounds = new Rectangle(
                worldPosition.x - 48f,
                worldPosition.y - 32f,
                96f,
                64f
            );

            String targetMap = getStringProperty(object, "targetMap", "");
            String targetSpawn = getStringProperty(object, "targetSpawn", "player");

            Gdx.app.log(
                "LEVEL",
                "Checking " + object.getName()
                    + " tileCol=" + tileCol
                    + " tileRow=" + tileRow
                    + " targetMap=" + targetMap
                    + " trigger=" + triggerBounds
                    + " player=" + playerBounds
            );

            if (playerBounds.overlaps(triggerBounds)) {
                if (targetMap.length() > 0) {
                    Gdx.app.log("LEVEL", "Transition to " + targetMap);
                    return new TransitionResult(targetMap, targetSpawn);
                }

                Gdx.app.log("LEVEL", "targetMap is empty on object: " + object.getName());
            }
        }

        return null;
    }

    private Vector2 tileToWorld(int tileCol, int tileRow) {
        float worldX = (tileCol + tileRow) * tileWidth / 2f;
        float worldY = (tileRow - tileCol) * tileHeight / 2f;

        return new Vector2(worldX, worldY);
    }

    private int getIntProperty(MapObject object, String propertyName) {
        Object value = object.getProperties().get(propertyName);

        if (value == null) {
            throw new IllegalArgumentException("Missing property: " + propertyName);
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

    private String getStringProperty(MapObject object, String propertyName, String fallback) {
        Object value = object.getProperties().get(propertyName);

        if (value == null) {
            return fallback;
        }

        return value.toString();
    }

    public static class TransitionResult {
        private final String targetMap;
        private final String targetSpawn;

        public TransitionResult(String targetMap, String targetSpawn) {
            this.targetMap = targetMap;
            this.targetSpawn = targetSpawn;
        }

        public String getTargetMap() {
            return targetMap;
        }

        public String getTargetSpawn() {
            return targetSpawn;
        }
    }
}
