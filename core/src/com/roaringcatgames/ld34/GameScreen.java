package com.roaringcatgames.ld34;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.roaringcatgames.ld34.components.*;
import com.roaringcatgames.ld34.systems.*;

/**
 * Created by barry on 12/9/15 @ 11:12 PM.
 */
public class GameScreen extends ScreenAdapter {

    private boolean isInitialized = false;

    private PooledEngine engine;

    private SpriteBatch batch;
    private IScreenDispatcher dispatcher;

    private Music titleMusic;
    private Music wave1Music;
    private Music wave2Music;
    private Music wave3Music;
    private Music finalMusic;

    private Music currentMusic;

    private Music volcanoRumble;

    private Entity wave1Left;
    private Entity wave1Right;
    private Entity wave2Left;
    private Entity wave2Right;
    private Entity wave3Left;
    private Entity wave3Right;


    private Entity fButton;
    private Entity jButton;
    private Entity safeBanner;

    private float minXFirballForce = 6f;

    public GameScreen(SpriteBatch batch, IScreenDispatcher dispatcher){
        super();
        this.batch = batch;
        this.dispatcher = dispatcher;
    }

    public void doEvent(String eventName){
        switch(eventName){
            case "MENUOVER":
                //Disable System
                titleMusic.stop();
                engine.getSystem(MenuSystem.class).setProcessing(false);
                engine.getSystem(HealthRenderSystem.class).setProcessing(true);
                addWaveEmitters();
                engine.getSystem(WaveSystem.class).setProcessing(true);
                break;
            case "STARTWAVE":
                Gdx.app.log("Game Screen", "Starting wave " + wave + "!");
                if(wave != 1) {
                    safeBanner.getComponent(StateComponent.class)
                            .set("DOWN");
                }
                switch(wave){
                    case 1:
                        currentMusic = wave1Music;
                        currentMusic.play();
                        currentMusic.setVolume(0.7f);
                        toggleWaves(wave1Left, wave1Right);
                    case 2:
                        currentMusic = wave2Music;
                        currentMusic.play();
                        currentMusic.setVolume(0.8f);
                        toggleWaves(wave1Left, wave1Right);
                        toggleWaves(wave2Left, wave2Right);
                        break;
                    case 3:
                        currentMusic = wave3Music;
                        currentMusic.play();
                        currentMusic.setVolume(0.9f);
                        toggleWaves(wave1Left, wave1Right);
                        toggleWaves(wave2Left, wave2Right);
                        toggleWaves(wave3Left, wave3Right);
                        break;
                    default:
                        break;
                }
                wave++;
                break;

            case "BEGINENDING":
                Gdx.app.log("Game Screen", "You Survived!");
                currentMusic.stop();
                finalMusic.stop();
                wave1Music.stop();
                wave2Music.stop();
                wave3Music.stop();
                titleMusic.stop();

                isEnding = true;
                volcanoRumble.play();
                volcanoRumble.setVolume(1f);
                volcanoRumble.setLooping(false);
                engine.getEntitiesFor(Family.all(VolcanoComponent.class).get())
                        .get(0)
                        .getComponent(StateComponent.class)
                        .set("ENDING")
                        .setLooping(false);

                break;
            case "STOPWAVE":
                Gdx.app.log("Game Screen", "Stopping Music");
                //PANNICING DON"T KNOW WHY CURRENT MUSIC IS WRONG
                //  END IT ALL!!!
                currentMusic.stop();
                finalMusic.stop();
                wave1Music.stop();
                wave2Music.stop();
                wave3Music.stop();
                titleMusic.stop();

                //TODO: Set banner animation state
                safeBanner.getComponent(StateComponent.class)
                        .set("UP");
                break;
            case "GAMEOVER":
                engine.getSystem(MovementSystem.class).setProcessing(false);
                Gdx.app.log("GameScreen", "YOU LOSE!!");
                break;
        }
    }


    private boolean isEnding = false;
    private int wave = 1;
    private void update(float delta){

        if(isEnding && !volcanoRumble.isPlaying()){
            finalMusic.play();
        }

        engine.update(delta);
        ActionProcessor.clear();
    }

    @Override
    public void render(float delta) {
        if(isInitialized) {
            update(delta);
        }else{
            init();
        }
    }


    private void init(){
        Gdx.app.log("GameScreen", "Initializing");
        isInitialized = true;
        engine = new PooledEngine();

        Gdx.input.setInputProcessor(new ActionProcessor(Input.Keys.F, Input.Keys.J, Input.Keys.SPACE));

        //Rendering system holds our camera so we hold a reference
        //  in case we need to pass it off to another system
        RenderingSystem renderingSystem = new RenderingSystem(batch);

        engine.addSystem(new CleanUpSystem(new Rectangle(-20f, -20f,
                renderingSystem.getScreenSizeInMeters().x + 40f, renderingSystem.getScreenSizeInMeters().y + 40f)));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new VolcanoSystem(Input.Keys.F, Input.Keys.J));
        engine.addSystem(new GravitySystem(new Vector2(0f, -9.8f)));
        engine.addSystem(new ScreenWrapSystem(0f, 60f));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new ArmySpawnerSystem());
        engine.addSystem(new ArmyUnitSystem(this));
        engine.addSystem(new WaveSystem(this));
        engine.addSystem(new LavaBallEmitterSystem());
        engine.addSystem(new LavaBallSystem(Assets.getMediumImpact()));
        engine.addSystem(new MenuSystem(this));

        //Rendering system should go last
        engine.addSystem(renderingSystem);
        engine.addSystem(new HealthRenderSystem(renderingSystem.getCamera()));
        //engine.addSystem(new DebugSystem(batch, engine, renderingSystem.getCamera()));
        engine.addSystem(new CitySystem(this));

        addClouds();
        addGroundEnvironment();
        engine.addEntity(buildBackground());
        engine.addEntity(buildVolcano());
        engine.addEntity(buildLavaBallEmitter(Input.Keys.F, -minXFirballForce, 5f));
        engine.addEntity(buildLavaBallEmitter(Input.Keys.J, minXFirballForce, 5f));

        engine.getSystem(HealthRenderSystem.class).setProcessing(false);
        engine.getSystem(WaveSystem.class).setProcessing(false);

        addMenu();

        safeBanner = engine.createEntity();

        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        safeBanner.add(TransformComponent.create()
                .setPosition((meterSize.x / 2f) + 4f, 24f, ZUtil.TownZ + 1f)
                .setRotation(0f)
                .setScale(1f, 1f));
        safeBanner.add(TextureComponent.create());
        safeBanner.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(false));
        safeBanner.add(AnimationComponent.create()
                .addAnimation("UP", new Animation(1f / 19f, Assets.getSafeBannerFrames()))
                .addAnimation("DOWN", new Animation(1f / 19f, Assets.getSafeBannerFrames(), Animation.PlayMode.REVERSED)));
        engine.addEntity(safeBanner);

        titleMusic = Assets.getTitleMusic();
        titleMusic.play();
        titleMusic.setLooping(true);
        titleMusic.setVolume(1f);

        wave1Music = Assets.getWaveOneMusic();
        wave1Music.setLooping(true);
        wave1Music.setVolume(1f);

        wave2Music = Assets.getWaveTwoMusic();
        wave2Music.setLooping(true);
        wave2Music.setVolume(1f);

        wave3Music = Assets.getWaveThreeMusic();
        wave3Music.setLooping(true);
        wave3Music.setVolume(1f);
//
        finalMusic = Assets.getFinalMusic();
        finalMusic.setLooping(true);
        finalMusic.setVolume(1f);

        volcanoRumble = Assets.getVolcanoRumble();

        isInitialized = true;
    }

    private void addWaveEmitters() {
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        float armyEmitterHeight = 6f;
        wave1Left = buildArmyEmitter(1, 2f, -3.75f, armyEmitterHeight, 8f, 0f, true);
        wave1Right = buildArmyEmitter(-1,2f, meterSize.x+3.75f, armyEmitterHeight, 8f, 4f, true);

        wave2Left = buildArmyEmitter(1, 2f, -2.5f, armyEmitterHeight, 6f, 3f, false);
        wave2Right = buildArmyEmitter(-1, 2f, meterSize.x+2.5f, armyEmitterHeight, 6f, 0f, false);

        wave3Left = buildArmyEmitter(1, 2f, -1.25f, armyEmitterHeight, 4f, 0f, false);
        wave3Right = buildArmyEmitter(-1, 2f, meterSize.x+1.5f, armyEmitterHeight, 4f, 2f, false);

        engine.addEntity(wave1Left);
        engine.addEntity(wave1Right);
        engine.addEntity(wave2Left);
        engine.addEntity(wave2Right);
        engine.addEntity(wave3Left);
        engine.addEntity(wave3Right);
    }

    private Entity buildLavaBallEmitter(int key, float xVel, float yVel){
        Entity e = engine.createEntity();

        e.add(LavaBallEmitterComponent.create()
                .setTriggerKey(key)
                .setEmissionVelocity(xVel, yVel));

        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        e.add(TransformComponent.create()
                .setPosition(meterSize.x / 2f, meterSize.y / 2f, 1f)
                .setRotation(15f)
                .setScale(1f, 1f));

        return e;
    }

    private Entity buildArmyEmitter(int direction, float baseUnitSpeed, float x, float y, float intervalSeconds, float headStart, boolean startActive) {
        Entity e = engine.createEntity();

        e.add(ArmySpawnerComponent.create()
                .setActive(startActive)
                .setDirection(direction)
                .setIntervalSeconds(intervalSeconds)
                .setElapsedTime(headStart)
                .setBaseUnitSpeed(baseUnitSpeed));
        e.add(TransformComponent.create()
                .setPosition(x, y));
        return e;
    }

    private Entity buildBackground(){
        Entity e = engine.createEntity();

        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        e.add(TransformComponent.create()
                .setPosition(meterSize.x / 2f, meterSize.y / 2f, 100f)
                .setRotation(0f)
                .setScale(1f, 1f));
        e.add(TextureComponent.create()
            .setRegion(Assets.getBackground()));
        return e;
    }

    private Entity buildVolcano(){
        Entity e = engine.createEntity();

        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        e.add(TransformComponent.create()
                .setPosition(meterSize.x / 2f, 14f, ZUtil.VolcanoZ)
                .setRotation(0f)
                .setScale(1f, 1f));

        e.add(TextureComponent.create());
        e.add(VolcanoComponent.create());
        e.add(StateComponent.create()
                .set("DEFAULT"));

        AnimationComponent a = AnimationComponent.create();
        for(ObjectMap.Entry<String, Array<TextureAtlas.AtlasRegion>> kvp : Assets.getVolcanoStateFrames()){

            float frameTime = 1f/8f;
            Animation.PlayMode mode = Animation.PlayMode.LOOP;
            if(kvp.key == "CHARGING"){
                frameTime = 1f/16f;
            }else if(kvp.key == "ENDING"){
                frameTime = 4f/66f;
                mode = Animation.PlayMode.NORMAL;
            }
            a.addAnimation(kvp.key, new Animation(frameTime, kvp.value, mode));
        }
        e.add(a);

        return e;
    }

    private void addGroundEnvironment(){
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();

        Entity trees = engine.createEntity();
        trees.add(TransformComponent.create()
                .setPosition(meterSize.x / 2f, 11f, ZUtil.TreesZ)
                .setScale(1f, 1f));
        trees.add(TextureComponent.create()
                .setRegion(Assets.getTreeLine()));
        engine.addEntity(trees);

        Entity dirt = engine.createEntity();
        dirt.add(TransformComponent.create()
                .setPosition(meterSize.x / 2f, 6f, ZUtil.DirtZ)
                .setScale(1f, 1f));
        dirt.add(TextureComponent.create()
                .setRegion(Assets.getDirt()));
        engine.addEntity(dirt);

        Entity grassBack = engine.createEntity();
        grassBack.add(TransformComponent.create()
                .setPosition(meterSize.x / 2f, 0f, 0f)
                .setScale(1f, 1f));
        grassBack.add(TextureComponent.create());
        grassBack.add(AnimationComponent.create()
            .addAnimation("DEFAULT", new Animation(1f / 3f, Assets.getGrassFrames(), Animation.PlayMode.LOOP)));
        grassBack.add(StateComponent.create()
            .set("DEFAULT")
            .setLooping(true));
        engine.addEntity(grassBack);

//        Entity grassFront = engine.createEntity();
//        grassFront.add(TransformComponent.create()
//                .setPosition(meterSize.x / 2f + 1f, 0f, 0f)
//                .setScale(1f, 1f));
//        grassFront.add(TextureComponent.create()
//                .setRegion(Assets.getFrontGrass()));
//        engine.addEntity(grassFront);

    }
    private void addClouds(){
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();

        float smallAdjust = 3f/4f;
        float backAdjust = 1f/2f;
        float midAdjust = 1f/3f;
        float frontAdjust = 1f/3f;

        engine.addEntity(createScreenWrappedEntity(meterSize.x / 2f, meterSize.y*backAdjust, 80f,
                0f, 1f, 1f, Assets.getBackCloudFrames(), 1f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x / 2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y*backAdjust, 80f,
                0f, 1f, 1f, Assets.getBackCloudFrames(), 1f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x / 2f, meterSize.y*midAdjust, 79f,
                0f, 1f, 1f, Assets.getMidBackCloudFrames(), 2f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x / 2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y * midAdjust, 79f,
                0f, 1f, 1f, Assets.getMidBackCloudFrames(), 2f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x / 2f, meterSize.y * midAdjust, 78f,
                0f, 1f, 1f, Assets.getMidFrontCloudFrames(), 3f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x / 2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y * midAdjust, 78f,
                0f, 1f, 1f, Assets.getMidFrontCloudFrames(), 3f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x / 2f, meterSize.y * frontAdjust, 77f,
                0f, 1f, 1f, Assets.getFrontCloudFrames(), 4f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x / 2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y * frontAdjust, 77f,
                0f, 1f, 1f, Assets.getFrontCloudFrames(), 4f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x / 4f, meterSize.y * smallAdjust, 77f,
                0f, 1f, 1f, Assets.getCloudPuffWhiteFrames(), 5f));

        engine.addEntity(createScreenWrappedEntity((meterSize.x / 4f) * 3f, meterSize.y * smallAdjust, 77f,
                0f, 1f, 1f, Assets.getCloudPuffBlueFrames(), 4f));


        Entity moon = engine.createEntity();
        moon.add(TransformComponent.create()
                .setPosition(50f, 23f, ZUtil.MoonZ)
                .setScale(0.35f, 0.35f));
        moon.add(TextureComponent.create()
            .setRegion(Assets.getMoon()));
        engine.addEntity(moon);

    }

    private void addMenu(){

        float topAdjust = 5f/6f;
        float midAdjust = 4f/6f;
        float lowAdjust = 2.75f/6f;

        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        Entity holdText = engine.createEntity();
        holdText.add(MenuItemComponent.create());
        holdText.add(TextureComponent.create()
                .setRegion(Assets.getHoldTextRegion()));
        holdText.add(TransformComponent.create()
                .setPosition(meterSize.x / 2f, meterSize.y * topAdjust, ZUtil.MenuZ));
        engine.addEntity(holdText);


        Entity releaseText = engine.createEntity();
        releaseText.add(MenuItemComponent.create());
        releaseText.add(TextureComponent.create()
                .setRegion(Assets.getReleaseTextRegion()));
        releaseText.add(TransformComponent.create()
                .setPosition(24f, meterSize.y * midAdjust, ZUtil.MenuZ));
        engine.addEntity(releaseText);


        Entity fireText = engine.createEntity();
        fireText.add(MenuItemComponent.create());
        fireText.add(TextureComponent.create());
        fireText.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f / 9f, Assets.getFireTextFrames())));
        fireText.add(TransformComponent.create()
            .setPosition(42f, meterSize.y * midAdjust, ZUtil.MenuZ));
        fireText.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(true));
        engine.addEntity(fireText);

        fButton = engine.createEntity();
        fButton.add(MenuItemComponent.create());
        fButton.add(TextureComponent.create());
        fButton.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f, Assets.getFFrame()))
                .addAnimation("PRESSED", new Animation(1f / 18f, Assets.getFDownFrame())));
        fButton.add(TransformComponent.create()
                .setPosition(meterSize.x / 4f, meterSize.y * lowAdjust, ZUtil.MenuZ));
        fButton.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(true));
        fButton.add(ButtonComponent.create()
            .setKey(Input.Keys.F));
        engine.addEntity(fButton);

        jButton = engine.createEntity();
        jButton.add(MenuItemComponent.create());
        jButton.add(TextureComponent.create());
        jButton.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f, Assets.getJFrame()))
                .addAnimation("PRESSED", new Animation(1f / 18f, Assets.getJDownFrame())));
        jButton.add(TransformComponent.create()
                .setPosition((meterSize.x / 4f) * 3f, meterSize.y * lowAdjust, ZUtil.MenuZ));
        jButton.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(true));
        jButton.add(ButtonComponent.create()
            .setKey(Input.Keys.J));
        engine.addEntity(jButton);


    }

    private Entity createScreenWrappedEntity(float xPos,
                                           float yPos,
                                           float zPos,
                                           float rotation,
                                           float scale,
                                           float aniRate,
                                           Array<TextureAtlas.AtlasRegion> defaultAnimation,
                                           float xSpeed) {
        Entity entity = engine.createEntity();
        entity.add(TransformComponent.create()
                .setPosition(xPos, yPos, zPos)
                .setRotation(rotation)
                .setScale(scale, scale));
        entity.add(TextureComponent.create());
        entity.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(aniRate, defaultAnimation)));
        entity.add(StateComponent.create()
                .set("DEFAULT"));
        entity.add(ScreenWrapComponent.create());
        entity.add(VelocityComponent.create()
                .setSpeed(xSpeed, 0f));
        entity.add(KinematicComponent.create());

        return entity;
    }

    private void toggleWaves(Entity wl, Entity wr){
        ArmySpawnerComponent wlc = wl.getComponent(ArmySpawnerComponent.class);
        wlc.isActive = !wlc.isActive;
        ArmySpawnerComponent wrc= wr.getComponent(ArmySpawnerComponent.class);
        wrc.isActive = !wrc.isActive;
    }


}