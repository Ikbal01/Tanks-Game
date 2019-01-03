package com.mygdx.game.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

/**
 * The base game object used for creation of every entity in the world.
 */
public abstract class GameObject {
    private static final float MILLISECONDS_DELIMITER = 1000.0f;

    protected Vector2 position;
    protected Rectangle bounds;
    protected boolean isDestroyed;

    public GameObject(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, width, height);

        isDestroyed = false;
    }

    /**
     * Checks whether total time is elapsed from the start of timer.
     *
     * @param totalTime time for checking
     * @param startTime start of timer
     * @return whether time is elapsed
     */
    protected boolean isElapsed(float totalTime, long startTime) {

        float elapsed = (System.currentTimeMillis() - startTime) / MILLISECONDS_DELIMITER;

        return totalTime < elapsed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public abstract void respondTankCollision(Tank tank);

    public abstract void respondBulletCollision(Bullet bullet);

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
