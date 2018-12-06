package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.enums.Direction;

import java.util.Random;

public class Enemy extends Tank {
    private static final float DIRECTION_CHANGE_TIME = 3f;
    private static final float FIRE_TIME_DURATION = 1f;
    private static final float EXPLOSION_DURATION = 0.2f;
    private static final float FROZEN_TIME = 15f;

    private Random random;
    private long startDirectionTime;
    private long startFireTime;
    private long startExplodingTime;
    private long startFrozenTime;

    public Enemy(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);
        random = new Random();

        startDirectionTime = System.currentTimeMillis();
        startFireTime = System.currentTimeMillis();
    }

    @Override
    public void update() {

        if (bullet != null && bullet.getState() == Bullet.State.DESTROYED) {
            bullet = null;
        }

        switch (getState()) {
            case SPAWNING:

            case NORMAL:
                move();
                if (DIRECTION_CHANGE_TIME < ((System.currentTimeMillis() - startDirectionTime) / 1000.0)) {
                    moveRandomCorridor();
                    startDirectionTime = System.currentTimeMillis();
                }
                if (FIRE_TIME_DURATION < ((System.currentTimeMillis() - startFireTime) / 1000.0)) {
                    fire();
                    startFireTime = System.currentTimeMillis();
                }
                break;

            case FROZEN:
                if (FROZEN_TIME < (System.currentTimeMillis() - startFrozenTime) / 1000.0) {
                    setState(State.NORMAL);
                }
                break;

            case EXPLODING:
                if (EXPLOSION_DURATION < (System.currentTimeMillis() - startExplodingTime) / 1000.0) {
                    setState(State.DESTROYED);
                }
                explode();
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

    public void stop() {
        startFrozenTime = System.currentTimeMillis();
        setState(State.FROZEN);
    }

    public void destroy() {
        startExplodingTime = System.currentTimeMillis();
        setState(State.EXPLODING);
    }

    @Override
    public void respondBrickCollision() {
        position.set(previousPosition.x, previousPosition.y);
        bounds.setX(previousPosition.x);
        bounds.setY(previousPosition.y);

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
        destroy();
    }

    @Override
    public void draw(float deltaTime)  {
        super.draw(deltaTime);
    }

}
