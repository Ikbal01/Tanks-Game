package com.mygdx.game.sprites;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.world.World;

import java.util.Iterator;
import java.util.Random;

public class Hero extends Tank{
    private Random random;

    private int lives;
    private int stars;
<<<<<<< HEAD
    private int kills;

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
            case SPAWNING:

            case NORMAL:

                if (bullets.size > 0 && bullets.get(0).getState() == Bullet.State.DESTROYED) {
                    bullets.clear();
                }

                if (bullets.size > 0) {
                    bullets.get(0).update();
                }
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
=======

    public Hero(float x, float y, SpriteBatch spriteBatch, int lives, int stars) {
        super(x, y, spriteBatch);

        this.lives = lives;
        this.stars = stars;
>>>>>>> 9caec4292eb64338f8139d248e2bd8a7466f8693
    }

    public void addExtraLife() {
        lives++;
    }

    public void addShield() {
        setState(State.SHIELD);
    }

    public void improve() {
        stars++;
<<<<<<< HEAD

        updateTankSpecif();
        updateAnimation();
=======
>>>>>>> 9caec4292eb64338f8139d248e2bd8a7466f8693
    }

    public void setWallBreakingMod() {
        setState(State.WALL_BREAKING);
    }

    public void setSuperTankMod() {
        setState(State.SUPER_TANK);

        category = TankCategory.values()[7];
        armour = 10;
        bulletVelocity = 8;
        velocity = 2.2f;

        updateAnimation();
    }

    public void setNormalMod() {
        setState(State.NORMAL);
        updateTankSpecif();
        updateAnimation();
    }

    private void respawn() {
        state = State.SPAWNING;

        stars = 0;
        position.set(208, 64);
        bounds.setX(208);
        bounds.setY(64);

        updateTankSpecif();
        updateAnimation();
        currAnimation = upMoveAnimation;
    }

    private void updateTankSpecif() {
        color = Color.values()[3];
        int categoryIndex = (2 + stars) % 8;
        category = TankCategory.values()[categoryIndex];
        armour = category.getArmour();
        bulletVelocity = category.getBulletVelocity();
        velocity = category.getVelocity();
    }

    @Override
    public void respondBrickCollision() {
        position.set(previousPosition.x, previousPosition.y);
        bounds.setX(previousPosition.x);
        bounds.setY(previousPosition.y);
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
    public void respondTankCollision(Tank tank) {
        if (tank.state != State.SPAWNING && state != State.SPAWNING) {
            respondBrickCollision();
        }
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        if (state == State.SPAWNING) {
            return;
        }

        if (getState() != State.SHIELD) {
            if (bullet.getTank() instanceof Enemy) {

                armour--;
                if (armour == 0) {
                    explode();
                }

            } else {
                state = State.FROZEN;
            }
        }
    }

    @Override
    public void respondFortressCollision() {
        respondBrickCollision();
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }
}
