package com.AS.assignment1.utils;

import com.badlogic.gdx.math.Vector2;

public class IsoUtils {

    public static Vector2 tileToWorld(int tileCol, int tileRow, int tileWidth, int tileHeight) {
        float worldX = (tileCol + tileRow) * tileWidth / 2f;
        float worldY = (tileRow - tileCol) * tileHeight / 2f;

        return new Vector2(worldX, worldY);
    }

    public static Vector2 worldToTile(float worldX, float worldY, int tileWidth, int tileHeight) {
        float halfTileWidth = tileWidth / 2f;
        float halfTileHeight = tileHeight / 2f;

        float tileCol = (worldX / halfTileWidth - worldY / halfTileHeight) / 2f;
        float tileRow = (worldX / halfTileWidth + worldY / halfTileHeight) / 2f;

        return new Vector2(tileCol, tileRow);
    }
}
