package com.AS.assignment1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SpawnManager {

    //Tiled map used to read spawn objects from the map
    private final TiledMap map;

    //Tile size from the Tiled map properties
    private final int tileWidth;
    private final int tileHeight;

    public SpawnManager(TiledMap map) {
        //Store the map reference
        this.map = map;

        //Read tile width and height from the map properties
        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    //Small data class used to store enemy spawn position and patrol pattern
    public static class EnemySpawn {

        //Enemy world position
        public final Vector2 position;

        //Enemy movement pattern
        public final String patrolPattern;

        public EnemySpawn(Vector2 position, String patrolPattern) {
            this.position = position;
            this.patrolPattern = patrolPattern;
        }
    }

    public Vector2 getSpawnPoint(String objectName, float fallbackX, float fallbackY) {
        //Use fallback position if the map or Spawns layer does not exist
        if (map == null || map.getLayers().get("Spawns") == null) {
            Gdx.app.log("SPAWN", "No Spawns layer found. Using fallback for " + objectName);
            return new Vector2(fallbackX, fallbackY);
        }

        //Get the object layer named Spawns
        MapLayer spawnLayer = map.getLayers().get("Spawns");

        //Search through every object in the Spawns layer
        for (MapObject object : spawnLayer.getObjects()) {

            //Skip objects that do not match the requested name
            if (!objectName.equals(object.getName())) {
                continue;
            }

            Gdx.app.log("SPAWN", "Found spawn object: " + objectName);

            //Check that the object has tile column and tile row properties
            if (!object.getProperties().containsKey("tileCol")
                || !object.getProperties().containsKey("tileRow")) {

                Gdx.app.log(
                    "SPAWN",
                    objectName + " missing tileCol/tileRow. Using fallback."
                );

                return new Vector2(fallbackX, fallbackY);
            }

            //Read the tile position from the object properties
            int tileCol = getIntProperty(object, "tileCol");
            int tileRow = getIntProperty(object, "tileRow");

            //Read optional position offsets
            float offsetX = getFloatProperty(object, "offsetX", 0f);
            float offsetY = getFloatProperty(object, "offsetY", 0f);

            //Convert the tile position into world position
            Vector2 worldPosition = tileToWorld(tileCol, tileRow);

            //Apply offset values to the final spawn position
            float finalX = worldPosition.x + offsetX;
            float finalY = worldPosition.y + offsetY;

            //Log the calculated spawn position for debugging
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

            //Return the final world position
            return new Vector2(finalX, finalY);
        }

        //Use fallback position if the object name was not found
        Gdx.app.log("SPAWN", "Spawn object not found: " + objectName + ". Using fallback.");
        return new Vector2(fallbackX, fallbackY);
    }

    private Vector2 tileToWorld(int tileCol, int tileRow) {
        //Convert isometric tile coordinates into world coordinates
        float worldX = (tileCol + tileRow) * tileWidth / 2f;
        float worldY = (tileRow - tileCol) * tileHeight / 2f;

        return new Vector2(worldX, worldY);
    }

    private int getIntProperty(MapObject object, String propertyName) {
        //Get the property value from the map object
        Object value = object.getProperties().get(propertyName);

        //Throw an error if the property is missing
        if (value == null) {
            throw new IllegalArgumentException("Missing property: " + propertyName);
        }

        //Return the value if it is already an Integer
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

        //Convert String value to int
        if (value instanceof String) {
            return Math.round(Float.parseFloat((String) value));
        }

        //Convert any other value type to int
        return Math.round(Float.parseFloat(value.toString()));
    }

    private float getFloatProperty(MapObject object, String propertyName, float fallback) {
        //Get the property value from the map object
        Object value = object.getProperties().get(propertyName);

        //Return fallback value if the property is missing
        if (value == null) {
            return fallback;
        }

        //Convert Integer value to float
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }

        //Return the value if it is already a Float
        if (value instanceof Float) {
            return (Float) value;
        }

        //Convert Double value to float
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }

        //Convert String value to float
        if (value instanceof String) {
            return Float.parseFloat((String) value);
        }

        //Convert any other value type to float
        return Float.parseFloat(value.toString());
    }

    public Array<EnemySpawn> getEnemySpawnsByPrefix(String prefix) {
        //List used to store enemy spawn data
        Array<EnemySpawn> enemySpawns = new Array<>();

        //Return an empty list if the map or Spawns layer does not exist
        if (map == null || map.getLayers().get("Spawns") == null) {
            return enemySpawns;
        }

        //Get the Spawns layer from the map
        MapLayer spawnLayer = map.getLayers().get("Spawns");

        //Search through every object in the Spawns layer
        for (MapObject object : spawnLayer.getObjects()) {
            String objectName = object.getName();

            //Skip objects that do not start with the given prefix
            if (objectName == null || !objectName.startsWith(prefix)) {
                continue;
            }

            //Skip enemy spawn objects that do not have tileCol and tileRow
            if (!object.getProperties().containsKey("tileCol") ||
                !object.getProperties().containsKey("tileRow")) {

                Gdx.app.log(
                    "SPAWN",
                    "Skipping enemy without tileCol/tileRow: " + objectName
                );

                continue;
            }

            //Read enemy tile position
            int tileCol = getIntProperty(object, "tileCol");
            int tileRow = getIntProperty(object, "tileRow");

            //Convert tile position into world position
            Vector2 position = tileToWorld(tileCol, tileRow);

            //Read enemy patrol pattern, or use a default pattern
            String patrolPattern = getStringProperty(
                object,
                "patrolPattern",
                "isoDownRight"
            );

            //Add the enemy spawn data to the list
            enemySpawns.add(new EnemySpawn(position, patrolPattern));

            //Log enemy spawn data for debugging
            Gdx.app.log(
                "SPAWN",
                objectName
                    + " tileCol=" + tileCol
                    + ", tileRow=" + tileRow
                    + ", pattern=" + patrolPattern
            );
        }

        return enemySpawns;
    }

    private String getStringProperty(MapObject object, String propertyName, String fallback) {
        //Get the property value from the map object
        Object value = object.getProperties().get(propertyName);

        //Return fallback value if the property is missing
        if (value == null) {
            return fallback;
        }

        //Convert the value to a string
        return value.toString();
    }

    public Array<Vector2> getSpawnPointsByPrefix(String prefix) {
        //List used to store spawn point positions
        Array<Vector2> spawnPoints = new Array<>();

        //Return an empty list if the map or Spawns layer does not exist
        if (map == null || map.getLayers().get("Spawns") == null) {
            Gdx.app.log("SPAWN", "No Spawns layer found for prefix: " + prefix);
            return spawnPoints;
        }

        //Get the Spawns layer from the map
        MapLayer spawnLayer = map.getLayers().get("Spawns");

        //Search through every object in the Spawns layer
        for (MapObject object : spawnLayer.getObjects()) {

            //Skip objects that do not start with the given prefix
            if (object.getName() == null || !object.getName().startsWith(prefix)) {
                continue;
            }

            //Skip objects without tileCol and tileRow properties
            if (!object.getProperties().containsKey("tileCol")
                || !object.getProperties().containsKey("tileRow")) {

                Gdx.app.log("SPAWN", "Missing tileCol/tileRow on object: " + object.getName());
                continue;
            }

            //Read the tile position
            int tileCol = getIntProperty(object, "tileCol");
            int tileRow = getIntProperty(object, "tileRow");

            //Read optional offsets
            float offsetX = getFloatProperty(object, "offsetX", 0f);
            float offsetY = getFloatProperty(object, "offsetY", 0f);

            //Convert tile position into world position
            Vector2 worldPosition = tileToWorld(tileCol, tileRow);

            //Apply offsets to the final position
            float finalX = worldPosition.x + offsetX;
            float finalY = worldPosition.y + offsetY;

            //Log the final spawn point position for debugging
            Gdx.app.log(
                "SPAWN",
                object.getName()
                    + " tileCol=" + tileCol
                    + ", tileRow=" + tileRow
                    + " -> x=" + finalX
                    + ", y=" + finalY
            );

            //Add the final spawn point to the list
            spawnPoints.add(new Vector2(finalX, finalY));
        }

        return spawnPoints;
    }
}
