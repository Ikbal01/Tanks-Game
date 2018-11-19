package com.mygdx.game.enums;

public enum Direction {
    UP(0),
    LEFT(2),
    DOWN(4),
    RIGHT(6);

    private int colmn;

    private Direction(int colmn) {
        this.colmn = colmn;
    }

    public int getColmn() {
        return colmn;
    }
}
