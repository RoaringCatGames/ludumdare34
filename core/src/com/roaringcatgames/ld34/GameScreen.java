package com.roaringcatgames.ld34;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.roaringcatgames.ld34.components.*;
import com.roaringcatgames.ld34.systems.AnimationSystem;
import com.roaringcatgames.ld34.systems.GravitySystem;
import com.roaringcatgames.ld34.systems.MovementSystem;
import com.roaringcatgames.ld34.systems.RenderingSystem;

/**
 * Created by barry on 12/9/15 @ 11:12 PM.
 */
public class GameScreen extends ScreenAdapter {

    private boolean isInitialized = false;

    private PooledEngine engine;

    private SpriteBatch batch;
    private IScreenDispatcher dispatcher;

    public GameScreen(SpriteBatch batch, IScreenDispatcher dispatcher){
        super();
        this.batch = batch;
        this.dispatcher = dispatcher;
    }

    private void init(){
        Gdx.app.log("GameScreen", "Initializing");
        isInitialized = true;
        engine = new PooledEngine();

        //Rendering system holds our camera so we hold a reference
        //  in case we need to pass it off to another system
        RenderingSystem renderingSystem = new RenderingSystem(batch);
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new GravitySystem(new Vector2(0f, -9.8f)));
        engine.addSystem(new MovementSystem());
        //Rendering system should go last
        engine.addSystem(renderingSystem);

        //Add some default Entities
        engine.addEntity(buildPuffin());

        engine.addEntity(buildLavaBall());

        isInitialized = true;
    }

    private Entity buildPuffin() {
        Entity e = engine.createEntity();

        AnimationComponent a = AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f / 16f, Assets.getPuffinArray(), Animation.PlayMode.LOOP))
                .addAnimation("RUNNING", new Animation(1f / 16f, Assets.getPuffinRunArray(), Animation.PlayMode.LOOP));
        e.add(a);
        StateComponent state = StateComponent.create()
                .set("DEFAULT");
        e.add(state);
        TextureComponent tc = TextureComponent.create();
        e.add(tc);

        TransformComponent tfc = TransformComponent.create()
                .setPosition(10f, 10f, 1f)
                .setRotation(15f)
                .setScale(0.25f, 0.25f);
        e.add(tfc);


        return e;
    }

    private Entity buildLavaBall(){
        Entity e = engine.createEntity();

        e.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f / 16f, Assets.getPuffinArray(), Animation.PlayMode.LOOP))
                .addAnimation("RUNNING", new Animation(1f / 16f, Assets.getPuffinRunArray(), Animation.PlayMode.LOOP)));

        e.add(StateComponent.create()
            .set("RUNNING"));

        e.add(TextureComponent.create());

        TransformComponent tfc = new TransformComponent();
        tfc.position.set(10f, 10f, 1f);
        tfc.rotation = 15f;
        tfc.scale.set(0.25f, 0.25f);
        e.add(TransformComponent.create()
            .setPosition(10f, 10f, 1f)
            .setRotation(15f)
            .setScale(0.25f, 0.25f));

        e.add(VelocityComponent.create()
            .setSpeed(15f, 20f));

        return e;
    }


    private void update(float delta){
        engine.update(delta);
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