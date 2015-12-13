package com.roaringcatgames.ld34;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
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

    private Entity wave1Left;
    private Entity wave1Right;
    private Entity wave2Left;
    private Entity wave2Right;
    private Entity wave3Left;
    private Entity wave3Right;


    public GameScreen(SpriteBatch batch, IScreenDispatcher dispatcher){
        super();
        this.batch = batch;
        this.dispatcher = dispatcher;
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
        engine.addSystem(new ArmySpawnerSystem());
        engine.addSystem(new LavaBallEmitterSystem());
        engine.addSystem(new LavaBallSystem(Assets.getMediumImpact()));

        //Rendering system should go last
        engine.addSystem(renderingSystem);

        addClouds();
        addGroundEnvironment();
        engine.addEntity(buildBackground());
        engine.addEntity(buildVolcano());
        engine.addEntity(buildLavaBallEmitter(Input.Keys.F, -5f, 5f));
        engine.addEntity(buildLavaBallEmitter(Input.Keys.J, 5f, 5f));

        addWaveEmitters();

        titleMusic = Assets.getTitleMusic();
        titleMusic.play();
        titleMusic.setLooping(true);
        titleMusic.setVolume(1f);
        isInitialized = true;
    }

    private void addWaveEmitters() {
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        wave1Left = buildArmyEmitter(1, -3.75f, 2.5f, 3f, true);
        wave1Right = buildArmyEmitter(-1, meterSize.x+3.75f, 2.5f, 3f, true);

        wave2Left = buildArmyEmitter(1, -2.5f, 2.5f, 2.5f, false);
        wave2Right = buildArmyEmitter(-1, meterSize.x+2.5f, 2.5f, 2.5f, false);

        wave3Left = buildArmyEmitter(1, -1.25f, 2.5f, 2f, false);
        wave3Right = buildArmyEmitter(-1, meterSize.x+1.5f, 2.5f, 2f, false);

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
                .setPosition(meterSize.x/2f, meterSize.y/2f, 1f)
                .setRotation(15f)
                .setScale(1f, 1f));

        return e;
    }

    private Entity buildArmyEmitter(int direction, float x, float y, float intervalSeconds, boolean startActive) {
        Entity e = engine.createEntity();

        e.add(ArmySpawnerComponent.create()
                .setActive(startActive)
                .setDirection(direction)
                .setIntervalSeconds(intervalSeconds));
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
                .setPosition(meterSize.x/2f, (meterSize.y/3f), ZUtil.VolcanoZ)
                .setRotation(0f)
                .setScale(1f, 1f));

        e.add(TextureComponent.create());
        e.add(VolcanoComponent.create());
        e.add(StateComponent.create()
            .set("DEFAULT"));

        AnimationComponent a = AnimationComponent.create();
        for(ObjectMap.Entry<String, Array<TextureAtlas.AtlasRegion>> kvp : Assets.getVolcanoStateFrames()){

            float frameTime = 1f/8f;
            if(kvp.key == "CHARGING"){
                frameTime = 1f/16f;
            }
            a.addAnimation(kvp.key, new Animation(frameTime, kvp.value, Animation.PlayMode.LOOP));
        }
        e.add(a);

        return e;
    }

    private void addGroundEnvironment(){
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();

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
        grassBack.add(TextureComponent.create()
                .setRegion(Assets.getBackGrass()));
        engine.addEntity(grassBack);

        Entity grassFront = engine.createEntity();
        grassFront.add(TransformComponent.create()
                .setPosition(meterSize.x/2f + 1f, 0f, 0f)
                .setScale(1f, 1f));
        grassFront.add(TextureComponent.create()
                .setRegion(Assets.getFrontGrass()));
        engine.addEntity(grassFront);

    }
    private void addClouds(){
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();

        engine.addEntity(createScreenWrappedEntity(meterSize.x / 2f, meterSize.y / 2f, 80f,
                0f, 1f, 1f, Assets.getBackCloudFrames(), 1f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x/2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y / 2f, 80f,
                0f, 1f, 1f, Assets.getBackCloudFrames(), 1f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x/2f, meterSize.y/3f, 79f,
                0f, 1f, 1f, Assets.getMidBackCloudFrames(), 2f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x/2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y/3f, 79f,
                0f, 1f, 1f, Assets.getMidBackCloudFrames(), 2f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x/2f, meterSize.y/3f, 78f,
                0f, 1f, 1f, Assets.getMidFrontCloudFrames(), 3f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x/2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y/3f, 78f,
                0f, 1f, 1f, Assets.getMidFrontCloudFrames(), 3f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x/2f, meterSize.y/3f, 77f,
                0f, 1f, 1f, Assets.getFrontCloudFrames(), 4f));
        engine.addEntity(createScreenWrappedEntity((meterSize.x/2f) - RenderingSystem.PixelsToMeters(1000f),
                meterSize.y/3f, 77f,
                0f, 1f, 1f, Assets.getFrontCloudFrames(), 4f));

        engine.addEntity(createScreenWrappedEntity(meterSize.x / 4f, (meterSize.y / 3f)*2f, 77f,
                0f, 1f, 1f, Assets.getCloudPuffWhiteFrames(), 5f));

        engine.addEntity(createScreenWrappedEntity((meterSize.x / 4f) * 3f, (meterSize.y / 3f) * 2f, 77f,
                0f, 1f, 1f, Assets.getCloudPuffBlueFrames(), 4f));

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

    private void update(float delta){

        //TODO: Remove:
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            toggleWaves(wave1Left, wave1Right);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            toggleWaves(wave2Left, wave2Right);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
            toggleWaves(wave3Left, wave3Right);
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
}