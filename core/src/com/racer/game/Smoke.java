package com.racer.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Smoke {
    private Animation<TextureRegion> smokeAnimation;
    private float smokeTimer;
    private Rectangle boundingBox;
    private float smokeSpeed;
    final private int textureTiles = 9;


    public Smoke(Texture texture, Rectangle boundingBox, float totalAnimationTime, float smokeSpeed) {
        this.boundingBox = boundingBox;

        // split the texture
        TextureRegion[][] textureRegion2D = TextureRegion.split(texture,1024,1024);

        // converting texture to 1D array
        TextureRegion[] textureRegion1D = new TextureRegion[textureTiles];
        int index = 0;

        // converting to 1D array
        for(int i = 0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                textureRegion1D[index] = textureRegion2D[i][j];
                index++;
            }
        }

        smokeAnimation = new Animation<TextureRegion>(totalAnimationTime/textureTiles,textureRegion1D);
        smokeTimer = 0;
        this.smokeSpeed = smokeSpeed;
    }

    public void update(float deltaTime)
    {
        smokeTimer+=deltaTime;
    }

    public void draw(SpriteBatch batch,float deltaTime)
    {
        this.boundingBox.y -= deltaTime * smokeSpeed;

        batch.draw(smokeAnimation.getKeyFrame(smokeTimer),
                boundingBox.x,boundingBox.y,
                boundingBox.width,boundingBox.height);

    }

    public boolean isFinished()
    {
        return smokeAnimation.isAnimationFinished(smokeTimer);
    }
}
