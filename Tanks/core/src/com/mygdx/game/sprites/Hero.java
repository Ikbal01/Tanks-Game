package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hero extends Tank{

    private int lives;

    public Hero(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);

        lives = 3;
    }

    @Override
    public void update() {
        handleInput();

        if (bullet != null && bullet.getState() == Bullet.State.DESTROYED) {
            bullet = null;
        }
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveRight();

        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveLeft();

        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveUp();

        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveDown();
        }
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
            //...
        }
    }
}
