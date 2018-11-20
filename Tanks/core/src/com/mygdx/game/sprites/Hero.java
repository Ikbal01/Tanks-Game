package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.screens.BattleScreen;

public class Hero extends DynamicGameObject{
    private static final int HERO_WIDTH = 32;
    private static final int HERO_HEIGHT = 32;

    private SpriteBatch spriteBatch;

    private Animation<TextureRegion> upMoveAnimation;
    private Animation<TextureRegion> downMoveAnimation;
    private Animation<TextureRegion> leftMoveAnimation;
    private Animation<TextureRegion> rightMoveAnimation;

    private Animation<TextureRegion> currAnimation;


    private Color color;
    private TankCategory category;
    private Direction direction;
    private Vector2 muzzle;

    private Array<Bullet> bullets;

    public Hero(float x, float y) {
        super(x, y, HERO_WIDTH, HERO_HEIGHT);

        muzzle = new Vector2(x + (HERO_WIDTH / 2.0f), y + HERO_HEIGHT);

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

    public Animation<TextureRegion> getAnimation(Color color, TankCategory category, Direction direction) {
        TextureRegion[] moveFrames = new TextureRegion[2];
        int x = HERO_WIDTH * color.getX() + HERO_WIDTH * direction.getColmn();
        int y = HERO_HEIGHT * color.getY() + HERO_HEIGHT * category.getRow();

        moveFrames[0] = new TextureRegion(BattleScreen.items, x, y, HERO_WIDTH, HERO_HEIGHT);
        moveFrames[1] = new TextureRegion(BattleScreen.items, x + HERO_WIDTH, y, HERO_WIDTH, HERO_HEIGHT);

        return new Animation<TextureRegion>(FRAME_DURATION, moveFrames);
    }

    public void fire() {
        bullets.add(new Bullet(muzzle.x, muzzle.y, direction));
    }

    public void moveUp() {
        currAnimation = upMoveAnimation;
        position.y += velocity;
        bounds.y += velocity;
        muzzle.set(position.x + (HERO_WIDTH / 4.0f), position.y + HERO_HEIGHT);
        direction = Direction.UP;
    }

    public void moveDown() {
        currAnimation = downMoveAnimation;
        position.y -= velocity;
        bounds.y -= velocity;
        muzzle.set(position.x + (HERO_WIDTH / 4.0f), position.y - (HERO_HEIGHT / 2.0f));
        direction = Direction.DOWN;
    }

    public void moveLeft() {
        currAnimation = leftMoveAnimation;
        position.x -= velocity;
        bounds.x -= velocity;
        muzzle.set(position.x - (HERO_WIDTH / 4.0f), position.y + (HERO_HEIGHT / 4.0f));
        direction = Direction.LEFT;
    }

    public void moveRight() {
        currAnimation = rightMoveAnimation;
        position.x += velocity;
        bounds.x += velocity;
        muzzle.set(position.x + (HERO_WIDTH), position.y + (HERO_HEIGHT / 4.0f));
        direction = Direction.RIGHT;
    }

    public void draw(SpriteBatch spriteBatch, float deltaTime)  {
        this.spriteBatch = spriteBatch;
        TextureRegion currentFrame = currAnimation.getKeyFrame(deltaTime, true);
        spriteBatch.draw(currentFrame, position.x, position.y);
        for (Bullet bullet : bullets) {
            bullet.update();
            bullet.draw(spriteBatch);
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTankCategory(TankCategory category) {
        this.category = category;
    }
}
