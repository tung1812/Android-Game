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
        enemies.add(new Enemy(x, y));
    }

    public void update(float deltaTime, CollisionManager collisionManager, Player player) {
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            enemy.update(deltaTime, collisionManager);

            if (player != null && player.canDealAttackDamage()) {
                if (player.getAttackBounds().overlaps(enemy.getBounds())) {
                    enemy.takeDamage(1);
                    player.registerAttackHit();
                }
            }

            if (enemy.isDead()) {
                enemy.dispose();
                enemies.removeIndex(i);
            }
        }
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
