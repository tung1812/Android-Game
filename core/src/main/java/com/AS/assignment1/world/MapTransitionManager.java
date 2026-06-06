package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;

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
            return null;
        }

        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        if (interactablesLayer == null) {
            return null;
        }

        int playerTileCol = getTileColFromWorld(playerBounds);
        int playerTileRow = getTileRowFromWorld(playerBounds);

        for (MapObject object : interactablesLayer.getObjects()) {
            if (object == null) {
                continue;
            }

            if (!object.getProperties().containsKey("tileCol")
                || !object.getProperties().containsKey("tileRow")) {

                Gdx.app.log(
                    "LEVEL",
                    "Missing tileCol/tileRow on object: " + object.getName()
                );
                continue;
            }

            int targetTileCol = getIntProperty(object, "tileCol");
            int targetTileRow = getIntProperty(object, "tileRow");

            String targetMap = getStringProperty(object, "targetMap", "");
            String targetSpawn = getStringProperty(object, "targetSpawn", "player");

            Gdx.app.log(
                "LEVEL",
                "Checking "
                    + object.getName()
                    + " targetTile=(" + targetTileCol + "," + targetTileRow + ")"
                    + " playerTile=(" + playerTileCol + "," + playerTileRow + ")"
                    + " targetMap=" + targetMap
            );

            int tileRange = 1;

            boolean isNearTarget =
                Math.abs(playerTileCol - targetTileCol) <= tileRange
                    && Math.abs(playerTileRow - targetTileRow) <= tileRange;

            if (isNearTarget) {
                if (targetMap.length() > 0) {
                    Gdx.app.log("LEVEL", "Transition to " + targetMap);
                    return new TransitionResult(targetMap, targetSpawn);
                }

                Gdx.app.log(
                    "LEVEL",
                    "targetMap is empty on object: " + object.getName()
                );
            }
        }

        return null;
    }

    private int getTileColFromWorld(Rectangle playerBounds) {
        float centerX = playerBounds.x + playerBounds.width / 2f;
        float centerY = playerBounds.y + playerBounds.height / 2f;

        return Math.round(centerX / tileWidth - centerY / tileHeight);
    }

    private int getTileRowFromWorld(Rectangle playerBounds) {
        float centerX = playerBounds.x + playerBounds.width / 2f;
        float centerY = playerBounds.y + playerBounds.height / 2f;

        return Math.round(centerX / tileWidth + centerY / tileHeight);
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

        if (value instanceof String) {
            return Math.round(Float.parseFloat((String) value));
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
