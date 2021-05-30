package com.racer.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Explosion {

    private Animation<TextureRegion> explosionAnimation;
    private float explosionTimer;
    private Rectangle boundingBox;

    final private int textureTiles = 6;

    Explosion(Texture texture, Rectangle boundingBox,float totalAnimationTime)
    {
        this.boundingBox = boundingBox;
        // split the texture
        TextureRegion[][] textureRegion2D = TextureRegion.split(texture,240,250);

        // converting texture to 1D array
        TextureRegion[] textureRegion1D = new TextureRegion[textureTiles];
        int index = 0;

        // converting to 1D array
        for(int i = 0;i<2;i++)
        {
            for(int j=0;j<3;j++)
            {
                textureRegion1D[index] = textureRegion2D[i][j];
                index++;
            }
        }

        explosionAnimation = new Animation<TextureRegion>(totalAnimationTime/textureTiles,textureRegion1D);
        explosionTimer = 0;
    }

    public void update(float deltaTime)
    {
        explosionTimer+=deltaTime;
    }

    public void draw(SpriteBatch batch)
    {
        batch.draw(explosionAnimation.getKeyFrame(explosionTimer),
                boundingBox.x,boundingBox.y,
                boundingBox.width,boundingBox.height);

    }

    public boolean isFinished()
    {
        return explosionAnimation.isAnimationFinished(explosionTimer);
    }

}
