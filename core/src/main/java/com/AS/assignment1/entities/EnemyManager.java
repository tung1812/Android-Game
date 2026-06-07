package com.AS.assignment1.entities;

import com.AS.assignment1.world.CollisionManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    private Array<Enemy> enemies;

    public EnemyManager() {
        enemies = new Array<>();
    }

    public void addEnemy(float x, float y) {
        enemies.add(new Enemy(x, y, "isoDownRight"));
    }

    public void addEnemy(float x, float y, String patrolPattern) {
        enemies.add(new Enemy(x, y, patrolPattern));
    }

    public boolean update(float deltaTime, CollisionManager collisionManager, Player player) {
        boolean playerDamaged = false;

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            enemy.update(deltaTime, collisionManager);

            if (player != null && !enemy.isDead()) {
                handlePlayerAttack(enemy, player);

                if (!enemy.isDead()) {
                    boolean damaged = handleEnemyContact(enemy, player);

                    if (damaged) {
                        playerDamaged = true;
                    }
                }
            }

            if (enemy.canBeRemoved()) {
                enemy.dispose();
                enemies.removeIndex(i);
            }
        }

        return playerDamaged;
    }

    private void handlePlayerAttack(Enemy enemy, Player player) {
        if (player.canDealAttackDamage()
            && player.getAttackBounds().overlaps(enemy.getBounds())) {

            enemy.takeDamage(1);
            player.registerAttackHit();
        }
    }

    private boolean handleEnemyContact(Enemy enemy, Player player) {
        if (enemy.getBounds().overlaps(player.getBounds())) {
            return player.takeDamage(1);
        }

        return false;
    }

    public void draw(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }

        enemies.clear();
    }
}
