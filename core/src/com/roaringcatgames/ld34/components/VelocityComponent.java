package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by barry on 12/12/15 @ 1:07 AM.
 */
public class VelocityComponent implements Component {
    public Vector2 speed = new Vector2();
    public Vector2 acceleration = new Vector2();
}
