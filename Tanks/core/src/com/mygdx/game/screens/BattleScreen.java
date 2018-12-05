package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Tanks;
import com.mygdx.game.world.World;

public class BattleScreen implements Screen {
    private Tanks game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;

    private float stateTime;

    public int horizontalCellCount;
    public int verticalCellCount;

    public BattleScreen(Tanks game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(World.WORLD_WIDTH, World.WORLD_HEIGHT, camera);

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        horizontalCellCount = 26;
        verticalCellCount = 26;

        world = new World(game.batch);
    }


    public void update() {
        world.update();
        camera.update();
        world.getRenderer().setView(camera);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.1f, 0.3f , 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.getRenderer().render();
        game.batch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();

        game.batch.begin();
        world.draw(stateTime);
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
