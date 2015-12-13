package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/13/15 @ 3:11 PM.
 */
public class MenuItemComponent implements Component{
    public static MenuItemComponent create(){
        return new MenuItemComponent();
    }
}
