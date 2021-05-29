package com.racer.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    // screen stuff
    private Camera camera;
    private Viewport viewport;

    // graphics
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;

    private TextureRegion[] backgrounds;
    private TextureRegion playerCarTextureRegion, rockTextureRegion;


    // timing stuff
    private float[] backgroundOffsets={0};
    private float backgroundMaxScrollingSpeed;

    // world stuff
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;

    // game objects
    private Rock rock1;
    private Car car;


    GameScreen(){
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);

        // set texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        backgrounds = new TextureRegion[1];
        backgrounds[0] = textureAtlas.findRegion("sandRoad");

        backgroundMaxScrollingSpeed = ((float)WORLD_HEIGHT)/4;

        // initialize texture regions
        rockTextureRegion = textureAtlas.findRegion("rock1");
        playerCarTextureRegion = textureAtlas.findRegion("redCar");
        playerCarTextureRegion.flip(false,true);


        // game objects setup
        rock1 = new Rock(2,WORLD_WIDTH/2,WORLD_HEIGHT*9/10,12,12,rockTextureRegion);
        car = new Car(2,WORLD_WIDTH/2,WORLD_HEIGHT*1/4,12,18,playerCarTextureRegion);

        batch = new SpriteBatch();
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        // background
        renderBackground(deltaTime);

        // rocks
        rock1.draw(batch);


        // player's car
        car.draw(batch);



        // speed effect

        // explosions


        batch.end();
    }

    private void renderBackground(float deltaTime){

        // the lowest layer
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed;
        //backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed/2;
        //backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed/4;

        for(int layer =0;layer<backgroundOffsets.length;layer++)
        {
            // when it is too far
            if(backgroundOffsets[layer] > WORLD_HEIGHT)
            {
                backgroundOffsets[layer] = 0;
            }

            // upper and lower part
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer],WORLD_WIDTH,WORLD_HEIGHT);
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer] + WORLD_HEIGHT ,WORLD_WIDTH,WORLD_HEIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {

    }
}
