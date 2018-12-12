package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class DynamicGameObject extends GameObject {
    public static final float FRAME_DURATION = 0.25f;

    protected float velocity;
    protected Animation<TextureRegion> explosionAnimation;

    public DynamicGameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity  = 1.0f;
    }

    public abstract void update();

    public abstract void draw(float deltaTime);

    public abstract void respondBrickCollision();

    public abstract void respondSteelCollision();

    public abstract void respondMapBoundsCollision();

    public abstract void respondTankCollision(Tank tank);

    public abstract void respondBulletCollision(Bullet bullet);

    public abstract void respondFortressCollision();

    protected abstract void explode();

    protected abstract void setExplosionAnimation();
}
