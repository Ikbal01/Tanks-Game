package com.mygdx.game.enums;

public enum TankCategory {
    FAST(5, 8),
    HEAVY_HEAVY(3, 7),
    HEAVY(7, 6),
    MEDIUM_HEAVY(2, 5),
    MEDIUM(6, 4),
    MEDIUM_LIGHT(1, 3),
    LIGHT_HEAVY(4, 2),
    LIGHT(0, 1);

    private int row;
    private int tankLevel;

    private TankCategory(int row, int tankLevel) {
        this.row = row;
        this.tankLevel = tankLevel;
    }

    public int getRow() {
        return row;
    }

    public int getTankLevel() {
        return tankLevel;
    }
}
