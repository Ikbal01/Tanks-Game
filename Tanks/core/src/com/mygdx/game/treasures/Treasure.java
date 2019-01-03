package com.mygdx.game.treasures;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.sprites.Bullet;
import com.mygdx.game.sprites.GameObject;
import com.mygdx.game.sprites.Tank;
import com.mygdx.game.world.World;

public abstract class Treasure extends GameObject {
    private static final float LIFE_TIME = 12f;

    public enum TreasureType {BASE_DEFENDER, ENEMY_KILLER,
        EXTRA_LIFE, SHIELD, TANK_IMPROVER, TIME_STOPPER, WALL_BREAKER}

    public enum State {ACTIVE, EXPIRED, USED}

    private State state;
    private TreasureType type;

    private SpriteBatch spriteBatch;
    private Texture texture;

    protected Sound sound;
    private long startTime;

    public Treasure(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, World.PIXELS_32, World.PIXELS_32);
        this.spriteBatch = spriteBatch;

        state = State.ACTIVE;
        startTime = System.currentTimeMillis();
    }

    /**
     * Checks whether life time of treasure is elapsed.
     * If is elapsed state is becoming EXPIRED (treasure is destroyed)
     */
    public void update() {
        if (isElapsed(LIFE_TIME, startTime)) {

            state = State.EXPIRED;
        }
    }

    public void draw() {
        spriteBatch.draw(texture, position.x, position.y);
    }

    @Override
    public void respondTankCollision(Tank tank) {
        state = State.USED;
        sound.play();
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        // do nothing
    }

    protected void setType(TreasureType type) {
        this.type = type;
    }

    protected void setTexture(Texture texture) {
        this.texture = texture;
    }

    public State getState() {
        return state;
    }

    public TreasureType getType() {
        return type;
    }
}
