package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.sprites.*;

public class World {
    public static final int WORLD_WIDTH = 512;
    public static final int WORLD_HEIGHT = 448;

    public static final int MAP_WIDTH = 416;
    public static final int MAP_HEIGHT = 416;

    public static final int CELL_SIZE = 8;

    public static final int PIXELS_32 = 32;

    private SpriteBatch spriteBatch;
    public static Texture items;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private Hero hero;
    private Array<Enemy> enemies;

    private Array<Steel> steels;
    private Array<Brick> bricks;

    private CollisionSystem collisionSystem;

    public World(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        items = new Texture(Gdx.files.internal("BattleTanksSheetTransparent.png"));

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("stage1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        hero = new Hero(16, 16, spriteBatch);
        enemies = new Array<Enemy>();
        enemies.add(new Enemy(16, 400, spriteBatch));
        enemies.add(new Enemy(64, 400, spriteBatch));
        enemies.add(new Enemy(400, 400, spriteBatch));
        enemies.add(new Enemy(256, 400, spriteBatch));

        initializeBricks();
        initializeSteels();

        collisionSystem = new CollisionSystem(this);

    }

    public void update() {
        collisionSystem.update();
    }

    private void initializeBricks() {
        bricks = new Array<Brick>();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        for (MapObject cell : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            bricks.add(new Brick(cell, layer));
        }
    }

    private void initializeSteels() {
        steels = new Array<Steel>();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        for (MapObject cell : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            steels.add(new Steel(cell, layer));
        }
    }

    public void draw(float stateTime) {
        hero.draw(stateTime);
        for (Enemy enemy : enemies) {
            enemy.draw(stateTime);
        }
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public Hero getHero() {
        return hero;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<Brick> getBricks() {
        return bricks;
    }

    public Array<Steel> getSteels() {
        return steels;
    }
}
