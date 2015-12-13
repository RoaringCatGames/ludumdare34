package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.Assets;
import com.roaringcatgames.ld34.ZUtil;
import com.roaringcatgames.ld34.components.*;

/**
 * Created by barry on 12/13/15 @ 11:20 AM.
 */
public class ArmySpawnerSystem extends IteratingSystem{

    private ComponentMapper<ArmySpawnerComponent> asm;
    private ComponentMapper<TransformComponent> tm;
    private Array<Entity> armySpawners;

    public ArmySpawnerSystem(){
        super(Family.all(ArmySpawnerComponent.class, TransformComponent.class).get());
        asm = ComponentMapper.getFor(ArmySpawnerComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
        armySpawners = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for(Entity spawner:armySpawners){
            ArmySpawnerComponent asc = asm.get(spawner);
            TransformComponent tc = tm.get(spawner);

            asc.elapsedTime += deltaTime;
            if(asc.elapsedTime - asc.lastSpawnTime >= asc.intervalSeconds || asc.lastSpawnTime == 0f){

                Entity armyItem = ((PooledEngine)getEngine()).createEntity();
                armyItem.add(ArmyUnitComponent.create());
                armyItem.add(TextureComponent.create());

                armyItem.add(TransformComponent.create()
                    .setPosition(tc.position.x, tc.position.y, ZUtil.ArmyZ)
                    .setScale(1f * asc.direction, 1f));
                armyItem.add(AnimationComponent.create()
                    .addAnimation("DEFAULT", new Animation(1f / 10f, Assets.getPikemanFrames())));
                armyItem.add(StateComponent.create()
                    .set("DEFAULT"));
                armyItem.add(VelocityComponent.create()
                    .setSpeed(5f * asc.direction, 0f));
                armyItem.add(KinematicComponent.create());

                getEngine().addEntity(armyItem);
                asc.lastSpawnTime = asc.elapsedTime;
            }

        }

        armySpawners.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ArmySpawnerComponent asc = asm.get(entity);
        if(asc.isActive){
            armySpawners.add(entity);
        }
    }
}
