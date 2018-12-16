package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

    private BitmapFont font;

    private World world;

    private float stateTime;

    public int horizontalCellCount;
    public int verticalCellCount;

    public BattleScreen(StageOption stageOption) {

        this.game = stageOption.getGame();
        camera = new OrthographicCamera();
        viewport = new FitViewport(World.WORLD_WIDTH, World.WORLD_HEIGHT, camera);

        font = new BitmapFont();

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

        drawStatusBar();

        game.spriteBatch.end();
    }

    private void drawStatusBar() {
        int kills1 = world.getPlayer1().getKills();
        int lives1 = world.getPlayer1().getLives();

        font.getData().setScale(1.0f, 1.0f);
        font.draw(game.spriteBatch,"Player 1\nkills: " + kills1 + "\nlives: " + lives1, 436, 420);

        if (world.isMultiplayer()) {
            int kills2 = world.getPlayer2().getKills();
            int lives2 = world.getPlayer2().getLives();

            font.draw(game.spriteBatch,"Player 2\nkills: " + kills2 + "\nlives: " + lives2, 436, 350);
        }

        int remainingEnemies = world.getStageEnemyCount() - world.getDestroyedEnemyCount();
        font.getData().setScale(0.9f, 0.9f);

        font.draw(game.spriteBatch, "Enemies: " + remainingEnemies, 436, 280);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
