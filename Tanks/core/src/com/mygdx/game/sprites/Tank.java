package com.mygdx.game.sprites;

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

    protected SpriteBatch spriteBatch;

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

    protected Bullet bullet;

    public double remainder;
    public int addition;

    public Tank(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, TANK_WIDTH, TANK_HEIGHT);
        this.spriteBatch = spriteBatch;

        muzzle = new Vector2(x + (TANK_WIDTH / 4.0f), y + TANK_HEIGHT);

        color = Color.GREEN;
        category = TankCategory.LIGHT;
        direction = Direction.UP;

        upMoveAnimation = getAnimation(color, category, Direction.UP);
        downMoveAnimation = getAnimation(color, category, Direction.DOWN);
        leftMoveAnimation = getAnimation(color, category, Direction.LEFT);
        rightMoveAnimation = getAnimation(color, category, Direction.RIGHT);

        currAnimation = upMoveAnimation;

    }

    private Animation<TextureRegion> getAnimation(Color color, TankCategory category, Direction direction) {
        TextureRegion[] moveFrames = new TextureRegion[2];
        int x = TANK_WIDTH * color.getX() + TANK_WIDTH * direction.getColmn();
        int y = TANK_HEIGHT * color.getY() + TANK_HEIGHT * category.getRow();

        moveFrames[0] = new TextureRegion(World.items, x, y, TANK_WIDTH, TANK_HEIGHT);
        moveFrames[1] = new TextureRegion(World.items, x + TANK_WIDTH, y, TANK_WIDTH, TANK_HEIGHT);

        return new Animation<TextureRegion>(FRAME_DURATION, moveFrames);
    }

    public void fire() {
        if (bullet == null) {
            bullet = new Bullet(muzzle.x, muzzle.y, direction, spriteBatch);
        }
    }

    public void moveUp() {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            setVerticalRail();
        }
        currAnimation = upMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().y += velocity;
        getBounds().y += velocity;
        muzzle.set(getPosition().x + 12, getPosition().y + TANK_HEIGHT);
        direction = Direction.UP;
    }

    public void moveDown() {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            setVerticalRail();
        }
        currAnimation = downMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().y -= velocity;
        getBounds().y -= velocity;
        muzzle.set(getPosition().x + 12, getPosition().y);
        direction = Direction.DOWN;
    }

    public void moveLeft() {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            setHorizontalRail();
        }
        currAnimation = leftMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().x -= velocity;
        getBounds().x -= velocity;
        muzzle.set(getPosition().x, getPosition().y + 12);
        direction = Direction.LEFT;
    }

    public void moveRight() {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            setHorizontalRail();
        }
        currAnimation = rightMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().x += velocity;
        getBounds().x += velocity;
        muzzle.set(getPosition().x + (TANK_WIDTH), getPosition().y + 12);
        direction = Direction.RIGHT;
    }

    protected void setHorizontalRail() {
        remainder = (getPosition().y % World.CELL_SIZE);
        addition = 8;
        if (remainder < (World.CELL_SIZE / 2)) {
            addition = 0;
        }
        getBounds().y = ((int)(getBounds().y / World.CELL_SIZE) * World.CELL_SIZE + addition);
        getPosition().y = ((int)(getPosition().y / World.CELL_SIZE) * World.CELL_SIZE + addition);
    }

    protected void setVerticalRail() {
        remainder = (getPosition().x % World.CELL_SIZE);
        addition = 8;
        if (remainder < (World.CELL_SIZE / 2)) {
            addition = 0;
        }
        getBounds().x = ((int)(getBounds().x / World.CELL_SIZE) * World.CELL_SIZE + addition);
        getPosition().x = ((int)(getPosition().x / World.CELL_SIZE) * World.CELL_SIZE + addition);
    }

    public Bullet getBullet() {
        return bullet;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTankCategory(TankCategory category) {
        this.category = category;
    }
}
