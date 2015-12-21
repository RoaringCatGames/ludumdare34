package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.kitten2d.ashley.components.*;
import com.roaringcatgames.ld34.Assets;
import com.roaringcatgames.ld34.GameScreen;
import com.roaringcatgames.ld34.ZUtil;
import com.roaringcatgames.ld34.components.*;

import java.util.Random;

/**
 * Created by barry on 12/13/15 @ 6:37 PM.
 */
public class CitySystem extends IteratingSystem {

    private GameScreen game;

    private boolean isInitialized = false;

    private Array<Entity> buildings;
    public CitySystem(GameScreen game){
        super(Family.all(BuildingComponent.class).get());
        this.game = game;
        buildings = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(!isInitialized){
            init();
        }else if (isInitialized && buildings.size == 0) {
            game.doEvent("GAMEOVER");
        }

        buildings.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        buildings.add(entity);
    }


    private void init(){
        addInitalBuildings();
    }

    private void addInitalBuildings(){
        addBuildingComponent(36f, 8f, 2f, 2f);
        addBuildingComponent(24f, 8f, 2f, 2f);
        addBuildingComponent(30f, 8f, 2f, 2f);
        addBuildingComponent(33f, 7f, 2f, 2f);
        addBuildingComponent(27f, 7f, 2f, 2f);
        addBuildingComponent(32f, 6f, 2f, 2f);
        addBuildingComponent(28f, 6f, 2f, 2f);
        isInitialized = true;
    }

    private void addBuildingComponent(float x, float y, float boundW, float boundH) {
        PooledEngine engine = (PooledEngine)getEngine();
        Entity bld = engine.createEntity();
        bld.add(TextureComponent.create());
        bld.add(BuildingComponent.create());
        bld.add(TransformComponent.create()
                .setPosition(x, y, ZUtil.TownZ)
                .setScale(0.5f, 0.5f));
        bld.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f / 15f, getBuildingFrames(false))));
        bld.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(false));
        bld.add(BoundsComponent.create()
                .setBounds(0f, 0f, boundW, boundH));
        bld.add(HealthComponent.create()
                .setMaxHealth(20f)
                .setHealth(20f));
        bld.add(DamageComponent.create()
                .setDPS(0.25f));
        engine.addEntity(bld);
    }

    private Array<TextureAtlas.AtlasRegion> getBuildingFrames(boolean includeWall){
        int bound = includeWall ? 7 : 6;
        int random = new Random().nextInt(bound);

        switch(random) {
            case 0:
                return Assets.getBuildingAFrames();
            case 1:
                return Assets.getBuildingBFrames();
            case 2:
                return Assets.getBuildingCFrames();
            case 3:
                return Assets.getBuildingDFrames();
            case 4:
                return Assets.getBuildingEFrames();
            case 5:
                return Assets.getBuildingFFrames();
            case 6:
                return Assets.getWallFrames();
            default:
                return Assets.getBuildingAFrames();
        }
    }
}
