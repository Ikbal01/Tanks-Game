package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * An animated game object.
 */
public abstract class DynamicGameObject extends GameObject {

    float velocity;
    Animation<TextureRegion> explosionAnimation;

    public DynamicGameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    /**
     * Generates an animation based on submitted parameters.
     *
     * @param path the path of png file
     * @param pngCount the count of png files in the animation
     * @param pngSize the size of png file in pixels
     * @param frameDuration the duration of each png file
     * @return generated animation
     */
    Animation<TextureRegion> generateAnimation(String path,
                                               int pngCount, int pngSize, float frameDuration) {

        TextureRegion[] spawning = new TextureRegion[pngCount];
        Texture currTexture;

        for (int i = 0; i < pngCount; i++) {

            currTexture = new Texture(Gdx.files.internal(path + (i + 1) + ".png"));
            spawning[i] = new TextureRegion(currTexture, pngSize, pngSize);
        }

        return new Animation<TextureRegion>(frameDuration, spawning);
    }

    public abstract void update();

    public abstract void draw(float deltaTime);

    public abstract void respondWallCollision();

    protected abstract void explode();
}
