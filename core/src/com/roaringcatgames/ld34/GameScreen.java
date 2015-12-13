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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    public GameScreen(SpriteBatch batch, IScreenDispatcher dispatcher){
        super();
        this.batch = batch;
        this.dispatcher = dispatcher;
    }

    private void init(){
        Gdx.app.log("GameScreen", "Initializing");
        isInitialized = true;
        engine = new PooledEngine();

        Gdx.input.setInputProcessor(new ActionProcessor(Input.Keys.F, Input.Keys.G));

        //Rendering system holds our camera so we hold a reference
        //  in case we need to pass it off to another system
        RenderingSystem renderingSystem = new RenderingSystem(batch);

        engine.addSystem(new CleanUpSystem(new Rectangle(-20f, -20f,
                renderingSystem.getScreenSizeInMeters().x + 40f, renderingSystem.getScreenSizeInMeters().y + 40f)));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new GravitySystem(new Vector2(0f, -9.8f)));
        engine.addSystem(new ScreenWrapSystem(0f, 60f));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new LavaBallEmitterSystem());
        engine.addSystem(new LavaBallSystem());
        //Rendering system should go last
        engine.addSystem(renderingSystem);

        addClouds();
        addGroundEnvironment();
        engine.addEntity(buildBackground());
        engine.addEntity(buildVolcano());
        engine.addEntity(buildLavaBallEmitter(Input.Keys.F, -5f, 5f));
        engine.addEntity(buildLavaBallEmitter(Input.Keys.G, 5f, 5f));

        titleMusic = Assets.getTitleMusic();
        titleMusic.play();
        titleMusic.setLooping(true);
        titleMusic.setVolume(1f);
        isInitialized = true;
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

    private Entity buildBackground(){
        Entity e = engine.createEntity();

        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        e.add(TransformComponent.create()
                .setPosition(meterSize.x/2f, meterSize.y/2f, 100f)
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
                .setPosition(meterSize.x/2f, (meterSize.y/3f) -4f, ZUtil.VolcanoZ)
                .setRotation(0f)
                .setScale(1f, 1f));

        e.add(TextureComponent.create());

        e.add(StateComponent.create()
            .set("DEFAULT"));

        AnimationComponent a = AnimationComponent.create();
        for(ObjectMap.Entry<String, Array<TextureAtlas.AtlasRegion>> kvp : Assets.getVolcanoStateFrames()){
                a.addAnimation(kvp.key, new Animation(1f/3f, kvp.value, Animation.PlayMode.LOOP));
        }
        e.add(a);

        return e;
    }

    private void addGroundEnvironment(){
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();

        Entity dirt = engine.createEntity();
        dirt.add(TransformComponent.create()
            .setPosition(meterSize.x/2f, 4f, ZUtil.VolcanoZ + 1f)
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
                .setPosition(meterSize.x/2f + 5f, 0f, 0f)
                .setScale(1f, 1f));
        grassFront.add(TextureComponent.create()
                .setRegion(Assets.getFrontGrass()));
        engine.addEntity(grassFront);

    }
    private void addClouds(){
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();

        Entity bgClouds = createScreenWrappedEntity(meterSize.x / 2f, meterSize.y / 2f, 80f,
                    0f, 1f, 1f, Assets.getBackCloudFrames(), 1f);

        Entity bmClouds = createScreenWrappedEntity(meterSize.x/2f, meterSize.y/3f, 79f,
                0f, 1f, 1f, Assets.getMidBackCloudFrames(), 2f);

        Entity fmClouds = createScreenWrappedEntity(meterSize.x/2f, meterSize.y/3f, 78f,
                0f, 1f, 1f, Assets.getMidFrontCloudFrames(), 3f);

        Entity fgClouds = createScreenWrappedEntity(meterSize.x/2f, meterSize.y/3f, 77f,
                0f, 1f, 1f, Assets.getFrontCloudFrames(), 4f);

        Entity whitePuff = createScreenWrappedEntity(meterSize.x / 4f, (meterSize.y / 3f)*2f, 77f,
                0f, 1f, 1f, Assets.getCloudPuffWhiteFrames(), 5f);

        Entity bluePuff = createScreenWrappedEntity((meterSize.x / 4f) * 3f, (meterSize.y / 3f) * 2f, 77f,
                0f, 1f, 1f, Assets.getCloudPuffBlueFrames(), 4f);

        engine.addEntity(bgClouds);
        engine.addEntity(bmClouds);
        engine.addEntity(fmClouds);
        engine.addEntity(fgClouds);
        engine.addEntity(whitePuff);
        engine.addEntity(bluePuff);
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


    private void update(float delta){
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