package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.enums.Color;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.enums.TankCategory;
import com.mygdx.game.screens.PlayScreen;

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

    public Hero(float x, float y) {
        super(x, y, HERO_WIDTH, HERO_HEIGHT);

        color = Color.PURPLE;
        category = TankCategory.LIGHT;
        direction = Direction.UP;

        upMoveAnimation = getAnimation(color, category, Direction.UP);
        downMoveAnimation = getAnimation(color, category, Direction.DOWN);
        leftMoveAnimation = getAnimation(color, category, Direction.LEFT);
        rightMoveAnimation = getAnimation(color, category, Direction.RIGHT);

        currAnimation = upMoveAnimation;
    }

    public Animation<TextureRegion> getAnimation(Color color, TankCategory category, Direction direction) {
        TextureRegion[] moveFrames = new TextureRegion[2];
        int x = HERO_WIDTH * color.getX() + HERO_WIDTH * direction.getColmn();
        int y = HERO_HEIGHT * color.getY() + HERO_HEIGHT * category.getRow();

        moveFrames[0] = new TextureRegion(PlayScreen.items, x, y, HERO_WIDTH, HERO_HEIGHT);
        moveFrames[1] = new TextureRegion(PlayScreen.items, x + HERO_WIDTH, y, HERO_WIDTH, HERO_HEIGHT);

        return new Animation<TextureRegion>(FRAME_DURATION, moveFrames);
    }

    public void moveUp() {
        currAnimation = upMoveAnimation;
        position.y += velocity;
    }

    public void moveDown() {
        currAnimation = downMoveAnimation;
        position.y -= velocity;
    }

    public void moveLeft() {
        currAnimation = leftMoveAnimation;
        position.x -= velocity;
    }

    public void moveRight() {
        currAnimation = rightMoveAnimation;
        position.x += velocity;
    }

    public void draw(SpriteBatch spriteBatch, float deltaTime)  {
        this.spriteBatch = spriteBatch;
        TextureRegion currentFrame = currAnimation.getKeyFrame(deltaTime, true);
        this.spriteBatch.draw(currentFrame, position.x, position.y);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTankCategory(TankCategory category) {
        this.category = category;
    }
}
