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
    public static final int WORLD_WIDTH = 448;
    public static final int WORLD_HEIGHT = 448;

    public static final int MAP_WIDTH = 416;
    public static final int MAP_HEIGHT = 416;

    public static final int BIG_TILE_STANDARD = 32;

    private SpriteBatch batch;
    public static Texture items;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private Hero hero;
    private Array<Brick> bricks;

    public World(SpriteBatch batch) {
        this.batch = batch;
        items = new Texture(Gdx.files.internal("BattleTanksSheet.png"));

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("stage1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        hero = new Hero(0, 32);
        setBricks();
    }

    public void update() {
        handleInput();

        checkBrickCollision(hero.getBullets());
        for (Bullet bullet : hero.getBullets()) {
            checkMapBoundsCollision(bullet);
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
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            hero.moveLeft();
            checkBrickCollision(hero);
            checkMapBoundsCollision(hero);
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            hero.moveUp();
            checkBrickCollision(hero);
            checkMapBoundsCollision(hero);

        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            hero.moveDown();
            checkBrickCollision(hero);
            checkMapBoundsCollision(hero);
        }
    }

    private void checkBrickCollision(Tank tank) {
        for (Brick brick : bricks) {
            if (tank.getBounds().overlaps(brick.getBounds())) {
                tank.respondBrickCollision();
            }
        }
    }

    private void checkBrickCollision(Array<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            if (bulletCollidesWithBricks(bullet)) {
                for (int i = 0; i < bricks.size; i++) {
                    if (bullet.getBigBounds().overlaps(bricks.get(i).getBounds())) {
                        bricks.get(i).destroy();
                        bricks.removeIndex(i);
                        i--;
                    }
                }
                bullet.respondBrickCollision();
            }
        }
    }

    private boolean bulletCollidesWithBricks(Bullet bullet) {
        if (bullet.isExplode()) {
            return false;
        }
        for (Brick brick : bricks) {
            if (bullet.getBounds().overlaps(brick.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private void checkMapBoundsCollision(DynamicGameObject dynamicGameObject) {
        int x = (int)dynamicGameObject.getPosition().x;
        int y = (int)dynamicGameObject.getPosition().y;
        int objectWidth = (int)dynamicGameObject.getBounds().getWidth();

        if (x < 0 || y < 0 || x > MAP_WIDTH - objectWidth || y > MAP_HEIGHT - objectWidth) {
            dynamicGameObject.respondBrickCollision();
        }
    }



    private void setBricks() {
        bricks = new Array<Brick>();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        for (MapObject cell : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            bricks.add(new Brick(cell, layer));
        }
        System.out.println(bricks.size);
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public void draw(float stateTime) {
        hero.draw(batch, stateTime);
    }
}
