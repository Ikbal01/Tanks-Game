package com.mygdx.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class StaticGameObject extends GameObject {
    private MapObject cell;
    private TiledMapTileLayer layer;

    public StaticGameObject(Rectangle bounds, MapObject cell, TiledMapTileLayer layer) {
        super(bounds.x, bounds.y, bounds.width, bounds.height);

        this.cell = cell;
        this.layer = layer;
    }

    public void destroyLayer() {

    }
}
