package com.racer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

    // initialize explosion and smoke texture
    private Texture explosionTexture;
    private Texture smokeTexture;
    private final int smokeness = 4; // biggner number -> less smoke
    private int smokeCounter = 0;

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
    private int bestScore;
    Preferences preferences = Gdx.app.getPreferences("game preferences");
    private boolean gameActive = false;
    private boolean menuActive = true;
    private boolean scoreMenuActive = false;

    // game objects
    //private Rock rock1;
    private List<Rock> rocks;
    private final int rockCount = 5;
    private Ufo ufo;
    private Car car;
    private LinkedList<Explosion> explosions;
    private LinkedList<Smoke> carSmokeList;

    // game stuff
    private BitmapFont scoreFont;
    private BitmapFont menuFont;
    private BitmapFont startGameFont;

    // Audio
    private final float musicVol = 0.7f;
    private final float sountEffectsVol = 1f;
    private Music music;
    private Sound gameStartSound;
    private Sound explosionSound;

    GameMenu menu;
    ScoreMenu scoreMenu;

    GameScreen(){
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);
        prepareFont();

        // set texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        smokeTexture = new Texture("smoke.png");
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

        music = Gdx.audio.newMusic(Gdx.files.internal("gameMusic.wav"));
        music.setLooping(true);
        music.setVolume(musicVol);
        music.play();

        gameStartSound = Gdx.audio.newSound(Gdx.files.internal("gameStart.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        bestScore = loadPersonalBest();

        // game objects setup
        rocks = new ArrayList<>();
        for(int i=0;i<rockCount;i++)
        {
            rocks.add(new Rock(WORLD_HEIGHT*i/rockCount,12,12,rockTextureRegion,WORLD_WIDTH,WORLD_HEIGHT));
        }

        car = new Car(90,WORLD_WIDTH/2,WORLD_HEIGHT*1/6,12,18,playerCarTextureRegion);
        ufo = new Ufo(-WORLD_WIDTH*2,WORLD_HEIGHT*5/7,35,35,WORLD_HEIGHT,WORLD_WIDTH,ufoTextureRegion);
        explosions = new LinkedList<>();
        carSmokeList = new LinkedList<>();
        batch = new SpriteBatch();


        menu = new GameMenu(WORLD_WIDTH/2,WORLD_HEIGHT*1.5f,WORLD_HEIGHT,WORLD_WIDTH,menuFont, startGameFont);
        scoreMenu = new ScoreMenu(WORLD_WIDTH/2,WORLD_HEIGHT*1.5f,WORLD_HEIGHT,WORLD_WIDTH, menuFont, startGameFont);
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        detectInput(deltaTime);

        // background
        renderBackground(deltaTime);

        // gameplay
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
        renderSmoke(deltaTime);
        renderExplosions(deltaTime);

        // Top layer
        if(gameActive)
        {
            // score HUD
            updateAndRenderScoreHUD();
        }

        // After crash menu
        if(scoreMenuActive)
        {
            scoreMenu.draw(batch,backgroundMaxScrollingSpeed,deltaTime);
        }

        // Menu
        if(menuActive)
        {
            menu.draw(batch,backgroundMaxScrollingSpeed,deltaTime);
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

    private void renderSmoke(float deltaTime)
    {
        ListIterator<Smoke> smokeListIterator = carSmokeList.listIterator();
        while(smokeListIterator.hasNext())
        {
            Smoke smoke = smokeListIterator.next();
            smoke.update(deltaTime);
            if(smoke.isFinished())
            {
                smokeListIterator.remove();
            }
            else
            {
                smoke.draw(batch,deltaTime);
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

                car.tiltRight(deltaTime);
                makeSmoke();
            }

            if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit<0)
            {
                float xChange = car.movementSpeed*deltaTime;
                xChange = Math.max(xChange,leftLimit);
                car.translate(-xChange,0f);

                // tilt the car
                car.tiltLeft(deltaTime);
                makeSmoke();
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
                //Vector2 carCenter = new Vector2(car.xPosition + car.width/2,car.yPosition+car.height/2);


                // right
                //if(carCenter.x+TOUCH_MOVEMENT_THRESHOLD<touchInWorld.x && rightLimit>0)
                if(WORLD_WIDTH/2<=touchInWorld.x && rightLimit>0)
                {
                    float xChange = car.movementSpeed*deltaTime;
                    xChange = Math.min(xChange,rightLimit);
                    car.translate(xChange,0f);
                    car.tiltRight(deltaTime);
                    makeSmoke();
                }

                // left
                //if(carCenter.x-TOUCH_MOVEMENT_THRESHOLD>touchInWorld.x && leftLimit<0)
                if(WORLD_WIDTH/2>touchInWorld.x && leftLimit<0)
                {
                    float xChange = car.movementSpeed*deltaTime;
                    xChange = Math.max(xChange,leftLimit);
                    car.translate(-xChange,0f);
                    car.tiltLeft(deltaTime);
                    makeSmoke();
                }


            }

            // no key pressed and no screen touched
            if(!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && !Gdx.input.isTouched())
            {
                // tilt to default position
                car.tiltToNormal(deltaTime);
            }
        }

        else if(!gameActive && (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)))
        {
            //startGame();
            if(menuActive && menu.isInPlace())
            {
                startGame();
            }
            else if(scoreMenuActive && scoreMenu.isInPlace())
            {
                menuActive = true;
                scoreMenuActive = false;
            }
        }

    }

    private void startGame()
    {
        //menu.resetPosition();
        menuActive = false;
        scoreMenuActive = false;
        gameActive = true;
        for (Rock r: rocks) {
            r.resetPosition();
        }
        car.resetPosition();
        gameStartSound.play(sountEffectsVol);
    }

    public void saveBestScore(int newBest)
    {
        preferences.putInteger("highscore", newBest);
        preferences.flush();
    }

    public void stopGame()
    {
        // replace later with after crash screen
        // todo, save score
        if(currentScore>bestScore)
        {
            bestScore = currentScore;
            saveBestScore(bestScore);
        }
        scoreMenu.setScores(bestScore,currentScore);
        currentScore = 0;

        menu.resetPosition();
        scoreMenu.resetPosition();
        //menuActive = true;
        scoreMenuActive = true;
        gameActive = false;
    }

    private void updateScore()
    {
        currentScore += 1 + (currentScore/8);
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
                explosionSound.play(sountEffectsVol);

                stopGame();
            }
        }
    }

    private void makeSmoke()
    {
        if(smokeCounter % smokeness == 0)
        {
            Rectangle smokeBox = new Rectangle();

            float smokeWidth = car.width*1.6f;
            float smokeHeight = smokeWidth;

            smokeBox.setWidth(smokeWidth);
            smokeBox.setHeight(smokeHeight);
            smokeBox.setX(car.xPosition+(car.width/2)-(smokeWidth/2));
            smokeBox.setY(car.yPosition-smokeHeight/1.5f);

            carSmokeList.add(new Smoke(smokeTexture,smokeBox,TOTAL_ANIMATION_TIME*0.75f,backgroundMaxScrollingSpeed*0.4f));
            smokeCounter=0;
        }
        smokeCounter++;
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

    private void prepareFont()
    {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font2.ttf"));

        // Game font
        // Creating bitmap font from file
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 14;
        fontParameter.borderWidth = 1.85f;
        fontParameter.color = new Color(1,1,1,0.9f);
        fontParameter.borderColor = new Color(0,0,0,0.9f);
        fontParameter.spaceX = 1;

        scoreFont = fontGenerator.generateFont(fontParameter);
        // Scale the font
        scoreFont.getData().setScale(0.43f);


        // Title font
        FreeTypeFontGenerator.FreeTypeFontParameter menuFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        menuFontParameter.size = 14;
        menuFontParameter.borderWidth = 1.85f;
        menuFontParameter.color = new Color(1,1,1,1f);
        menuFontParameter.borderColor = new Color(0,0,0,1f);
        menuFontParameter.spaceX = 1;

        menuFont = fontGenerator.generateFont(menuFontParameter);
        // Scale the font
        menuFont.getData().setScale(0.43f);


        //menuFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Start game font - normal font
        FreeTypeFontGenerator.FreeTypeFontParameter startGameFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        startGameFontParameter.size = 14;
        startGameFontParameter.borderWidth = 1.85f;
        startGameFontParameter.color = new Color(1,1,1,1f);
        startGameFontParameter.borderColor = new Color(0,0,0,1f);
        startGameFontParameter.spaceX = 0;

        startGameFont = fontGenerator.generateFont(startGameFontParameter);
        // Scale the font
        startGameFont.getData().setScale(0.23f);
    }

    private void updateAndRenderScoreHUD()
    {
        String text = String.valueOf(currentScore);
        //String text = String.format("%03d",currentScore);
        scoreFont.draw(batch,text,WORLD_WIDTH - scoreFont.getScaleX()*3,WORLD_HEIGHT-(scoreFont.getScaleY()*4),0, Align.right,false);
    }

    private int loadPersonalBest()
    {
        return preferences.getInteger("highscore");
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
        music.dispose();
        System.exit(0);
    }

    @Override
    public void show() {

    }
}
