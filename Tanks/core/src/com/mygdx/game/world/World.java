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
import com.mygdx.game.sprites.Brick;
import com.mygdx.game.sprites.Bullet;
import com.mygdx.game.sprites.Hero;

public class World {
    public static final int WORLD_WIDTH = 448;
    public static final int WORLD_HEIGHT = 448;

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

        for (Brick brick : bricks) {
            if (hero.getBounds().overlaps(brick.getBounds())) {
                hero.moveBack();
            }
        }
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            hero.fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            hero.moveRight();
            System.out.printf("%s %s\n", hero.getPosition().x, hero.getPosition().y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            hero.moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            hero.moveUp();
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            hero.moveDown();
        }


    }

    public void setBricks() {
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
