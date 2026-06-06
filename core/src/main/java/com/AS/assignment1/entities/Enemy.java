package com.AS.assignment1.entities;

import com.AS.assignment1.world.CollisionManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private Texture spriteSheet;
    private Animation<TextureRegion> walkAnimation;

    private float x;
    private float y;
    private float width;
    private float height;
    private float animationTime;

    private float startX;
    private float speed;
    private float patrolDistance;
    private int direction;

    public Enemy(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;

        width = 64f;
        height = 64f;

        animationTime = 0f;

        speed = 60f;
        patrolDistance = 120f;
        direction = 1; // 1 = right, -1 = left

        loadAnimation();
    }

    private void loadAnimation() {
        spriteSheet = new Texture("Enemies/MiniHalberdMan.png");

        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 32, 32);

        Array<TextureRegion> walkFrames = new Array<>();

        // Row 0 has empty frames at the end, which causes flashing.
        // Row 1 has 6 visible frames, so it is safer for now.
        int row = 1;

        for (int col = 0; col < 6; col++) {
            walkFrames.add(frames[row][col]);
        }

        walkAnimation = new Animation<>(0.15f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float deltaTime, CollisionManager collisionManager) {
        animationTime += deltaTime;

        float dx = direction * speed * deltaTime;

        if (canMove(dx, 0, collisionManager)) {
            x += dx;
        } else {
            direction *= -1;
            return;
        }

        if (x > startX + patrolDistance) {
            x = startX + patrolDistance;
            direction = -1;
        } else if (x < startX - patrolDistance) {
            x = startX - patrolDistance;
            direction = 1;
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
        TextureRegion currentFrame = walkAnimation.getKeyFrame(animationTime, true);

        // Make a copy so flipping does not permanently affect the animation frame.
        TextureRegion frameToDraw = new TextureRegion(currentFrame);

        if (direction < 0) {
            frameToDraw.flip(true, false);
        }

        batch.draw(
            frameToDraw,
            x - width / 2f,
            y - height * 0.15f,
            width,
            height
        );
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
