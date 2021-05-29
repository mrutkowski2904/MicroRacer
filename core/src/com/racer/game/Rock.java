package com.racer.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Rock {

    // rock characteristics
    float movementSpeed; // world units per second


    // position & dimension
    // (lower-left corner)
    float xPosition,yPosition;
    float width,height;

    // graphics information
    TextureRegion rockTextureRegion;


    // position - center of the rock
    public Rock(float movementSpeed, float xCenter,
                float yCenter, float width,
                float height,
                TextureRegion rockTexture) {

        this.movementSpeed = movementSpeed;
        this.xPosition = xCenter - width/2;
        this.yPosition = yCenter - height/2;
        this.width = width;
        this.height = height;
        this.rockTextureRegion = rockTexture;
    }

    public void draw(Batch batch)
    {
        batch.draw(rockTextureRegion,xPosition,yPosition,width,height);

    }
}
