package com.mygdx.game.sprites;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class GameObject {
    protected Vector2 position;
    protected Rectangle bounds;

    public GameObject(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle((int)(x - width / 2), (int)(y - height / 2), (int)width, (int)height);
    }
}
