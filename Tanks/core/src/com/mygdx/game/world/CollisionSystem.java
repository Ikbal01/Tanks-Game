package com.mygdx.game.world;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.sprites.*;
import com.mygdx.game.treasures.Treasure;

import static com.mygdx.game.world.World.MAP_HEIGHT;
import static com.mygdx.game.world.World.MAP_WIDTH;

/**
 * Deals with collision detection and response for all game objects.
 */
public class CollisionSystem {
    private static final int MAP_BORDER_WIDTH = 16;

    private World world;

    private Hero player1;
    private Hero player2;
    private Array<Tank> tanks;
    private Array<GameObject> staticGameObjects;
    private Treasure treasure;

    public CollisionSystem(World world) {
        this.world = world;

        player1 = world.getPlayer1();
        player2 = world.getPlayer2();
        tanks = world.getTanks();

        staticGameObjects = world.getStaticGameObjects();
        treasure = world.getTreasure();
    }

    public void update() {
        tanks = world.getTanks();

        for (int i = 0; i < tanks.size; i++) {
            Tank tank = tanks.get(i);

            verifyMapBoundsCollision(tank);
            verifyStaticObjectsCollision(tank);
            verifyTanksCollision(tank);

            for (Bullet bullet : tank.getBullets()) {
                verifyMapBoundsCollision(bullet);
                verifyStaticObjectsCollision(bullet);
                verifyTanksCollision(bullet);
                verifyBulletCollision(bullet);
            }
        }

        if (player1.getState() != Tank.State.DESTROYED) {
            verifyTreasureCollision(player1);
        }

        if (player2.getState() != Tank.State.DESTROYED) {
            verifyTreasureCollision(player2);
        }

        treasure = world.getTreasure();
    }

    /**
     * Keeps game objects within map boundaries.
     * @param dynamicGameObject
     */
    private void verifyMapBoundsCollision(DynamicGameObject dynamicGameObject) {
        int x = (int)dynamicGameObject.getPosition().x;
        int y = (int)dynamicGameObject.getPosition().y;

        int objectWidth = (int)dynamicGameObject.getBounds().getWidth();

        if (x < MAP_BORDER_WIDTH || y < MAP_BORDER_WIDTH || x > (MAP_WIDTH + MAP_BORDER_WIDTH) - objectWidth
                || y > (MAP_HEIGHT + MAP_BORDER_WIDTH) - objectWidth) {
            dynamicGameObject.respondWallCollision();
        }
    }

    private void verifyTreasureCollision(Hero hero) {
        if (treasure != null && treasure.getState() == Treasure.State.ACTIVE
                && hero.getBounds().overlaps(treasure.getBounds())) {

            switch (treasure.getType()) {
                case ENEMY_KILLER:
                    world.killEnemies();
                    break;

                case TIME_STOPPER:
                    world.stopTime();
                    break;

                case EXTRA_LIFE:
                    hero.addExtraLife();
                    break;

                case WALL_BREAKER:
                    hero.setWallBreakingMod();
                    break;

                case BASE_DEFENDER:
                    world.getFortress().setDefenceMode();
                    break;

                case TANK_IMPROVER:
                    hero.improve();
                    break;

                case SHIELD:
                    hero.setShieldMod();
                    break;
            }

            treasure.respondTankCollision(hero);
        }
    }

    private void verifyTanksCollision(DynamicGameObject dynamicGameObject) {
        for (Tank tank : tanks) {
            if (dynamicGameObject != tank && dynamicGameObject != tank.getBullet()
                    && dynamicGameObject.getBounds().overlaps(tank.getBounds())) {

                if (dynamicGameObject instanceof Bullet) {
                    tank.respondBulletCollision((Bullet) dynamicGameObject);

                } else {
                    tank.respondTankCollision((Tank) dynamicGameObject);
                }
                dynamicGameObject.respondTankCollision(tank);
                break;
            }
        }
    }

    private void verifyStaticObjectsCollision(Tank tank) {
        for (GameObject statGameObj : staticGameObjects) {

            if (tank.getBounds().overlaps(statGameObj.getBounds())) {

                statGameObj.respondTankCollision(tank);
                tank.respondWallCollision();
                break;
            }
        }
    }

    private void verifyBulletCollision(Bullet bullet) {
        for (Tank tank : tanks) {
            Bullet tankBullet = tank.getBullet();

            if (tankBullet != null && tankBullet != bullet
                    && tankBullet.getBounds().overlaps(bullet.getBounds())) {

                tankBullet.respondBulletCollision(bullet);
                bullet.respondBulletCollision(tankBullet);
                break;
            }
        }
    }

    private void verifyStaticObjectsCollision(Bullet bullet) {
        if (bulletCollidesWithStaticObjs(bullet)) {

            for (GameObject statGameObj : staticGameObjects) {

                if (bullet.getBigBounds().overlaps(statGameObj.getBounds())) {
                    statGameObj.respondBulletCollision(bullet);
                }
            }
            bullet.respondWallCollision();
        }
    }

    private boolean bulletCollidesWithStaticObjs(Bullet bullet) {
        if (bullet.getState() == Bullet.State.FLYING) {
            for (GameObject statGameObj : staticGameObjects) {

                if (!(statGameObj instanceof Water) && bullet.getBounds().overlaps(statGameObj.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }
}
