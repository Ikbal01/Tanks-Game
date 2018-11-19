package com.mygdx.game.enums;

public enum TankCategory {
    HEAVY_HEAVY(3),
    HEAVY(7),
    MEDIUM_HEAVY(2),
    MEDIUM(6),
    MEDIUM_LIGHT(1),
    FAST(5),
    LIGHT_HEAVY(4),
    LIGHT(0);

    private int row;

    private TankCategory(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }
}
