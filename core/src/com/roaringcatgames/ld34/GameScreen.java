package com.roaringcatgames.ld34;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.roaringcatgames.ld34.components.*;
import com.roaringcatgames.ld34.systems.*;

import javax.swing.*;

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
        engine.addSystem(new MovementSystem());
        engine.addSystem(new LavaBallEmitterSystem());
        engine.addSystem(new LavaBallSystem());
        //Rendering system should go last
        engine.addSystem(renderingSystem);

        engine.addEntity(buildVolcano());
        engine.addEntity(buildLavaBallEmitter(Input.Keys.F, -5f, 10f));
        engine.addEntity(buildLavaBallEmitter(Input.Keys.G, 5f, 15f));

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

    private Entity buildVolcano(){
        Entity e = engine.createEntity();

        Vector2 meterSize = RenderingSystem.getScreenSizeInMeters();
        e.add(TransformComponent.create()
                .setPosition(meterSize.x/2f, meterSize.y/2f, 1f)
                .setRotation(0f)
                .setScale(1f, 1f));

        return e;
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