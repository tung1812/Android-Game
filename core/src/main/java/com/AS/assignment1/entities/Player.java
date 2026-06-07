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

    public enum Direction {
        DOWN,
        LEFT,
        RIGHT,
        UP
    }

    private Texture idleDownSheet;
    private Texture walkDownSheet;
    private Texture punchDownSheet;

    private Texture idleLeftSheet;
    private Texture walkLeftSheet;
    private Texture punchLeftSheet;

    private Texture idleUpSheet;
    private Texture walkUpSheet;
    private Texture punchUpSheet;

    private Animation<TextureRegion> idleDownAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> punchDownAnimation;

    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> punchLeftAnimation;

    private Animation<TextureRegion> idleUpAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> punchUpAnimation;

    private PlayerState playerState;
    private Direction direction;

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

        width = 65f;
        height = 65f;
        speed = 160f;

        animationTime = 0f;
        attacking = false;
        playerState = PlayerState.IDLE;
        direction = Direction.DOWN;

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
        idleDownSheet = new Texture("ReikoAnimations/bowling/idle.png");
        walkDownSheet = new Texture("ReikoAnimations/bowling/walk.png");
        punchDownSheet = new Texture("ReikoAnimations/bowling/punch.png");

        idleLeftSheet = new Texture("ReikoAnimations/bowling/idleleft.png");
        walkLeftSheet = new Texture("ReikoAnimations/bowling/leftwalk.png");
        punchLeftSheet = new Texture("ReikoAnimations/bowling/leftpunch.png");

        idleUpSheet = new Texture("ReikoAnimations/bowling/backidle.png");
        walkUpSheet = new Texture("ReikoAnimations/bowling/backwalk.png");
        punchUpSheet = new Texture("ReikoAnimations/bowling/backpunch.png");

        idleDownAnimation = createHorizontalAnimation(idleDownSheet, 0.18f);
        walkDownAnimation = createHorizontalAnimation(walkDownSheet, 0.12f);
        punchDownAnimation = createHorizontalAnimation(punchDownSheet, 0.10f);

        idleLeftAnimation = createHorizontalAnimation(idleLeftSheet, 0.18f);
        walkLeftAnimation = createHorizontalAnimation(walkLeftSheet, 0.12f);
        punchLeftAnimation = createHorizontalAnimation(punchLeftSheet, 0.10f);

        idleUpAnimation = createHorizontalAnimation(idleUpSheet, 0.18f);
        walkUpAnimation = createHorizontalAnimation(walkUpSheet, 0.12f);
        punchUpAnimation = createHorizontalAnimation(punchUpSheet, 0.10f);

        idleDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        punchDownAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        idleLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        punchLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        idleUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        punchUpAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createHorizontalAnimation(Texture sheet, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();

        int frameHeight = sheet.getHeight();
        int frameCount = Math.max(1, sheet.getWidth() / frameHeight);
        int frameWidth = sheet.getWidth() / frameCount;

        for (int i = 0; i < frameCount; i++) {
            frames.add(new TextureRegion(
                sheet,
                i * frameWidth,
                0,
                frameWidth,
                frameHeight
            ));
        }

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
                    direction = Direction.LEFT;
                    lastDirectionX = -1f;
                    lastDirectionY = 0f;
                    moving = tryMove(-speed * deltaTime, 0, collisionManager);
                }

                if (rightButton.contains(touchX, touchY)) {
                    direction = Direction.RIGHT;
                    lastDirectionX = 1f;
                    lastDirectionY = 0f;
                    moving = tryMove(speed * deltaTime, 0, collisionManager);
                }

                if (upButton.contains(touchX, touchY)) {
                    direction = Direction.UP;
                    lastDirectionX = 0f;
                    lastDirectionY = 1f;
                    moving = tryMove(0, speed * deltaTime, collisionManager);
                }

                if (downButton.contains(touchX, touchY)) {
                    direction = Direction.DOWN;
                    lastDirectionX = 0f;
                    lastDirectionY = -1f;
                    moving = tryMove(0, -speed * deltaTime, collisionManager);
                }
            }
        }

        if (attacking) {
            playerState = PlayerState.ATTACK;

            if (getAttackAnimation().isAnimationFinished(animationTime)) {
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
        TextureRegion frameToDraw = new TextureRegion(frame);

        if (direction == Direction.RIGHT) {
            frameToDraw.flip(true, false);
        }

        if (damageTimer > 0f) {
            int flashStep = (int) (damageTimer * 12f);

            if (flashStep % 2 == 0) {
                batch.setColor(1f, 0.25f, 0.25f, 1f);
            } else {
                batch.setColor(1f, 1f, 1f, 1f);
            }
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

    private TextureRegion getCurrentFrame() {
        if (playerState == PlayerState.ATTACK) {
            return getAttackAnimation().getKeyFrame(animationTime, false);
        }

        if (playerState == PlayerState.RUN) {
            return getRunAnimation().getKeyFrame(animationTime, true);
        }

        return getIdleAnimation().getKeyFrame(animationTime, true);
    }

    private Animation<TextureRegion> getIdleAnimation() {
        if (direction == Direction.UP) {
            return idleUpAnimation;
        }

        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            return idleLeftAnimation;
        }

        return idleDownAnimation;
    }

    private Animation<TextureRegion> getRunAnimation() {
        if (direction == Direction.UP) {
            return walkUpAnimation;
        }

        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            return walkLeftAnimation;
        }

        return walkDownAnimation;
    }

    private Animation<TextureRegion> getAttackAnimation() {
        if (direction == Direction.UP) {
            return punchUpAnimation;
        }

        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            return punchLeftAnimation;
        }

        return punchDownAnimation;
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

    public void setPosition(float newX, float newY) {
        x = newX;
        y = newY;
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

    public boolean takeDamage(int amount) {
        if (damageTimer > 0) {
            return false;
        }

        health -= amount;

        if (health < 0) {
            health = 0;
        }

        damageTimer = damageCooldown;

        return true;
    }

    public Rectangle getAttackBounds() {
        float bodyHalfWidth = 18f;
        float attackLength = 52f;
        float attackWidth = 42f;

        float attackCenterY = y + 12f;

        if (lastDirectionX > 0) {
            return new Rectangle(
                x + bodyHalfWidth,
                attackCenterY - attackWidth / 2f,
                attackLength,
                attackWidth
            );
        }

        if (lastDirectionX < 0) {
            return new Rectangle(
                x - bodyHalfWidth - attackLength,
                attackCenterY - attackWidth / 2f,
                attackLength,
                attackWidth
            );
        }

        if (lastDirectionY > 0) {
            return new Rectangle(
                x - attackWidth / 2f,
                attackCenterY + bodyHalfWidth,
                attackWidth,
                attackLength
            );
        }

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
        idleDownSheet.dispose();
        walkDownSheet.dispose();
        punchDownSheet.dispose();

        idleLeftSheet.dispose();
        walkLeftSheet.dispose();
        punchLeftSheet.dispose();

        idleUpSheet.dispose();
        walkUpSheet.dispose();
        punchUpSheet.dispose();
    }
}
