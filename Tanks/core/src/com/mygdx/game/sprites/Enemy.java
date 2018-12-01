package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.enums.Direction;

import java.util.Random;

public class Enemy extends Tank {
    private static float DIRECTION_CHANGE_TIME = 180f;
    private Random random;
    private float elapsedTime = 0;
    private float fireTime = 0;
    private static float fireTimeDuration = 60f;

    public Enemy(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);
        random = new Random();
    }

    public void update() {
        elapsedTime++;
        if (DIRECTION_CHANGE_TIME < elapsedTime) {
            moveRandomCorridor();
        }


        if (bullet != null && bullet.isDestroyed()) {
            bullet = null;
        }

        move();

        fireTime++;
        if (fireTimeDuration < fireTime) {
            fire();
            fireTime = 0;
        }
    }

    public void draw(float deltaTime)  {
        update();

        TextureRegion currentFrame = currAnimation.getKeyFrame(deltaTime, true);
        spriteBatch.draw(currentFrame, getPosition().x, getPosition().y);

        if (bullet != null) {
            bullet.update();
            bullet.draw(deltaTime);
        }
    }

    private void moveRandomCorridor() {
        int rand = random.nextInt(2) + 1;

        if (direction == Direction.UP || direction == Direction.DOWN) {
            if ((getPosition().y - 16) % 32 < 4) {
                switch (rand) {
                    case 1:
                        direction = Direction.LEFT;
                        break;
                    case 2:
                        direction = Direction.RIGHT;
                }
                setHorizontalRail();
                System.out.println(getPosition().x + " " + getPosition().y);

                elapsedTime = 0;
            }
        } else {
            if ((getPosition().x - 16) % 32 < 4) {
                switch (rand) {
                    case 1:
                        direction = Direction.UP;

                        break;
                    case 2:
                        direction = Direction.DOWN;
                }
                setVerticalRail();
                System.out.println(getPosition().x + " " + getPosition().y);

                elapsedTime = 0;
            }
        }
    }

    public void move() {
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

        setVerticalRail();
        setHorizontalRail();
    }
}
