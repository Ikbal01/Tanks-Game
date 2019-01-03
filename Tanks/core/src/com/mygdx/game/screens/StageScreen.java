package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.game.Tanks;
import com.mygdx.game.world.StageOption;


public class StageScreen extends ScreenAdapter {
    private static final float STAGE_SCREEN_DURATION = 0.8f;
    private static final float MILLISECONDS_DELIMITER = 1000.0f;

    private Tanks game;
    private StageOption stageOption;

    private OrthographicCamera camera;
    private Texture stageTexture;
    private long timer;

    private BitmapFont font;

    public StageScreen(StageOption stageOption) {
        this.stageOption = stageOption;
        this.game = stageOption.getGame();

        camera = new OrthographicCamera(Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        camera.position.set(Tanks.DESKTOP_SCREEN_WIDTH / 2f, Tanks.DESKTOP_SCREEN_HEIGHT / 2f, 0);

        font = new BitmapFont();
        stageTexture = new Texture(Gdx.files.internal("stages\\stage_" + stageOption.getStage() + ".png"));

        timer = System.currentTimeMillis();
    }

    public void update() {
        if (STAGE_SCREEN_DURATION < (System.currentTimeMillis() - timer) / MILLISECONDS_DELIMITER) {
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

        drawStatusBar();

        game.spriteBatch.end();
    }

    private void drawStatusBar() {
        int kills1 = stageOption.getTotalKills()[0];
        int lives1 = stageOption.getLives()[0];

        font.getData().setScale(2f, 2f);
        font.draw(game.spriteBatch, kills1 + "\n" + lives1, 279, 228);

        int kills2 = stageOption.getTotalKills()[1];
        int lives2 = stageOption.getLives()[1];
        font.draw(game.spriteBatch, kills2 + "\n" + lives2, 644, 228);
    }

    @Override
    public void dispose() {
        stageTexture.dispose();
    }
}
