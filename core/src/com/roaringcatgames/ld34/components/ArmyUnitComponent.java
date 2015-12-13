package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/13/15 @ 12:22 PM.
 */
public class ArmyUnitComponent implements Component {

    public boolean isSpeaking = false;
    public String unitType = "pike";
    public static ArmyUnitComponent create(){
        return new ArmyUnitComponent();
    }

    public ArmyUnitComponent setUnitType(String unitType){
        this.unitType = unitType;
        return this;
    }
}
