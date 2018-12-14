package com.mygdx.game.treasures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.sprites.Bullet;
import com.mygdx.game.sprites.Tank;

public class TankImprover extends Treasure {

    public TankImprover(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);

        setTexture(new Texture(Gdx.files.internal("treasures\\improve.png")));
        setType(TreasureType.TANK_IMPROVER);
    }
}
