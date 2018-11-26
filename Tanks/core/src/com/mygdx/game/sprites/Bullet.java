package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.world.World;

public class Bullet extends DynamicGameObject {
    private static final int BULLET_WIDTH = 8;
    private static final int BULLET_HEIGHT = 8;

    private SpriteBatch spriteBatch;

    private Texture texture;
    private Animation<TextureRegion> explosionAnimation;

    private Direction direction;
    private Rectangle bigBounds;

    private boolean isDestroyed;
    private boolean isExplode;

    private float explosionDuration = 15f;
    private float elapsed = 0f;

    public Bullet(float x, float y, Direction direction, SpriteBatch spriteBatch) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
        this.direction = direction;
        this.spriteBatch = spriteBatch;

        velocity = 4.5f;
        isDestroyed = false;
        isExplode = false;

        setTexture(direction);
        setExplosionAnimation();
        setBigBounds();
    }

    public void draw(float deltaTime) {
        if (!isExplode) {
            spriteBatch.draw(texture, getPosition().x, getPosition().y);
        } else if (elapsed < explosionDuration) {
            TextureRegion currentFrame = explosionAnimation.getKeyFrame(deltaTime, true);
            spriteBatch.draw(currentFrame, bigBounds.x, bigBounds.y);
            elapsed += deltaTime;
        } else {
            isDestroyed = true;
        }
    }

    public void update() {
        move(direction);
    }

    private void move(Direction direction) {
        switch (direction) {
            case RIGHT:
                getPosition().x += velocity;
                getBounds().x += velocity;
                bigBounds.x += velocity;
                break;
            case LEFT:
                getPosition().x -= velocity;
                getBounds().x -= velocity;
                bigBounds.x -= velocity;

                break;
            case DOWN:
                getPosition().y -= velocity;
                getBounds().y -= velocity;
                bigBounds.y -= velocity;

                break;
            case UP:
                getPosition().y += velocity;
                getBounds().y += velocity;
                bigBounds.y += velocity;

                break;
        }
    }

    private void setTexture(Direction direction) {
        switch (direction) {
            case UP:
                texture = new Texture(Gdx.files.internal("bullet_up.png"));
                break;
            case DOWN:
                texture = new Texture(Gdx.files.internal("bullet_down.png"));
                break;
            case LEFT:
                texture = new Texture(Gdx.files.internal("bullet_left.png"));
                break;
            case RIGHT:
                texture = new Texture(Gdx.files.internal("bullet_right.png"));
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

    private void setExplosionAnimation() {
        TextureRegion[] explosionFrames = new TextureRegion[3];
        int x = 16 * World.BIG_TILE_STANDARD;
        int y = 8 * World.BIG_TILE_STANDARD;
        explosionFrames[0] = new TextureRegion(World.items
                , x, y, World.BIG_TILE_STANDARD, World.BIG_TILE_STANDARD);
        explosionFrames[1] = new TextureRegion(World.items
                , x + World.BIG_TILE_STANDARD, y, World.BIG_TILE_STANDARD, World.BIG_TILE_STANDARD);
        explosionFrames[2] = new TextureRegion(World.items
                , x + 2 * World.BIG_TILE_STANDARD, y, World.BIG_TILE_STANDARD, World.BIG_TILE_STANDARD);

        explosionAnimation = new  Animation<TextureRegion>(0.25f, explosionFrames);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public Rectangle getBigBounds() {
        return bigBounds;
    }

    public boolean isExplode() {
        return isExplode;
    }

    @Override
    public void respondBrickCollision() {
        for (int i = 0; i < 3; i++) {
            move(direction);
        }
        velocity = 0;
        isExplode = true;
    }
}
