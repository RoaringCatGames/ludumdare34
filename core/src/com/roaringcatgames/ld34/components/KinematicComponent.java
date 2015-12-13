package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/12/15 @ 11:36 PM.
 */
public class KinematicComponent implements Component{
    public static KinematicComponent create(){
        return new KinematicComponent();
    }
}
