package com.mygdx.game.enums;

public enum  Color {
    GRAY(8, 0),
    PURPLE(8, 8),
    GREEN(0, 8),
    YELLOW(0, 0);

    // Coordinates from png file
    private int x;
    private int y;

    private Color(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
