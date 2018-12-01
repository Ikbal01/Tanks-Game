package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    public static final int BIG_TILE_STANDARD = 32;

    private SpriteBatch spriteBatch;
    public static Texture items;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private Hero hero;
    private Array<Enemy> enemies;

    private Array<Steel> steels;
    private Array<Brick> bricks;

    public World(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        items = new Texture(Gdx.files.internal("BattleTanksSheet.png"));

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("stage1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        hero = new Hero(16, 16, spriteBatch);
        enemies = new Array<Enemy>();
        enemies.add(new Enemy(16, 400, spriteBatch));
        enemies.add(new Enemy(16, 400, spriteBatch));
        enemies.add(new Enemy(400, 400, spriteBatch));
        enemies.add(new Enemy(400, 400, spriteBatch));

        setBricks();
        setSteels();
    }

    public void update() {
        handleInput();


        for (Enemy enemy : enemies) {
            checkBrickCollision(enemy);
            checkMapBoundsCollision(enemy);
            checkSteelCollision(enemy);

            if (enemy.getBullet() != null) {
                checkBrickCollision(enemy.getBullet());
                checkSteelCollision(enemy.getBullet());
                checkMapBoundsCollision(enemy.getBullet());
            }
        }

        if (hero.getBullet() != null) {
            checkBrickCollision(hero.getBullet());
            checkSteelCollision(hero.getBullet());
            checkMapBoundsCollision(hero.getBullet());
            if (!hero.getBullet().isExplode()) {
                checkEnemyCollision(hero.getBullet());
            }
        }
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            hero.fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            hero.moveRight();
            checkBrickCollision(hero);
            checkMapBoundsCollision(hero);
            checkSteelCollision(hero);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            hero.moveLeft();
            checkBrickCollision(hero);
            checkMapBoundsCollision(hero);
            checkSteelCollision(hero);

        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            hero.moveUp();
            checkBrickCollision(hero);
            checkMapBoundsCollision(hero);
            checkSteelCollision(hero);

        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            hero.moveDown();
            checkBrickCollision(hero);
            checkMapBoundsCollision(hero);
            checkSteelCollision(hero);

        }
    }

    private void checkEnemyCollision(Bullet bullet) {
        for (int i = 0; i < enemies.size; i++) {
            if (bullet.getBounds().overlaps(enemies.get(i).getBounds())) {
                bullet.respondWallCollision();
                enemies.removeIndex(i);
                break;
            }
        }
    }

    private void checkBrickCollision(Tank tank) {
        for (Brick brick : bricks) {
            if (tank.getBounds().overlaps(brick.getBounds())) {
                tank.respondWallCollision();
            }
        }
    }

    private void checkSteelCollision(Tank tank) {
        for (Steel steel : steels) {
            if (tank.getBounds().overlaps(steel.getBounds())) {
                tank.respondWallCollision();

            }
        }
    }

    private void checkBrickCollision(Bullet bullet) {
        if (bulletCollidesWithBricks(bullet)) {
            for (int i = 0; i < bricks.size; i++) {
                if (bullet.getBigBounds().overlaps(bricks.get(i).getBounds())) {
                    bricks.get(i).destroy();
                    bricks.removeIndex(i);
                    i--;
                }
            }
            bullet.respondWallCollision();
        }
    }

    private boolean bulletCollidesWithBricks(Bullet bullet) {
        if (!bullet.isExplode()) {
            for (Brick brick : bricks) {
                if (bullet.getBounds().overlaps(brick.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkSteelCollision(Bullet bullet) {
        for (Steel steel : steels) {
            if (bullet.getBounds().overlaps(steel.getBounds())) {
                bullet.respondWallCollision();
            }
        }
    }

    private void checkMapBoundsCollision(DynamicGameObject dynamicGameObject) {
        int x = (int)dynamicGameObject.getPosition().x;
        int y = (int)dynamicGameObject.getPosition().y;
        int objectWidth = (int)dynamicGameObject.getBounds().getWidth();

        if (x < 16 || y < 16 || x > (MAP_WIDTH + 16) - objectWidth
                || y > (MAP_HEIGHT + 16) - objectWidth) {
            dynamicGameObject.respondWallCollision();
        }
    }

    private void setBricks() {
        bricks = new Array<Brick>();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        for (MapObject cell : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            bricks.add(new Brick(cell, layer));
        }
    }

    private void setSteels() {
        steels = new Array<Steel>();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        for (MapObject cell : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            steels.add(new Steel(cell, layer));
        }
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public void draw(float stateTime) {
        hero.draw(stateTime);
        for (Enemy enemy : enemies) {
            enemy.draw(stateTime);
        }
    }

}
