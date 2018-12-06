package com.mygdx.game.world;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.sprites.*;
import com.mygdx.game.treasures.Treasure;

import static com.mygdx.game.world.World.MAP_HEIGHT;
import static com.mygdx.game.world.World.MAP_WIDTH;

public class CollisionSystem {
    private World world;

    private Hero hero;
    private Array<Enemy> enemies;

    private Array<Steel> steels;
    private Array<Brick> bricks;

    private Treasure treasure;

    public CollisionSystem(World world) {
        this.world = world;

        hero = world.getHero();
        enemies = world.getEnemies();

        steels = world.getSteelBlocks();
        bricks = world.getBricks();

        treasure = world.getTreasure();
    }

    public void update() {
        verifyHero();
        verifyEnemies();

        treasure = world.getTreasure();
    }

    private void verifyHero() {
        verifyTankCollision(hero);
        verifyBrickCollision(hero);
        verifySteelCollision(hero);
        verifyMapBoundsCollision(hero);
        verifyTreasureCollision(hero);

        if (hero.getBullet() != null && hero.getBullet().getState() == Bullet.State.FLYING) {
            verifyBrickCollision(hero.getBullet());
            verifySteelCollision(hero.getBullet());
            verifyMapBoundsCollision(hero.getBullet());
            verifyBulletCollision(hero.getBullet());

            if (hero.getBullet().getState() != Bullet.State.EXPLODING) {
                verifyEnemyCollision(hero.getBullet());
            }
        }
    }

    private void verifyEnemies() {
        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);

            if (enemy.getState() != Tank.State.EXPLODING) {
                verifyTankCollision(enemy);
                verifyBrickCollision(enemy);
                verifyMapBoundsCollision(enemy);
                verifySteelCollision(enemy);
            }

            if (enemy.getBullet() != null && enemy.getBullet().getState() == Bullet.State.FLYING) {

                verifyBrickCollision(enemy.getBullet());
                verifySteelCollision(enemy.getBullet());
                verifyMapBoundsCollision(enemy.getBullet());
                verifyBulletCollision(enemy.getBullet());
            }
        }
    }

    private void verifyMapBoundsCollision(DynamicGameObject dynamicGameObject) {
        int x = (int)dynamicGameObject.getPosition().x;
        int y = (int)dynamicGameObject.getPosition().y;

        int objectWidth = (int)dynamicGameObject.getBounds().getWidth();

        if (x < 16 || y < 16 || x > (MAP_WIDTH + 16) - objectWidth
                || y > (MAP_HEIGHT + 16) - objectWidth) {
            dynamicGameObject.respondMapBoundsCollision();
        }
    }

    private void verifySteelCollision(DynamicGameObject dynamicGameObject) {
        for (Steel steel : steels) {
            if (dynamicGameObject.getBounds().overlaps(steel.getBounds())) {
                dynamicGameObject.respondSteelCollision();
                break;
            }
        }
    }

    private void verifyBrickCollision(Tank tank) {
        for (Brick brick : bricks) {
            if (tank.getBounds().overlaps(brick.getBounds())) {

                if (tank.getState() != Tank.State.WALL_BREAKING) {
                    tank.respondBrickCollision();
                    break;
                } else {
                    brick.destroy();
                }
            }
        }
    }

    private void verifyTankCollision(Tank tank) {
        for (Enemy enemy : enemies) {
            if (tank != enemy) {
                if (tank.getBounds().overlaps(enemy.getBounds())) {
                    tank.respondTankCollision();
                    break;
                }
            }
        }

        if (tank != hero) {
            if (tank.getBounds().overlaps(hero.getBounds())) {
                tank.respondTankCollision();
            }
        }
    }

    private void verifyTreasureCollision(Hero hero) {
        if (treasure != null && hero.getBounds().overlaps(treasure.getBounds())) {
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
                    hero.addWallBreakingMod();
                    break;

                case BASE_DEFENDER:
                    world.defendBase();
                    break;

                case TANK_IMPROVER:
                    hero.improve();
                    break;

                case SHIELD:
                    hero.addShield();
                    break;
            }

            treasure.respondTankCollision();
        }
    }

    private void verifyBrickCollision(Bullet bullet) {
        if (bulletCollidesWithBricks(bullet)) {

            for (Brick brick : bricks) {

                if (bullet.getBigBounds().overlaps(brick.getBounds())) {
                    brick.destroy();
                }
            }
            bullet.respondBrickCollision();
        }
    }

    private boolean bulletCollidesWithBricks(Bullet bullet) {
        if (bullet.getState() != Bullet.State.EXPLODING) {
            for (Brick brick : bricks) {
                if (bullet.getBounds().overlaps(brick.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void verifyEnemyCollision(Bullet bullet) {
        for (Enemy enemy : enemies) {
            if (bullet.getBounds().overlaps(enemy.getBounds())) {
                bullet.respondTankCollision();
                enemy.respondBulletCollision();
                break;
            }
        }
    }

    private void verifyBulletCollision(Bullet bullet) {
        for (Enemy enemy : enemies) {
            if (enemy.getBullet() != null && bullet != enemy.getBullet()) {
                if (bullet.getBounds().overlaps(enemy.getBullet().getBounds())) {
                    bullet.respondBulletCollision();
                    enemy.getBullet().respondBulletCollision();
                }
            }
        }

        if (hero.getBullet() != null && bullet != hero.getBullet()) {
            if (bullet.getBounds().overlaps(hero.getBullet().getBounds())) {
                bullet.respondBulletCollision();
                hero.getBullet().respondBulletCollision();
            }
        }
    }

}
