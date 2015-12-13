package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.roaringcatgames.ld34.ActionProcessor;
import com.roaringcatgames.ld34.Assets;
import com.roaringcatgames.ld34.ZUtil;
import com.roaringcatgames.ld34.components.*;

/**
 * Created by barry on 12/12/15 @ 12:09 PM.
 */
public class LavaBallEmitterSystem extends IteratingSystem {

    private ComponentMapper<LavaBallEmitterComponent> lbem;
    private ComponentMapper<TransformComponent> tm;
    private ArrayMap<Integer, Array<Entity>> emitterMap;
    private ArrayMap<Integer, Float> charges;

    private float _maxChargeModifier = 2f;
    private float _chargeIncrease =6f;

    public LavaBallEmitterSystem() {
        super(Family.all(LavaBallEmitterComponent.class).get());
        lbem = ComponentMapper.getFor(LavaBallEmitterComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
        emitterMap = new ArrayMap<>();
        charges = new ArrayMap<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for(int key : emitterMap.keys()) {
            if (ActionProcessor.isKeyJustReleased(key)) {
                for (Entity e : emitterMap.get(key)) {
                    LavaBallEmitterComponent comp = lbem.get(e);
                    TransformComponent tfc = tm.get(e);
                    this.getEngine().addEntity(buildLavaBall(tfc.position,
                            comp.emissionVelocity.cpy().scl(charges.get(key))));
                    charges.put(key, 0f);
                }
            }else if (ActionProcessor.isKeyDown(key)) {
                float currentCharge = charges.get(key);
                if(currentCharge < _maxChargeModifier){
                    //Throttle to max charge
                    currentCharge = Math.min(_maxChargeModifier, (currentCharge + _chargeIncrease*deltaTime));
                    charges.put(key, currentCharge);
                }

            }

            //Clear all the entities
            emitterMap.get(key).clear();
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LavaBallEmitterComponent lbe = lbem.get(entity);
        if(!emitterMap.containsKey(lbe.triggerKey)){
            emitterMap.put(lbe.triggerKey, new Array<Entity>());
        }

        if(!charges.containsKey(lbe.triggerKey)){
            charges.put(lbe.triggerKey, 0.1f);
        }

        emitterMap.get(lbe.triggerKey).add(entity);
    }

    private Entity buildLavaBall(Vector3 origin, Vector2 vel){
        Entity e = ((PooledEngine)this.getEngine()).createEntity();

        e.add(LavaBallComponent.create());

        e.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f / 16f, Assets.getLavaBallFrames()))
                .addAnimation("EXPLODING", new Animation(1f / 16f, Assets.getLavaBallExplodingFrames(), Animation.PlayMode.NORMAL)));

        e.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(true));

        e.add(TextureComponent.create());

        float xScale = vel.x < 0f ? -0.25f : 0.25f;
        e.add(TransformComponent.create()
                .setPosition(origin.x, origin.y, ZUtil.VolcanoZ + 1f)
                .setRotation(15f)
                .setScale(xScale, 1f));

        e.add(VelocityComponent.create()
                .setSpeed(vel.x, vel.y));

        e.add(BoundsComponent.create()
            .setBounds(0f, 0f, 3f, 3f));

        return e;
    }
}
