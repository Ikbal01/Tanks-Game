package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Tanks;

public class MenuScreen extends ScreenAdapter {
    private Tanks game;
    private OrthographicCamera camera;

    private Texture[] cases;
    private Texture currentCase;
    private int index;

    private boolean selected;
    private boolean multiplayer;

    public MenuScreen(Tanks game) {
        this.game = game;

        camera = new OrthographicCamera(Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        camera.position.set(Tanks.DESKTOP_SCREEN_WIDTH / 2, Tanks.DESKTOP_SCREEN_HEIGHT / 2, 0);

        cases = new Texture[8];
        setCases();
        index = 0;

        selected = false;
        multiplayer = false;
    }

    public void update() {
        handleInput();
        currentCase = cases[index];
        camera.update();
    }

    private void handleInput() {
        if (!selected) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && index < 1) {
                index++;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && index > 0) {
                index--;
            }
        } else {
            if (!multiplayer) {

                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && index < 4) {
                    index++;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && index > 2) {
                    index--;
                }
            } else {

                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && index < 7) {
                    index++;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && index > 5) {
                    index--;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (!selected) {
                selected = true;
                if (index == 0) {
                    index = 2;
                } else {
                    index = 5;
                    multiplayer = true;
                }
            } else {
                setScreen();
            }
        }
    }

    private void setCases() {
        for (int i = 0; i < cases.length; i++) {
            cases[i] = new Texture(Gdx.files.internal("menuScreen\\menu" + i + ".png"));
        }
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.1f, 0.3f , 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();
        game.spriteBatch.draw(currentCase, 0, 0,
                Tanks.DESKTOP_SCREEN_WIDTH, Tanks.DESKTOP_SCREEN_HEIGHT);
        game.spriteBatch.end();
    }

    @Override
    public void dispose() {
        for (Texture aCase : cases) {
            aCase.dispose();
        }
    }
}
