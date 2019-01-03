package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.screens.MenuScreen;
import com.mygdx.game.sprites.Hero;
import com.mygdx.game.sprites.Tank;

/**
 * Handles keyboard input.
 */
public class GameController {
    private World world;

    private Hero player1;
    private Hero player2;

    private boolean isMultiplayer;
    private boolean isPaused;

    public GameController(World world) {
        this.world = world;

        player1 = world.getPlayer1();
        player2 = world.getPlayer2();
        isMultiplayer = world.isMultiplayer();

        isPaused = false;
    }

    public void update() {
        handlePlayer1Input();
        if (isMultiplayer) {
            handlePlayer2Input();
        }

        handlePauseMenuButtonInput();
        handleTankModeInput();
    }

    /**
     * Sets the tank mode for both players.
     */
    private void handleTankModeInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            player1.setSuperTankMode();
            player2.setSuperTankMode();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            player1.setNormalMode();
            player2.setNormalMode();
        }
    }

    /**
     * Pauses and unpauses the game.
     */
    private void handlePauseMenuButtonInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            world.getMusic().stop();
            world.getGame().setScreen(new MenuScreen(world.getGame()));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (isPaused) {
                world.setNormalState();
                isPaused = false;
            } else {
                world.setPauseState();
                isPaused = true;
            }
        }
    }

    private void handlePlayer1Input() {
        if (player1.getState() == Tank.State.EXPLODING) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player1.fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player1.move(Direction.RIGHT);

        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player1.move(Direction.LEFT);

        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player1.move(Direction.UP);

        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player1.move(Direction.DOWN);
        }
    }


    private void handlePlayer2Input() {
        if (player1.getState() == Tank.State.EXPLODING) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            player2.fire();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player2.move(Direction.RIGHT);

        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player2.move(Direction.LEFT);

        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player2.move(Direction.UP);

        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player2.move(Direction.DOWN);
        }
    }
}
