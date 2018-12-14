package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Tanks;
import com.mygdx.game.sprites.Fortress;
import com.mygdx.game.world.StageOption;
import com.mygdx.game.world.World;

public class BattleScreen extends ScreenAdapter {
    private Tanks game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;

    private float stateTime;

    public int horizontalCellCount;
    public int verticalCellCount;

    public BattleScreen(StageOption stageOption) {

        this.game = stageOption.getGame();
        camera = new OrthographicCamera();
        viewport = new FitViewport(World.WORLD_WIDTH, World.WORLD_HEIGHT, camera);

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        horizontalCellCount = 26;
        verticalCellCount = 26;

        world = new World(stageOption);
    }


    public void update() {
        world.update();
        camera.update();
        world.getRenderer().setView(camera);
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.1f, 0.3f , 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int[] mainLayers = {0, 1, 2, 3, 4};
        world.getRenderer().render(mainLayers);

        if (world.getFortress().getState() == Fortress.State.DEFENCE) {
            world.getRenderer().render(new int[] {5});
        }

        game.spriteBatch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();

        game.spriteBatch.begin();
        world.draw(stateTime);
        game.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
