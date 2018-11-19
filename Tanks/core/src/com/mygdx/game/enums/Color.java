package com.mygdx.game.enums;

public enum  Color {
    YELLOW(0, 0),
    GREEN(0, 8),
    PURPLE(8, 8),
    GRAY(8, 0);

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
