package com.AS.assignment1.entities;

import com.AS.assignment1.world.CollisionManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {

    public enum PlayerState {
        IDLE,
        RUN,
        ATTACK
    }

    private Texture idleSheet;
    private Texture runSheet;
    private Texture attackSheet;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> attackAnimation;

    private PlayerState playerState;

    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;

    private float animationTime;
    private boolean attacking;

    private float lastDirectionX;
    private float lastDirectionY;

    private boolean attackHitRegistered;

    private int maxHealth;
    private int health;

    private float damageCooldown;
    private float damageTimer;

    public Player(float startX, float startY) {
        x = startX;
        y = startY;

        width = 96f;
        height = 96f;
        speed = 160f;

        animationTime = 0f;
        attacking = false;
        playerState = PlayerState.IDLE;
        lastDirectionX = 0f;
        lastDirectionY = -1f;
        attackHitRegistered = false;

        maxHealth = 5;
        health = maxHealth;

        damageCooldown = 1.0f;
        damageTimer = 0f;

        loadAnimations();
    }

    private void loadAnimations() {
        idleSheet = new Texture("ReikoAnimations/ReikoIdle.png");
        runSheet = new Texture("ReikoAnimations/run.png");
        attackSheet = new Texture("ReikoAnimations/attack.png");

        idleAnimation = createOneFrameAnimation(idleSheet, 0.18f);
        runAnimation = createGridAnimation(runSheet, 5, 2, 6, 0.12f);
        attackAnimation = createGridAnimation(attackSheet, 5, 2, 7, 0.10f);

        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        runAnimation.setPlayMode(Animation.PlayMode.LOOP);
        attackAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createOneFrameAnimation(Texture sheet, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(sheet));

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        return animation;
    }

    private Animation<TextureRegion> createGridAnimation(
        Texture sheet,
        int columns,
        int rows,
        int frameCount,
        float frameDuration
    ) {
        Array<TextureRegion> frames = new Array<>();

        int frameWidth = sheet.getWidth() / columns;
        int frameHeight = sheet.getHeight() / rows;

        int addedFrames = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (addedFrames < frameCount) {
                    frames.add(new TextureRegion(
                        sheet,
                        col * frameWidth,
                        row * frameHeight,
                        frameWidth,
                        frameHeight
                    ));

                    addedFrames++;
                }
            }
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        return animation;
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
        animationTime += deltaTime;

        if (damageTimer > 0) {
            damageTimer -= deltaTime;
        }

        boolean moving = false;

        if (touching) {
            if (attackButton.contains(touchX, touchY) && !attacking) {
                attacking = true;
                attackHitRegistered = false;
                animationTime = 0f;
                playerState = PlayerState.ATTACK;
            }

            if (!attacking) {
                if (leftButton.contains(touchX, touchY)) {
                    lastDirectionX = -1f;
                    lastDirectionY = 0f;
                    moving = tryMove(-speed * deltaTime, 0, collisionManager);
                }

                if (rightButton.contains(touchX, touchY)) {
                    lastDirectionX = 1f;
                    lastDirectionY = 0f;
                    moving = tryMove(speed * deltaTime, 0, collisionManager);
                }

                if (upButton.contains(touchX, touchY)) {
                    lastDirectionX = 0f;
                    lastDirectionY = 1f;
                    moving = tryMove(0, speed * deltaTime, collisionManager);
                }

                if (downButton.contains(touchX, touchY)) {
                    lastDirectionX = 0f;
                    lastDirectionY = -1f;
                    moving = tryMove(0, -speed * deltaTime, collisionManager);
                }
            }
        }

        if (attacking) {
            playerState = PlayerState.ATTACK;

            if (attackAnimation.isAnimationFinished(animationTime)) {
                attacking = false;
                animationTime = 0f;
                playerState = PlayerState.IDLE;
            }
        } else if (moving) {
            playerState = PlayerState.RUN;
        } else {
            playerState = PlayerState.IDLE;
        }
    }

    private boolean tryMove(float dx, float dy, CollisionManager collisionManager) {
        float targetX = x + dx;
        float targetY = y + dy;

        if (collisionManager == null ||
            !collisionManager.isBlockedAtCharacter(targetX, targetY, 12f)) {
            x = targetX;
            y = targetY;
            return true;
        }

        return false;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion frame = getCurrentFrame();

        batch.draw(
            frame,
            x - width / 2f,
            y - height * 0.15f,
            width,
            height
        );
    }

    private TextureRegion getCurrentFrame() {
        if (playerState == PlayerState.RUN) {
            return runAnimation.getKeyFrame(animationTime, true);
        }

        if (playerState == PlayerState.ATTACK) {
            return attackAnimation.getKeyFrame(animationTime, false);
        }

        return idleAnimation.getKeyFrame(animationTime, true);
    }

    public void heal(int amount) {
        health += amount;

        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public Rectangle getBounds() {
        return new Rectangle(
            x - 16f,
            y,
            32f,
            32f
        );
    }

    public void takeDamage(int amount) {
        if (damageTimer > 0) {
            return;
        }

        health -= amount;

        if (health < 0) {
            health = 0;
        }

        damageTimer = damageCooldown;
    }

    public Rectangle getAttackBounds() {
        float bodyHalfWidth = 18f;
        float attackLength = 52f;
        float attackWidth = 42f;

        float attackCenterY = y + 12f;

        // Facing right
        if (lastDirectionX > 0) {
            return new Rectangle(
                x + bodyHalfWidth,
                attackCenterY - attackWidth / 2f,
                attackLength,
                attackWidth
            );
        }

        // Facing left
        if (lastDirectionX < 0) {
            return new Rectangle(
                x - bodyHalfWidth - attackLength,
                attackCenterY - attackWidth / 2f,
                attackLength,
                attackWidth
            );
        }

        // Facing up
        if (lastDirectionY > 0) {
            return new Rectangle(
                x - attackWidth / 2f,
                attackCenterY + bodyHalfWidth,
                attackWidth,
                attackLength
            );
        }

        // Facing down
        return new Rectangle(
            x - attackWidth / 2f,
            attackCenterY - bodyHalfWidth - attackLength,
            attackWidth,
            attackLength
        );
    }

    public boolean canDealAttackDamage() {
        return attacking && !attackHitRegistered;
    }

    public void registerAttackHit() {
        attackHitRegistered = true;
    }

    public void dispose() {
        idleSheet.dispose();
        runSheet.dispose();
        attackSheet.dispose();
    }
}
