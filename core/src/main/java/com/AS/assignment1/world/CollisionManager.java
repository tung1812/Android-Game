package com.AS.assignment1.world;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class CollisionManager {

    //Tile layer used to check blocked tiles
    private final TiledMapTileLayer collisionLayer;

    //Tile size from the Tiled map
    private final int tileWidth;
    private final int tileHeight;

    //Map size measured in number of tiles
    private final int mapWidth;
    private final int mapHeight;

    public CollisionManager(TiledMap map) {
        //Get the collision layer from the Tiled map
        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Collision");

        //Read tile size from the map properties
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        //Read map size from the map properties
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
    }

    public boolean isBlockedAtCharacter(float worldX, float worldY, float radius) {
        //Check the center and nearby points around the character
        //This makes collision more accurate than checking only one point
        return isBlockedAtWorld(worldX, worldY)
            || isBlockedAtWorld(worldX - radius, worldY)
            || isBlockedAtWorld(worldX + radius, worldY)
            || isBlockedAtWorld(worldX, worldY + radius * 0.5f)
            || isBlockedAtWorld(worldX, worldY - radius * 0.5f);
    }

    public boolean isBlockedAtWorld(float worldX, float worldY) {
        //If there is no collision layer, nothing is blocked
        if (collisionLayer == null) {
            return false;
        }

        //Convert world position into tile coordinates
        Vector2 tilePosition = worldToTile(worldX, worldY);

        //Convert tile position to integer tile index
        int tileCol = (int) Math.floor(tilePosition.x);
        int tileRow = (int) Math.floor(tilePosition.y);

        //Treat positions outside the map as blocked
        if (tileCol < 0 || tileCol >= mapWidth || tileRow < 0 || tileRow >= mapHeight) {
            return true;
        }

        //If the collision layer has a tile at this position, it is blocked
        return collisionLayer.getCell(tileCol, tileRow) != null;
    }

    //Debug method used to check which tile a world position belongs to
    public Vector2 getTileAtWorld(float worldX, float worldY) {
        return worldToTile(worldX, worldY);
    }

    private Vector2 worldToTile(float worldX, float worldY) {
        //Calculate half tile size for isometric coordinate conversion
        float halfTileWidth = tileWidth / 2f;
        float halfTileHeight = tileHeight / 2f;

        //Convert isometric world coordinates into tile column and tile row
        float tileCol = (worldX / halfTileWidth - worldY / halfTileHeight) / 2f;
        float tileRow = (worldX / halfTileWidth + worldY / halfTileHeight) / 2f;

        //Return the calculated tile position
        return new Vector2(tileCol, tileRow);
    }
}
