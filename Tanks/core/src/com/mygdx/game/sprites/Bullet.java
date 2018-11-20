package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.enums.Direction;
import com.mygdx.game.screens.BattleScreen;

public class Bullet extends DynamicGameObject {
    private static final int BULLET_WIDTH = 16;
    private static final int BULLET_HEIGHT = 16;

    private TextureRegion textureRegion;
    private Direction direction;

    private SpriteBatch spriteBatch;

    public Bullet(float x, float y, Direction direction) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
        this.direction = direction;
        int pos = this.direction.getColmn() * 8;

        velocity = 4.5f;
        textureRegion = new TextureRegion(BattleScreen.items,  642 + pos,  200, BULLET_WIDTH, BULLET_HEIGHT);
    }

    public void draw(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        spriteBatch.draw(textureRegion, position.x, position.y);
    }

    public void update() {
        move(direction);
    }

    private void move(Direction direction) {
        switch (direction) {
            case RIGHT:
                position.x += velocity;
                break;
            case LEFT:
                position.x -= velocity;
                break;
            case DOWN:
                position.y -= velocity;
                break;
            case UP:
                position.y += velocity;
                break;
        }
    }
}
