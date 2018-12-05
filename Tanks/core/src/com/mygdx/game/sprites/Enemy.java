package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.enums.Direction;

import java.util.Random;

public class Enemy extends Tank {
    private static float DIRECTION_CHANGE_TIME = 180f;
    private static float FIRE_TIME_DURATION = 60f;

    public enum State {SPAWNING, NORMAL, EXPLODING};
    public State currentState;

    private Random random;
    private float elapsedTime = 0;
    private float fireTime = 0;

    public Enemy(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);
        random = new Random();

        currentState = State.NORMAL;
    }

    public void update() {
        elapsedTime++;
        if (DIRECTION_CHANGE_TIME < elapsedTime) {
            moveRandomCorridor();
            elapsedTime = 0;
        }


        if (bullet != null && bullet.getState() == Bullet.State.DESTROYED) {
            bullet = null;
        }

        move();

        fireTime++;
        if (FIRE_TIME_DURATION < fireTime) {
            fire();
            fireTime = 0;
        }

        switch (currentState) {
            case NORMAL:

                break;
            case SPAWNING:
                break;
            case EXPLODING:
                break;
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
    public void respondBrickCollision() {
        getPosition().x = previousPosition.x;
        getPosition().y = previousPosition.y;
        getBounds().x = previousPosition.x;
        getBounds().y = previousPosition.y;

        changeDirection();

        setVerticalRail();
        setHorizontalRail();
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
        currentState = State.EXPLODING;
        explode(deltaTime);
    }

    @Override
    public void draw(float deltaTime)  {
        update();
        super.draw(deltaTime);
    }
}
