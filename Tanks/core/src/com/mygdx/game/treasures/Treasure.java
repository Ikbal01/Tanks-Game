package com.mygdx.game.treasures;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.sprites.Bullet;
import com.mygdx.game.sprites.GameObject;
import com.mygdx.game.sprites.Tank;
import com.mygdx.game.world.World;

public abstract class Treasure extends GameObject {
    public static final float LIFE_TIME = 12f;

    public enum TreasureType {BASE_DEFENDER, ENEMY_KILLER,
        EXTRA_LIFE, SHIELD, TANK_IMPROVER, TIME_STOPPER, WALL_BREAKER}

    public enum State {ACTIVE, EXPIRED, USED}

    private Texture texture;
    private State currentState;
    private SpriteBatch spriteBatch;
    private long startTime;
    private TreasureType type;
    protected Sound sound;

    public Treasure(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, World.PIXELS_32, World.PIXELS_32);
        this.spriteBatch = spriteBatch;
        startTime = System.currentTimeMillis();
        currentState = State.ACTIVE;
    }

    public void update() {
        if (LIFE_TIME < ((System.currentTimeMillis() - startTime) / 1000.0)) {
            currentState = State.EXPIRED;
        }
    }

    public void draw() {
        spriteBatch.draw(texture, getPosition().x, getPosition().y);
    }

    public State getState() {
        return currentState;
    }

    protected void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void respondTankCollision(Tank tank) {
        currentState = State.USED;
        sound.play();
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        // do nothing
    }

    public TreasureType getType() {
        return type;
    }

    protected void setType(TreasureType type) {
        this.type = type;
    }
}
