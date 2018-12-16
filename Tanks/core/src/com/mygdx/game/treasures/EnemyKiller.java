package com.mygdx.game.treasures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EnemyKiller extends Treasure {

    public EnemyKiller(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);

        setTexture(new Texture(Gdx.files.internal("treasures\\killEnemy.png")));
        setType(TreasureType.ENEMY_KILLER);

        sound = Gdx.audio.newSound(Gdx.files.internal("sound\\treasure.wav"));
    }
}
