package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.ActionProcessor;
import com.roaringcatgames.ld34.components.AnimationComponent;
import com.roaringcatgames.ld34.components.StateComponent;
import com.roaringcatgames.ld34.components.VolcanoComponent;

/**
 * Created by barry on 12/13/15 @ 9:56 AM.
 */
public class VolcanoSystem extends IteratingSystem {

    private int[] firingKeys;
    private ComponentMapper<StateComponent> sm;
    private ComponentMapper<AnimationComponent> am;

    private Array<Entity> volcanoes;

    public VolcanoSystem(int...listenKeys){
        super(Family.all(VolcanoComponent.class, StateComponent.class, AnimationComponent.class).get());
        sm = ComponentMapper.getFor(StateComponent.class);
        am = ComponentMapper.getFor(AnimationComponent.class);
        this.firingKeys = listenKeys;
        this.volcanoes = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean isFiring = false;
        boolean isCharging = false;

        for(int key:firingKeys) {
            if (ActionProcessor.isKeyDown(key)) {
                isCharging = true;
            }
            if (ActionProcessor.isKeyJustReleased(key)) {
                isFiring = true;
            }
        }

        for(Entity e:volcanoes) {
            StateComponent sc = sm.get(e);
            AnimationComponent ac = am.get(e);
            if (isFiring && sc.get() != "FIRING") {
                sc.set("FIRING");
                sc.setLooping(false);
            }else if(isCharging && sc.get() != "CHARGING" &&
                    (sc.get() != "FIRING" ||
                    ac.animations.get(sc.get()).isAnimationFinished(sc.time))){
                sc.set("CHARGING");
                sc.setLooping(true);
            }else if(!isCharging && !isFiring &&
                    ac.animations.get(sc.get()).isAnimationFinished(sc.time)){
                sc.set("DEFAULT");
                sc.setLooping(true);
            }
        }

        volcanoes.clear();

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        volcanoes.add(entity);
    }
}
