package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Tanks;
import com.mygdx.game.screens.GameOverScreen;
import com.mygdx.game.screens.MenuScreen;
import com.mygdx.game.screens.StageScreen;
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

    private static final int FORTRESS_INIT_POS_X = 208;
    private static final int FORTRESS_INIT_POS_Y = 16;

    private static final int NEW_TREASURE_TIME = 15;
    private static final int NEW_ENEMIES_GENERATE_TIME = 18;
    private static final int MUSIC_TIME = 56;
    private static final float GAME_OVER_TIME = 7f;

    public enum State {NORMAL, PAUSE, GAME_OVER, NEXT_LEVEL}
    private State state;

    public static Texture items;
    private Texture pause;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMapTileLayer layer;

    private Tanks game;
    private SpriteBatch spriteBatch;
    private StageOption stageOption;
    private boolean isMultiplayer;
    private MenuScreen.Difficulty difficulty;
    private int stage;
    private int[] lives;
    private int[] stars;
    private int[] kills;

    private Hero player1;
    private Hero player2;
    private Array<Tank> tanks;

    private Array<GameObject> staticGameObjects;
    private Fortress fortress;

    private Treasure treasure;

    private CollisionSystem collisionSystem;
    private GameController gameController;

    private long treasureTimer;
    private long newEnemiesTimer;
    private long musicTimer;
    private long gameOverTimer;

    private int stageEnemyCount;
    private int destroyedEnemyCount;
    private int createdEnemyCount;

    private Random random;

    private Sound music;

    public World(StageOption stageOption) {

        this.stageOption = stageOption;
        this.game = stageOption.getGame();
        this.isMultiplayer = stageOption.isMultiplayer();
        this.spriteBatch = game.spriteBatch;
        this.difficulty = stageOption.getDifficulty();
        this.stage = stageOption.getStage();
        this.lives = stageOption.getLives();
        this.stars = stageOption.getStars();
        this.kills = stageOption.getTotalKills();
        state = State.NORMAL;

        random = new Random();

        items = new Texture(Gdx.files.internal("BattleTanksSheetTransparent.png"));
        pause = new Texture(Gdx.files.internal("gameOver\\pause.png"));
        destroyedEnemyCount = 0;
        createdEnemyCount = 0;
        calculateStageMaxEnemyCount();

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps\\stage" + stageOption.getStage() + ".tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        layer = (TiledMapTileLayer) map.getLayers().get(1);

        tanks = new Array<Tank>();
        initPlayers();
        generateEnemies();

        staticGameObjects = new Array<GameObject>();
        fortress = new Fortress(FORTRESS_INIT_POS_X, FORTRESS_INIT_POS_Y, this);
        staticGameObjects.add(fortress);
        initializeBricks();
        initializeSteel();
        initializeWater();

        collisionSystem = new CollisionSystem(this);
        gameController = new GameController(this);

        treasureTimer = System.currentTimeMillis();
        newEnemiesTimer = System.currentTimeMillis();
        gameOverTimer = System.currentTimeMillis();

        musicTimer = System.currentTimeMillis();
        music = Gdx.audio.newSound(Gdx.files.internal("sound\\NES Boss.mp3"));
        music.play(0.3f);
    }

    public void update() {

        switch (state) {
            case NORMAL:

                if (stageEnemyCount == destroyedEnemyCount) {
                    state = State.NEXT_LEVEL;
                }

                removeDestroyedTanks();
                removeDestroyedStaticObjects();

                for (int i = 0; i < tanks.size; i++) {
                    tanks.get(i).update();
                }

                generateEnemies();
                updateTreasure();
                fortress.update();

                gameController.update();
                collisionSystem.update();
                break;

            case PAUSE:
                gameController.update();
                break;

            case NEXT_LEVEL:
                StageOption stageOption = generateStageOption();
                game.setScreen(new StageScreen(stageOption));
                break;

            case GAME_OVER:
                music.stop();

                if (GAME_OVER_TIME < (System.currentTimeMillis() - gameOverTimer) / 1000.0) {
                    int player1Kills = player1.getKills();
                    int player2Kills = 0;
                    if (isMultiplayer) {
                        player2Kills = player2.getKills();
                    }
                    game.setScreen(new GameOverScreen(game, new int[] {player1Kills, player2Kills}, isMultiplayer));
                }

                collisionSystem.update();

                for (int i = 0; i < tanks.size; i++) {
                    tanks.get(i).update();
                }
                fortress.update();

                break;
        }

        updateMusic();
    }

    private void removeDestroyedTanks() {
        Iterator<Tank> iterator = tanks.iterator();

        while (iterator.hasNext()) {
            Tank tank = iterator.next();
            if (tank.getState() == Tank.State.DESTROYED) {
                if (tank instanceof Enemy) {
                    destroyedEnemyCount++;
                }
                iterator.remove();
            }
        }
    }

    private void removeDestroyedStaticObjects() {
        Iterator<GameObject> iterator = staticGameObjects.iterator();

        while (iterator.hasNext()) {
            GameObject object = iterator.next();

            if (object.isDestroyed()) {
                iterator.remove();
            }
        }
    }

    private void updateMusic() {
        if (MUSIC_TIME < (System.currentTimeMillis() - musicTimer) / 1000.0) {
            music.play(0.3f);
            musicTimer = System.currentTimeMillis();
        }
    }

    private void updateTreasure() {
        if (NEW_TREASURE_TIME < ((System.currentTimeMillis() - treasureTimer) / 1000.0)) {
            generateTreasure();
            treasureTimer = System.currentTimeMillis();
        }

        if (treasure != null) {
            treasure.update();

            if (treasure.getState() != Treasure.State.ACTIVE) {
                treasure = null;
            }
        }
    }

    private void initPlayers() {
        player1 = new Hero(PLAYER_1_SPAWNING_POS_X, PLAYER_1_SPAWNING_POS_Y,
                this, lives[0], stars[0], kills[0]);
        tanks.add(player1);

        player2 = new Hero(PLAYER_2_SPAWNING_POS_X, PLAYER_2_SPAWNING_POS_Y,
                this, lives[1], stars[1], kills[1]);
        tanks.add(player2);

        if (!stageOption.isMultiplayer()) {
            player2.setState(Tank.State.DESTROYED);
        }

    }

    private void initializeBricks() {
        for (MapObject cell : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            staticGameObjects.add(new Brick(cell, layer));
        }
    }

    private void initializeSteel() {
        for (MapObject cell : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            staticGameObjects.add(new Steel(cell, layer));
        }
    }

    private void initializeWater() {
        for (MapObject cell : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            staticGameObjects.add(new Water(cell, layer));
        }
    }

    private void generateEnemies() {
        if (NEW_ENEMIES_GENERATE_TIME < ((System.currentTimeMillis() - newEnemiesTimer) / 1000.0)
                && createdEnemyCount < stageEnemyCount && tanks.size < 12) {

            tanks.add(new Enemy(Enemies_SPAWNING_POS_1_X, Enemies_SPAWNING_POS_Y, this));
            tanks.add(new Enemy(Enemies_SPAWNING_POS_2_X, Enemies_SPAWNING_POS_Y, this));
            tanks.add(new Enemy(Enemies_SPAWNING_POS_3_X, Enemies_SPAWNING_POS_Y, this));

            createdEnemyCount += 3;
            newEnemiesTimer = System.currentTimeMillis();
        }
    }

    private void generateTreasure() {

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

    private StageOption generateStageOption() {
        StageOption stageOption = new StageOption();

        stageOption.setGame(game);
        stageOption.setDifficulty(difficulty);
        stageOption.setMultiplayer(isMultiplayer);
        stageOption.setStage(++stage);
        stageOption.setLives(player1.getLives(), player2.getLives());
        stageOption.setStars(player1.getStars(), player2.getStars());
        stageOption.setTotalKills(player1.getKills(), player2.getKills());

        return stageOption;
    }

    private void calculateStageMaxEnemyCount() {
        switch (difficulty) {
            case EASY:
                stageEnemyCount = 15 + stage * 3;
                break;
            case NORMAL:
                stageEnemyCount = 15 + stage * 6;
                break;
            case HARD:
                stageEnemyCount = 15 + stage * 9;
        }
    }

    public void draw(float stateTime) {

        for (Tank tank : tanks) {
            tank.draw(stateTime);
        }

        if (treasure != null) {
            treasure.draw();
        }

        fortress.draw();

        if (state == State.PAUSE) {
            spriteBatch.draw(pause, 16, 16, 416, 416);
        }
    }

    public void killEnemies() {
        for (Tank tank : tanks) {
            if (tank instanceof Enemy) {
                tank.explode();
            }
        }
    }

    public void stopTime() {
        for (Tank tank : tanks) {
            if (tank instanceof Enemy) {
                ((Enemy) tank).stop();
            }
        }
    }

    public void setGameOverState() {
        state = State.GAME_OVER;
    }

    public void setPauseState() {
        state = State.PAUSE;
    }

    public void setNormalState() {
        state = State.NORMAL;
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
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

    public Array<Tank> getTanks() {
        return tanks;
    }

    public Array<GameObject> getStaticGameObjects() {
        return staticGameObjects;
    }

    public Treasure getTreasure() {
        return treasure;
    }

    public Fortress getFortress() {
        return fortress;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public MenuScreen.Difficulty getDifficulty() {
        return difficulty;
    }

    public Tanks getGame() {
        return game;
    }

    public int getStage() {
        return stage;
    }

    public State getState() {
        return state;
    }

    public int getStageEnemyCount() {
        return stageEnemyCount;
    }

    public int getDestroyedEnemyCount() {
        return destroyedEnemyCount;
    }
}
