package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hero extends Tank{

    private int lives;

    public Hero(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);

        lives = 3;
    }

    public void addExtraLife() {
        lives++;
    }

    public void addShield() {
        setState(State.SHIELD);
    }

    public void improve() {
        //...
    }

    public void addWallBreakingMod() {
        setState(State.WALL_BREAKING);
    }

    @Override
    public void draw(float deltaTime)  {
        super.draw(deltaTime);
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
    public void respondTankCollision() {
        respondBrickCollision();
    }


    @Override
    public void respondBulletCollision() {
        if (getState() != State.SHIELD) {

        }
    }

    public void respondTeammateBulletCollision() {
        setState(State.FROZEN);
    }

    @Override
    public void respondFortressCollison() {
        respondBrickCollision();
    }
}
