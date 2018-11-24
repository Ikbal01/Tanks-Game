package com.mygdx.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class StaticGameObject extends GameObject {
    private MapObject cell;
    private TiledMapTileLayer layer;

    private boolean isDestroyed;

    public StaticGameObject(Rectangle bounds, MapObject cell, TiledMapTileLayer layer) {
        super(bounds.x, bounds.y, bounds.width, bounds.height);

        this.cell = cell;
        this.layer = layer;

        isDestroyed = false;
    }

    public void destroyLayer() {
        int x = (int)(getPosition().x / 8 );
        int y = (int)(getPosition().y / 8 );


        if (layer.getCell(x, y) != null) {

            layer.getCell(x, y).setTile(null);
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed() {
        isDestroyed = true;
    }
}
