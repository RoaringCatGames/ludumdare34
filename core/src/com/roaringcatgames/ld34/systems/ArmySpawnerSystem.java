package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.kitten2d.ashley.components.*;
import com.roaringcatgames.ld34.Assets;
import com.roaringcatgames.ld34.ZUtil;
import com.roaringcatgames.ld34.components.ArmySpawnerComponent;
import com.roaringcatgames.ld34.components.ArmyUnitComponent;

import java.util.Random;

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
        Random r = new Random();

        for(Entity spawner:armySpawners){
            ArmySpawnerComponent asc = asm.get(spawner);
            TransformComponent tc = tm.get(spawner);

            asc.elapsedTime += deltaTime;
            if(asc.elapsedTime - asc.lastSpawnTime >= asc.intervalSeconds || asc.lastSpawnTime == 0f){

                //Randomly Horses
                boolean isHorse = r.nextFloat() > 0.90f;

                Entity armyItem = ((PooledEngine)getEngine()).createEntity();
                armyItem.add(ArmyUnitComponent.create());
                armyItem.add(TextureComponent.create());

                float y = Math.max(5f, (r.nextFloat() * tc.position.y + 3f));
                armyItem.add(TransformComponent.create()
                    .setPosition(tc.position.x, y, ZUtil.ArmyZ)
                    .setScale(0.5f * -asc.direction, 0.5f));

                armyItem.add(StateComponent.create()
                    .set("DEFAULT")
                    .setLooping(true));
                armyItem.add(VelocityComponent.create()
                    .setSpeed(asc.baseUnitSpeed * asc.direction, 0f));

                if(isHorse) {
                    armyItem.add(AnimationComponent.create()
                            .addAnimation("DEFAULT", new Animation(1f / 10f, Assets.getHorsemanFrames())));
                    armyItem.add(BoundsComponent.create()
                            .setBounds(0f, 0f, 6f, 4f));
                    armyItem.add(DamageComponent.create()
                            .setDPS(2f));
                    armyItem.add(HealthComponent.create()
                            .setHealth(4f)
                            .setMaxHealth(4f));
                }else{
                    armyItem.add(AnimationComponent.create()
                            .addAnimation("DEFAULT", new Animation(1f / 10f, Assets.getPikemanFrames())));
                    armyItem.add(BoundsComponent.create()
                            .setBounds(0f, 0f, 4f, 4f));
                    armyItem.add(DamageComponent.create()
                            .setDPS(1f));
                    armyItem.add(HealthComponent.create()
                            .setHealth(2f)
                            .setMaxHealth(2f));
                }

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
