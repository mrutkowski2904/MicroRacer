package com.racer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    // screen stuff
    final private Camera camera;
    final private Viewport viewport;

    // graphics
    final private SpriteBatch batch;
    final private TextureAtlas textureAtlas;

    private TextureRegion[] backgrounds;
    private TextureRegion playerCarTextureRegion, rockTextureRegion,ufoTextureRegion;

    // timing stuff
    private float[] backgroundOffsets={0};
    private float backgroundMaxScrollingSpeed;

    // world stuff
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;

    // game objects
    //private Rock rock1;
    private List<Rock> rocks;
    private Ufo ufo;
    private Car car;


    GameScreen(){
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);

        // set texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        backgrounds = new TextureRegion[1];
        backgrounds[0] = textureAtlas.findRegion("sandRoad");


        backgroundMaxScrollingSpeed = ((float)WORLD_HEIGHT)/2;

        // initialize texture regions
        rockTextureRegion = textureAtlas.findRegion("rock1");
        playerCarTextureRegion = textureAtlas.findRegion("redCar");
        ufoTextureRegion = textureAtlas.findRegion("ufo");

        playerCarTextureRegion.flip(false,true);
        ufoTextureRegion.flip(true,false);

        // game objects setup
        rocks = new ArrayList<Rock>();
        rocks.add(new Rock(WORLD_HEIGHT*3/4,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));
        rocks.add(new Rock(WORLD_HEIGHT*2/4,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));
        rocks.add(new Rock(WORLD_HEIGHT*1/4,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));
        rocks.add(new Rock(0,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));

        car = new Car(90,WORLD_WIDTH/2,WORLD_HEIGHT*1/6,12,18,playerCarTextureRegion);
        ufo = new Ufo(-WORLD_WIDTH*2,WORLD_HEIGHT*4/6,35,35,WORLD_HEIGHT,WORLD_WIDTH,ufoTextureRegion);

        batch = new SpriteBatch();
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        detectInput(deltaTime);

        // background
        renderBackground(deltaTime);

        // rocks
        for (Rock r: rocks) {
            r.draw(batch,backgroundMaxScrollingSpeed,deltaTime);
        }

        // player's car
        car.draw(batch);


        // other effects
        ufo.draw(batch,backgroundMaxScrollingSpeed,deltaTime);

        // explosions


        detectColision();

        batch.end();
    }

    private void detectInput(float deltaTime) {
        // keyboard
        float leftLimit,rightLimit;
        leftLimit = -car.getBoundingBox().x;
        rightLimit = WORLD_WIDTH - car.getBoundingBox().x - car.width;

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit>0)
        {
            float xChange = car.movementSpeed*deltaTime;
            xChange = Math.min(xChange,rightLimit);
            car.translate(xChange,0f);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit<0)
        {
            float xChange = car.movementSpeed*deltaTime;
            xChange = Math.max(xChange,leftLimit);
            car.translate(-xChange,0f);
        }

        //touch

    }

    private void detectColision()
    {
        for (Rock rock: rocks) {
            if(car.touchesRock(rock.getBoundingBox()))
            {
                System.out.println("Player hits rock");
            }
        }
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
