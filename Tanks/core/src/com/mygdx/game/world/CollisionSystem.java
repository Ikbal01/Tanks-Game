package com.mygdx.game.world;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.sprites.*;
import com.mygdx.game.treasures.Treasure;

import static com.mygdx.game.world.World.MAP_HEIGHT;
import static com.mygdx.game.world.World.MAP_WIDTH;

public class CollisionSystem {
    private World world;
    private boolean isMultiplayer;

    private Hero player1;
    private Hero player2;
    private Array<Enemy> enemies;

    private Array<Steel> steelBlocks;
    private Array<Brick> bricks;
    private Fortress fortress;
    private Treasure treasure;


    public CollisionSystem(World world) {
        this.world = world;
        isMultiplayer = world.isMultiplayer();

        player1 = world.getPlayer1();
        if (isMultiplayer) {
            player2 = world.getPlayer2();
        }

        enemies = world.getEnemies();

        steelBlocks = world.getSteelBlocks();
        bricks = world.getBricks();
        fortress = world.getFortress();

        treasure = world.getTreasure();
    }

    public void update() {
        if (player1.getState() != Tank.State.DESTROYED) {
            verifyHero(player1);
        }
        if (isMultiplayer && player2.getState() != Tank.State.DESTROYED) {
            verifyHero(player2);
        }
        verifyEnemies();

        treasure = world.getTreasure();
    }

    private void verifyHero(Hero hero) {
        verifyBrickCollision(hero);
        verifySteelCollision(hero);
        verifyMapBoundsCollision(hero);
        verifyTreasureCollision(hero);
        verifyFortressCollision(hero);

        verifyTankCollision(hero);
        verifyHeroBullets(hero);
    }

    private void verifyHeroBullets(Hero hero) {
        for (Bullet bullet : hero.getBullets()) {
            if (bullet.getState() == Bullet.State.FLYING) {

                verifyBrickCollision(bullet);
                verifySteelCollision(bullet);
                verifyMapBoundsCollision(bullet);
                verifyBulletCollision(bullet);
                verifyFortressCollision(bullet);

                if (bullet.getState() != Bullet.State.EXPLODING) {
                    verifyEnemyCollision(bullet);
                }
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
                verifyFortressCollision(enemy);
            }

            if (enemy.getBullet() != null && enemy.getBullet().getState() == Bullet.State.FLYING) {
                Bullet bullet = enemy.getBullet();

                verifyBrickCollision(bullet);
                verifySteelCollision(bullet);
                verifyMapBoundsCollision(bullet);
                verifyBulletCollision(bullet);
                verifyFortressCollision(bullet);
                verifyHeroCollision(bullet);
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
        for (Steel steel : steelBlocks) {
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

    private void verifyFortressCollision(Tank tank) {
        if (tank.getBounds().overlaps(fortress.getBounds())) {
            tank.respondTankCollision(tank);
        }
    }

    private void verifyTankCollision(Tank tank) {
        for (Enemy enemy : enemies) {
            verifyTankCollision(tank, enemy);
        }

        verifyTankCollision(tank, player1);
        if (isMultiplayer) {
            verifyTankCollision(tank, player2);
        }
    }

    private void verifyTankCollision(Tank tankOne, Tank tankTwo) {
        if (tankOne != tankTwo && tankOne.getBounds().overlaps(tankTwo.getBounds())) {
            tankOne.respondTankCollision(tankTwo);
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
                    hero.setWallBreakingMod();
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
        if (bullet.getState() != Bullet.State.FLYING) {
            return;
        }
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

    private void verifyHeroCollision(Bullet bullet) {
        verifyTankCollision(player1, bullet);
        if (isMultiplayer) {
            verifyTankCollision(player2, bullet);
        }
    }

    private void verifyEnemyCollision(Bullet bullet) {
        for (Enemy enemy : enemies) {
            verifyTankCollision(enemy, bullet);
        }
    }

    private void verifyTankCollision(Tank tank, Bullet bullet) {
        if (bullet.getBounds().overlaps(tank.getBounds())) {
            bullet.respondTankCollision(tank);
            tank.respondBulletCollision(bullet);
        }
    }

    private void verifyBulletCollision(Bullet bullet) {
        for (Enemy enemy : enemies) {
            verifyBulletCollision(enemy, bullet);
        }

        verifyBulletCollision(player1, bullet);
        if (isMultiplayer) {
            verifyBulletCollision(player2, bullet);
        }
    }

    private void verifyBulletCollision(Tank tank, Bullet bullet) {
        if (tank.getBullet() != null && bullet != tank.getBullet()
                && bullet.getBounds().overlaps(tank.getBullet().getBounds())) {

            bullet.respondBulletCollision(tank.getBullet());
            tank.getBullet().respondBulletCollision(bullet);
        }
    }

    private void verifyFortressCollision(Bullet bullet) {
        if (bullet.getState() != Bullet.State.FLYING) {
            return;
        }
        if (bullet.getBounds().overlaps(fortress.getBounds())) {
            world.setState(World.State.GAME_OVER);
        }
    }
}
