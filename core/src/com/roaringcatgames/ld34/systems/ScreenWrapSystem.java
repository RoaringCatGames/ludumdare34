package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.roaringcatgames.ld34.components.*;

/**
 * Created by barry on 12/12/15 @ 5:52 PM.
 */
public class ScreenWrapSystem extends IteratingSystem {

    ComponentMapper<BoundsComponent> bm;
    ComponentMapper<VelocityComponent> vm;
    ComponentMapper<TransformComponent> tm;
    ComponentMapper<TextureComponent>txm;

    private float left;
    private float right;

    public ScreenWrapSystem(float leftSide, float rightSide){
        super(Family.all(TransformComponent.class, VelocityComponent.class, ScreenWrapComponent.class)
                .one(BoundsComponent.class, TextureComponent.class).get());
        this.left = leftSide;
        this.right = rightSide;

        bm = ComponentMapper.getFor(BoundsComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
        txm = ComponentMapper.getFor(TextureComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {


        BoundsComponent bc = bm.get(entity);
        VelocityComponent vc = vm.get(entity);
        TransformComponent tc = tm.get(entity);

        if(bc == null){
            TextureComponent txc = txm.get(entity);
            float regionWidthInMeters = txc.region.getRegionWidth()*RenderingSystem.PIXELS_TO_METRES;
            if(vc.speed.x > 0 &&
               tc.position.x - (regionWidthInMeters/2f) > right){

                Gdx.app.log("ScreenWrapSystem", "Initial X Position: " + tc.position.x);
                tc.position.set(left - regionWidthInMeters / 2f, tc.position.y, tc.position.z);
                Gdx.app.log("ScreenWrapSystem", "New X Position: " + tc.position.x);
            }else if(
               (vc.speed.x < 0 &&
               (regionWidthInMeters/2f) + tc.position.x < left)){

                tc.position.set(right + regionWidthInMeters/2f,
                        tc.position.y, tc.position.z);
            }
        }else {
            if (bc.bounds.x > right && vc.speed.x > 0) {
                 tc.position.set(left - (bc.bounds.width/2f), tc.position.y, tc.position.z);
            } else if (bc.bounds.x + bc.bounds.width < left && vc.speed.x < 0) {
                tc.position.set(right + (bc.bounds.width/2f), tc.position.y, tc.position.z);
            }
        }
    }
}
