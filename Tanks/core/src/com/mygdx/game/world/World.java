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

    private static final int PLAYER_1_SPAWNING_POS_X = 160;
    private static final int PLAYER_1_SPAWNING_POS_Y = 16;
    private static final int PLAYER_2_SPAWNING_POS_X = 256;
    private static final int PLAYER_2_SPAWNING_POS_Y = 16;

    private static final int Enemies_SPAWNING_POS_1_X = 16;
    private static final int Enemies_SPAWNING_POS_2_X = 208;
    private static final int Enemies_SPAWNING_POS_3_X = 400;
    private static final int Enemies_SPAWNING_POS_Y = 400;

    private static final int FORTRESS_POS_X = 208;
    private static final int FORTRESS_POS_Y = 16;

    private static final int NEW_TREASURE_TIME = 17;
    private static final int NEW_ENEMIES_GENERATE_TIME = 30;

    public enum State {PLAYING, GAME_OVER, NEXT_lEVEL}
    private State state;

    private SpriteBatch spriteBatch;
    public static Texture items;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMapTileLayer layer;

    private Hero player1;
    private Hero player2;
    private Array<Enemy> enemies;

    private Array<Steel> steelBlocks;
    private Array<Brick> bricks;
    private Fortress fortress;

    private Treasure treasure;

    private CollisionSystem collisionSystem;
    private PlayersController playersController;

    private long treasureTimer;
    private long newEnemiesTimer;

    private boolean multiplayer;
    private int stage;

    public World(SpriteBatch spriteBatch, boolean multiplayer, int stage) {
        this.spriteBatch = spriteBatch;
        this.multiplayer = multiplayer;
        this.stage = stage;

        state = State.PLAYING;

        items = new Texture(Gdx.files.internal("BattleTanksSheetTransparent.png"));

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("stage" + stage + ".tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        layer = (TiledMapTileLayer) map.getLayers().get(1);

        player1 = new Hero(PLAYER_1_SPAWNING_POS_X, PLAYER_1_SPAWNING_POS_Y, spriteBatch);

        if (multiplayer) {
            player2 = new Hero(PLAYER_2_SPAWNING_POS_X, PLAYER_2_SPAWNING_POS_Y, spriteBatch);
        }

        enemies = new Array<Enemy>();

        generateEnemies();

        initializeBricks();
        initializeSteels();
        fortress = new Fortress(FORTRESS_POS_X, FORTRESS_POS_Y);

        collisionSystem = new CollisionSystem(this);
        playersController = new PlayersController(player1, player2, multiplayer);

        treasureTimer = System.currentTimeMillis();
        newEnemiesTimer = System.currentTimeMillis();
    }

    public void update() {
        removeDestroyedEnemies();
        removeDestroyedBricks();
        removeDestroyedSteelBlocks();

        if (player1.getState() != Tank.State.DESTROYED) {
            player1.update();
        }
        if (player2 != null && player2.getState() != Tank.State.DESTROYED) {
            player2.update();
        }

        for (Enemy enemy : enemies) {
            enemy.update();
        }

        generateEnemies();
        updateTreasure();

        playersController.update();
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

        for (MapObject cell : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            bricks.add(new Brick(cell, layer));
        }
    }

    private void initializeSteels() {
        steelBlocks = new Array<Steel>();

        for (MapObject cell : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            steelBlocks.add(new Steel(cell, layer));
        }
    }

    private void generateEnemies() {
        if (NEW_ENEMIES_GENERATE_TIME < ((System.currentTimeMillis() - newEnemiesTimer) / 1000.0)) {
            enemies.add(new Enemy(Enemies_SPAWNING_POS_1_X, Enemies_SPAWNING_POS_Y, spriteBatch));
            enemies.add(new Enemy(Enemies_SPAWNING_POS_2_X, Enemies_SPAWNING_POS_Y, spriteBatch));
            enemies.add(new Enemy(Enemies_SPAWNING_POS_3_X, Enemies_SPAWNING_POS_Y, spriteBatch));

            newEnemiesTimer = System.currentTimeMillis();
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
        player1.draw(stateTime);
        player2.draw(stateTime);
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

    public Hero getPlayer1() {
        return player1;
    }

    public Hero getPlayer2() {
        return player2;
    }

    public void setState(State state) {
        this.state = state;
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

    public Fortress getFortress() {
        return fortress;
    }

    public boolean isMultiplayer() {
        return multiplayer;
    }
}
