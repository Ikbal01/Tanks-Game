package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Hero extends Tank{
    public Hero(float x, float y, SpriteBatch spriteBatch) {
        super(x, y, spriteBatch);
    }

    public void update() {
        if (bullet != null && bullet.isDestroyed()) {
            bullet = null;
        }
    }
    public void draw(float deltaTime)  {
        update();

        TextureRegion currentFrame = currAnimation.getKeyFrame(deltaTime, true);
        spriteBatch.draw(currentFrame, getPosition().x, getPosition().y);

        if (bullet != null) {
            bullet.update();
            bullet.draw(deltaTime);
        }
    }

    @Override
    public void respondWallCollision() {
        getPosition().x = previousPosition.x;
        getPosition().y = previousPosition.y;
        getBounds().x = previousPosition.x;
        getBounds().y = previousPosition.y;
    }
}
