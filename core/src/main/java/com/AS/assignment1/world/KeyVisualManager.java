package com.AS.assignment1.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyVisualManager {

    //Tiled map used to read key objects from the Interactables layer
    private final TiledMap map;

    //Tile size from the Tiled map properties
    private final int tileWidth;
    private final int tileHeight;

    //Sprite sheet used for the key animations
    private Texture keySheet;

    //Animation played while the key is waiting to be collected
    private Animation<TextureRegion> idleAnimation;

    //Animation played after the key has been collected
    private Animation<TextureRegion> disappearAnimation;

    //Timer used for the looping idle animation
    private float idleAnimationTime;

    //Stores the disappear animation timer for each collected key object
    private Map<MapObject, Float> disappearTimers;

    //Stores key objects that have already finished their disappear animation
    private Set<MapObject> finishedDisappearAnimations;

    public KeyVisualManager(TiledMap map) {
        //Store the map reference
        this.map = map;

        //Read tile width and height from the map properties
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        //Start the idle animation timer at 0
        idleAnimationTime = 0f;

        //Create collections used to track disappear animations
        disappearTimers = new HashMap<>();
        finishedDisappearAnimations = new HashSet<>();

        //Load key sprite sheet and create animations
        loadKeyAnimations();
    }

    private void loadKeyAnimations() {
        //Load the key sprite sheet
        keySheet = new Texture("Items/blue_coin_sheet.png");

        //Define the number of columns and rows in the sprite sheet
        int columns = 8;
        int rows = 2;

        //Calculate the size of each animation frame
        int frameWidth = keySheet.getWidth() / columns;
        int frameHeight = keySheet.getHeight() / rows;

        //Split the sprite sheet into separate texture regions
        TextureRegion[][] regions = TextureRegion.split(
            keySheet,
            frameWidth,
            frameHeight
        );

        //Lists used to store animation frames
        Array<TextureRegion> idleFrames = new Array<>();
        Array<TextureRegion> disappearFrames = new Array<>();

        //First row is used for the idle animation
        for (int col = 0; col < columns; col++) {
            idleFrames.add(regions[0][col]);
        }

        //Second row is used for the disappear animation
        for (int col = 0; col < columns; col++) {
            disappearFrames.add(regions[1][col]);
        }

        //Create the looping idle animation
        idleAnimation = new Animation<>(0.12f, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        //Create the disappear animation that plays once
        disappearAnimation = new Animation<>(0.08f, disappearFrames);
        disappearAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void update(float deltaTime) {
        //Update the idle animation timer
        idleAnimationTime += deltaTime;

        //Stop updating if the map or Interactables layer does not exist
        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        //Get the Interactables layer from the map
        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        //Check every object in the Interactables layer
        for (MapObject object : interactablesLayer.getObjects()) {

            //Skip objects that are not keys
            if (!isKeyObject(object)) {
                continue;
            }

            //Skip keys that have not been collected yet
            if (!isCollected(object)) {
                continue;
            }

            //Skip keys whose disappear animation has already finished
            if (finishedDisappearAnimations.contains(object)) {
                continue;
            }

            //Get the current disappear timer for this key
            float currentTimer = disappearTimers.containsKey(object)
                ? disappearTimers.get(object)
                : 0f;

            //Increase the disappear animation timer
            currentTimer += deltaTime;

            //Save the updated timer for this key
            disappearTimers.put(object, currentTimer);

            //Mark the key as finished if its disappear animation is complete
            if (disappearAnimation.isAnimationFinished(currentTimer)) {
                finishedDisappearAnimations.add(object);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        //Stop drawing if the map or Interactables layer does not exist
        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        //Get the Interactables layer from the map
        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        //Draw every key object in the Interactables layer
        for (MapObject object : interactablesLayer.getObjects()) {

            //Skip objects that are not keys
            if (!isKeyObject(object)) {
                continue;
            }

            //Do not draw the key after its disappear animation has finished
            if (finishedDisappearAnimations.contains(object)) {
                continue;
            }

            //Frame that will be drawn for this key
            TextureRegion frame;

            //Use disappear animation if the key has been collected
            if (isCollected(object)) {
                float disappearTime = disappearTimers.containsKey(object)
                    ? disappearTimers.get(object)
                    : 0f;

                frame = disappearAnimation.getKeyFrame(disappearTime, false);

                //Otherwise, use the looping idle animation
            } else {
                frame = idleAnimation.getKeyFrame(idleAnimationTime, true);
            }

            //Read the visual tile column, or use tileCol as fallback
            int visualTileCol = getIntProperty(
                object,
                "visualTileCol",
                getIntProperty(object, "tileCol", 0)
            );

            //Read the visual tile row, or use tileRow as fallback
            int visualTileRow = getIntProperty(
                object,
                "visualTileRow",
                getIntProperty(object, "tileRow", 0)
            );

            //Convert the tile position into world position
            Vector2 worldPosition = tileToWorld(visualTileCol, visualTileRow);

            //Read key scale from the object properties, or use 1.5 as default
            float scale = getFloatProperty(object, "keyScale", 1.5f);

            //Calculate the final draw size
            float drawWidth = frame.getRegionWidth() * scale;
            float drawHeight = frame.getRegionHeight() * scale;

            //Draw the key centered on its world position
            batch.draw(
                frame,
                worldPosition.x - drawWidth / 2f,
                worldPosition.y + 8f,
                drawWidth,
                drawHeight
            );
        }
    }

    private boolean isKeyObject(MapObject object) {
        //Read the object type and name
        String type = getStringProperty(object, "type", "");
        String objectName = object.getName();

        //Treat the object as a key if its type is "key" or its name starts with "key"
        return "key".equalsIgnoreCase(type)
            || (objectName != null && objectName.toLowerCase().startsWith("key"));
    }

    private boolean isCollected(MapObject object) {
        //Read the collected property from the object
        Object value = object.getProperties().get("collected");

        //If the property does not exist, the key is not collected
        if (value == null) {
            return false;
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

    private int getIntProperty(MapObject object, String propertyName, int fallback) {
        //Get the property value from the object
        Object value = object.getProperties().get(propertyName);

        //Return fallback if the property is missing
        if (value == null) {
            return fallback;
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

    private float getFloatProperty(MapObject object, String propertyName, float fallback) {
        //Get the property value from the object
        Object value = object.getProperties().get(propertyName);

        //Return fallback if the property is missing
        if (value == null) {
            return fallback;
        }

        //Convert Integer value to float
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }

        //Return the value directly if it is already a Float
        if (value instanceof Float) {
            return (Float) value;
        }

        //Convert Double value to float
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }

        //Convert any other value type to float
        return Float.parseFloat(value.toString());
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

    public void dispose() {
        //Dispose the key sprite sheet to free memory
        keySheet.dispose();
    }
}
