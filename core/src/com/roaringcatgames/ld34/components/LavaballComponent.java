package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/12/15 @ 12:03 PM.
 */
public class LavaBallComponent implements Component{

    public static LavaBallComponent create(){
        return new LavaBallComponent();
    }
}
