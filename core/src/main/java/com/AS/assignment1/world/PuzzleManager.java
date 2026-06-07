package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;

public class PuzzleManager {
    private final TiledMap map;
    private final int tileWidth;
    private final int tileHeight;

    private boolean hasKey;

    public PuzzleManager(TiledMap map) {
        this.map = map;
        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);
        this.hasKey = false;
    }

    public void update(float playerX, float playerY) {
        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        Vector2 playerTile = worldToTile(playerX, playerY);

        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        for (MapObject object : interactablesLayer.getObjects()) {
            String type = getStringProperty(object, "type", "");
            String objectName = object.getName();

            boolean isKey =
                "key".equalsIgnoreCase(type) ||
                    (objectName != null && objectName.toLowerCase().startsWith("key"));

            if (!isKey) {
                continue;
            }

            if (object.getProperties().containsKey("collected")
                && Boolean.TRUE.equals(object.getProperties().get("collected"))) {
                continue;
            }

            int keyTileCol = getIntProperty(object, "tileCol");
            int keyTileRow = getIntProperty(object, "tileRow");

            float distanceCol = Math.abs(playerTile.x - keyTileCol);
            float distanceRow = Math.abs(playerTile.y - keyTileRow);

            if (distanceCol <= 0.8f && distanceRow <= 0.8f) {
                hasKey = true;
                object.getProperties().put("collected", true);

                Gdx.app.log(
                    "PUZZLE",
                    "Key collected: " + object.getName()
                        + " at tileCol=" + keyTileCol
                        + ", tileRow=" + keyTileRow
                        + " | playerTileCol=" + playerTile.x
                        + ", playerTileRow=" + playerTile.y
                );
            }
        }
    }

    public boolean hasKey() {
        return hasKey;
    }

    private Vector2 worldToTile(float worldX, float worldY) {
        float halfTileWidth = tileWidth / 2f;
        float halfTileHeight = tileHeight / 2f;

        float tileCol = (worldX / halfTileWidth - worldY / halfTileHeight) / 2f;
        float tileRow = (worldX / halfTileWidth + worldY / halfTileHeight) / 2f;

        return new Vector2(tileCol, tileRow);
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
}
