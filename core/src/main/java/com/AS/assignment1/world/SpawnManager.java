package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SpawnManager {
    private final TiledMap map;
    private final int tileWidth;
    private final int tileHeight;

    public SpawnManager(TiledMap map) {
        this.map = map;

        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    public Vector2 getSpawnPoint(String objectName, float fallbackX, float fallbackY) {
        if (map == null || map.getLayers().get("Spawns") == null) {
            Gdx.app.log("SPAWN", "No Spawns layer found. Using fallback for " + objectName);
            return new Vector2(fallbackX, fallbackY);
        }

        MapLayer spawnLayer = map.getLayers().get("Spawns");

        for (MapObject object : spawnLayer.getObjects()) {
            if (!objectName.equals(object.getName())) {
                continue;
            }

            Gdx.app.log("SPAWN", "Found spawn object: " + objectName);

            if (!object.getProperties().containsKey("tileCol")
                || !object.getProperties().containsKey("tileRow")) {

                Gdx.app.log(
                    "SPAWN",
                    objectName + " missing tileCol/tileRow. Using fallback."
                );

                return new Vector2(fallbackX, fallbackY);
            }

            int tileCol = getIntProperty(object, "tileCol");
            int tileRow = getIntProperty(object, "tileRow");

            float offsetX = getFloatProperty(object, "offsetX", 0f);
            float offsetY = getFloatProperty(object, "offsetY", 0f);

            Vector2 worldPosition = tileToWorld(tileCol, tileRow);

            float finalX = worldPosition.x + offsetX;
            float finalY = worldPosition.y + offsetY;

            Gdx.app.log(
                "SPAWN",
                objectName
                    + " tileCol=" + tileCol
                    + ", tileRow=" + tileRow
                    + " -> worldX=" + finalX
                    + ", worldY=" + finalY
                    + ", offsetX=" + offsetX
                    + ", offsetY=" + offsetY
            );

            return new Vector2(finalX, finalY);
        }

        Gdx.app.log("SPAWN", "Spawn object not found: " + objectName + ". Using fallback.");
        return new Vector2(fallbackX, fallbackY);
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

        if (value instanceof String) {
            return Math.round(Float.parseFloat((String) value));
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

        if (value instanceof String) {
            return Float.parseFloat((String) value);
        }

        return Float.parseFloat(value.toString());
    }

    public Array<Vector2> getSpawnPointsByPrefix(String prefix) {
        Array<Vector2> spawnPoints = new Array<>();

        if (map == null || map.getLayers().get("Spawns") == null) {
            Gdx.app.log("SPAWN", "No Spawns layer found for prefix: " + prefix);
            return spawnPoints;
        }

        MapLayer spawnLayer = map.getLayers().get("Spawns");

        for (MapObject object : spawnLayer.getObjects()) {
            if (object.getName() == null || !object.getName().startsWith(prefix)) {
                continue;
            }

            if (!object.getProperties().containsKey("tileCol")
                || !object.getProperties().containsKey("tileRow")) {

                Gdx.app.log("SPAWN", "Missing tileCol/tileRow on object: " + object.getName());
                continue;
            }

            int tileCol = getIntProperty(object, "tileCol");
            int tileRow = getIntProperty(object, "tileRow");

            float offsetX = getFloatProperty(object, "offsetX", 0f);
            float offsetY = getFloatProperty(object, "offsetY", 0f);

            Vector2 worldPosition = tileToWorld(tileCol, tileRow);

            float finalX = worldPosition.x + offsetX;
            float finalY = worldPosition.y + offsetY;

            Gdx.app.log(
                "SPAWN",
                object.getName()
                    + " tileCol=" + tileCol
                    + ", tileRow=" + tileRow
                    + " -> x=" + finalX
                    + ", y=" + finalY
            );

            spawnPoints.add(new Vector2(finalX, finalY));
        }

        return spawnPoints;
    }
}
