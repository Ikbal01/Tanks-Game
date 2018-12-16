package com.mygdx.game.treasures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ExtraLife extends Treasure {

    public ExtraLife(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);

        setTexture(new Texture(Gdx.files.internal("treasures\\extraLife.png")));
        setType(TreasureType.EXTRA_LIFE);

        sound = Gdx.audio.newSound(Gdx.files.internal("sound\\extraLife.wav"));
    }
}
