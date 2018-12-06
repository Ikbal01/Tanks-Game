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
import com.mygdx.game.treasures.*;

import java.util.Iterator;
import java.util.Random;

public class World {
    public static final int WORLD_WIDTH = 512;
    public static final int WORLD_HEIGHT = 448;

    public static final int MAP_WIDTH = 416;
    public static final int MAP_HEIGHT = 416;

    public static final int CELL_SIZE = 8;

    public static final int PIXELS_32 = 32;

    private static final int NEW_TREASURE_TIME = 17;

    private SpriteBatch spriteBatch;
    public static Texture items;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private Hero hero;
    private Array<Enemy> enemies;

    private Array<Steel> steelBlocks;
    private Array<Brick> bricks;

    private Treasure treasure;

    private CollisionSystem collisionSystem;

    private long treasureTimer;

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

        treasureTimer = System.currentTimeMillis();
    }

    public void update() {
        removeDestroyedEnemies();
        removeDestroyedBricks();
        removeDestroyedSteelBlocks();

        hero.update();

        for (Enemy enemy : enemies) {
            enemy.update();
        }
        updateTreasure();
        collisionSystem.update();
    }

    private void removeDestroyedEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.getState() == Tank.State.DESTROYED) {
                iterator.remove();
            }
        }
    }

    private void removeDestroyedBricks() {
        Iterator<Brick> iterator = bricks.iterator();

        while (iterator.hasNext()) {
            Brick brick = iterator.next();

            if (brick.isDestroyed()) {
                iterator.remove();
            }
        }
    }

    private void removeDestroyedSteelBlocks() {
        Iterator<Steel> iterator = steelBlocks.iterator();

        while (iterator.hasNext()) {
            Steel steelBlock = iterator.next();

            if (steelBlock.isDestroyed()) {
                iterator.remove();
            }
        }
    }

    private void updateTreasure() {
        if (NEW_TREASURE_TIME < ((System.currentTimeMillis() - treasureTimer) / 1000.0)) {
            generateTreasure();
            treasureTimer = System.currentTimeMillis();
        }
        if (treasure != null) {
            treasure.update();
        }

        if (treasure != null && treasure.getState() != Treasure.State.ACTIVE) {
            treasure = null;
        }
    }

    private void initializeBricks() {
        bricks = new Array<Brick>();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        for (MapObject cell : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            bricks.add(new Brick(cell, layer));
        }
    }

    private void initializeSteels() {
        steelBlocks = new Array<Steel>();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        for (MapObject cell : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            steelBlocks.add(new Steel(cell, layer));
        }
    }

    private void generateTreasure() {
        Random random = new Random();

        int ran = random.nextInt(8) + 1;
        int x = random.nextInt(369) + 16;
        int y = random.nextInt(369) + 16;

        switch (ran)  {
            case 1:
                treasure = new EnemyKiller(x, y, spriteBatch);
                break;
            case 2:
                treasure = new BaseDefender(x, y, spriteBatch);
                break;
            case 3:
                treasure = new ExtraLife(x, y, spriteBatch);
                break;
            case 4:
                treasure = new Shield(x, y, spriteBatch);
                break;
            case 5:
                treasure = new TankImprover(x, y, spriteBatch);
                break;
            case 6:
                treasure = new TimeStopper(x, y, spriteBatch);
                break;
            case 7:
                treasure = new WallBreaker(x, y, spriteBatch);
                break;
        }
    }

    public void draw(float stateTime) {
        hero.draw(stateTime);
        for (Enemy enemy : enemies) {
            enemy.draw(stateTime);
        }
        if (treasure != null) {
            treasure.draw();
        }
    }

    public void killEnemies() {
        for (Enemy enemy : enemies) {
            enemy.destroy();
        }
    }

    public void defendBase() {
        //...
    }

    public void stopTime() {
        for (Enemy enemy : enemies) {
            enemy.stop();
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

    public Array<Steel> getSteelBlocks() {
        return steelBlocks;
    }

    public Treasure getTreasure() {
        return treasure;
    }
}
