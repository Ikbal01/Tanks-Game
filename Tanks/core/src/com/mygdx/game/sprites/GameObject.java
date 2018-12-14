package com.mygdx.game.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected Vector2 position;
    protected Rectangle bounds;
    protected boolean isDestroyed;

    public GameObject(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, width, height);

        isDestroyed = false;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public abstract void respondTankCollision(Tank tank);

    public abstract void respondBulletCollision(Bullet bullet);

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
