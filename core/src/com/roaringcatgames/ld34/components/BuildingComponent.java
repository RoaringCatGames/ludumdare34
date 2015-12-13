package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/13/15 @ 4:43 PM.
 */
public class BuildingComponent implements Component {
    public static BuildingComponent create(){
        return new BuildingComponent();
    }
}
