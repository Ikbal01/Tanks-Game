package com.mygdx.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Represents a steel square.
 */
public class Steel extends StaticGameObject {

    public Steel(MapObject cell, TiledMapTileLayer layer) {
        super(((RectangleMapObject)cell).getRectangle(), cell, layer);
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        Tank tank = bullet.getTank();

        if (tank.getState() == Tank.State.SUPER_TANK
                && bullet.getState() == Bullet.State.FLYING) {

            destroy();
        }
    }
}
