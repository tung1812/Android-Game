package com.AS.assignment1.entities;

import com.AS.assignment1.world.CollisionManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {

    //Possible states of the player
    public enum PlayerState {
        IDLE,
        RUN,
        ATTACK
    }

    //Possible directions the player can face
    public enum Direction {
        DOWN,
        LEFT,
        RIGHT,
        UP
    }

    //Textures for down-facing animations
    private Texture idleDownSheet;
    private Texture walkDownSheet;
    private Texture punchDownSheet;

    //Textures for left-facing animations
    private Texture idleLeftSheet;
    private Texture walkLeftSheet;
    private Texture punchLeftSheet;

    //Textures for up-facing animations
    private Texture idleUpSheet;
    private Texture walkUpSheet;
    private Texture punchUpSheet;

    //Down-facing animations
    private Animation<TextureRegion> idleDownAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> punchDownAnimation;

    //Left-facing animations
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> punchLeftAnimation;

    //Up-facing animations
    private Animation<TextureRegion> idleUpAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> punchUpAnimation;

    //Current player state and facing direction
    private PlayerState playerState;
    private Direction direction;

    //Player position, size, and movement speed
    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;

    //Timer used for animation playback
    private float animationTime;

    //Stores whether the player is currently attacking
    private boolean attacking;

    //Stores the last movement direction for attack hitbox direction
    private float lastDirectionX;
    private float lastDirectionY;

    //Prevents one attack animation from hitting multiple times
    private boolean attackHitRegistered;

    //Player health values
    private int maxHealth;
    private int health;

    //Damage cooldown prevents the player from taking damage too quickly
    private float damageCooldown;
    private float damageTimer;

    public Player(float startX, float startY) {
        //Set the starting position
        x = startX;
        y = startY;

        //Set player size and speed
        width = 65f;
        height = 65f;
        speed = 160f;

        //Set default animation and state values
        animationTime = 0f;
        attacking = false;
        playerState = PlayerState.IDLE;
        direction = Direction.DOWN;

        //Set default facing direction to down
        lastDirectionX = 0f;
        lastDirectionY = -1f;

        //No attack hit has been registered at the start
        attackHitRegistered = false;

        //Set player health
        maxHealth = 5;
        health = maxHealth;

        //Set damage cooldown timer
        damageCooldown = 1.0f;
        damageTimer = 0f;

        //Load all player animations
        loadAnimations();
    }

    private void loadAnimations() {
        //Load down-facing animation sprite sheets
        idleDownSheet = new Texture("ReikoAnimations/bowling/idle.png");
        walkDownSheet = new Texture("ReikoAnimations/bowling/walk.png");
        punchDownSheet = new Texture("ReikoAnimations/bowling/punch.png");

        // Load left-facing animation sprite sheets
        idleLeftSheet = new Texture("ReikoAnimations/bowling/idleleft.png");
        walkLeftSheet = new Texture("ReikoAnimations/bowling/leftwalk.png");
        punchLeftSheet = new Texture("ReikoAnimations/bowling/leftpunch.png");

        //Load up-facing animation sprite sheets
        idleUpSheet = new Texture("ReikoAnimations/bowling/backidle.png");
        walkUpSheet = new Texture("ReikoAnimations/bowling/backwalk.png");
        punchUpSheet = new Texture("ReikoAnimations/bowling/backpunch.png");

        //Create down-facing animations
        idleDownAnimation = createHorizontalAnimation(idleDownSheet, 0.18f);
        walkDownAnimation = createHorizontalAnimation(walkDownSheet, 0.12f);
        punchDownAnimation = createHorizontalAnimation(punchDownSheet, 0.10f);

        //Create left-facing animations
        idleLeftAnimation = createHorizontalAnimation(idleLeftSheet, 0.18f);
        walkLeftAnimation = createHorizontalAnimation(walkLeftSheet, 0.12f);
        punchLeftAnimation = createHorizontalAnimation(punchLeftSheet, 0.10f);

        //Create up-facing animations
        idleUpAnimation = createHorizontalAnimation(idleUpSheet, 0.18f);
        walkUpAnimation = createHorizontalAnimation(walkUpSheet, 0.12f);
        punchUpAnimation = createHorizontalAnimation(punchUpSheet, 0.10f);

        //Idle and walk animations should loop
        idleDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkDownAnimation.setPlayMode(Animation.PlayMode.LOOP);

        //Attack animation should play once
        punchDownAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        //Set play modes for left-facing animations
        idleLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        punchLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        //Set play modes for up-facing animations
        idleUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        punchUpAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createHorizontalAnimation(Texture sheet, float frameDuration) {
        //Create an array to store animation frames
        Array<TextureRegion> frames = new Array<>();

        //The sprite sheet is arranged horizontally
        int frameHeight = sheet.getHeight();

        //Calculate how many frames are in the sheet
        int frameCount = Math.max(1, sheet.getWidth() / frameHeight);

        //Calculate each frame width
        int frameWidth = sheet.getWidth() / frameCount;

        //Split the sheet into individual frames
        for (int i = 0; i < frameCount; i++) {
            frames.add(new TextureRegion(
                sheet,
                i * frameWidth,
                0,
                frameWidth,
                frameHeight
            ));
        }

        //Return the created animation
        return new Animation<>(frameDuration, frames);
    }

    public void update(
        float deltaTime,
        Rectangle leftButton,
        Rectangle rightButton,
        Rectangle upButton,
        Rectangle downButton,
        Rectangle attackButton,
        float touchX,
        float touchY,
        boolean touching,
        CollisionManager collisionManager
    ) {
        //Update animation timer
        animationTime += deltaTime;

        //Reduce damage cooldown timer if it is active
        if (damageTimer > 0) {
            damageTimer -= deltaTime;
        }

        //Tracks whether the player moved this frame
        boolean moving = false;

        //Only process controls when the player is touching the screen
        if (touching) {

            //Start attack if the attack button is touched and the player is not already attacking
            if (attackButton.contains(touchX, touchY) && !attacking) {
                attacking = true;
                attackHitRegistered = false;
                animationTime = 0f;
                playerState = PlayerState.ATTACK;
            }

            //Movement is disabled during attack animation
            if (!attacking) {

                //Move left
                if (leftButton.contains(touchX, touchY)) {
                    direction = Direction.LEFT;
                    lastDirectionX = -1f;
                    lastDirectionY = 0f;
                    moving = tryMove(-speed * deltaTime, 0, collisionManager);
                }

                //Move right
                if (rightButton.contains(touchX, touchY)) {
                    direction = Direction.RIGHT;
                    lastDirectionX = 1f;
                    lastDirectionY = 0f;
                    moving = tryMove(speed * deltaTime, 0, collisionManager);
                }

                //Move up
                if (upButton.contains(touchX, touchY)) {
                    direction = Direction.UP;
                    lastDirectionX = 0f;
                    lastDirectionY = 1f;
                    moving = tryMove(0, speed * deltaTime, collisionManager);
                }

                //Move down
                if (downButton.contains(touchX, touchY)) {
                    direction = Direction.DOWN;
                    lastDirectionX = 0f;
                    lastDirectionY = -1f;
                    moving = tryMove(0, -speed * deltaTime, collisionManager);
                }
            }
        }

        //Keep the player in attack state until the attack animation finishes
        if (attacking) {
            playerState = PlayerState.ATTACK;

            //End attack after the animation finishes
            if (getAttackAnimation().isAnimationFinished(animationTime)) {
                attacking = false;
                animationTime = 0f;
                playerState = PlayerState.IDLE;
            }

            //Use run state if the player moved
        } else if (moving) {
            playerState = PlayerState.RUN;

            //Otherwise, use idle state
        } else {
            playerState = PlayerState.IDLE;
        }
    }

    private boolean tryMove(float dx, float dy, CollisionManager collisionManager) {
        //Calculate the target position
        float targetX = x + dx;
        float targetY = y + dy;

        //Move only if there is no collision at the target position
        if (collisionManager == null ||
            !collisionManager.isBlockedAtCharacter(targetX, targetY, 12f)) {
            x = targetX;
            y = targetY;
            return true;
        }

        //Movement failed because the target position is blocked
        return false;
    }

    public void draw(SpriteBatch batch) {
        //Get the current animation frame
        TextureRegion frame = getCurrentFrame();

        //Copy the frame so flipping does not affect the original texture region
        TextureRegion frameToDraw = new TextureRegion(frame);

        //Flip left-facing sprites to create right-facing sprites
        if (direction == Direction.RIGHT) {
            frameToDraw.flip(true, false);
        }

        //Flash red when the player recently took damage
        if (damageTimer > 0f) {
            int flashStep = (int) (damageTimer * 12f);

            if (flashStep % 2 == 0) {
                batch.setColor(1f, 0.25f, 0.25f, 1f);
            } else {
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }

        //Draw the player sprite
        batch.draw(
            frameToDraw,
            x - width / 2f,
            y - height * 0.15f,
            width,
            height
        );

        //Reset batch color after drawing the player
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private TextureRegion getCurrentFrame() {
        //Return the attack frame if the player is attacking
        if (playerState == PlayerState.ATTACK) {
            return getAttackAnimation().getKeyFrame(animationTime, false);
        }

        //Return the run frame if the player is moving
        if (playerState == PlayerState.RUN) {
            return getRunAnimation().getKeyFrame(animationTime, true);
        }

        //Return the idle frame by default
        return getIdleAnimation().getKeyFrame(animationTime, true);
    }

    private Animation<TextureRegion> getIdleAnimation() {
        //Use up idle animation when facing up
        if (direction == Direction.UP) {
            return idleUpAnimation;
        }

        //Use left idle animation for both left and right
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            return idleLeftAnimation;
        }

        //Use down idle animation by default
        return idleDownAnimation;
    }

    private Animation<TextureRegion> getRunAnimation() {
        //Use up walking animation when facing up
        if (direction == Direction.UP) {
            return walkUpAnimation;
        }

        //Use left walking animation for both left and right
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            return walkLeftAnimation;
        }

        //Use down walking animation by default
        return walkDownAnimation;
    }

    private Animation<TextureRegion> getAttackAnimation() {
        //Use up attack animation when facing up
        if (direction == Direction.UP) {
            return punchUpAnimation;
        }

        //Use left attack animation for both left and right
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            return punchLeftAnimation;
        }

        //Use down attack animation by default
        return punchDownAnimation;
    }

    public void heal(int amount) {
        //Increase player health
        health += amount;

        //Prevent health from going above max health
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public boolean isDead() {
        //Return true if the player has no health left
        return health <= 0;
    }

    public int getHealth() {
        //Return current health
        return health;
    }

    public int getMaxHealth() {
        //Return maximum health
        return maxHealth;
    }

    public float getX() {
        //Return player X position
        return x;
    }

    public float getY() {
        //Return player Y position
        return y;
    }

    public void setPosition(float newX, float newY) {
        //Set player position
        x = newX;
        y = newY;
    }

    public boolean isAttacking() {
        //Return whether the player is currently attacking
        return attacking;
    }

    public Rectangle getBounds() {
        //Return a small collision rectangle around the player body
        return new Rectangle(
            x - 16f,
            y,
            28f,
            32f
        );
    }

    public boolean takeDamage(int amount) {
        //Do not take damage if the player is still in cooldown
        if (damageTimer > 0) {
            return false;
        }

        //Reduce player health
        health -= amount;

        //Prevent health from going below 0
        if (health < 0) {
            health = 0;
        }

        //Start damage cooldown
        damageTimer = damageCooldown;

        //Return true because damage was applied
        return true;
    }

    public Rectangle getAttackBounds() {
        //Size values used to create the attack hitbox
        float bodyHalfWidth = 18f;
        float attackLength = 52f;
        float attackWidth = 42f;

        //Attack hitbox is slightly above the player's base position
        float attackCenterY = y + 12f;

        //Attack hitbox when facing right
        if (lastDirectionX > 0) {
            return new Rectangle(
                x + bodyHalfWidth,
                attackCenterY - attackWidth / 2f,
                attackLength,
                attackWidth
            );
        }

        //Attack hitbox when facing left
        if (lastDirectionX < 0) {
            return new Rectangle(
                x - bodyHalfWidth - attackLength,
                attackCenterY - attackWidth / 2f,
                attackLength,
                attackWidth
            );
        }

        //Attack hitbox when facing up
        if (lastDirectionY > 0) {
            return new Rectangle(
                x - attackWidth / 2f,
                attackCenterY + bodyHalfWidth,
                attackWidth,
                attackLength
            );
        }

        //Attack hitbox when facing down
        return new Rectangle(
            x - attackWidth / 2f,
            attackCenterY - bodyHalfWidth - attackLength,
            attackWidth,
            attackLength
        );
    }

    public boolean canDealAttackDamage() {
        //The player can deal damage only once per attack animation
        return attacking && !attackHitRegistered;
    }

    public void registerAttackHit() {
        //Mark the current attack as already used
        attackHitRegistered = true;
    }

    public void dispose() {
        //Dispose down-facing animation textures
        idleDownSheet.dispose();
        walkDownSheet.dispose();
        punchDownSheet.dispose();

        //Dispose left-facing animation textures
        idleLeftSheet.dispose();
        walkLeftSheet.dispose();
        punchLeftSheet.dispose();

        //Dispose up-facing animation textures
        idleUpSheet.dispose();
        walkUpSheet.dispose();
        punchUpSheet.dispose();
    }
}
