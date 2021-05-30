package com.racer.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.graalvm.compiler.word.Word;

import java.util.Random;

public class Rock {

    // position & dimension
    // (lower-left corner)
    float xPosition,yPosition;
    float width,height;
    //float rockOffset;
    int WORLD_HEIGHT;
    int WORLD_WIDTH;
    int startingYPosition;

    final int X_POS_PADDING=5;

    // graphics information
    TextureRegion rockTextureRegion;

    // position - center of the rock
    public Rock(int yPositionInitialOffset,float width,float height,
                TextureRegion rockTexture,int WORLD_WIDTH,int WORLD_HEIGHT) {

        //this.xPosition = xCenter - width/2;
        //this.yPosition = yCenter - height/2;

        this.width = width;
        this.height = height;
        this.rockTextureRegion = rockTexture;

        this.WORLD_HEIGHT = WORLD_HEIGHT;
        this.WORLD_WIDTH = WORLD_WIDTH;

        this.xPosition = getRandomXPos();
        this.startingYPosition = WORLD_HEIGHT + yPositionInitialOffset;
        this.yPosition = startingYPosition;
    }

    public void resetPosition()
    {
        this.yPosition = startingYPosition;
    }

    public void draw(Batch batch)
    {
        batch.draw(rockTextureRegion,xPosition,yPosition,width,height);
    }

    public void draw(Batch batch,float backgroundMaxScrollingSpeed,float deltaTime)
    {
        this.yPosition -= deltaTime * backgroundMaxScrollingSpeed;

        if(this.yPosition+this.height < 0)
        {
            this.yPosition = WORLD_HEIGHT;
            this.xPosition = getRandomXPos();
        }

        batch.draw(rockTextureRegion,xPosition,yPosition,width,height);

    }

    public Rectangle getBoundingBox()
    {
        return new Rectangle(xPosition,yPosition,width,height);
    }

    private int getRandomXPos()
    {
        Random rand = new Random();
        int result = rand.nextInt(WORLD_WIDTH-(2*X_POS_PADDING));
        result+=X_POS_PADDING/2;
        return result;
    }
}
