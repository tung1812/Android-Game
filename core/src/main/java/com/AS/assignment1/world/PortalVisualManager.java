package com.AS.assignment1.world;

import com.AS.assignment1.utils.IsoUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PortalVisualManager {

    //Tiled map used to read portal objects from the Interactables layer
    private final TiledMap map;

    //Tile size from the Tiled map properties
    private final int tileWidth;
    private final int tileHeight;

    //Texture used when the portal is inactive
    private Texture inactivePortalTexture;

    //Sprite sheet used when the portal is active
    private Texture activePortalSheet;

    //Single frame used for the inactive portal
    private TextureRegion inactivePortalFrame;

    //Animation used for the active portal
    private Animation<TextureRegion> activePortalAnimation;

    //Timer used to update the portal animation
    private float animationTime;

    public PortalVisualManager(TiledMap map) {
        //Store the map reference
        this.map = map;

        //Read tile width and height from the map properties
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        //Start the animation timer at 0
        animationTime = 0f;

        //Load portal textures and animations
        loadPortalAssets();
    }

    private void loadPortalAssets() {
        //Load inactive and active portal textures
        inactivePortalTexture = new Texture("Portals/portal_inactive.png");
        activePortalSheet = new Texture("Portals/portal_active_sheet.png");

        //Define how many frames are inside the active portal sprite sheet
        int frameCount = 6;

        //Calculate the width and height of each active portal frame
        int frameWidth = activePortalSheet.getWidth() / frameCount;
        int frameHeight = activePortalSheet.getHeight();

        //Create the inactive portal frame
        inactivePortalFrame = new TextureRegion(
            inactivePortalTexture,
            0,
            0,
            Math.min(frameWidth, inactivePortalTexture.getWidth()),
            Math.min(frameHeight, inactivePortalTexture.getHeight())
        );

        //List used to store active portal animation frames
        Array<TextureRegion> activeFrames = new Array<>();

        //Split the active portal sprite sheet into separate frames
        for (int i = 0; i < frameCount; i++) {
            activeFrames.add(new TextureRegion(
                activePortalSheet,
                i * frameWidth,
                0,
                frameWidth,
                frameHeight
            ));
        }

        //Create the looping active portal animation
        activePortalAnimation = new Animation<>(0.12f, activeFrames);
        activePortalAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float deltaTime) {
        //Increase the animation timer
        animationTime += deltaTime;
    }

    public void draw(SpriteBatch batch, boolean hasKey, boolean enemiesCleared) {
        //Stop drawing if the map or Interactables layer does not exist
        if (map == null || map.getLayers().get("Interactables") == null) {
            return;
        }

        //Get the Interactables layer from the map
        MapLayer interactablesLayer = map.getLayers().get("Interactables");

        //Draw every portal object in the Interactables layer
        for (MapObject object : interactablesLayer.getObjects()) {

            //A portal object must have a targetMap property
            String targetMap = getStringProperty(object, "targetMap", "");

            //Skip objects that are not map transitions
            if (targetMap.length() == 0) {
                continue;
            }

            //Check whether this portal requires a key
            boolean requiresKey = getBooleanProperty(object, "requiresKey", false);

            //The portal is active if it does not need a key, the player has a key, or it is the win portal
            boolean requiresEnemiesCleared = getBooleanProperty(
                object,
                "requiresEnemiesCleared",
                false
            );

            boolean portalIsActive = true;

            if (requiresKey && !hasKey) {
                portalIsActive = false;
            }

            if (requiresEnemiesCleared && !enemiesCleared) {
                portalIsActive = false;
            }

            //Frame that will be drawn for this portal
            TextureRegion frame;

            //Use the active animation when the portal is active
            if (portalIsActive) {
                frame = activePortalAnimation.getKeyFrame(animationTime, true);

                //Use the inactive frame when the portal is locked
            } else {
                frame = inactivePortalFrame;
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

            //Convert isometric tile position into world position
            Vector2 worldPosition = IsoUtils.tileToWorld(
                visualTileCol,
                visualTileRow,
                tileWidth,
                tileHeight
            );

            //Read portal scale from the object properties, or use 1 as default
            float scale = getFloatProperty(object, "portalScale", 1f);

            //Calculate the final draw size
            float drawWidth = frame.getRegionWidth() * scale;
            float drawHeight = frame.getRegionHeight() * scale;

            //Draw the portal centered on its world position
            batch.draw(
                frame,
                worldPosition.x - drawWidth / 2f,
                worldPosition.y,
                drawWidth,
                drawHeight
            );
        }
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

        //Return the value directly if it is already a Float
        if (value instanceof Float) {
            return (Float) value;
        }

        //Convert Double value to float
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }

        //Convert Integer value to float
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }

        //Convert any other value type to float
        return Float.parseFloat(value.toString());
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

        //Convert any other value type to boolean
        return Boolean.parseBoolean(value.toString());
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
        //Dispose portal textures to free memory
        inactivePortalTexture.dispose();
        activePortalSheet.dispose();
    }
}
