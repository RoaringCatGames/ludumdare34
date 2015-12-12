package com.roaringcatgames.ld34;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.roaringcatgames.ld34.components.*;
import com.roaringcatgames.ld34.systems.*;

/**
 * Created by barry on 12/9/15 @ 11:12 PM.
 */
public class GameScreen extends ScreenAdapter {

    private boolean isInitialized = false;
    private float elapsedTime = 0f;
    private int secondsToSplash = 10;
    private World world;

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

        world = new World(new Vector2(0f, -9.8f), true);
        //Add Texture Component
        engine = new PooledEngine();


        RenderingSystem renderingSystem = new RenderingSystem(batch);
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));

        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new UselessStateSwapSystem());

        Entity e = buildPuffin(world);
        engine.addEntity(e);
        engine.addEntity(buildFloorEntity(world));

        isInitialized = true;
    }

    private Entity buildPuffin(World world) {
        Entity e = engine.createEntity();
        e.add(new PuffinComponent());

        AnimationComponent a = new AnimationComponent();
        a.animations.put("DEFAULT", new Animation(1f/16f, Assets.getPuffinArray(), Animation.PlayMode.LOOP));
        a.animations.put("RUNNING", new Animation(1f/16f, Assets.getPuffinRunArray(), Animation.PlayMode.LOOP));
        e.add(a);
        StateComponent state = new StateComponent();
        state.set("DEFAULT");
        e.add(state);
        TextureComponent tc = new TextureComponent();
        e.add(tc);

        TransformComponent tfc = new TransformComponent();
        tfc.position.set(10f, 10f, 1f);
        tfc.rotation = 15f;
        tfc.scale.set(0.25f, 0.25f);
        e.add(tfc);

        BodyComponent bc = new BodyComponent();
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Set our body's starting position in the world
        bodyDef.position.set(10f, 23f);

        // Create our body in the world using our body definition
        bc.body = world.createBody(bodyDef);
        bc.body.applyAngularImpulse(50f, true);

        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(2f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 20f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        bc.body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();

        e.add(bc);
        return e;
    }


    private void update(float delta){
        engine.update(delta);

        elapsedTime += delta;
        if(elapsedTime/1000f > secondsToSplash){
            world.dispose();
            dispatcher.endCurrentScreen();
        }
    }

    @Override
    public void render(float delta) {
        if(isInitialized) {
            update(delta);
        }else{
            init();
        }
    }

    private Entity buildFloorEntity(World world){

        Entity e = engine.createEntity();
        Vector2 screenMeters = RenderingSystem.getScreenSizeInMeters();
        Gdx.app.log("Splash Screen", "Screen Meters:" + screenMeters.x + " x " + screenMeters.y);
        Vector2 screenPixels = RenderingSystem.getScreenSizeInPixesl();
        Gdx.app.log("Splash Screen", "Screen Pixels:" + screenPixels.x + " x " + screenPixels.y);
        // Create our body definition
        BodyDef groundBodyDef =new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;

        // Set its world position
        groundBodyDef.position.set(new Vector2(6f, 1f));
        groundBodyDef.angle = 45f;

        BodyComponent bc = new BodyComponent();
        // Create a body from the defintion and add it to the world
        bc.body = world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)

        groundBox.setAsBox(5f, 1f);

        // Create a fixture from our polygon shape and add it to our ground body
        bc.body.createFixture(groundBox, 0.0f);


        // Clean up after ourselves
        groundBox.dispose();

        e.add(bc);

        return e;
    }
}