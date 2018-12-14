package com.mygdx.game.sprites;

import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.world.World;

import java.util.Random;

public class Enemy extends Tank {
    private static final float DIRECTION_CHANGE_TIME = 3f;
    private static final float FIRE_TIME = 1f;

    private Random random;
    private long directionTimer;
    private long fireTimer;

    public Enemy(float x, float y, World world) {
        super(x, y, world);

        state = State.SPAWNING;

        random = new Random();

        color = Color.values()[random.nextInt(3)];

        int randomCategoryIndex = random.nextInt((world.getDifficulty().getIndex()) + world.getStage()) % 8;
        category = TankCategory.values()[randomCategoryIndex];

        armour = category.getArmour();
        lives = 0;
        bulletVelocity = category.getBulletVelocity();
        velocity = category.getVelocity();
        updateAnimation();
        currAnimation = downMoveAnimation;

        directionTimer = System.currentTimeMillis();
        fireTimer = System.currentTimeMillis();
    }

    @Override
    public void update() {
        super.update();

        switch (state) {

            case SPAWNING:
            case NORMAL:

                move(direction);
                if (DIRECTION_CHANGE_TIME < ((System.currentTimeMillis() - directionTimer) / 1000.0)) {
                    moveRandomCorridor();
                    directionTimer = System.currentTimeMillis();
                }
                if (FIRE_TIME < ((System.currentTimeMillis() - fireTimer) / 1000.0)) {
                    fire();
                    fireTimer = System.currentTimeMillis();
                }
                break;
        }
    }

    @Override
    public void draw(float deltaTime)  {
        super.draw(deltaTime);
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
        position.set(previousPosition.x, previousPosition.y);
        bounds.setX(previousPosition.x);
        bounds.setY(previousPosition.y);

        changeDirection();

        setVerticalRail();
        setHorizontalRail();
    }

    @Override
    public void respondTankCollision(Tank tank) {
        if (tank.state != State.SPAWNING && state != State.SPAWNING) {
            respondWallCollision();
        }
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        if (state == State.SPAWNING || bullet.getState() != Bullet.State.FLYING) {
            return;
        }

        if (bullet.getTank() instanceof Hero) {

            armour--;
            if (armour == 0) {
                Hero hero = (Hero)bullet.getTank();
                hero.increaseKills();
                explode();
            }
        }

    }
}
