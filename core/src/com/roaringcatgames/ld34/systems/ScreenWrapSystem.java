package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.roaringcatgames.ld34.components.BoundsComponent;
import com.roaringcatgames.ld34.components.ScreenWrapComponent;
import com.roaringcatgames.ld34.components.TransformComponent;
import com.roaringcatgames.ld34.components.VelocityComponent;

/**
 * Created by barry on 12/12/15 @ 5:52 PM.
 */
public class ScreenWrapSystem extends IteratingSystem {

    ComponentMapper<BoundsComponent> bm;
    ComponentMapper<VelocityComponent> vm;
    ComponentMapper<TransformComponent> tm;

    private float left;
    private float right;

    public ScreenWrapSystem(float leftSide, float rightSide){
        super(Family.all(TransformComponent.class, VelocityComponent.class, ScreenWrapComponent.class).get());
        this.left = leftSide;
        this.right = rightSide;

        bm = ComponentMapper.getFor(BoundsComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {


        BoundsComponent bc = bm.get(entity);
        VelocityComponent vc = vm.get(entity);
        TransformComponent tc = tm.get(entity);

        //REDO!!
        if((bc.bounds.x > right && vc.speed.x > 0) ||
                (bc.bounds.x + bc.bounds.width < left && vc.speed.x < 0)    ){
            tc.scale.x *= -1f;
            vc.speed.x *= -1f;
        }
    }
}
