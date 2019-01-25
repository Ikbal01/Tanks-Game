package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.game.Tanks;

/**
 * Renders the Game Over screen effects.
 */
public class GameOverScreen extends ScreenAdapter {
    private static final float SCREEN_DURATION = 3.2f;
    private static final float MILLISECONDS_DELIMITER = 1000.0f;

    private Tanks game;
    private int[] totalKills;
    private boolean isGameEnd;

    private OrthographicCamera camera;

    private Texture gameOverTexture;

    private BitmapFont font;

    private long screenDurationTimer;

    public GameOverScreen(Tanks game, int[] totalKills, boolean isGameEnd) {
        this.game = game;
        this.totalKills = totalKills;
        this.isGameEnd = isGameEnd;

        camera = new OrthographicCamera(Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        camera.position.set(Tanks.DESKTOP_SCREEN_WIDTH / 2f, Tanks.DESKTOP_SCREEN_HEIGHT / 2f, 0);

        font = new BitmapFont();

        setGameOverTexture();

        screenDurationTimer = System.currentTimeMillis();
    }

    public void update() {
        if (SCREEN_DURATION < (System.currentTimeMillis() - screenDurationTimer) / MILLISECONDS_DELIMITER) {
            game.setScreen(new MenuScreen(game));
        }

        camera.update();
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.1f, 0.4f , 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();
        game.spriteBatch.draw(gameOverTexture, 0, 0,
                Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        drawStatusBar();

        game.spriteBatch.end();
    }

    private void drawStatusBar() {
        font.getData().setScale(2, 2);

        font.draw(game.spriteBatch, totalKills[0] + "", 272, 210);
        font.draw(game.spriteBatch, totalKills[1] + "", 632, 210);
    }

    private void setGameOverTexture() {
        if (isGameEnd) {
            gameOverTexture = new Texture(Gdx.files.internal("gameOver\\EndScreen.png"));

        } else {
            gameOverTexture = new Texture(Gdx.files.internal("gameOver\\GameOver.png"));
        }
    }

    @Override
    public void dispose() {
        gameOverTexture.dispose();
    }
}
