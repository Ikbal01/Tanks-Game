package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.enums.Direction;

public class Bullet extends DynamicGameObject {
    private static final int BULLET_WIDTH = 8;
    private static final int BULLET_HEIGHT = 8;

    private static final float EXPLOSION_DURATION = 0.15f;

    private SpriteBatch spriteBatch;

    private Texture texture;
    private TextureRegion currentFrame;

    private Animation<TextureRegion> explosionAnimation;

    private Tank tank;
    private Direction direction;
    private Rectangle bigBounds;

    public enum State {FLYING, EXPLODING, DESTROYED}
    private State state;

    private float deltaTime;

    private long elapsed;

    private Sound wallCollSound;

    public Bullet(float x, float y, Tank tank, Direction direction, SpriteBatch spriteBatch, float velocity) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT);

        this.direction = direction;
        this.spriteBatch = spriteBatch;
        this.velocity = velocity;
        this.tank = tank;

        state = State.FLYING;

        setTexture();
        currentFrame = new TextureRegion(texture, 8, 8);

        wallCollSound = Gdx.audio.newSound(Gdx.files.internal("sound\\wallColl.wav"));
    }

    @Override
    public void update() {

        switch (state) {
            case FLYING:
                move();
                break;

            case EXPLODING:
                if (EXPLOSION_DURATION < (System.currentTimeMillis() - elapsed) / 1000.0) {
                    state = State.DESTROYED;
                }
                break;
        }
    }

    @Override
    public void draw(float deltaTime) {
        this.deltaTime = deltaTime;
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

    @Override
    public void explode() {
        if (explosionAnimation == null) {
            velocity = 4;
            for (int i = 0; i < 3; i++) {
                move();
            }

            if (tank instanceof Hero) {
                wallCollSound.play();
            }

            setBigBounds();
            setExplosionAnimation();

            state = State.EXPLODING;
            elapsed = System.currentTimeMillis();
        }
    }

    private void move() {
        switch (direction) {
            case RIGHT:
                getPosition().x += velocity;
                getBounds().x += velocity;
                break;

            case LEFT:
                getPosition().x -= velocity;
                getBounds().x -= velocity;
                break;

            case DOWN:
                getPosition().y -= velocity;
                getBounds().y -= velocity;
                break;

            case UP:
                getPosition().y += velocity;
                getBounds().y += velocity;
                break;
        }
    }

    private void setTexture() {
        switch (direction) {
            case UP:
                texture = new Texture(Gdx.files.internal("bullet\\bullet_up.png"));
                break;

            case DOWN:
                texture = new Texture(Gdx.files.internal("bullet\\bullet_down.png"));
                break;

            case LEFT:
                texture = new Texture(Gdx.files.internal("bullet\\bullet_left.png"));
                break;

            case RIGHT:
                texture = new Texture(Gdx.files.internal("bullet\\bullet_right.png"));
                break;
        }
    }

    @Override
    public void setExplosionAnimation() {
        TextureRegion[] explosionFrames = new TextureRegion[3];

        Texture tempTexture = new Texture(Gdx.files.internal("bulletExplosion\\bulletExpl1.png"));
        explosionFrames[0] = new TextureRegion(tempTexture, 32, 32);

        tempTexture = new Texture(Gdx.files.internal("bulletExplosion\\bulletExpl2.png"));
        explosionFrames[1] = new TextureRegion(tempTexture, 32, 32);

        tempTexture = new Texture(Gdx.files.internal("bulletExplosion\\bulletExpl3.png"));
        explosionFrames[2] = new TextureRegion(tempTexture, 32, 32);

        explosionAnimation = new  Animation<TextureRegion>(0.25f, explosionFrames);
    }

    private void setBigBounds() {
        float x = 0;
        float y = 0;
        switch (direction) {
            case RIGHT:
                x = getPosition().x - 24;
                y = getPosition().y - 12;
                break;
            case LEFT:
                x = getPosition().x;
                y = getPosition().y - 12;
                break;
            case DOWN:
                x = getPosition().x - 12;
                y = getPosition().y;
                break;
            case UP:
                x = getPosition().x - 12;
                y = getPosition().y - 24;
                break;
        }
        bigBounds = new Rectangle(x, y, 32, 32);
    }

    @Override
    public void respondWallCollision() {
        if (state == State.FLYING) {

            explode();
        }
    }

    @Override
    public void respondTankCollision(Tank tank) {
        if (tank.state != Tank.State.SPAWNING && tank != this.tank) {
            if (!(tank instanceof Enemy && this.tank instanceof Enemy)) {
                explode();
            }
        }
    }

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
