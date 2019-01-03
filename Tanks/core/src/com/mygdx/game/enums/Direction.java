package com.mygdx.game.enums;

public enum Direction {
    UP(0),
    LEFT(2),
    DOWN(4),
    RIGHT(6);

    // Coordinate from png file
    private int column;

    Direction(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }
}
