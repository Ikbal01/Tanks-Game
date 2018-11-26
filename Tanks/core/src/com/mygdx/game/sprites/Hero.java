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

public class Hero extends Tank{
    private SpriteBatch spriteBatch;

    private Animation<TextureRegion> upMoveAnimation;
    private Animation<TextureRegion> downMoveAnimation;
    private Animation<TextureRegion> leftMoveAnimation;
    private Animation<TextureRegion> rightMoveAnimation;

    private Animation<TextureRegion> currAnimation;
    private Vector2 previousPosition;

    private Color color;
    private TankCategory category;
    private Direction direction;
    private Vector2 muzzle;

    private Array<Bullet> bullets;

    public Hero(float x, float y) {
        super(x, y, TANK_WIDTH, TANK_HEIGHT);

        muzzle = new Vector2(x + (TANK_WIDTH / 4.0f), y + TANK_HEIGHT);

        color = Color.GREEN;
        category = TankCategory.LIGHT;
        direction = Direction.UP;

        upMoveAnimation = getAnimation(color, category, Direction.UP);
        downMoveAnimation = getAnimation(color, category, Direction.DOWN);
        leftMoveAnimation = getAnimation(color, category, Direction.LEFT);
        rightMoveAnimation = getAnimation(color, category, Direction.RIGHT);

        currAnimation = upMoveAnimation;

        bullets = new Array<Bullet>();
    }

    private Animation<TextureRegion> getAnimation(Color color, TankCategory category, Direction direction) {
        TextureRegion[] moveFrames = new TextureRegion[2];
        int x = TANK_WIDTH * color.getX() + TANK_WIDTH * direction.getColmn();
        int y = TANK_HEIGHT * color.getY() + TANK_HEIGHT * category.getRow();

        moveFrames[0] = new TextureRegion(World.items, x, y, TANK_WIDTH, TANK_HEIGHT);
        moveFrames[1] = new TextureRegion(World.items, x + TANK_WIDTH, y, TANK_WIDTH, TANK_HEIGHT);

        return new Animation<TextureRegion>(FRAME_DURATION, moveFrames);
    }

    public void update() {
        for (int i = 0; i < bullets.size; i++) {
            if (bullets.get(i).isDestroyed()) {
                bullets.removeIndex(i);
                i--;
            }
        }
    }

    public void fire() {
        bullets.add(new Bullet(muzzle.x, muzzle.y, direction, spriteBatch));
    }

    public void moveUp() {
        setVerticalRail();
        currAnimation = upMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().y += velocity;
        getBounds().y += velocity;
        muzzle.set(getPosition().x + 12, getPosition().y + TANK_HEIGHT);
        direction = Direction.UP;
    }

    public void moveDown() {
        setVerticalRail();
        currAnimation = downMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().y -= velocity;
        getBounds().y -= velocity;
        muzzle.set(getPosition().x + 12, getPosition().y);
        direction = Direction.DOWN;
    }

    public void moveLeft() {
        setHorizontalRail();
        currAnimation = leftMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().x -= velocity;
        getBounds().x -= velocity;
        muzzle.set(getPosition().x, getPosition().y + 12);
        direction = Direction.LEFT;
    }

    public void moveRight() {
        setHorizontalRail();
        currAnimation = rightMoveAnimation;
        previousPosition = new Vector2(getPosition());
        getPosition().x += velocity;
        getBounds().x += velocity;
        muzzle.set(getPosition().x + (TANK_WIDTH), getPosition().y + 12);
        direction = Direction.RIGHT;
    }

    private void setHorizontalRail() {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            getBounds().y = getBounds().y - (getBounds().y % 8);
            getPosition().y = getPosition().y - (getPosition().y % 8);
        }
    }

    private void setVerticalRail() {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            getBounds().x = getBounds().x - (getBounds().x % 8);
            getPosition().x = getPosition().x - (getPosition().x % 8);
        }
    }

    public void draw(SpriteBatch spriteBatch, float deltaTime)  {
        update();

        this.spriteBatch = spriteBatch;
        TextureRegion currentFrame = currAnimation.getKeyFrame(deltaTime, true);
        spriteBatch.draw(currentFrame, getPosition().x, getPosition().y);

        for (Bullet bullet : bullets) {
            bullet.update();
            bullet.draw(deltaTime);
        }
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTankCategory(TankCategory category) {
        this.category = category;
    }

    @Override
    public void respondBrickCollision() {
        getPosition().x = previousPosition.x;
        getPosition().y = previousPosition.y;
        getBounds().x = previousPosition.x;
        getBounds().y = previousPosition.y;
    }
}
