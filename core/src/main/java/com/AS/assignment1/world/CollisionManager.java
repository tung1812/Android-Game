package com.AS.assignment1.world;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class CollisionManager {
    private final TiledMapTileLayer collisionLayer;

    private final int tileWidth;
    private final int tileHeight;
    private final int mapWidth;
    private final int mapHeight;

    public CollisionManager(TiledMap map) {
        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Collision");

        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
    }

    public boolean isBlockedAtCharacter(float worldX, float worldY, float radius) {
        return isBlockedAtWorld(worldX, worldY)
            || isBlockedAtWorld(worldX - radius, worldY)
            || isBlockedAtWorld(worldX + radius, worldY)
            || isBlockedAtWorld(worldX, worldY + radius * 0.5f)
            || isBlockedAtWorld(worldX, worldY - radius * 0.5f);
    }

    public boolean isBlockedAtWorld(float worldX, float worldY) {
        if (collisionLayer == null) {
            return false;
        }

        Vector2 tilePosition = worldToTile(worldX, worldY);

        int tileCol = (int) Math.floor(tilePosition.x);
        int tileRow = (int) Math.floor(tilePosition.y);

        if (tileCol < 0 || tileCol >= mapWidth || tileRow < 0 || tileRow >= mapHeight) {
            return true;
        }

        return collisionLayer.getCell(tileCol, tileRow) != null;
    }

    private Vector2 worldToTile(float worldX, float worldY) {
        float halfTileWidth = tileWidth / 2f;
        float halfTileHeight = tileHeight / 2f;

        float tileCol = (worldX / halfTileWidth - worldY / halfTileHeight) / 2f;
        float tileRow = (worldX / halfTileWidth + worldY / halfTileHeight) / 2f;

        return new Vector2(tileCol, tileRow);
    }
}
