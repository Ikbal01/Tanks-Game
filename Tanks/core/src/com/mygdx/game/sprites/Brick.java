package com.mygdx.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Brick extends StaticGameObject{
    public Brick(MapObject cell, TiledMapTileLayer layer) {
        super(((RectangleMapObject)cell).getRectangle(), cell, layer);
    }

    public void destroy() {
        destroyLayer();
    }
}
