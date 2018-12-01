package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.enums.Direction;

import java.util.Random;

public class Enemy extends Tank {
    private Random random;
    private float directionDuration = 200f;
    private float elapsedTime = 0;
    private float fireTime = 0;
    private float fireTimeDuration = 40f;
    private boolean canFire = true;

    public Enemy(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);
        random = new Random();
    }

    public void update(float deltaTime) {
        elapsedTime++;
        if (directionDuration < elapsedTime) {
            changeDirection();
            setHorizontalRail();
            setVerticalRail();
            elapsedTime = 0;
        }

        fireTime++;
        if (fireTimeDuration < fireTime) {
            canFire = true;
        }

        if (bullet != null && bullet.isDestroyed()) {
            bullet = null;
        }

        move(direction);

        if (canFire) {
            fire();
            canFire = false;
            fireTime = 0;
        }
    }

    public void draw(float deltaTime)  {
        update(deltaTime);

        TextureRegion currentFrame = currAnimation.getKeyFrame(deltaTime, true);
        spriteBatch.draw(currentFrame, getPosition().x, getPosition().y);

        if (bullet != null) {
            bullet.update();
            bullet.draw(deltaTime);
        }
    }

    public void move(Direction direction) {
        switch (direction) {
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
        }
    }

    private void changeDirection() {
        int rand = random.nextInt(4) + 1;
        switch (rand) {
            case 1:
                direction = Direction.UP;
                break;
            case 2:
                direction = Direction.LEFT;
                break;
            case 3:
                direction = Direction.DOWN;
                break;
            case 4:
                direction = Direction.RIGHT;
                break;
        }
    }

    @Override
    public void respondWallCollision() {
        getPosition().x = previousPosition.x;
        getPosition().y = previousPosition.y;
        getBounds().x = previousPosition.x;
        getBounds().y = previousPosition.y;

        changeDirection();
        setHorizontalRail();
        setVerticalRail();
    }
}
