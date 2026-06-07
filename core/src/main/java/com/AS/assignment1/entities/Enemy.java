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
    private Texture spriteSheet;
    private enum EnemyState {
        WALK,
        DYING,
        DEAD
    }
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private EnemyState enemyState;

    private float stateTime;
    private float hitFlashTimer;

    private float x;
    private float y;
    private float width;
    private float height;
    private float animationTime;

    private float startX;
    private float speed;
    private float patrolDistance;
    private int direction;
    private int health;
    private boolean dead;
    private float hitCooldown;
    private float hitTimer;
    private float patrolDirX;
    private float patrolDirY;
    private float startY;



    public Enemy(float startX, float startY, String patrolPattern) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;

        width = 64f;
        height = 64f;

        health = 2;
        dead = false;
        hitCooldown = 0.8f;
        hitTimer = 0f;

        animationTime = 0f;
        stateTime = 0f;
        hitFlashTimer = 0f;
        enemyState = EnemyState.WALK;

        speed = 60f;
        patrolDistance = 120f;
        direction = 1; // 1 = right, -1 = left
        setPatrolPattern(patrolPattern);

        loadAnimation();
    }

    public Enemy(float startX, float startY) {
        this(startX, startY, "isoDownRight");
    }
    private void loadAnimation() {
        spriteSheet = new Texture("Enemies/MiniHalberdMan.png");

        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 32, 32);

        Array<TextureRegion> walkFrames = new Array<>();
        Array<TextureRegion> deathFrames = new Array<>();

        // Your current walking row.
        int walkRow = 1;

        for (int col = 0; col < 6; col++) {
            walkFrames.add(frames[walkRow][col]);
        }

        // The bottom row appears to contain the falling / dying frames.
        int deathRow = frames.length - 1;

        for (int col = 0; col < frames[deathRow].length; col++) {
            deathFrames.add(frames[deathRow][col]);
        }

        walkAnimation = new Animation<>(0.15f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        deathAnimation = new Animation<>(0.12f, deathFrames);
        deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void update(float deltaTime, CollisionManager collisionManager) {
        if (enemyState == EnemyState.DEAD) {
            return;
        }

        stateTime += deltaTime;

        if (hitTimer > 0) {
            hitTimer -= deltaTime;
        }

        if (hitFlashTimer > 0) {
            hitFlashTimer -= deltaTime;
        }

        if (enemyState == EnemyState.DYING) {
            if (deathAnimation.isAnimationFinished(stateTime)) {
                enemyState = EnemyState.DEAD;
                dead = true;
            }

            return;
        }

        animationTime += deltaTime;



        float dx = direction * patrolDirX * speed * deltaTime;
        float dy = direction * patrolDirY * speed * deltaTime;

        if (canMove(dx, dy, collisionManager)) {
            x += dx;
            y += dy;
        } else {
            direction *= -1;
            return;
        }

        float distanceFromStart = Vector2.dst(startX, startY, x, y);

        if (distanceFromStart > patrolDistance) {
            direction *= -1;
        }
    }

    private boolean canMove(float dx, float dy, CollisionManager collisionManager) {
        float targetX = x + dx;
        float targetY = y + dy;

        if (collisionManager == null) {
            return true;
        }

        return !collisionManager.isBlockedAtCharacter(targetX, targetY, 6f);
    }

    public void draw(SpriteBatch batch) {
        if (enemyState == EnemyState.DEAD) {
            return;
        }

        TextureRegion currentFrame;

        if (enemyState == EnemyState.DYING) {
            currentFrame = deathAnimation.getKeyFrame(stateTime, false);
        } else {
            currentFrame = walkAnimation.getKeyFrame(animationTime, true);
        }

        TextureRegion frameToDraw = new TextureRegion(currentFrame);

        if (direction < 0) {
            frameToDraw.flip(true, false);
        }

        if (hitFlashTimer > 0 && enemyState != EnemyState.DYING) {
            batch.setColor(1f, 0.35f, 0.35f, 1f);
        }

        batch.draw(
            frameToDraw,
            x - width / 2f,
            y - height * 0.15f,
            width,
            height
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void setPatrolPattern(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            pattern = "isoDownRight";
        }

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

        if ("horizontal".equalsIgnoreCase(pattern)) {
            patrolDirX = 1f;
            patrolDirY = 0f;
        } else if ("isoUpRight".equalsIgnoreCase(pattern)) {
            patrolDirX = 1f;
            patrolDirY = -0.5f;
        } else if ("vertical".equalsIgnoreCase(pattern)) {
            patrolDirX = 0f;
            patrolDirY = 1f;
        } else {
            // Default: isoDownRight
            patrolDirX = 1f;
            patrolDirY = 0.5f;
        }

        normalizePatrolDirection();
    }

    private void normalizePatrolDirection() {
        float length = (float) Math.sqrt(patrolDirX * patrolDirX + patrolDirY * patrolDirY);

        if (length == 0) {
            patrolDirX = 1f;
            patrolDirY = 0f;
            return;
        }

        patrolDirX /= length;
        patrolDirY /= length;
    }

    public Rectangle getBounds() {
        return new Rectangle(
            x - 22f,
            y + 4f,
            44f,
            40f
        );
    }

    public void takeDamage(int amount) {
        if (enemyState == EnemyState.DEAD || enemyState == EnemyState.DYING || hitTimer > 0) {
            return;
        }

        health -= amount;
        hitTimer = hitCooldown;
        hitFlashTimer = 0.15f;

        if (health <= 0) {
            enemyState = EnemyState.DYING;
            stateTime = 0f;
        }
    }

    public boolean isDead() {
        return enemyState == EnemyState.DEAD || enemyState == EnemyState.DYING;
    }

    public boolean canBeRemoved() {
        return enemyState == EnemyState.DEAD;
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
