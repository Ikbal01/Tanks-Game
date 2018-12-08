package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.sprites.Hero;

public class PlayersController {
    private Hero player1;
    private Hero player2;
    private boolean multiplayer;

    public PlayersController(Hero player1, Hero player2, boolean multiplayer) {
        this.player1 = player1;
        this.player2 = player2;
        this.multiplayer = multiplayer;
    }

    public void update() {
        handlePlayer1Input();
        if (multiplayer) {
            handlePlayer2Input();
        }
    }

    public void handlePlayer1Input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player1.fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player1.moveRight();

        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player1.moveLeft();

        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player1.moveUp();

        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player1.moveDown();
        }
    }


    public void handlePlayer2Input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            player2.fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player2.moveRight();

        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player2.moveLeft();

        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player2.moveUp();

        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player2.moveDown();
        }
    }
}
