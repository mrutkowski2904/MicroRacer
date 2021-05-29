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

    // graphics information
    TextureRegion carTextureRegion;

    public Car(float movementSpeed, float xCenter, float yCenter, float width, float height, TextureRegion carTextureRegion) {
        this.movementSpeed = movementSpeed;
        this.xPosition = xCenter - width/2;
        this.yPosition = yCenter - height/2;
        this.width = width;
        this.height = height;
        this.carTextureRegion = carTextureRegion;
    }

    public boolean touchesRock(Rectangle rockRectangle)
    {
        Rectangle carRect = new Rectangle(this.xPosition,this.yPosition,this.width,this.height);
        return carRect.overlaps(rockRectangle);
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
        batch.draw(carTextureRegion,xPosition,yPosition,width,height);
    }
}
