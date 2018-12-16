package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.game.Tanks;
import com.mygdx.game.world.World;

public class GameOverScreen extends ScreenAdapter {
    private Tanks game;
    private boolean isMultiplayer;
    private int[] totalKills;

    private OrthographicCamera camera;
    private Texture gameOverTexture;
    private BitmapFont font;


    public GameOverScreen(Tanks game, int[] totalKills, boolean isMultiplayer) {
        this.game = game;
        this.totalKills = totalKills;
        this.isMultiplayer = isMultiplayer;

        camera = new OrthographicCamera(Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        camera.position.set(Tanks.DESKTOP_SCREEN_WIDTH / 2, Tanks.DESKTOP_SCREEN_HEIGHT / 2, 0);

        gameOverTexture = new Texture(Gdx.files.internal("gameOver\\gameOver.png"));

        font = new BitmapFont();
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
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
        font.draw(game.spriteBatch,"Player 1\nTotal kills: " + totalKills[0], 250, 300);

        if (isMultiplayer) {
            font.draw(game.spriteBatch,"Player 2\nTotal kills: " + totalKills[1], 500, 300);
        }
    }

    @Override
    public void dispose() {
        gameOverTexture.dispose();
    }
}
