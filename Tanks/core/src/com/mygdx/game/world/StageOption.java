package com.mygdx.game.world;

import com.mygdx.game.Tanks;
import com.mygdx.game.screens.MenuScreen;

/**
 * Deals with information specific to each stage, such as difficulty, number of enemies, etc.
 */
public class StageOption {

    private Tanks game;
    private MenuScreen.Difficulty difficulty;
    private boolean isMultiplayer;
    private int stage;
    private int[] lives;
    private int[] stars;
    private int[] totalKills;

    public StageOption() {
        lives = new int[2];
        stars = new int[2];
        totalKills = new int[2];
    }

    public int[] getTotalKills() {
        return totalKills;
    }

    public int[] getLives() {
        return lives;
    }

    public int[] getStars() {
        return stars;
    }

    public Tanks getGame() {
        return game;
    }

    public int getStage() {
        return stage;
    }

    public MenuScreen.Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public void setLives(int player1, int player2) {
        this.lives[0] = player1;
        this.lives[1] = player2;
    }

    public void setStars(int player1, int player2) {
        this.stars[0] = player1;
        this.stars[1] = player2;
    }

    public void setTotalKills(int player1, int player2) {
        this.totalKills[0] = player1;
        this.totalKills[1] = player2;
    }

    public void setGame(Tanks game) {
        this.game = game;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public void setDifficulty(MenuScreen.Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setMultiplayer(boolean multiplayer) {
        isMultiplayer = multiplayer;
    }
}
