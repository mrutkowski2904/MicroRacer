package com.racer.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

public class GameMenu {
    float xPosition, yPosition;
    float startingYPosition;

    int WORLD_HEIGHT;
    int WORLD_WIDTH;

    BitmapFont titleFont;
    BitmapFont normalFont;

    public GameMenu(float xPosition, float yPosition, int WORLD_HEIGHT, int WORLD_WIDTH, BitmapFont titleFont, BitmapFont normalFont) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.startingYPosition = yPosition;
        this.WORLD_HEIGHT = WORLD_HEIGHT;
        this.WORLD_WIDTH = WORLD_WIDTH;
        this.titleFont = titleFont;
        this.normalFont = normalFont;
    }

    public void resetPosition()
    {
        this.yPosition = startingYPosition;
    }


    public void draw(Batch batch, float backgroundMaxScrollingSpeed, float deltaTime)
    {
        if(this.yPosition+titleFont.getScaleY() > WORLD_HEIGHT*3/4)
        {
            //this.yPosition = WORLD_HEIGHT;
            this.yPosition -= deltaTime * backgroundMaxScrollingSpeed*2;
        }

        String titleText = "MICRO RACER";
        String startGameText = "Tap anywhere to start";
        //String text = String.format("%03d",currentScore);
        titleFont.draw(batch,titleText,WORLD_WIDTH/2,this.yPosition,0, Align.center,false);
        normalFont.draw(batch,startGameText,0,this.yPosition-(13),WORLD_WIDTH, Align.center,true);
    }
}
