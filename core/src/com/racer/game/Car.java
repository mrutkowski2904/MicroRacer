package com.racer.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Car {
    // car characteristics
    float movementSpeed; // world units per second

    // position & dimension
    // (lower-left corner)
    float xPosition,yPosition;
    float width,height;

    float startingXPosition;

    // for turning visuals
    private float turningSpeed = 100f;
    private float centeringSpeed = 130f;
    private final float maxTilt = 10f;

    // how close to the initial position tilt has to be, to reset it without animation
    private final float resetTiltThreshold = 2f;
    float rotation = 0.0f;

    // graphics information
    TextureRegion carTextureRegion;

    public Car(float movementSpeed, float xCenter, float yCenter, float width, float height, TextureRegion carTextureRegion) {
        this.movementSpeed = movementSpeed;
        this.xPosition = xCenter - width/2;
        this.startingXPosition = this.xPosition;
        this.yPosition = yCenter - height/2;
        this.width = width;
        this.height = height;
        this.carTextureRegion = carTextureRegion;
    }

    public boolean touchesRock(Rectangle rockRectangle)
    {
        // easy mode
        Rectangle carRect = new Rectangle(this.xPosition+this.width/4,this.yPosition,this.width/2,this.height*2/3);

        // hard mode (broken, one side collides when closer to rock)
        //Rectangle carRect = new Rectangle(this.xPosition,this.yPosition,this.width,this.height);

        return carRect.overlaps(rockRectangle);
    }

    public void resetPosition()
    {
        xPosition = startingXPosition;
    }

    public Rectangle getBoundingBox()
    {
        return new Rectangle(xPosition,yPosition,width,height);
    }

    public void translate(float xChange,float yChange)
    {
        xPosition+=xChange;
        yPosition+=yChange;
    }

    public void draw(Batch batch)
    {
        //batch.draw(carTextureRegion,xPosition,yPosition,width,height);
        batch.draw(carTextureRegion,xPosition,yPosition,width,height,width,height,1f,1f,rotation);
    }

    public void tiltLeft(float deltaTime)
    {
        if(rotation<maxTilt)
        {
            this.rotation += deltaTime*turningSpeed;
        }

    }

    public void tiltRight(float deltaTime)
    {
        if(Math.abs(rotation)<maxTilt)
        {
            this.rotation -= deltaTime*turningSpeed;
        }

    }

    public void tiltToNormal(float deltaTime)
    {
        // car centered - skip
        if(rotation == 0)
        {
            return;
        }

        // car nearly centered - center skip animation

        if(Math.abs(rotation)<resetTiltThreshold)
        {
            rotation = 0;
            return;
        }


        // car titled right
        if(rotation < 0)
        {
            //while (Math.abs(rotation)>resetTiltThreshold)
            //{
                rotation += centeringSpeed * deltaTime;
            //}
            //rotation = 0;
        }

        // car tilted left
        else if (rotation > 0)
        {
            //while (Math.abs(rotation)>resetTiltThreshold)
            //{
                rotation -= centeringSpeed * deltaTime;
            //}
            //rotation = 0;
        }
    }
}
