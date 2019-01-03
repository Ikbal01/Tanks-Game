package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
    // Тhe sizes of the tank
    public static final int TANK_WIDTH = 32;
    public static final int TANK_HEIGHT = 32;
    // The size of the explosion animation of the tank
    private static final int EXPLOSION_ANIMATION_WIDTH = 64;

    private static final float MOVE_ANIM_FRAME_DURATION = 0.25f;
    private static final float SPAWNING_ANIM_FRAME_DURATION = 0.05f;
    private static final float EXPLOSION_ANIM_FRAME_DURATION = 0.4f;

    // The duration of SPAWNING state
    private static final int SPAWNING_TIME = 3;
    // The duration of FROZEN state
    private static final int FROZEN_TIME = 12;
    // The duration of tank explosion
    private static final float EXPLOSION_TIME = 0.25f;

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

    // Where the bullet is firing
    protected Vector2 muzzle;

    // The bullets that are fired in SUPER_TANK state
    protected Array<Bullet> bullets;

    protected int armour;
    protected int lives;
    protected float bulletVelocity;

    protected float deltaTime;

    private long spawningTimer;
    private long explodingTimer;
    private long frozenTimer;

    private Sound explosionSound;

    public Tank(float x, float y, World world) {
        super(x, y, TANK_WIDTH, TANK_HEIGHT);

        this.spriteBatch = world.getSpriteBatch();
        this.world = world;

        muzzle = new Vector2(x + (TANK_WIDTH / 4.0f), y + TANK_HEIGHT);
        bullets = new Array<Bullet>();

        direction = Direction.UP;

        previousPosition = new Vector2(getPosition());

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sound\\dead.wav"));

        setSpawningState();
    }

    /**
     * Changes the specifications and the direction of
     * the tank according to its status at that time
     */
    @Override
    public void update() {

        switch (state) {
            case SPAWNING:
                if (isElapsed(SPAWNING_TIME, spawningTimer)) {

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
                if (isElapsed(FROZEN_TIME, frozenTimer)) {
                    setNormalState();
                }

                break;

            case EXPLODING:
                if (isElapsed(EXPLOSION_TIME, explodingTimer)) {
                    setDestroyedState();
                    break;
                }
                break;
        }
        updateBullet();
    }

    /**
     * Updates animations if there is a change
     * in specifications of the tank
     */
    protected void updateAnimation() {

        upMoveAnimation = getAnimation(Direction.UP);
        downMoveAnimation = getAnimation(Direction.DOWN);
        leftMoveAnimation = getAnimation(Direction.LEFT);
        rightMoveAnimation = getAnimation(Direction.RIGHT);
    }

    /**
     * Generates an animation depending on category and direction of the tank
     *
     * @param direction direction of movement
     * @return generated animation
     */
    private Animation<TextureRegion> getAnimation(Direction direction) {
        TextureRegion[] moveFrames = new TextureRegion[2];
        int x = TANK_WIDTH * color.getX() + TANK_WIDTH * direction.getColumn();
        int y = TANK_HEIGHT * color.getY() + TANK_HEIGHT * category.getRow();

        moveFrames[0] = new TextureRegion(World.items, x, y, TANK_WIDTH, TANK_HEIGHT);
        moveFrames[1] = new TextureRegion(World.items, x + TANK_WIDTH, y, TANK_WIDTH, TANK_HEIGHT);

        return new Animation<TextureRegion>(MOVE_ANIM_FRAME_DURATION, moveFrames);
    }

    protected void updateBullet() {
        if (bullets.size > 0 && bullets.get(0).getState() == Bullet.State.DESTROYED) {
            bullets.clear();
        }

        if (bullets.size > 0) {
            bullets.get(0).update();
        }
    }

    /**
     * Shoots (creates) a bullet if this tank does not have other shot bullets
     */
    public void fire() {
        if (bullets.size == 0) {

            Vector2 currMuzzle = getMuzzle();
            bullets.add(new Bullet(currMuzzle.x, currMuzzle.y, this, direction, spriteBatch, bulletVelocity));
        }
    }

    /**
     * Freezes the tank (can not move) for a while. If there is a
     * bullet shot by this tank explodes.
     */
    public void stop() {
        if (state != State.SUPER_TANK) {

            state = State.FROZEN;
            if (bullets.size > 0) {
                bullets.get(0).explode();
            }
            frozenTimer = System.currentTimeMillis();
        }
    }

    /**
     * Sets appropriate position coordinates if direction is changed
     *
     * @param direction the direction of movement
     */
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

            previousPosition.set(position);

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
    public void explode() {
        explosionSound.play();

        explodingTimer = System.currentTimeMillis();

        state = State.EXPLODING;
        explosionAnimation = generateAnimation("explosion\\explosion", 2,
                EXPLOSION_ANIMATION_WIDTH, EXPLOSION_ANIM_FRAME_DURATION);
        currAnimation = explosionAnimation;

        position.set(position.x - (TANK_WIDTH / 2f), position.y - TANK_HEIGHT / 2f);
    }

    /**
     * Returns to its previous position
     */
    protected void goBack() {
        position.set(previousPosition.x, previousPosition.y);
        bounds.setX(previousPosition.x);
        bounds.setY(previousPosition.y);
    }

    /**
     * Aligns the tank correctly with respect to the rails to ease change of horizontal movement.
     */
    protected void setHorizontalRail() {
        double remainder = (position.y % World.CELL_SIZE);
        int addition = 8;
        if (remainder < (World.CELL_SIZE / 2)) {
            addition = 0;
        }
        bounds.y = ((int)(bounds.y / World.CELL_SIZE) * World.CELL_SIZE + addition);
        position.y = ((int)(position.y / World.CELL_SIZE) * World.CELL_SIZE + addition);
    }

    /**
     * Aligns the tank correctly with respect to the rails to ease change of vertical movement.
     */
    protected void setVerticalRail() {
        double remainder = (position.x % World.CELL_SIZE);
        int addition = 8;
        if (remainder < (World.CELL_SIZE / 2)) {
            addition = 0;
        }
        bounds.x = ((int)(bounds.x / World.CELL_SIZE) * World.CELL_SIZE + addition);
        position.x = ((int)(position.x / World.CELL_SIZE) * World.CELL_SIZE + addition);
    }

    public void setSpawningState() {
        state = State.SPAWNING;
        spawningAnimation = generateAnimation("spawning\\spawn", 2,
                TANK_WIDTH, SPAWNING_ANIM_FRAME_DURATION);
        spawningTimer = System.currentTimeMillis();
    }

    public void setNormalState() {
        state = State.NORMAL;
    }

    public void setDestroyedState() {
        state = State.DESTROYED;
    }

    /**
     * Calculates the position in which bullet will be created
     *
     * @return the position in which the bullet will be created
     */
    public Vector2 getMuzzle() {
        Vector2 muzzPosition = new Vector2();

        int half = (TANK_WIDTH - Bullet.BULLET_WIDTH) / 2;

        switch (direction) {
            case UP:
                muzzPosition.set(position.x + half, position.y + TANK_HEIGHT);
                break;
            case DOWN:
                muzzPosition.set(position.x + half, position.y);
                break;
            case LEFT:
                muzzPosition.set(position.x, position.y + half);
                break;
            case RIGHT:
                muzzPosition.set(position.x + (TANK_WIDTH), position.y + half);
                break;
        }

        return muzzPosition;
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
}
