package com.AS.assignment1.entities;

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

    public void update(float deltaTime) {
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
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
