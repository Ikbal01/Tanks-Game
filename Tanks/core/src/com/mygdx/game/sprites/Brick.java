package com.mygdx.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Brick extends StaticGameObject {
    public Brick(MapObject cell, TiledMapTileLayer layer) {
        super(((RectangleMapObject) cell).getRectangle(), cell, layer);
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        if (bullet.getState() == Bullet.State.FLYING) {
            destroy();
        }
    }
}
