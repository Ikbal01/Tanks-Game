package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.world.World;

public abstract class Tank extends DynamicGameObject {
    public static final int TANK_WIDTH = 32;
    public static final int TANK_HEIGHT = 32;

    private static final int SPAWNING_TIME = 3;
    private static final float EXPLOSION_DURATION = 0.25f;


    public enum State {SPAWNING, NORMAL, SUPER_TANK, WALL_BREAKING, SHIELD, FROZEN, EXPLODING, DESTROYED}
    protected State state;

    protected SpriteBatch spriteBatch;
    protected World world;

    private Animation<TextureRegion> spawningAnimation;

    protected Animation<TextureRegion> upMoveAnimation;
    protected Animation<TextureRegion> downMoveAnimation;
    protected Animation<TextureRegion> leftMoveAnimation;
    protected Animation<TextureRegion> rightMoveAnimation;

    protected Animation<TextureRegion> currAnimation;
    protected Vector2 previousPosition;

    protected Color color;
    protected TankCategory category;
    protected Direction direction;
    protected Vector2 muzzle;

    protected Array<Bullet> bullets;

    protected int armour;
    protected float bulletVelocity;

    private double remainder;
    private int addition;

    protected float deltaTime;

    private long spawningElapsed;
    private long startExplodingTime;


    public Tank(float x, float y, World world) {
        super(x, y, TANK_WIDTH, TANK_HEIGHT);

        this.spriteBatch = world.getSpriteBatch();
        this.world = world;

        muzzle = new Vector2(x + (TANK_WIDTH / 4.0f), y + TANK_HEIGHT);
        bullets = new Array<Bullet>();

        direction = Direction.UP;

        previousPosition = new Vector2(getPosition());

        spawningElapsed = System.currentTimeMillis();

        setSpawningAnimation();
    }

    @Override
    public void update() {

        switch (state) {
            case SPAWNING:
                if (SPAWNING_TIME < (System.currentTimeMillis() - spawningElapsed) / 1000.0 ) {

                    Array<Tank> tanks = world.getAllTanks();

                    state = State.NORMAL;

                    for (Tank tank : tanks) {
                        if (tank != this && this.getBounds().overlaps(tank.getBounds())) {
                            state = State.SPAWNING;
                        }
                    }
                }
            case NORMAL:
                if (bullets.size > 0 && bullets.get(0).getState() == Bullet.State.DESTROYED) {
                    bullets.clear();
                }

                if (bullets.size > 0) {
                    bullets.get(0).update();
                }
                break;

            case EXPLODING:
                if (EXPLOSION_DURATION < (System.currentTimeMillis() - startExplodingTime) / 1000.0) {
                    setState(State.DESTROYED);
                }
                break;
        }
    }

    protected void updateAnimation() {

        upMoveAnimation = getAnimation(Direction.UP);
        downMoveAnimation = getAnimation(Direction.DOWN);
        leftMoveAnimation = getAnimation(Direction.LEFT);
        rightMoveAnimation = getAnimation(Direction.RIGHT);
    }

    private Animation<TextureRegion> getAnimation(Direction direction) {
        TextureRegion[] moveFrames = new TextureRegion[2];
        int x = TANK_WIDTH * color.getX() + TANK_WIDTH * direction.getColmn();
        int y = TANK_HEIGHT * color.getY() + TANK_HEIGHT * category.getRow();

        moveFrames[0] = new TextureRegion(World.items, x, y, TANK_WIDTH, TANK_HEIGHT);
        moveFrames[1] = new TextureRegion(World.items, x + TANK_WIDTH, y, TANK_WIDTH, TANK_HEIGHT);

        return new Animation<TextureRegion>(FRAME_DURATION, moveFrames);
    }

    private void setSpawningAnimation() {
        TextureRegion[] spawning = new TextureRegion[2];

        Texture tempTexture = new Texture(Gdx.files.internal("spawning\\spawn1.png"));
        spawning[0] = new TextureRegion(tempTexture, 32, 32);

        tempTexture = new Texture(Gdx.files.internal("spawning\\spawn2.png"));
        spawning[1] = new TextureRegion(tempTexture, 32, 32);

        spawningAnimation = new Animation<TextureRegion>(0.05f, spawning);
    }

    public void fire() {
        if (bullets.size == 0) {
            bullets.add(new Bullet(muzzle.x, muzzle.y, this, direction, spriteBatch, bulletVelocity));
        }
    }

    public void moveUp() {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            setVerticalRail();
        }
        currAnimation = upMoveAnimation;
        previousPosition.set(getPosition());

        position.y += velocity;
        bounds.y += velocity;
        muzzle.set(getPosition().x + 12, getPosition().y + TANK_HEIGHT);
        direction = Direction.UP;
    }

    public void moveDown() {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            setVerticalRail();
        }
        currAnimation = downMoveAnimation;
        previousPosition.set(getPosition());

        position.y -= velocity;
        bounds.y -= velocity;
        muzzle.set(getPosition().x + 12, getPosition().y);
        direction = Direction.DOWN;
    }

    public void moveLeft() {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            setHorizontalRail();
        }
        currAnimation = leftMoveAnimation;
        previousPosition.set(getPosition());

        position.x -= velocity;
        bounds.x -= velocity;
        muzzle.set(getPosition().x, getPosition().y + 12);
        direction = Direction.LEFT;
    }

    public void moveRight() {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            setHorizontalRail();
        }
        currAnimation = rightMoveAnimation;
        previousPosition.set(getPosition());
        position.x += velocity;
        bounds.x += velocity;
        muzzle.set(getPosition().x + (TANK_WIDTH), getPosition().y + 12);
        direction = Direction.RIGHT;
    }

    protected void setHorizontalRail() {
        remainder = (getPosition().y % World.CELL_SIZE);
        addition = 8;
        if (remainder < (World.CELL_SIZE / 2)) {
            addition = 0;
        }
        bounds.y = ((int)(getBounds().y / World.CELL_SIZE) * World.CELL_SIZE + addition);
        position.y = ((int)(getPosition().y / World.CELL_SIZE) * World.CELL_SIZE + addition);
    }

    protected void setVerticalRail() {
        remainder = (getPosition().x % World.CELL_SIZE);
        addition = 8;
        if (remainder < (World.CELL_SIZE / 2)) {
            addition = 0;
        }
        bounds.x = ((int)(getBounds().x / World.CELL_SIZE) * World.CELL_SIZE + addition);
        position.x = ((int)(getPosition().x / World.CELL_SIZE) * World.CELL_SIZE + addition);
    }

    @Override
    public void draw(float deltaTime)  {
        this.deltaTime = deltaTime;
        TextureRegion currentFrame = currAnimation.getKeyFrame(deltaTime, true);
        spriteBatch.draw(currentFrame, getPosition().x, getPosition().y);

        if (state == State.SPAWNING) {
            TextureRegion currentFr = spawningAnimation.getKeyFrame(deltaTime, true);
            spriteBatch.draw(currentFr, getPosition().x, getPosition().y);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(deltaTime);
        }
    }

    @Override
    protected void setExplosionAnimation() {
        TextureRegion[] explosionFrames = new TextureRegion[2];

        Texture tempTexture = new Texture(Gdx.files.internal("explosion\\explosion1.png"));
        explosionFrames[0] = new TextureRegion(tempTexture, 64, 64);

        tempTexture = new Texture(Gdx.files.internal("explosion\\explosion2.png"));
        explosionFrames[1] = new TextureRegion(tempTexture, 64, 64);

        explosionAnimation = new  Animation<TextureRegion>(0.4f, explosionFrames);
    }

    @Override
    public void explode() {
        startExplodingTime = System.currentTimeMillis();
        setState(State.EXPLODING);

        setExplosionAnimation();
        currAnimation = explosionAnimation;
        position.set(getPosition().x - 16, getPosition().y - 16);
    }

    public Bullet getBullet() {
        return bullets.size > 0 ? bullets.get(0) : null;
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Tank.State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
