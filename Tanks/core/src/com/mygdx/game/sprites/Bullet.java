package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.enums.Direction;

public class Bullet extends DynamicGameObject {
    private static final int BULLET_WIDTH = 8;
    private static final int BULLET_HEIGHT = 8;

    private static final float EXPLOSION_DURATION = 7f;

    private SpriteBatch spriteBatch;

    private Texture texture;
    private TextureRegion currentFrame;

    private Animation<TextureRegion> explosionAnimation;

    private Direction direction;
    private Rectangle bigBounds;

    public enum State {FLYING, EXPLODING, DESTROYED};
    private State currentState;

    private float deltaTime;

    private float elapsed;

    public Bullet(float x, float y, Direction direction, SpriteBatch spriteBatch) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
        this.direction = direction;
        this.spriteBatch = spriteBatch;

        velocity = 4.5f;

        currentState = State.FLYING;

        setTexture();
        currentFrame = new TextureRegion(texture, 8, 8);

        elapsed = 0;
    }

    public void update() {
        switch (currentState) {
            case FLYING:
                move();
                break;

            case EXPLODING:
                explode();
                elapsed++;
                break;
        }

        if (elapsed > EXPLOSION_DURATION) {
            currentState = State.DESTROYED;
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
    public void draw(float deltaTime) {
        this.deltaTime = deltaTime;
        switch (currentState) {
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
            setExplosionAnimation();
            currentState = State.EXPLODING;
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

    @Override
    public void respondBrickCollision() {
        for (int i = 0; i < 3; i++) {
            move();
        }

        setBigBounds();
        explode();
    }

    @Override
    public void respondSteelCollision() {
        respondBrickCollision();
    }

    @Override
    public void respondMapBoundsCollision() {
        respondBrickCollision();
    }

    @Override
    public void respondTankCollision() {
        respondBrickCollision();
    }

    @Override
    public void respondBulletCollision() {
        currentState = State.DESTROYED;
    }

    @Override
    public void respondFortressCollison() {
        respondBrickCollision();
    }

    public Rectangle getBigBounds() {
        setBigBounds();
        return bigBounds;
    }

    public State getState() {
        return currentState;
    }
}
