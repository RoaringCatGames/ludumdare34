package com.roaringcatgames.ld34;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roaringcatgames.kitten2d.ashley.components.AnimationComponent;
import com.roaringcatgames.kitten2d.ashley.components.StateComponent;
import com.roaringcatgames.kitten2d.ashley.components.TextureComponent;
import com.roaringcatgames.kitten2d.ashley.components.TransformComponent;
import com.roaringcatgames.kitten2d.ashley.systems.AnimationSystem;
import com.roaringcatgames.kitten2d.ashley.systems.RenderingSystem;

/**
 * Created by barry on 12/8/15 @ 8:24 PM.
 */
public class SplashScreen extends LazyInitScreen {

    SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private IScreenDispatcher dispatcher;
    private PooledEngine engine;

    private int minSpalshSeconds = 2;
    private float elapsedTime = 0f;

    public SplashScreen(SpriteBatch batch, IScreenDispatcher dispatcher){
        this.batch = batch;
        this.dispatcher = dispatcher;
    }

    @Override
    void init() {
        this.shapeRenderer = new ShapeRenderer();

        engine = new PooledEngine();
        RenderingSystem render = new RenderingSystem(batch, ScreenHelper.PPM);
        engine.addSystem(new AnimationSystem());
        engine.addSystem(render);

        Entity e = engine.createEntity();

        e.add(TransformComponent.create()
                .setPosition(render.getCamera().viewportWidth/2f, render.getCamera().viewportHeight/2f, 100f)
                .setRotation(0f)
                .setScale(1f, 1f));
        e.add(TextureComponent.create()
                .setRegion(Assets.getBackground()));
        engine.addEntity(e);

        Entity loading = engine.createEntity();
        loading.add(TransformComponent.create()
            .setPosition(render.getCamera().viewportWidth/2f, render.getCamera().viewportHeight/2f, 0f)
            .setScale(1f, 1f));
        loading.add(TextureComponent.create());
        loading.add(StateComponent.create()
            .set("DEFAULT")
            .setLooping(false));
        loading.add(AnimationComponent.create()
            .addAnimation("DEFAULT", new Animation(1f / 5f, Assets.getLoadingFrames())));

        engine.addEntity(loading);
    }

    @Override
    void update(float delta) {
        elapsedTime += delta;

        if(Assets.am.update() && elapsedTime >= minSpalshSeconds){
            Gdx.app.log("Splash Screen", "Assets are Loaded!");
            dispatcher.endCurrentScreen();
        }else {
            engine.update(delta);

            //TODO: Juice with a progress bar Component/System
//            Gdx.gl20.glLineWidth(1f);
//            shapeRenderer.setProjectionMatrix(cam.combined);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.setColor(Color.CYAN);
//            shapeRenderer.rect(0, 0, cam.viewportWidth * Assets.am.getProgress(), cam.viewportHeight / 5f);
//            shapeRenderer.end();
        }
    }
}

