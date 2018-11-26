package com.mygdx.game.sprites;

public class Tank extends DynamicGameObject {
    public static final int TANK_WIDTH = 32;
    public static final int TANK_HEIGHT = 32;

    public Tank(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void respondBrickCollision() {

    }
}
