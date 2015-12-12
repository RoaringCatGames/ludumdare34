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
                .setPosition(meterSize.x/2f, meterSize.y/3f, 21f)
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

    private void addClouds(){
        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        Entity bgClouds = engine.createEntity();
        bgClouds.add(TransformComponent.create()
                .setPosition(meterSize.x/2f, meterSize.y/3f, 80f)
                .setRotation(0f)
                .setScale(1f, 1f));
        bgClouds.add(TextureComponent.create());
        bgClouds.add(AnimationComponent.create()
            .addAnimation("DEFAULT", new Animation(1f, Assets.getBackCloudFrames())));
        bgClouds.add(StateComponent.create()
            .set("DEFAULT"));

        Entity bmClouds = engine.createEntity();
        bmClouds.add(TransformComponent.create()
                .setPosition(meterSize.x/2f, meterSize.y/3f, 79f)
                .setRotation(0f)
                .setScale(1f, 1f));
        bmClouds.add(TextureComponent.create());
        bmClouds.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f, Assets.getMidBackCloudFrames())));
        bmClouds.add(StateComponent.create()
                .set("DEFAULT"));
        bmClouds.add(ScreenWrapComponent.create());

        Entity fmClouds = engine.createEntity();
        fmClouds.add(TransformComponent.create()
                .setPosition(meterSize.x/2f, meterSize.y/3f, 78f)
                .setRotation(0f)
                .setScale(1f, 1f));
        fmClouds.add(TextureComponent.create());
        fmClouds.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f, Assets.getMidFrontCloudFrames())));
        fmClouds.add(StateComponent.create()
                .set("DEFAULT"));
        fmClouds.add(ScreenWrapComponent.create());

        Entity fgClouds = engine.createEntity();
        fgClouds.add(TransformComponent.create()
                .setPosition(meterSize.x/2f, meterSize.y/3f, 77f)
                .setRotation(0f)
                .setScale(1f, 1f));
        fgClouds.add(TextureComponent.create());
        fgClouds.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f, Assets.getFrontCloudFrames())));
        fgClouds.add(StateComponent.create()
                .set("DEFAULT"));
        fgClouds.add(ScreenWrapComponent.create());

        Entity whitePuff = engine.createEntity();
        whitePuff.add(TransformComponent.create()
                .setPosition(meterSize.x/4f, (meterSize.y/3f)*2f, 76f)
                .setRotation(0f)
                .setScale(1f, 1f));
        whitePuff.add(TextureComponent.create());
        whitePuff.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f, Assets.getCloudPuffWhiteFrames())));
        whitePuff.add(StateComponent.create()
                .set("DEFAULT"));
        whitePuff.add(ScreenWrapComponent.create());

        Entity bluePuff = engine.createEntity();
        bluePuff.add(TransformComponent.create()
                .setPosition((meterSize.x / 4f) * 3f, (meterSize.y / 3f) * 2f, 76f)
                .setRotation(0f)
                .setScale(1f, 1f));
        bluePuff.add(TextureComponent.create());
        bluePuff.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f, Assets.getCloudPuffBlueFrames())));
        bluePuff.add(StateComponent.create()
                .set("DEFAULT"));
        bluePuff.add(ScreenWrapComponent.create());

        engine.addEntity(bgClouds);
        engine.addEntity(bmClouds);
        engine.addEntity(fmClouds);
        engine.addEntity(fgClouds);
        engine.addEntity(whitePuff);
        engine.addEntity(bluePuff);

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