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

    public void update(float deltaTime, CollisionManager collisionManager) {
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, collisionManager);
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
