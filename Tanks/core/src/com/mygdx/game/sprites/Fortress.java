package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.world.World;

public class Fortress extends GameObject {
    private static final int FORTRESS_DEFENCE_POS_X = 192;
    private static final int FORTRESS_DEFENCE_POS_Y = 16;
    private static final int FORTRESS_DEFENCE_WIDTH = 64;
    private static final int FORTRESS_DEFENCE_HEIGHT = 48;
    private static final int FORTRESS_DEFENCE_TIME = 15;

    public enum State {NORMAL, DEFENCE, DEAD}
    private State state;

    private World world;
    private float x;
    private float y;

    private long timer;
    private Texture deadTexture;

    private Sound destrSound;

    public Fortress(float x, float y, World world) {
        super(x, y, World.PIXELS_32, World.PIXELS_32);
        this.x = x;
        this.y = y;
        this.world = world;

        state = State.NORMAL;
        deadTexture = new Texture(Gdx.files.internal("fortress\\dead.png"));

        destrSound = Gdx.audio.newSound(Gdx.files.internal("sound\\fortressDestr.wav"));
    }

    public void update() {
        if (state == State.DEFENCE
                && FORTRESS_DEFENCE_TIME < (System.currentTimeMillis() - timer) / 1000) {

            setNormalMode();
        }
    }

    public void draw() {
        if (state == State.DEAD) {
            world.getSpriteBatch().draw(deadTexture, x, y, World.PIXELS_32, World.PIXELS_32);
        }
    }

    public void setDefenceMod() {
        state = State.DEFENCE;

        position.set(FORTRESS_DEFENCE_POS_X, FORTRESS_DEFENCE_POS_Y);
        bounds.set(FORTRESS_DEFENCE_POS_X, FORTRESS_DEFENCE_POS_Y
                , FORTRESS_DEFENCE_WIDTH, FORTRESS_DEFENCE_HEIGHT);

        timer = System.currentTimeMillis();
    }

    public void setNormalMode() {
        position.set(x, y);
        bounds.set(x, y, World.PIXELS_32, World.PIXELS_32);
        state = State.NORMAL;
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
            destrSound.play();
            world.setGameOverState();
        }
    }

    public State getState() {
        return state;
    }
}
