package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Tanks;
import com.mygdx.game.world.StageOption;

public class StageScreen extends ScreenAdapter {
    private static final int STAGE_DURATION = 1;

    private Tanks game;
    private StageOption stageOption;

    private OrthographicCamera camera;
    private Texture stageTexture;
    private long timer;


    public StageScreen(StageOption stageOption) {
        this.stageOption = stageOption;
        this.game = stageOption.getGame();

        camera = new OrthographicCamera(Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        camera.position.set(Tanks.DESKTOP_SCREEN_WIDTH / 2, Tanks.DESKTOP_SCREEN_HEIGHT / 2, 0);

        stageTexture = new Texture(Gdx.files.internal("stages\\stage_" + stageOption.getStage() + ".png"));

        timer = System.currentTimeMillis();
    }

    public void update() {
        if (STAGE_DURATION < (System.currentTimeMillis() - timer) / 1000) {
            game.setScreen(new BattleScreen(stageOption));
        }

        camera.update();
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.1f, 0.2f , 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();
        game.spriteBatch.draw(stageTexture, 0, 0,
                Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        game.spriteBatch.end();
    }

    @Override
    public void dispose() {
        stageTexture.dispose();
    }
}
