package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.ZUtil;
import com.roaringcatgames.ld34.components.*;

/**
 * Created by barry on 12/12/15 @ 12:00 PM.
 */
public class LavaBallSystem extends IteratingSystem {

    private Sound lavaHitSound;
    private float absMaxRotation = 75f;
    private float maxScale = 1f;
    private float rotationRate = 60f;
    private float scaleRate = 0.1f;

    private Array<Entity> lavaBalls;
    private Array<Entity> units;
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<VelocityComponent> vm;
    private ComponentMapper<ArmyUnitComponent> am;
    private ComponentMapper<BoundsComponent> bm;
    private ComponentMapper<HealthComponent> hm;
    private ComponentMapper<DamageComponent> dm;

    public LavaBallSystem(Sound lavaHitSound) {
        super(Family
                .all(VelocityComponent.class, TransformComponent.class)
                .one(LavaBallComponent.class, ArmyUnitComponent.class)
                .get());
        tm = ComponentMapper.getFor(TransformComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        am = ComponentMapper.getFor(ArmyUnitComponent.class);
        bm = ComponentMapper.getFor(BoundsComponent.class);
        hm = ComponentMapper.getFor(HealthComponent.class);
        dm = ComponentMapper.getFor(DamageComponent.class);

        lavaBalls = new Array<>();
        units = new Array<>();
        this.lavaHitSound = lavaHitSound;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for(Entity unit:units){
            BoundsComponent bc = bm.get(unit);
            for(Entity lava:lavaBalls) {
                TransformComponent tc = tm.get(lava);
                if (bc.bounds.contains(tc.position.x, tc.position.y)){

                    DamageComponent ld = dm.get(lava);
                    HealthComponent unitHealth = hm.get(unit);
                    if(unitHealth != null) {
                        unitHealth.health = Math.max(0f, (unitHealth.health - ld.dps * deltaTime));
                    }else {
                        getEngine().removeEntity(unit);
                    }
                }
            }
        }

        for(Entity entity:lavaBalls){
            //lavaBalls.add(entity);
            TransformComponent tc = tm.get(entity);
            VelocityComponent vc = vm.get(entity);

            if(vc.speed.y > 0){
                tc.position.set(tc.position.x, tc.position.y, ZUtil.FireballBackZ);
            }else{
                tc.position.set(tc.position.x, tc.position.y, ZUtil.FireballFrontZ);
            }

            float currentScale = tc.scale.x;
            float newScale;
            if(currentScale < 0){
                newScale = currentScale - (scaleRate*deltaTime);
                newScale = Math.max(-maxScale, newScale);

                tc.rotation += rotationRate*deltaTime;
                tc.rotation = Math.min(tc.rotation, absMaxRotation);
            }else{
                newScale = currentScale + (scaleRate*deltaTime);
                newScale = Math.min(maxScale, newScale);

                tc.rotation -= rotationRate*deltaTime;
                tc.rotation = Math.max(tc.rotation, -absMaxRotation);
            }
            tc.scale.set(newScale, newScale);

            if(tc.position.y <= 1f){
                lavaHitSound.play();
                entity.removeAll();
                getEngine().removeEntity(entity);
            }
        }




        lavaBalls.clear();
        units.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ArmyUnitComponent ac = am.get(entity);

        if(ac != null){
            units.add(entity);
        }else{
            lavaBalls.add(entity);
        }
    }
}
