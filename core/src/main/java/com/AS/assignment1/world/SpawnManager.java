package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

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

            if (object.getProperties().containsKey("tileCol")
                && object.getProperties().containsKey("tileRow")) {

                int tileCol = getIntProperty(object, "tileCol");
                int tileRow = getIntProperty(object, "tileRow");

                Vector2 worldPosition = tileToWorld(tileCol, tileRow);

                Gdx.app.log(
                    "SPAWN",
                    objectName + " tileCol=" + tileCol
                        + ", tileRow=" + tileRow
                        + " -> worldX=" + worldPosition.x
                        + ", worldY=" + worldPosition.y
                );

                return worldPosition;
            }

            if (object instanceof PointMapObject) {
                PointMapObject pointObject = (PointMapObject) object;

                return new Vector2(
                    pointObject.getPoint().x,
                    pointObject.getPoint().y
                );
            }

            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                return new Vector2(
                    rect.x + rect.width / 2f,
                    rect.y
                );
            }
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
}
