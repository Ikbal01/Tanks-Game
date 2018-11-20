package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Tanks;
import com.mygdx.game.sprites.Hero;

public class BattleScreen implements Screen {
    private static final int WORLD_WIDTH = 544;
    private static final int WORLD_HEIGHT = 544;

    private Tanks game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public static Texture items;

    private Hero hero;
    private float stateTime;

    public BattleScreen(Tanks game) {

        items = new Texture(Gdx.files.internal("BattleTanksSheet.png"));


        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("firstMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        hero = new Hero(0, 0);

    }

    @Override
    public void show() {

    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            hero.fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            hero.moveRight();
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            hero.moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            hero.moveUp();
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            hero.moveDown();
        }
    }

    public void update() {
        handleInput();
        camera.update();
        renderer.setView(camera);
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(1, 0 , 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        game.batch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();

        game.batch.begin();
        hero.draw(game.batch, stateTime);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
