package com.AS.assignment1.entities;

import com.AS.assignment1.world.CollisionManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public class Enemy {

    //Sprite sheet used for enemy animations
    private Texture spriteSheet;

    //Possible states of the enemy
    private enum EnemyState {
        WALK,
        DYING,
        DEAD
    }

    //Enemy walking and death animations
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;

    //Current enemy state
    private EnemyState enemyState;

    //Timer used for state-based animation, especially death animation
    private float stateTime;

    //Timer used to flash the enemy when it takes damage
    private float hitFlashTimer;

    //Enemy position and size
    private float x;
    private float y;
    private float width;
    private float height;

    //Timer used for walking animation
    private float animationTime;

    //Starting position used for patrol distance checking
    private float startX;
    private float startY;

    //Enemy movement settings
    private float speed;
    private float patrolDistance;

    //Patrol direction multiplier: 1 means forward, -1 means backward
    private int direction;

    //Enemy health
    private int health;

    //Stores whether the enemy is dead
    private boolean dead;

    //Prevents the enemy from being hit too quickly
    private float hitCooldown;
    private float hitTimer;

    //Normalized patrol direction
    private float patrolDirX;
    private float patrolDirY;

    public Enemy(float startX, float startY, String patrolPattern) {
        //Set starting position
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;

        //Set enemy size
        width = 64f;
        height = 64f;

        //Set enemy health and hit cooldown
        health = 2;
        dead = false;
        hitCooldown = 0.8f;
        hitTimer = 0f;

        //Set animation timers and initial state
        animationTime = 0f;
        stateTime = 0f;
        hitFlashTimer = 0f;
        enemyState = EnemyState.WALK;

        //Set patrol movement values
        speed = 60f;
        patrolDistance = 120f;

        //1 means moving forward, -1 means moving backward
        direction = 1;

        //Apply the selected patrol pattern
        setPatrolPattern(patrolPattern);

        //Load enemy animations
        loadAnimation();
    }

    public Enemy(float startX, float startY) {
        //Use isoDownRight as the default patrol pattern
        this(startX, startY, "isoDownRight");
    }

    private void loadAnimation() {
        //Load enemy sprite sheet
        spriteSheet = new Texture("Enemies/MiniHalberdMan.png");

        //Split the sprite sheet into 32x32 frames
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 32, 32);

        //Lists used to store walking and death frames
        Array<TextureRegion> walkFrames = new Array<>();
        Array<TextureRegion> deathFrames = new Array<>();

        //Row used for the walking animation
        int walkRow = 1;

        //Add walking frames from the selected row
        for (int col = 0; col < 6; col++) {
            walkFrames.add(frames[walkRow][col]);
        }

        //Use the bottom row for the death animation
        int deathRow = frames.length - 1;

        //Add all frames from the death row
        for (int col = 0; col < frames[deathRow].length; col++) {
            deathFrames.add(frames[deathRow][col]);
        }

        //Create the walking animation and make it loop
        walkAnimation = new Animation<>(0.15f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        //Create the death animation and make it play once
        deathAnimation = new Animation<>(0.12f, deathFrames);
        deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void update(float deltaTime, CollisionManager collisionManager) {
        //Do nothing if the enemy is fully dead
        if (enemyState == EnemyState.DEAD) {
            return;
        }

        //Update the state timer
        stateTime += deltaTime;

        //Reduce hit cooldown timer
        if (hitTimer > 0) {
            hitTimer -= deltaTime;
        }

        //Reduce hit flash timer
        if (hitFlashTimer > 0) {
            hitFlashTimer -= deltaTime;
        }

        //Handle dying animation
        if (enemyState == EnemyState.DYING) {

            //Change to DEAD state after the death animation finishes
            if (deathAnimation.isAnimationFinished(stateTime)) {
                enemyState = EnemyState.DEAD;
                dead = true;
            }

            return;
        }

        //Update walking animation timer
        animationTime += deltaTime;

        //Calculate movement based on patrol direction and speed
        float dx = direction * patrolDirX * speed * deltaTime;
        float dy = direction * patrolDirY * speed * deltaTime;

        //Move the enemy if the target position is not blocked
        if (canMove(dx, dy, collisionManager)) {
            x += dx;
            y += dy;
        } else {
            // Reverse direction if blocked by collision
            direction *= -1;
            return;
        }

        //Check how far the enemy moved from its start position
        float distanceFromStart = Vector2.dst(startX, startY, x, y);

        //Reverse direction if the enemy reaches the patrol limit
        if (distanceFromStart > patrolDistance) {
            direction *= -1;
        }
    }

    private boolean canMove(float dx, float dy, CollisionManager collisionManager) {
        //Calculate target position
        float targetX = x + dx;
        float targetY = y + dy;

        //Allow movement if there is no collision manager
        if (collisionManager == null) {
            return true;
        }

        //Return true only if the target position is not blocked
        return !collisionManager.isBlockedAtCharacter(targetX, targetY, 6f);
    }

    public void draw(SpriteBatch batch) {
        //Do not draw the enemy after it is fully dead
        if (enemyState == EnemyState.DEAD) {
            return;
        }

        TextureRegion currentFrame;

        //Use death animation if the enemy is dying
        if (enemyState == EnemyState.DYING) {
            currentFrame = deathAnimation.getKeyFrame(stateTime, false);
        } else {
            //Otherwise use walking animation
            currentFrame = walkAnimation.getKeyFrame(animationTime, true);
        }

        //Copy the frame so flipping does not affect the original frame
        TextureRegion frameToDraw = new TextureRegion(currentFrame);

        //Flip the sprite when the enemy moves in the opposite direction
        if (direction < 0) {
            frameToDraw.flip(true, false);
        }

        //Flash red when the enemy is hit
        if (hitFlashTimer > 0 && enemyState != EnemyState.DYING) {
            batch.setColor(1f, 0.35f, 0.35f, 1f);
        }

        //Draw the enemy sprite
        batch.draw(
            frameToDraw,
            x - width / 2f,
            y - height * 0.15f,
            width,
            height
        );

        //Reset batch color after drawing
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void setPatrolPattern(String pattern) {
        //Use default pattern if no pattern is provided
        if (pattern == null || pattern.length() == 0) {
            pattern = "isoDownRight";
        }

        //Choose a random patrol pattern if requested
        if ("random".equalsIgnoreCase(pattern)) {
            int randomPattern = MathUtils.random(0, 2);

            if (randomPattern == 0) {
                pattern = "horizontal";
            } else if (randomPattern == 1) {
                pattern = "isoDownRight";
            } else {
                pattern = "isoUpRight";
            }
        }

        //Move left and right
        if ("horizontal".equalsIgnoreCase(pattern)) {
            patrolDirX = 1f;
            patrolDirY = 0f;

            //Move diagonally in an isometric up-right direction
        } else if ("isoUpRight".equalsIgnoreCase(pattern)) {
            patrolDirX = 1f;
            patrolDirY = -0.5f;

            //Move vertically
        } else if ("vertical".equalsIgnoreCase(pattern)) {
            patrolDirX = 0f;
            patrolDirY = 1f;

            //Default movement: isometric down-right direction
        } else {
            patrolDirX = 1f;
            patrolDirY = 0.5f;
        }

        //Normalize the direction so all patterns move at consistent speed
        normalizePatrolDirection();
    }

    private void normalizePatrolDirection() {
        //Calculate direction vector length
        float length = (float) Math.sqrt(patrolDirX * patrolDirX + patrolDirY * patrolDirY);

        //Use horizontal movement if the vector length is invalid
        if (length == 0) {
            patrolDirX = 1f;
            patrolDirY = 0f;
            return;
        }

        //Normalize patrol direction
        patrolDirX /= length;
        patrolDirY /= length;
    }

    public Rectangle getBounds() {
        //Return enemy collision box
        return new Rectangle(
            x - 14f,
            y - 8f,
            26f,
            40f
        );
    }

    public void takeDamage(int amount) {
        //Do not take damage if the enemy is dead, dying, or still in hit cooldown
        if (enemyState == EnemyState.DEAD || enemyState == EnemyState.DYING || hitTimer > 0) {
            return;
        }

        //Reduce enemy health
        health -= amount;

        //Start hit cooldown and flash effect
        hitTimer = hitCooldown;
        hitFlashTimer = 0.15f;

        //Start death animation if health reaches zero
        if (health <= 0) {
            enemyState = EnemyState.DYING;
            stateTime = 0f;
        }
    }

    public boolean isDead() {
        //Return true if the enemy is dying or fully dead
        return enemyState == EnemyState.DEAD || enemyState == EnemyState.DYING;
    }

    public boolean canBeRemoved() {
        //Enemy can be removed only after the death animation finishes
        return enemyState == EnemyState.DEAD;
    }

    public void dispose() {
        //Dispose enemy sprite sheet to free memory
        spriteSheet.dispose();
    }
}
