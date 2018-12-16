package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
    private static final int FROZEN_TIME = 12;
    private static final float EXPLOSION_DURATION = 0.25f;


    public enum State {SPAWNING, NORMAL, SUPER_TANK, WALL_BREAKING,
        SHIELD, FROZEN, EXPLODING, DESTROYED}
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
    protected int lives;
    protected float bulletVelocity;

    private double remainder;
    private int addition;

    protected float deltaTime;

    private long spawningTimer;
    private long explodingTimer;
    private long frozenTimer;

    private Sound deadSound;
    private Sound moveSound;

    public Tank(float x, float y, World world) {
        super(x, y, TANK_WIDTH, TANK_HEIGHT);

        this.spriteBatch = world.getSpriteBatch();
        this.world = world;

        muzzle = new Vector2(x + (TANK_WIDTH / 4.0f), y + TANK_HEIGHT);
        bullets = new Array<Bullet>();

        direction = Direction.UP;

        previousPosition = new Vector2(getPosition());

        deadSound = Gdx.audio.newSound(Gdx.files.internal("sound\\dead.wav"));
        moveSound = Gdx.audio.newSound(Gdx.files.internal("sound\\move.wav"));

        setSpawningState();
    }

    @Override
    public void update() {

        switch (state) {
            case SPAWNING:
                if (SPAWNING_TIME < (System.currentTimeMillis() - spawningTimer) / 1000.0 ) {

                    Array<Tank> tanks = world.getTanks();
                    boolean overlaps = false;

                    for (Tank tank : tanks) {
                        if (tank != this && tank.getBounds().overlaps(this.getBounds())) {
                            overlaps = true;
                        }
                    }

                    if (!overlaps) {
                        state = State.NORMAL;
                    }
                }
                break;

            case FROZEN:
                if (FROZEN_TIME < (System.currentTimeMillis() - frozenTimer) / 1000.0) {
                    setNormalState();
                }

                break;

            case EXPLODING:
                if (EXPLOSION_DURATION < (System.currentTimeMillis() - explodingTimer) / 1000.0) {
                    setDestroyedState();
                    break;
                }
                break;
        }
        updateBullet();
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

    protected void updateBullet() {
        if (bullets.size > 0 && bullets.get(0).getState() == Bullet.State.DESTROYED) {
            bullets.clear();
        }

        if (bullets.size > 0) {
            bullets.get(0).update();
        }
    }

    public void fire() {
        if (bullets.size == 0) {
            Vector2 currMuzzle = getMuzzle();
            bullets.add(new Bullet(currMuzzle.x, currMuzzle.y, this, direction, spriteBatch, bulletVelocity));
        }
    }

    public void stop() {
        if (state != State.SUPER_TANK) {

            state = State.FROZEN;
            if (bullets.size > 0) {
                bullets.get(0).explode();
            }
            frozenTimer = System.currentTimeMillis();
        }
    }

    public void move(Direction direction) {

        if (state != State.FROZEN && world.getState() != World.State.PAUSE) {

            if ((this.direction == Direction.LEFT || this.direction == Direction.RIGHT)
                    && (direction == Direction.DOWN || direction == Direction.UP)) {

                setVerticalRail();
            }

            if ((this.direction == Direction.UP || this.direction == Direction.DOWN)
                    && (direction == Direction.LEFT || direction == Direction.RIGHT)) {

                setHorizontalRail();
            }

            previousPosition.set(getPosition());

            switch (direction) {
                case UP:
                    moveUp();
                    break;
                case DOWN:
                    moveDown();
                    break;
                case LEFT:
                    moveLeft();
                    break;
                case RIGHT:
                    moveRight();
                    break;
            }
        }
    }

    private void moveUp() {
        currAnimation = upMoveAnimation;
        direction = Direction.UP;

        position.y += velocity;
        bounds.y += velocity;
    }

    private void moveDown() {
        currAnimation = downMoveAnimation;
        direction = Direction.DOWN;

        position.y -= velocity;
        bounds.y -= velocity;
    }

    private void moveLeft() {
        currAnimation = leftMoveAnimation;
        direction = Direction.LEFT;

        position.x -= velocity;
        bounds.x -= velocity;
    }

    private void moveRight() {
        currAnimation = rightMoveAnimation;
        direction = Direction.RIGHT;

        position.x += velocity;
        bounds.x += velocity;
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
        deadSound.play();

        explodingTimer = System.currentTimeMillis();
        setState(State.EXPLODING);

        setExplosionAnimation();
        currAnimation = explosionAnimation;
        position.set(getPosition().x - 16, getPosition().y - 16);
    }

    public Vector2 getMuzzle() {
        Vector2 muzzPosition = new Vector2();

        switch (direction) {
            case UP:
                muzzPosition.set(getPosition().x + 12, getPosition().y + TANK_HEIGHT);
                break;
            case DOWN:
                muzzPosition.set(getPosition().x + 12, getPosition().y);
                break;
            case LEFT:
                muzzPosition.set(getPosition().x, getPosition().y + 12);
                break;
            case RIGHT:
                muzzPosition.set(getPosition().x + (TANK_WIDTH), getPosition().y + 12);
                break;
        }

        return muzzPosition;
    }

    protected void goBack() {
        position.set(previousPosition.x, previousPosition.y);
        bounds.setX(previousPosition.x);
        bounds.setY(previousPosition.y);
    }

    public Bullet getBullet() {
        return bullets.size > 0 ? bullets.get(0) : null;
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public Tank.State getState() {
        return state;
    }

    public void setSpawningState() {
        state = State.SPAWNING;
        setSpawningAnimation();
        spawningTimer = System.currentTimeMillis();
    }

    public void setNormalState() {
        state = State.NORMAL;
    }

    public void setDestroyedState() {
        state = State.DESTROYED;
    }

    public void setState(State state) {
        this.state = state;
    }
}
