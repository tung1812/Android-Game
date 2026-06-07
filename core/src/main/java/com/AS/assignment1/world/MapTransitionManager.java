package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MapTransitionManager {

    //Tiled map used to read transition objects from the Interactables layer
    private final TiledMap map;

    //Tile size from the Tiled map properties
    private final int tileWidth;
    private final int tileHeight;

    public MapTransitionManager(TiledMap map) {
        //Store the map reference
        this.map = map;

        //Read tile width and height from the map properties
        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    public TransitionResult checkTransition(Rectangle playerBounds) {
        //Stop checking if the map or Interactables layer does not exist
        if (map == null || map.getLayers().get("Interactables") == null) {
            return null;
        }

        //Get the Interactables layer from the map
        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        //Check every object in the Interactables layer
        for (MapObject object : interactablesLayer.getObjects()) {

            //Skip objects without tile position properties
            if (!object.getProperties().containsKey("tileCol") ||
                !object.getProperties().containsKey("tileRow")) {
                continue;
            }

            //Read the tile position of the transition object
            int tileCol = getIntProperty(object, "tileCol");
            int tileRow = getIntProperty(object, "tileRow");

            //Convert tile position into world position
            Vector2 worldPosition = tileToWorld(tileCol, tileRow);

            //Create a small trigger area around the transition object
            Rectangle triggerBounds = new Rectangle(
                worldPosition.x - 24f,
                worldPosition.y - 12f,
                48f,
                32f
            );

            //Check whether the player overlaps the transition trigger area
            if (playerBounds.overlaps(triggerBounds)) {

                //Read transition settings from the object properties
                String targetMap = getStringProperty(object, "targetMap", "");
                String targetSpawn = getStringProperty(object, "targetSpawn", "player");
                boolean requiresKey = getBooleanProperty(object, "requiresKey", false);

                //Return transition data if a target map is provided
                if (targetMap.length() > 0) {
                    Gdx.app.log("LEVEL", "Transition to " + targetMap);
                    return new TransitionResult(targetMap, targetSpawn, requiresKey);
                }
            }
        }

        //Return null if no transition trigger was touched
        return null;
    }

    private boolean getBooleanProperty(MapObject object, String propertyName, boolean fallback) {
        //Get the property value from the object
        Object value = object.getProperties().get(propertyName);

        //Return fallback if the property is missing
        if (value == null) {
            return fallback;
        }

        //Return the value directly if it is already a Boolean
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        //Convert the value to a boolean
        return Boolean.parseBoolean(value.toString());
    }

    private Vector2 tileToWorld(int tileCol, int tileRow) {
        //Convert isometric tile coordinates into world coordinates
        float worldX = (tileCol + tileRow) * tileWidth / 2f;
        float worldY = (tileRow - tileCol) * tileHeight / 2f;

        return new Vector2(worldX, worldY);
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

    public static class TransitionResult {

        //Target map file that the player should move to
        private final String targetMap;

        //Spawn point name used after entering the target map
        private final String targetSpawn;

        //Whether this transition requires the player to have a key
        private final boolean requiresKey;

        public TransitionResult(String targetMap, String targetSpawn, boolean requiresKey) {
            // Store transition data
            this.targetMap = targetMap;
            this.targetSpawn = targetSpawn;
            this.requiresKey = requiresKey;
        }

        public String getTargetMap() {
            //Return the target map path
            return targetMap;
        }

        public String getTargetSpawn() {
            //Return the target spawn point name
            return targetSpawn;
        }

        public boolean requiresKey() {
            //Return whether this transition requires a key
            return requiresKey;
        }
    }
}
