package com.mygdx.game.sprites;

public class DynamicGameObject extends GameObject {
    public static final float FRAME_DURATION = 0.25f;

    protected float velocity;
    private float shotTime;

    public DynamicGameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity  = 2;
    }

}
