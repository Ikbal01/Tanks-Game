package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.world.World;

/**
 * The fortress holds an eagle that must be protected by the players.
 */
public class Fortress extends GameObject {
    private static final int FORTRESS_WIDTH = 32;
    private static final int FORTRESS_HEIGHT = 32;

    // Defence state position coordinates
    private static final int FORTRESS_DEFENCE_POS_X = 192;
    private static final int FORTRESS_DEFENCE_POS_Y = 16;

    // Defence state sizes
    private static final int FORTRESS_DEFENCE_WIDTH = 64;
    private static final int FORTRESS_DEFENCE_HEIGHT = 48;

    private static final int FORTRESS_DEFENCE_TIME = 15;

    public enum State {NORMAL, DEFENCE, DEAD}
    private State state;

    private World world;

    // Normal state position coordinates
    private float x;
    private float y;

    private long timer;

    private Texture deadTexture;
    private Sound explosionSound;

    public Fortress(float x, float y, World world) {
        super(x, y, FORTRESS_WIDTH, FORTRESS_HEIGHT);
        this.world = world;
        this.x = x;
        this.y = y;

        state = State.NORMAL;

        deadTexture = new Texture(Gdx.files.internal("fortress\\dead.png"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sound\\fortressDestr.wav"));
    }

    /**
     * Checks whether defence mod time is elapsed.
     * If is elapsed sets normal mod.
     */
    public void update() {
        if (state == State.DEFENCE && isElapsed(FORTRESS_DEFENCE_TIME, timer)) {

            setNormalMode();
        }
    }

    public void draw() {
        if (state == State.DEAD) {
            world.getSpriteBatch().draw(deadTexture, x, y, FORTRESS_WIDTH, FORTRESS_HEIGHT);
        }
    }

    /**
     * Becomes invulnerable with broader boundaries.
     */
    public void setDefenceMode() {
        state = State.DEFENCE;

        position.set(FORTRESS_DEFENCE_POS_X, FORTRESS_DEFENCE_POS_Y);
        bounds.set(FORTRESS_DEFENCE_POS_X, FORTRESS_DEFENCE_POS_Y
                , FORTRESS_DEFENCE_WIDTH, FORTRESS_DEFENCE_HEIGHT);

        timer = System.currentTimeMillis();
    }

    /**
     * Becomes vulnerable with normal boundaries
     */
    public void setNormalMode() {
        state = State.NORMAL;

        position.set(x, y);
        bounds.set(x, y, FORTRESS_WIDTH, FORTRESS_HEIGHT);
    }

    @Override
    public void respondTankCollision(Tank tank) {
        // do nothing
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        if (state != State.DEFENCE && bullet.getState() == Bullet.State.FLYING
                && bullet.getBounds().overlaps(this.bounds)) {

            state = State.DEAD;
            explosionSound.play();
            world.setGameOverState();
        }
    }

    public State getState() {
        return state;
    }
}
