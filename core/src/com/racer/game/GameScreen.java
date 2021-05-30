package com.racer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GameScreen implements Screen {

    // screen stuff
    final private Camera camera;
    final private Viewport viewport;

    // graphics
    final private SpriteBatch batch;
    final private TextureAtlas textureAtlas;

    private TextureRegion[] backgrounds;
    private TextureRegion playerCarTextureRegion, rockTextureRegion,ufoTextureRegion;

    // initialize explosion texture
    private Texture explosionTexture;

    // timing stuff
    private float[] backgroundOffsets={0};
    private float backgroundMaxScrollingSpeed;

    // world stuff
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 1f;
    private final float TOTAL_ANIMATION_TIME = 0.5f;

    // gameplay
    private int currentScore = 0;
    private boolean gameActive = true;

    // game objects
    //private Rock rock1;
    private List<Rock> rocks;
    private Ufo ufo;
    private Car car;
    private LinkedList<Explosion> explosions;

    // Score HUD
    BitmapFont font;
    float hudVerticalMargin,hudLeftX,hudRightX,hudCenterX;


    GameScreen(){
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);

        // set texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        // explosion texture
        explosionTexture = new Texture("explosion.png");

        backgrounds = new TextureRegion[1];
        backgrounds[0] = textureAtlas.findRegion("sandRoad");

        // initialize texture regions
        rockTextureRegion = textureAtlas.findRegion("rock1");
        playerCarTextureRegion = textureAtlas.findRegion("redCar");
        ufoTextureRegion = textureAtlas.findRegion("ufo");

        playerCarTextureRegion.flip(false,true);
        ufoTextureRegion.flip(true,false);

        backgroundMaxScrollingSpeed = ((float)WORLD_HEIGHT)/2;

        // game objects setup
        rocks = new ArrayList<Rock>();
        rocks.add(new Rock(WORLD_HEIGHT*3/4,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));
        rocks.add(new Rock(WORLD_HEIGHT*2/4,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));
        rocks.add(new Rock(WORLD_HEIGHT*1/4,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));
        rocks.add(new Rock(0,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));

        car = new Car(90,WORLD_WIDTH/2,WORLD_HEIGHT*1/6,12,18,playerCarTextureRegion);
        ufo = new Ufo(-WORLD_WIDTH*2,WORLD_HEIGHT*4/6,35,35,WORLD_HEIGHT,WORLD_WIDTH,ufoTextureRegion);
        explosions = new LinkedList<>();

        batch = new SpriteBatch();

        prepareScoreHUD();
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        detectInput(deltaTime);

        // background
        renderBackground(deltaTime);

        // game active
        if(gameActive)
        {
            // player's car
            car.draw(batch);

            // collision
            detectColision(deltaTime);

        }

        // rocks
        for (Rock r: rocks) {
            r.draw(batch,backgroundMaxScrollingSpeed,deltaTime);
        }

        // other effects
        ufo.draw(batch,backgroundMaxScrollingSpeed,deltaTime);
        renderExplosions(deltaTime);

        // Top layer
        if(gameActive)
        {
            // score HUD
            updateAndRenderScoreHUD();
        }

        batch.end();
    }

    private void renderExplosions(float deltaTime)
    {

        ListIterator<Explosion> explosionListIterator = explosions.listIterator();
        while (explosionListIterator.hasNext())
        {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if(explosion.isFinished())
            {
                explosionListIterator.remove();
            }
            else
            {
                explosion.draw(batch);
            }

        }
    }

    private void detectInput(float deltaTime) {

        // keyboard
        float leftLimit,rightLimit;
        leftLimit = -car.getBoundingBox().x;
        rightLimit = WORLD_WIDTH - car.getBoundingBox().x - car.width;

        // Game controls
        if(gameActive)
        {
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

            //touch and mouse
            if(Gdx.input.isTouched())
            {
                // get screen position
                float xTouchPixels = Gdx.input.getX();
                float yTouchPixels = Gdx.input.getY();

                // convert screen position to world units
                Vector2 touchPoint = new Vector2(xTouchPixels,yTouchPixels);
                Vector2 touchInWorld = viewport.unproject(touchPoint);
                Vector2 carCenter = new Vector2(car.xPosition + car.width/2,car.yPosition+car.height/2);


                // right
                if(carCenter.x+TOUCH_MOVEMENT_THRESHOLD<touchInWorld.x && rightLimit>0)
                {
                    float xChange = car.movementSpeed*deltaTime;
                    xChange = Math.min(xChange,rightLimit);
                    car.translate(xChange,0f);
                }

                // left
                if(carCenter.x-TOUCH_MOVEMENT_THRESHOLD>touchInWorld.x && leftLimit<0)
                {
                    float xChange = car.movementSpeed*deltaTime;
                    xChange = Math.max(xChange,leftLimit);
                    car.translate(-xChange,0f);
                }


            }
        }

    }

    private void updateScore()
    {
        currentScore += 1;
    }

    private void detectColision(float deltaTime)
    {
        for (Rock rock: rocks) {

            // Car hits rock
            if(car.touchesRock(rock.getBoundingBox()))
            {
                Rectangle explostionBox = new Rectangle();
                explostionBox.setWidth(car.width*2);
                explostionBox.setHeight(car.height*2);
                explostionBox.setX(car.xPosition-car.width/2);
                explostionBox.setY(car.yPosition-car.height/2);

                explosions.add(new Explosion(explosionTexture,explostionBox,TOTAL_ANIMATION_TIME));
                gameActive = false;
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

                if(gameActive)
                {
                    updateScore();
                }
            }

            // upper and lower part
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer],WORLD_WIDTH,WORLD_HEIGHT);
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer] + WORLD_HEIGHT ,WORLD_WIDTH,WORLD_HEIGHT);
        }
    }

    private void prepareScoreHUD()
    {
        // Creating bitmap font from file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 14;
        fontParameter.borderWidth = 1.85f;
        fontParameter.color = new Color(1,1,1,0.9f);
        fontParameter.borderColor = new Color(0,0,0,0.9f);
        fontParameter.spaceX = 1;

        font = fontGenerator.generateFont(fontParameter);
        // Scale the font
        font.getData().setScale(0.55f);

    }

    private void updateAndRenderScoreHUD()
    {
        String text = String.valueOf(currentScore);
        //String text = String.format("%03d",currentScore);
        font.draw(batch,text,WORLD_WIDTH - font.getScaleX()*2,WORLD_HEIGHT-(font.getScaleY()*3),0, Align.right,false);
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
