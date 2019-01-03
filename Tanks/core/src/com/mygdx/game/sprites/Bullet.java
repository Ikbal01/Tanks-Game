package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.enums.Direction;

/**
 * Represents an animated tank bullet.
 */
public class Bullet extends DynamicGameObject {

    // Size of bullet in pixels
    public static final int BULLET_WIDTH = 8;
    public static final int BULLET_HEIGHT = 8;

    // Size of explosion animation in pixels
    private static final int EXPLOSION_ANIM_WIDTH = 32;

    // Duration of explosion animation
    private static final float EXPLOSION_TIME = 0.15f;
    
    private static final float EXPLOSION_ANIM_FRAME_DURATION = 0.4f;

    private SpriteBatch spriteBatch;

    // The image of bullet depends on the direction of movement
    private Texture bulletImage;
    // The image of bullet depends on its state
    private TextureRegion currentFrame;

    private Animation<TextureRegion> explosionAnimation;

    private Tank tank;
    private Direction direction;
    // The explosion range of bullet
    private Rectangle bigBounds;

    public enum State {FLYING, EXPLODING, DESTROYED}
    // Current state
    private State state;

    // Counts the elapsed time of explosion
    private long explosionTimer;

    private Sound collisionSound;

    public Bullet(float x, float y, Tank tank, Direction direction, SpriteBatch spriteBatch, float velocity) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT);

        this.direction = direction;
        this.spriteBatch = spriteBatch;
        this.velocity = velocity;
        this.tank = tank;

        state = State.FLYING;

        setBulletImage();
        currentFrame = new TextureRegion(bulletImage, BULLET_WIDTH, BULLET_HEIGHT);

        collisionSound = Gdx.audio.newSound(Gdx.files.internal("sound\\wallColl.wav"));
    }

    /**
     * If state is FLYING the bullet moves in its direction with
     * velocity value in every frame. If state is EXPLODING checks
     * whether explosion animation time is elapsed and if it is true
     * sets state DESTROYED.
     */
    @Override
    public void update() {

        switch (state) {
            case FLYING:
                move();
                break;

            case EXPLODING:
                if (isElapsed(EXPLOSION_TIME, explosionTimer)) {

                    state = State.DESTROYED;
                }
                break;
        }
    }

    /**
     * Draws appropriate animation depending on the state.
     *
     * @param deltaTime the time between the start of the previous and the start of the current call to render.
     */
    @Override
    public void draw(float deltaTime) {

        switch (state) {
            case FLYING:
                spriteBatch.draw(currentFrame, getPosition().x, getPosition().y);
                break;

            case EXPLODING:
                currentFrame = explosionAnimation.getKeyFrame(deltaTime, true);
                spriteBatch.draw(currentFrame, getBigBounds().x, getBigBounds().y);
                break;
        }
    }

    /**
     * Sets explosion animation for a while. If this bullet
     * is shot from Hero, plays collision sound.
     */
    @Override
    public void explode() {
        if (state == State.FLYING) {

            velocity = 12;
            move();

            if (tank instanceof Hero) {
                collisionSound.play();
            }

            setBigBounds();
            explosionAnimation = generateAnimation("bulletExplosion\\bulletExpl", 3,
                    EXPLOSION_ANIM_WIDTH, EXPLOSION_ANIM_FRAME_DURATION);

            state = State.EXPLODING;
            explosionTimer = System.currentTimeMillis();
        }
    }

    /**
     * Changes the position of this bullet in direction
     * of its movement with velocity value.
     */
    private void move() {
        switch (direction) {
            case RIGHT:
                position.x += velocity;
                bounds.x += velocity;
                break;

            case LEFT:
                position.x -= velocity;
                bounds.x -= velocity;
                break;

            case DOWN:
                position.y -= velocity;
                bounds.y -= velocity;
                break;

            case UP:
                position.y += velocity;
                bounds.y += velocity;
                break;
        }
    }

    /**
     * Generates the image of the bullet which depends
     * on the direction of movement.
     */
    private void setBulletImage() {
        switch (direction) {
            case UP:
                bulletImage = new Texture(Gdx.files.internal("bullet\\bullet_up.png"));
                break;

            case DOWN:
                bulletImage = new Texture(Gdx.files.internal("bullet\\bullet_down.png"));
                break;

            case LEFT:
                bulletImage = new Texture(Gdx.files.internal("bullet\\bullet_left.png"));
                break;

            case RIGHT:
                bulletImage = new Texture(Gdx.files.internal("bullet\\bullet_right.png"));
                break;
        }
    }

    /**
     * Sets the bounds of explosion range.
     */
    private void setBigBounds() {
        float x = 0;
        float y = 0;

        switch (direction) {
            case RIGHT:
                x = position.x - 24;
                y = position.y - 12;
                break;
            case LEFT:
                x = position.x;
                y = position.y - 12;
                break;
            case DOWN:
                x = position.x - 12;
                y = position.y;
                break;
            case UP:
                x = position.x - 12;
                y = position.y - 24;
                break;
        }
        bigBounds = new Rectangle(x, y, BULLET_WIDTH * 4, BULLET_HEIGHT * 4);
    }

    /**
     * Explodes, if there is a wall collision and state is FLYING.
     */
    @Override
    public void respondWallCollision() {
        if (state == State.FLYING) {

            explode();
        }
    }

    /**
     * If there is a tank collision and the given tank is not
     * the tank from which bullet is shot and its
     * state is not SPAWNING, explodes. There is no collision
     * between enemies.
     *
     * @param tank the tank which collides with the bullet
     */
    @Override
    public void respondTankCollision(Tank tank) {
        if (tank.state != Tank.State.SPAWNING && tank != this.tank
            && (!(tank instanceof Enemy && this.tank instanceof Enemy))) {

                explode();
        }
    }

    /**
     * The bullet destroys without explosion animation
     *
     * @param bullet the bullet which collides with this bullet
     */
    @Override
    public void respondBulletCollision(Bullet bullet) {
        state = State.DESTROYED;
    }

    public Rectangle getBigBounds() {
        setBigBounds();
        return bigBounds;
    }

    public State getState() {
        return state;
    }

    public Tank getTank() {
        return tank;
    }
}
