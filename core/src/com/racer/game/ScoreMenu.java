package com.racer.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

public class ScoreMenu {
    float xPosition, yPosition;
    float startingYPosition;

    float targetYPosition;

    int WORLD_HEIGHT;
    int WORLD_WIDTH;

    BitmapFont bigFont;
    BitmapFont normalFont;

    private int bestScore = 0;
    private int currentScore = 0;

    public ScoreMenu(float xPosition, float yPosition, int WORLD_HEIGHT, int WORLD_WIDTH, BitmapFont bigFont, BitmapFont normalFont) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.startingYPosition = yPosition;
        this.WORLD_HEIGHT = WORLD_HEIGHT;
        this.WORLD_WIDTH = WORLD_WIDTH;
        this.normalFont = normalFont;
        targetYPosition = WORLD_HEIGHT*3/4;
        this.bigFont = bigFont;
    }

    public void setScores(int best, int current)
    {
        this.currentScore = current;
        this.bestScore = best;
    }

    public void draw(Batch batch, float backgroundMaxScrollingSpeed, float deltaTime)
    {
        if(this.yPosition+normalFont.getScaleY() > targetYPosition)
        {
            //this.yPosition = WORLD_HEIGHT;
            this.yPosition -= deltaTime * backgroundMaxScrollingSpeed*2;
        }

        String message = "You crashed!";
        String currentScoreMsg = "Your score:";
        String yourBestScoreMsg = "Your best score:";
        String instruction = "Tap anywhere to continue";

        bigFont.draw(batch,message,0,this.yPosition,WORLD_WIDTH, Align.center,true);

        normalFont.draw(batch,currentScoreMsg,0,this.yPosition-13,WORLD_WIDTH, Align.center,true);
        normalFont.draw(batch,String.valueOf(currentScore),0,this.yPosition-19,WORLD_WIDTH, Align.center,true);

        normalFont.draw(batch,yourBestScoreMsg,0,this.yPosition-26,WORLD_WIDTH, Align.center,true);
        normalFont.draw(batch,String.valueOf(bestScore),0,this.yPosition-32,WORLD_WIDTH, Align.center,true);

        normalFont.draw(batch,instruction,0,this.yPosition-45,WORLD_WIDTH, Align.center,true);
    }

    public void resetPosition()
    {
        this.yPosition = startingYPosition;
    }

    public boolean isInPlace()
    {
        if(yPosition <= targetYPosition)
        {
            return true;
        }
        return false;
    }
}
