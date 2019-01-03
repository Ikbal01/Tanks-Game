package com.mygdx.game.sprites;

import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.world.World;

import java.util.Random;

/**
 * Enemy tanks.
 */
public class Enemy extends Tank {
    // Direction changing interval in random corridor (multiple 32 pixels)
    private static final float CORR_DIRECTION_CHANGE_TIME = 3f;
    // Direction changing interval
    private static final float RANDOM_DIRECTION_CHANGE_TIME = 4.5f;
    // The waiting time in a collision
    private static final float COLLISION_WAIT_TIME = 0.2f;
    // The minimum time for shooting
    private static final float FIRE_TIME = 1f;

    private Random random;

    private long directionTimer;
    private long randomDirectionTimer;
    private long collisionWaitTimer;
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

        long currTime = System.currentTimeMillis();
        directionTimer = currTime;
        randomDirectionTimer = currTime;
        collisionWaitTimer = currTime;
        fireTimer = currTime;
    }

    /**
     * Changes the specifications and the direction of
     * the tank according to its status at that time
     */
    @Override
    public void update() {
        super.update();

        switch (state) {

            case SPAWNING:
            case NORMAL:

                if (isElapsed(COLLISION_WAIT_TIME, collisionWaitTimer)) {
                    move(direction);
                }

                if (isElapsed(CORR_DIRECTION_CHANGE_TIME, directionTimer)) {
                    moveRandomCorridor();
                    directionTimer = System.currentTimeMillis();
                }
                if (isElapsed(RANDOM_DIRECTION_CHANGE_TIME, randomDirectionTimer)) {
                    changeDirection();

                    randomDirectionTimer = System.currentTimeMillis();
                }

                if (isElapsed(FIRE_TIME, fireTimer)) {
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

    /**
     * Turns its direction of movement if it is near a corridor
     * ((its position + 16 (status bar width)) % 32 < 4 (static game object width / 2))
     */
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

    /**
     * Changes the direction of movement in a random direction
     */
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

        setVerticalRail();
        setHorizontalRail();
    }

    /**
     * Returns to the previous position, waits for
     * a while and changes its direction randomly.
     */
    @Override
    public void respondWallCollision() {
        goBack();
        changeDirection();

        collisionWaitTimer = System.currentTimeMillis();
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        if (state == State.SPAWNING || bullet.getState() != Bullet.State.FLYING) {
            return;
        }

        if (bullet.getTank() instanceof Hero) {
            armour--;

            color = Color.values()[random.nextInt(3)];
            updateAnimation();

            if (armour == 0) {
                Hero hero = (Hero)bullet.getTank();
                hero.increaseKills();

                explode();
            }
        }
    }

    /**
     * Returns to the previous position, waits for a while and changes
     * its direction randomly if both tanks are not in state SPAWNING
     *
     * @param tank the tank which collides with the Enemy
     */
    @Override
    public void respondTankCollision(Tank tank) {

        if (tank.state != State.SPAWNING && state != State.SPAWNING) {

            goBack();
            changeDirection();

            collisionWaitTimer = System.currentTimeMillis();
        }
    }
}
