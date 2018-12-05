package com.mygdx.game.treasures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EnemyKiller extends Treasure {
    private Texture texture;

    public EnemyKiller(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);

        texture = new Texture(Gdx.files.internal("treasures\\killEnemy.png"));
    }

    @Override
    public void draw(float deltaTime) {
        spriteBatch.draw(texture, getPosition().x, getPosition().y);
    }
}
