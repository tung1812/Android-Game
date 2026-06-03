package com.AS.assignment1;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private Texture spriteSheet;
    private Animation<TextureRegion> walkAnimation;

    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;
    private float animationTime;

    private int direction;
    private int health;
    private boolean dead;

    public Enemy(float startX, float startY) {
        x = startX;
        y = startY;

        width = 64f;
        height = 64f;
        speed = 60f;
        animationTime = 0f;

        direction = 1;
        health = 2;
        dead = false;

        loadAnimation();
    }

    private void loadAnimation() {
        spriteSheet = new Texture("Enemies/MiniHalberdMan.png");

        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 32, 32);

        Array<TextureRegion> walkFrames = new Array<>();

        // Use row 1 for a simple walking animation.
        // You can change this row later if the animation looks wrong.
        int row = 1;

        for (int col = 0; col < 6; col++) {
            walkFrames.add(frames[row][col]);
        }

        walkAnimation = new Animation<>(0.12f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float deltaTime) {
        if (dead) {
            return;
        }

        animationTime += deltaTime;
        x += direction * speed * deltaTime;
    }

    public void reverseDirection() {
        direction *= -1;
    }

    public void takeDamage(int amount) {
        if (dead) {
            return;
        }

        health -= amount;

        if (health <= 0) {
            dead = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (dead) {
            return;
        }

        TextureRegion frame = walkAnimation.getKeyFrame(animationTime, true);

        // Flip the sprite depending on movement direction.
        boolean shouldFaceLeft = direction < 0;

        if (frame.isFlipX() != shouldFaceLeft) {
            frame.flip(true, false);
        }

        batch.draw(
            frame,
            x - width / 2f,
            y,
            width,
            height
        );
    }

    public Rectangle getBounds() {
        return new Rectangle(
            x - width / 4f,
            y,
            width / 2f,
            height / 2f
        );
    }

    public boolean isDead() {
        return dead;
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
