package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hero extends Tank{
    public Hero(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);
    }

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

    @Override
    public void draw(float deltaTime)  {
        update();
        super.draw(deltaTime);
    }

    @Override
    public void respondBrickCollision() {
        getPosition().x = previousPosition.x;
        getPosition().y = previousPosition.y;
        getBounds().x = previousPosition.x;
        getBounds().y = previousPosition.y;
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

    }
}
