package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.ActionProcessor;
import com.roaringcatgames.ld34.Assets;
import com.roaringcatgames.ld34.GameScreen;
import com.roaringcatgames.ld34.ZUtil;
import com.roaringcatgames.ld34.components.*;

import java.security.Key;

/**
 * Created by barry on 12/13/15 @ 1:32 PM.
 */
public class MenuSystem extends IteratingSystem {

    private Array<Entity> buttons;
    private Array<Entity> lavaBalls;
    private Array<Entity> armyUnits;

    private ComponentMapper<StateComponent> sm;
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<BoundsComponent> bm;

    private ComponentMapper<ButtonComponent> btnm;
    private ComponentMapper<LavaBallComponent> lbm;
    private ComponentMapper<ArmyUnitComponent> aum;
    private ComponentMapper<VelocityComponent> vm;

    private int fireballCount = 0;
    private boolean hasGeneratedTargets = false;


    public MenuSystem(){
        super(Family.one(ButtonComponent.class, LavaBallComponent.class, ArmyUnitComponent.class).get());

        sm = ComponentMapper.getFor(StateComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
        bm = ComponentMapper.getFor(BoundsComponent.class);
        btnm = ComponentMapper.getFor(ButtonComponent.class);
        lbm = ComponentMapper.getFor(LavaBallComponent.class);
        aum = ComponentMapper.getFor(ArmyUnitComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);

        buttons = new Array<>();
        lavaBalls = new Array<>();
        armyUnits = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(ActionProcessor.isKeyJustReleased(Input.Keys.F) || ActionProcessor.isKeyJustReleased(Input.Keys.J)){
            fireballCount += 1;
        }

        if(hasGeneratedTargets && armyUnits.size == 0){
            this.setProcessing(false);
        }
        for(Entity e:buttons){
            checkButtonState(e);
        }

        for(Entity e:armyUnits){
            //Check if hit by lavaball
            TransformComponent tc = tm.get(e);
            VelocityComponent vc = vm.get(e);
            BoundsComponent bc = bm.get(e);
            if(vc.speed.x > 0 && tc.position.x >= 10f ||
                    vc.speed.x < 0 && tc.position.x <= 50f){
                vc.setSpeed(0f, 0f);
            }

            for(Entity lava:lavaBalls){
                TransformComponent lc = tm.get(lava);
                if(bc.bounds.contains(lc.position.x, lc.position.y)){
                    getEngine().removeEntity(e);
                }
            }
        }

        if(!hasGeneratedTargets && fireballCount > 2){
            generateTargets();
        }

        buttons.clear();
        armyUnits.clear();
        lavaBalls.clear();
    }

    private void checkButtonState(Entity button){
        int key = btnm.get(button).key;
        StateComponent sc = sm.get(button);

        if(ActionProcessor.isKeyDown(key) && sc.get() != "PRESSED"){
            sc.set("PRESSED");
        }else if(ActionProcessor.isKeyJustReleased(key)){
            sc.set("DEFAULT");
        }
    }

    private void generateTargets(){
        Entity leftUnit = buildUnitComponent(-1.25f, 2.5f, 1f, 10f);
        Entity rightUnit = buildUnitComponent(61.25f, 2.5f, -1f, -10f);

        getEngine().addEntity(leftUnit);
        getEngine().addEntity(rightUnit);

        hasGeneratedTargets = true;
    }

    private Entity buildUnitComponent(float x, float y, float scaleX, float xSpeed) {
        Entity e = ((PooledEngine)getEngine()).createEntity();
        e.add(TextureComponent.create());
        e.add(ArmyUnitComponent.create());
        e.add(TransformComponent.create()
                .setPosition(x, y, ZUtil.ArmyZ)
                .setScale(scaleX, 1f));
        e.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f / 10f, Assets.getPikemanFrames())));
        e.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(true));
        e.add(VelocityComponent.create()
                .setSpeed(xSpeed, 0f));
        e.add(KinematicComponent.create());
        e.add(BoundsComponent.create()
            .setBounds(0f, 0f, 4f, 8f));
        return e;
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ButtonComponent bc = btnm.get(entity);
        if(bc != null){
            buttons.add(entity);
            return;
        }

        LavaBallComponent lc = lbm.get(entity);
        if(lc != null){
            lavaBalls.add(entity);
            return;
        }

        ArmyUnitComponent ac = aum.get(entity);
        if(ac != null){
            armyUnits.add(entity);
            return;
        }

        Gdx.app.log("MenuSystem", "Entity Doesn't have proper COmponents somehow");


    }
}
