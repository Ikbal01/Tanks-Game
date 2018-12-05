package com.mygdx.game.treasures;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.sprites.GameObject;
import com.mygdx.game.world.World;

public abstract class Treasure extends GameObject {
    protected SpriteBatch spriteBatch;
    protected int lifeTime;
    protected int time;

    public Treasure(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, World.PIXELS_32, World.PIXELS_32);
        this.spriteBatch = spriteBatch;
        time = 0;
    }

    public abstract void draw(float deltaTime);
}
