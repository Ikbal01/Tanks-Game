package com.mygdx.game.treasures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Shield extends Treasure {

    public Shield(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);

        setTexture(new Texture(Gdx.files.internal("treasures\\shieldd.png")));
        setType(TreasureType.SHIELD);

        sound = Gdx.audio.newSound(Gdx.files.internal("sound\\shield.wav"));
    }
}
