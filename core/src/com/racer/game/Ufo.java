package com.racer.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Ufo {

    // position & dimension
    // (lower-left corner)
    float xPosition,yPosition;
    float width,height;

    int WORLD_HEIGHT;
    int WORLD_WIDTH;

    // graphics information
    TextureRegion ufoTextureRegion;

    public Ufo(float xPosition, float yPosition, float width, float height, int WORLD_HEIGHT, int WORLD_WIDTH, TextureRegion ufoTextureRegion) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.WORLD_HEIGHT = WORLD_HEIGHT;
        this.WORLD_WIDTH = WORLD_WIDTH;
        this.ufoTextureRegion = ufoTextureRegion;
    }



    public void draw(Batch batch, float backgroundMaxScrollingSpeed, float deltaTime)
    {
        this.xPosition += deltaTime * backgroundMaxScrollingSpeed*2;

        if(this.xPosition-this.height > WORLD_WIDTH*24)
        {
            this.xPosition = 0-this.width;
        }

        batch.draw(ufoTextureRegion,xPosition,yPosition,width,height);
    }
}
