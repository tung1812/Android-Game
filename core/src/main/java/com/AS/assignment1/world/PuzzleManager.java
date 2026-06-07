package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;

public class PuzzleManager {

    //Tiled map used to read key objects from the Interactables layer
    private final TiledMap map;

    //Tile size from the Tiled map properties
    private final int tileWidth;
    private final int tileHeight;

    //Stores whether the player has collected a key
    private boolean hasKey;

    //Stores whether this map contains any key object
    private boolean hasKeyObject;

    public PuzzleManager(TiledMap map) {
        //Store the map reference
        this.map = map;

        //Read tile width and height from the map properties
        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);

        //Player starts without a key
        this.hasKey = false;

        //Check whether this map contains at least one key object
        this.hasKeyObject = detectKeyObject();
    }

    public void update(float playerX, float playerY) {
        //Stop updating if the map or Interactables layer does not exist
        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        //Convert the player world position into tile coordinates
        Vector2 playerTile = worldToTile(playerX, playerY);

        //Get the Interactables layer from the map
        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        //Check every object in the Interactables layer
        for (MapObject object : interactablesLayer.getObjects()) {

            //Skip objects that are not keys
            if (!isKeyObject(object)) {
                continue;
            }

            //Skip keys that have already been collected
            if (object.getProperties().containsKey("collected")
                && Boolean.TRUE.equals(object.getProperties().get("collected"))) {
                continue;
            }

            //Read the key tile position
            int keyTileCol = getIntProperty(object, "tileCol");
            int keyTileRow = getIntProperty(object, "tileRow");

            //Calculate distance between the player tile and the key tile
            float distanceCol = Math.abs(playerTile.x - keyTileCol);
            float distanceRow = Math.abs(playerTile.y - keyTileRow);

            //Collect the key if the player is close enough to it
            if (distanceCol <= 0.8f && distanceRow <= 0.8f) {
                hasKey = true;

                //Mark this key object as collected
                object.getProperties().put("collected", true);

                //Log key collection information for debugging
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
        //Return whether the player currently has a key
        return hasKey;
    }

    public boolean hasKeyObject() {
        //Return whether the map contains any key object
        return hasKeyObject;
    }

    private boolean detectKeyObject() {
        //Return false if the map or Interactables layer does not exist
        if (map == null || map.getLayers().get("Interactables") == null) {
            return false;
        }

        //Get the Interactables layer from the map
        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        //Search for at least one key object
        for (MapObject object : interactablesLayer.getObjects()) {
            if (isKeyObject(object)) {
                return true;
            }
        }

        //No key object was found
        return false;
    }

    private boolean isKeyObject(MapObject object) {
        // Return false if the object is null
        if (object == null) {
            return false;
        }

        // Read the object type and name
        String type = getStringProperty(object, "type", "");
        String objectName = object.getName();

        // Treat the object as a key if its type is "key" or its name starts with "key"
        return "key".equalsIgnoreCase(type)
            || (objectName != null && objectName.toLowerCase().startsWith("key"));
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

    private int getIntProperty(MapObject object, String propertyName) {
        //Get the property value from the object
        Object value = object.getProperties().get(propertyName);

        //Throw an error if the required property is missing
        if (value == null) {
            throw new IllegalArgumentException("Missing property: " + propertyName);
        }

        //Return the value directly if it is already an Integer
        if (value instanceof Integer) {
            return (Integer) value;
        }

        //Convert Float value to int
        if (value instanceof Float) {
            return Math.round((Float) value);
        }

        //Convert Double value to int
        if (value instanceof Double) {
            return (int) Math.round((Double) value);
        }

        //Convert any other value type to int
        return Math.round(Float.parseFloat(value.toString()));
    }

    private String getStringProperty(MapObject object, String propertyName, String fallback) {
        //Get the property value from the object
        Object value = object.getProperties().get(propertyName);

        //Return fallback if the property is missing
        if (value == null) {
            return fallback;
        }

        //Convert the value to a string
        return value.toString();
    }
}
