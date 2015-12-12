package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by barry on 12/12/15 @ 12:11 PM.
 */
public class LavaBallEmitterComponent implements Component {

    public Vector2 emissionVelocity = new Vector2(0f, 0f);
    public int triggerKey = Input.Keys.ANY_KEY;

    public static LavaBallEmitterComponent create(){
        return new LavaBallEmitterComponent();
    }

    public LavaBallEmitterComponent setEmissionVelocity(Vector2 vel){
        this.emissionVelocity.set(vel.x, vel.y);
        return this;
    }

    public LavaBallEmitterComponent setEmissionVelocity(float x, float y){
        this.emissionVelocity.set(x, y);
        return this;
    }

    public LavaBallEmitterComponent setTriggerKey(int key){
        this.triggerKey = key;
        return this;
    }
}
