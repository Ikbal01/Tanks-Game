package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.world.World;

import java.util.Iterator;

public class Hero extends Tank{
    // Duration of WALL_BREAKING state
    private static final int WALL_BREAKING_TIME = 12;
    // Duration of SHIELD state
    private static final int SHIELD_TIME = 12;

    private static final int RESPAWN_POS_X = 208;
    private static final int RESPAWN_POS_Y = 64;

    // The progress of the tank
    private int stars;
    private int kills;
    private int lives;

    // Counts the elapsed time of WALL_COLLISION state
    private long wallBreakingTimer;
    // Counts the elapsed time of SHIELD state
    private long shieldTimer;
    // The sound which plays in shooting
    private Sound fireSound;

    public Hero(float x, float y, World world, int lives, int stars, int kills) {
        super(x, y, world);

        this.lives = lives;
        this.stars = stars;
        this.kills = kills;

        state = State.SPAWNING;
        updateTankSpecif();
        updateAnimation();

        // Default animation
        currAnimation = upMoveAnimation;

        fireSound = Gdx.audio.newSound(Gdx.files.internal("sound\\fire.wav"));
    }

    /**
     * Updates the hero's status as the game progresses.
     */
    @Override
    public void update() {
        super.update();

        switch (state) {
            case SHIELD:
                if (isElapsed(SHIELD_TIME, shieldTimer)) {
                    state = State.NORMAL;
                }

                updateBullet();
                break;

            case WALL_BREAKING:
                if (isElapsed(WALL_BREAKING_TIME, wallBreakingTimer)) {
                    state = State.NORMAL;
                }
                updateBullet();
                break;

            case SUPER_TANK:
                updateSuperTankBullets();
                break;

            case DESTROYED:
                if (lives > 0) {
                    lives--;
                    respawn();
                }
                break;
        }
    }

    /**
     * If state is SUPER_TANK creates a bullet (shoots a bullet)
     * and plays shooting sound. If there is no other bullets
     * calls super.fire() and plays shooting sound.
     */
    @Override
    public void fire() {
        if (getState() != State.SUPER_TANK) {
            if (bullets.size == 0) {
                fireSound.play();
                super.fire();
            }
        } else {
            fireSound.play();
            bullets.add(new Bullet(getMuzzle().x, getMuzzle().y,
                    this, direction, spriteBatch, bulletVelocity));
        }
    }

    /**
     * Changes the category, armour, bullet's velocity
     * and velocity depending on the progress of the tank.
     */
    private void updateTankSpecif() {
        color = Color.YELLOW;
        int categoryIndex = (1 + stars) % 8;
        category = TankCategory.values()[categoryIndex];
        armour = category.getArmour();
        bulletVelocity = category.getBulletVelocity();
        velocity = category.getVelocity();
    }

    /**
     * Updates the bullets in SUPER_TANK state (In SUPER_TANK state the
     * tank can shoot multiple bullets otherwise can shoot only one bullet).
     * Removes destroyed bullets.
     */
    private void updateSuperTankBullets() {
        Iterator<Bullet> iterator = bullets.iterator();

        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            if (bullet.getState() == Bullet.State.DESTROYED) {
                iterator.remove();
            } else {
                bullet.update();
            }
        }
    }

    public void addExtraLife() {
        lives++;
    }

    /**
     * Improves tank's specifications
     */
    public void improve() {
        if (state != State.SUPER_TANK) {
            stars++;

            updateTankSpecif();
            updateAnimation();
        }
    }

    private void respawn() {
        stars = 0;
        position.set(RESPAWN_POS_X, RESPAWN_POS_Y);
        bounds.setX(RESPAWN_POS_X);
        bounds.setY(RESPAWN_POS_Y);
        currAnimation = upMoveAnimation;

        setSpawningState();
        updateTankSpecif();
        updateAnimation();
    }

    /**
     * Returns to its previous position in a collision with a wall.
     */
    @Override
    public void respondWallCollision() {
        goBack();
    }

    /**
     * Returns to its previous position if both
     * tanks are not in SPAWNING state
     *
     * @param tank the tank which collides with this tank
     */
    @Override
    public void respondTankCollision(Tank tank) {
        if (tank.state != State.SPAWNING && state != State.SPAWNING) {
            goBack();
        }
    }

    /**
     * Gets damage if bullet is shot by Enemy whereas freezes for a while.
     *
     * @param bullet the bullet which collides with this tank
     */
    @Override
    public void respondBulletCollision(Bullet bullet) {
        if (state == State.SPAWNING || bullet.getState() != Bullet.State.FLYING
                || bullet.getTank() == this) {

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

    /**
     * In this mod the tank does not get damage
     */
    public void setShieldMod() {
        if (state != State.SUPER_TANK) {

            state = State.SHIELD;

            shieldTimer = System.currentTimeMillis();
        }
    }

    /**
     * In this mod walls are destroyed in collision with this tank.
     */
    public void setWallBreakingMod() {
        if (state != State.SUPER_TANK) {

            state = State.WALL_BREAKING;

            wallBreakingTimer = System.currentTimeMillis();
        }
    }

    /**
     * Returns to the normal state if the state is SUPER_TANK
     */
    public void setNormalMode() {
        if (state != State.DESTROYED) {

            state = State.NORMAL;

            updateTankSpecif();
            updateAnimation();
            currAnimation = upMoveAnimation;
        }
    }

    /**
     * Cheats mod
     */
    public void setSuperTankMode() {
        if (state != State.DESTROYED) {

            state = State.SUPER_TANK;

            int mostPowerfulCat = 7;
            category = TankCategory.values()[mostPowerfulCat];
            updateAnimation();
            currAnimation = upMoveAnimation;
            armour = 10;
            bulletVelocity = 8;
            velocity = 2.2f;
        }
    }

    public void increaseKills() {
        kills++;
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
}
