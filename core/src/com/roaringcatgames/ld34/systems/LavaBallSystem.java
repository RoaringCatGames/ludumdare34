package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.ZUtil;
import com.roaringcatgames.ld34.components.LavaBallComponent;
import com.roaringcatgames.ld34.components.TransformComponent;
import com.roaringcatgames.ld34.components.VelocityComponent;

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
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<VelocityComponent> vm;


    public LavaBallSystem(Sound lavaHitSound) {
        super(Family
                .all(VelocityComponent.class, TransformComponent.class)
                .one(LavaBallComponent.class)
                .get());
        tm = ComponentMapper.getFor(TransformComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);

        lavaBalls = new Array<>();
        this.lavaHitSound = lavaHitSound;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        //lavaBalls.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
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
}
