package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.MenuScreen;

public class Tanks extends Game {
	public static final int DESKTOP_SCREEN_WIDTH = 880;
	public static final int DESKTOP_SCREEN_HEIGHT = 768;

	public SpriteBatch spriteBatch;

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
	}
}
