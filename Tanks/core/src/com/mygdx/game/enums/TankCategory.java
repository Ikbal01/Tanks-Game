package com.mygdx.game.enums;

public enum TankCategory {
<<<<<<< HEAD
    LIGHT(0, 1, 4.5f, 1),
    FAST(5, 1, 4.5f, 2),
    LIGHT_HEAVY(4, 1, 4.5f, 1.2f),
    MEDIUM_LIGHT(1, 1, 5, 1.2f),
    MEDIUM(6, 2, 5f, 1.2f),
    MEDIUM_HEAVY(2, 3, 5.5f, 1),
    HEAVY(7, 3, 5.5f, 0.9f),
    HEAVY_HEAVY(3, 4, 6, 0.7f);

    private int row;
    private int armour;
    private float bulletSpeed;
    private float velocity;

    private TankCategory(int row, int armour, float bulletSpeed, float velocity) {
        this.row = row;
        this.armour = armour;
        this.bulletSpeed = bulletSpeed;
        this.velocity = velocity;
=======
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
>>>>>>> 9caec4292eb64338f8139d248e2bd8a7466f8693
    }

    public int getRow() {
        return row;
    }

<<<<<<< HEAD
    public int getArmour() {
        return armour;
    }

    public float getBulletVelocity() {
        return bulletSpeed;
    }

    public float getVelocity() {
        return velocity;
=======
    public int getTankLevel() {
        return tankLevel;
>>>>>>> 9caec4292eb64338f8139d248e2bd8a7466f8693
    }
}
