package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/13/15 @ 5:54 PM.
 */
public class DamageComponent implements Component{

    public float dps = 0f;

    public static DamageComponent create(){
        return new DamageComponent();
    }

    public DamageComponent setDPS(float dps){
        this.dps = dps;
        return this;
    }
}
