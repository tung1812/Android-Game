package com.AS.assignment1.entities;

import com.AS.assignment1.world.CollisionManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {

    //List used to store all enemies in the current level
    private Array<Enemy> enemies;

    public EnemyManager() {
        //Create an empty enemy list
        enemies = new Array<>();
    }

    public void addEnemy(float x, float y) {
        //Add an enemy with the default patrol pattern
        enemies.add(new Enemy(x, y, "isoDownRight"));
    }

    public void addEnemy(float x, float y, String patrolPattern) {
        //Add an enemy with a custom patrol pattern
        enemies.add(new Enemy(x, y, patrolPattern));
    }

    public boolean update(float deltaTime, CollisionManager collisionManager, Player player) {
        //Tracks whether the player was damaged during this update
        boolean playerDamaged = false;

        //Loop backwards so enemies can be safely removed from the list
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            //Update enemy movement, animation, collision, and state
            enemy.update(deltaTime, collisionManager);

            //Only check player interaction if the player exists and the enemy is not dead
            if (player != null && !enemy.isDead()) {

                //Check if the player's attack hits this enemy
                handlePlayerAttack(enemy, player);

                //If the enemy is still alive, check if it touches the player
                if (!enemy.isDead()) {
                    boolean damaged = handleEnemyContact(enemy, player);

                    //Store that the player was damaged this frame
                    if (damaged) {
                        playerDamaged = true;
                    }
                }
            }

            //Remove the enemy after its death animation has finished
            if (enemy.canBeRemoved()) {
                enemy.dispose();
                enemies.removeIndex(i);
            }
        }

        //Return whether the player took damage this frame
        return playerDamaged;
    }

    private void handlePlayerAttack(Enemy enemy, Player player) {
        //Damage the enemy if the player is attacking and the attack hitbox overlaps the enemy
        if (player.canDealAttackDamage()
            && player.getAttackBounds().overlaps(enemy.getBounds())) {

            enemy.takeDamage(1);

            //Register the attack hit so one attack only damages once
            player.registerAttackHit();
        }
    }

    private boolean handleEnemyContact(Enemy enemy, Player player) {
        //Damage the player if the enemy touches the player
        if (enemy.getBounds().overlaps(player.getBounds())) {
            return player.takeDamage(1);
        }

        //No damage was applied
        return false;
    }

    public void draw(SpriteBatch batch) {
        //Draw every enemy in the level
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    public void drawHitboxDebug(ShapeRenderer shapeRenderer) {
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                continue;
            }

            Rectangle bounds = enemy.getBounds();

            shapeRenderer.rect(
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height
            );
        }
    }

    public boolean areAllEnemiesDefeated() {
        return enemies.size == 0;
    }

    public void dispose() {
        //Dispose every enemy texture/resource
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }

        //Clear the enemy list
        enemies.clear();
    }
}
