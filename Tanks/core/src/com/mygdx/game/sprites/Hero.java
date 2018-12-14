package com.mygdx.game.sprites;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.world.World;

import java.util.Iterator;
import java.util.Random;

public class Hero extends Tank{
    private static final int WALL_BREAKING_TIME = 12;
    private static final int SHIELD_TIME = 12;

    private Random random;

    private int lives;
    private int stars;
    private int kills;

    private long wallBreakingTimer;
    private long shieldTimer;

    public Hero(float x, float y, World world, int lives, int stars, int kills) {
        super(x, y, world);

        this.lives = lives;
        this.stars = stars;
        this.kills = kills;

        random = new Random();

        state = State.SPAWNING;
        updateTankSpecif();
        updateAnimation();

        currAnimation = upMoveAnimation;
    }

    @Override
    public void update() {
        super.update();

        switch (state) {
            case SHIELD:
                if (SHIELD_TIME < (System.currentTimeMillis() - shieldTimer) / 1000.0) {
                    state = State.NORMAL;
                }

                updateBullet();
                break;

            case WALL_BREAKING:
                if (WALL_BREAKING_TIME < (System.currentTimeMillis() - wallBreakingTimer) / 1000.0) {
                    state = State.NORMAL;
                }
                updateBullet();
                break;

            case SUPER_TANK:
                Iterator<Bullet> iterator = bullets.iterator();

                while (iterator.hasNext()) {
                    Bullet bullet = iterator.next();
                    if (bullet.getState() == Bullet.State.DESTROYED) {
                        iterator.remove();
                    }
                }

                for (Bullet bullet : bullets) {
                    bullet.update();
                }
                break;

            case DESTROYED:
                if (lives > 0) {
                    lives--;
                    respawn();
                }
                break;
        }
    }

    @Override
    public void fire() {
        if (getState() != State.SUPER_TANK) {
            super.fire();
        } else {
            bullets.add(new Bullet(muzzle.x, muzzle.y, this, direction, spriteBatch, bulletVelocity));
        }
    }

    private void updateTankSpecif() {
        color = Color.values()[3];
        int categoryIndex = (1 + stars) % 8;
        category = TankCategory.values()[categoryIndex];
        armour = category.getArmour();
        bulletVelocity = category.getBulletVelocity();
        velocity = category.getVelocity();
    }

    public void addExtraLife() {
        lives++;
    }

    public void improve() {
        stars++;

        updateTankSpecif();
        updateAnimation();
    }

    private void respawn() {
        stars = 0;
        position.set(208, 64);
        bounds.setX(208);
        bounds.setY(64);
        currAnimation = upMoveAnimation;

        setSpawningState();
        updateTankSpecif();
        updateAnimation();
    }

    @Override
    public void respondWallCollision() {
        position.set(previousPosition.x, previousPosition.y);
        bounds.setX(previousPosition.x);
        bounds.setY(previousPosition.y);
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

        if (getState() != State.SHIELD) {
            if (bullet.getTank() instanceof Enemy) {

                armour--;
                if (armour == 0) {
                    explode();
                }

            } else {
                stop();
            }
        }
    }

    public void setShieldMod() {
        setState(State.SHIELD);

        shieldTimer = System.currentTimeMillis();
    }

    public void setWallBreakingMod() {
        setState(State.WALL_BREAKING);
        wallBreakingTimer = System.currentTimeMillis();
    }

    public void setNormalMod() {
        setState(State.NORMAL);
        updateTankSpecif();
        updateAnimation();
    }

    public void setSuperTankMod() {
        setState(State.SUPER_TANK);

        category = TankCategory.values()[7];
        armour = 10;
        bulletVelocity = 8;
        velocity = 2.2f;

        updateAnimation();
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public int getLives() {
        return lives;
    }

    public int getStars() {
        return stars;
    }

    public int getKills() {
        return kills;
    }

    public void increaseKills() {
        kills++;
    }
}
