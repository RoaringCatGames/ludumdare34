package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/13/15 @ 1:33 PM.
 */
public class ButtonComponent implements Component {
    public int key;

    public static ButtonComponent create(){
        return new ButtonComponent();
    }

    public ButtonComponent setKey(int key){
        this.key = key;
        return this;
    }
}
