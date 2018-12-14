package com.mygdx.game.enums;

public enum TankCategory {
    FAST(5, 1, 4.5f, 2),
    LIGHT(0, 1, 4.5f, 1),
    LIGHT_HEAVY(4, 1, 4.5f, 1.2f),
    MEDIUM_LIGHT(1, 1, 5, 1.2f),
    MEDIUM(6, 2, 5f, 1.2f),
    MEDIUM_HEAVY(2, 2, 5.5f, 1),
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
    }

    public int getRow() {
        return row;
    }

    public int getArmour() {
        return armour;
    }

    public float getBulletVelocity() {
        return bulletSpeed;
    }

    public float getVelocity() {
        return velocity;
    }
}
