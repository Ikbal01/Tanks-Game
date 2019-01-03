package com.mygdx.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Brick extends StaticGameObject {

    public Brick(MapObject cell, TiledMapTileLayer layer) {
        super(((RectangleMapObject) cell).getRectangle(), cell, layer);
    }

    /**
     * Destroys, if bullet's state is FLYING.
     *
     * @param bullet the bullet which collides with this brick
     */
    @Override
    public void respondBulletCollision(Bullet bullet) {

        if (bullet.getState() == Bullet.State.FLYING) {
            destroy();
        }
    }
}
