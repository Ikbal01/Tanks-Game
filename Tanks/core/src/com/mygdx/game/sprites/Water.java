package com.mygdx.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Water extends StaticGameObject {
    public Water(MapObject cell, TiledMapTileLayer layer) {
        super(((RectangleMapObject)cell).getRectangle(), cell, layer);
    }

    @Override
    public void respondTankCollision(Tank tank) {
        // do nothing
    }

    @Override
    public void respondBulletCollision(Bullet bullet) {
        // do nothing
    }
}
