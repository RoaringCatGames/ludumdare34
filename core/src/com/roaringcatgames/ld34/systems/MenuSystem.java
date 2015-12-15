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

/**
 * Created by barry on 12/13/15 @ 1:32 PM.
 */
public class MenuSystem extends IteratingSystem {

    private GameScreen game;
    private Array<Entity> buttons;
    private Array<Entity> lavaBalls;
    private Array<Entity> armyUnits;
    private Array<Entity> menuItems;

    private ComponentMapper<StateComponent> sm;
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<BoundsComponent> bm;

    private ComponentMapper<ButtonComponent> btnm;
    private ComponentMapper<LavaBallComponent> lbm;
    private ComponentMapper<ArmyUnitComponent> aum;
    private ComponentMapper<VelocityComponent> vm;
    private ComponentMapper<StateComponent> statem;
    private ComponentMapper<MenuItemComponent> mim;

    private int fireballCount = 0;
    private boolean hasGeneratedTargets = false;

    Entity leftBubble;
    Entity rightBubble;


    public MenuSystem(GameScreen game){
        super(Family.one(ButtonComponent.class, LavaBallComponent.class, ArmyUnitComponent.class, MenuItemComponent.class).get());
        this.game = game;
        sm = ComponentMapper.getFor(StateComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
        bm = ComponentMapper.getFor(BoundsComponent.class);
        btnm = ComponentMapper.getFor(ButtonComponent.class);
        lbm = ComponentMapper.getFor(LavaBallComponent.class);
        aum = ComponentMapper.getFor(ArmyUnitComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        statem = ComponentMapper.getFor(StateComponent.class);
        mim = ComponentMapper.getFor(MenuItemComponent.class);

        buttons = new Array<>();
        lavaBalls = new Array<>();
        armyUnits = new Array<>();
        menuItems = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(ActionProcessor.isKeyJustReleased(Input.Keys.F) || ActionProcessor.isKeyJustReleased(Input.Keys.J)){
            fireballCount += 1;
        }

        if(hasGeneratedTargets && armyUnits.size == 0){
            Gdx.app.log("Menu System", "Menu Items count:" + menuItems.size);
            for(Entity e:menuItems){
                TransformComponent tc = tm.get(e);
                tc.hide();
            }
            game.doEvent("MENUOVER");

            return;
        }

        for(Entity e:buttons){
            checkButtonState(e);
        }

        for(Entity e:armyUnits){
            //Check if hit by lavaball
            TransformComponent tc = tm.get(e);
            VelocityComponent vc = vm.get(e);
            BoundsComponent bc = bm.get(e);
            ArmyUnitComponent ac = aum.get(e);
            StateComponent sc = statem.get(e);

            if(vc.speed.x > 0 && tc.position.x >= 10f ||
                    vc.speed.x < 0 && tc.position.x <= 50f){


                if(!ac.isSpeaking){

                    Entity bubble = ((PooledEngine)getEngine()).createEntity();
                    bubble.add(MenuItemComponent.create());
                    bubble.add(TextureComponent.create());
                    bubble.add(AnimationComponent.create()
                        .addAnimation("DEFAULT", new Animation(1f / 9f, Assets.getHitMeBubbleFrames())));
                    bubble.add(StateComponent.create()
                            .set("DEFAULT")
                        .setLooping(true));
                    bubble.add(TransformComponent.create()
                        .setPosition(tc.position.x, tc.position.y + 4f, tc.position.z));

                    getEngine().addEntity(bubble);
                    ac.isSpeaking = true;
                    if(vc.speed.x > 0){
                        //pikeman
                        bubble.add(TransformComponent.create()
                                .setPosition(tc.position.x, tc.position.y + 4f, tc.position.z));
                        leftBubble = bubble;
                    }else{
                        bubble.add(TransformComponent.create()
                                .setPosition(tc.position.x, tc.position.y + 6f, tc.position.z));
                        rightBubble = bubble;
                    }
                }

                vc.setSpeed(0f, 0f);
                sc.setLooping(false);

            }

            for(Entity lava:lavaBalls){
                TransformComponent lc = tm.get(lava);
                if(bc.bounds.contains(lc.position.x, lc.position.y)){
                    getEngine().removeEntity(e);

                    if(ac.unitType == "pike"){
                        getEngine().removeEntity(leftBubble);
                    }else{
                        getEngine().removeEntity(rightBubble);
                    }
                }
            }
        }

        if(!hasGeneratedTargets && fireballCount > 2){
            generateTargets();
        }

        buttons.clear();
        armyUnits.clear();
        lavaBalls.clear();
        menuItems.clear();
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
        Entity leftUnit = buildUnitComponent(-1.25f, 5f, -1f, 10f, false);
        Entity rightUnit = buildUnitComponent(61.25f, 5f, 1f, -10f, true);

        getEngine().addEntity(leftUnit);
        getEngine().addEntity(rightUnit);

        hasGeneratedTargets = true;
    }

    private Entity buildUnitComponent(float x, float y, float scaleX, float xSpeed, boolean isHorse) {
        Entity e = ((PooledEngine)getEngine()).createEntity();
        e.add(TextureComponent.create());
        String unitType = isHorse ? "horse" : "pike";
        e.add(ArmyUnitComponent.create()
            .setUnitType(unitType));
        e.add(TransformComponent.create()
                .setPosition(x, y, ZUtil.ArmyZ)
                .setScale(2f * scaleX, 2f));
        if(isHorse) {
            e.add(AnimationComponent.create()
                    .addAnimation("DEFAULT", new Animation(1f / 10f, Assets.getHorsemanFrames())));
        }else{
            e.add(AnimationComponent.create()
                    .addAnimation("DEFAULT", new Animation(1f / 10f, Assets.getPikemanFrames())));
        }

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

        MenuItemComponent mic = mim.get(entity);
        if(mic != null){
            menuItems.add(entity);
        }
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

        if(mic != null){
            return;
        }

        Gdx.app.log("MenuSystem", "Entity Doesn't have proper COmponents somehow");


    }
}
