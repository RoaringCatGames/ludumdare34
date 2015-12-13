package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.roaringcatgames.ld34.components.BoundsComponent;
import com.roaringcatgames.ld34.components.ScreenWrapComponent;
import com.roaringcatgames.ld34.components.TransformComponent;

/**
 * Created by barry on 12/12/15 @ 1:21 PM.
 */
public class CleanUpSystem extends IteratingSystem {

    private Rectangle bounds;
    private ComponentMapper<TransformComponent> tm;

    public CleanUpSystem(Rectangle bounds) {
        super(Family.all(TransformComponent.class)
                .exclude(ScreenWrapComponent.class).get());
        tm = ComponentMapper.getFor(TransformComponent.class);
        this.bounds = bounds;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tf = tm.get(entity);

        if(tf.position.x < bounds.x ||
           tf.position.x > bounds.x + bounds.width ||
           tf.position.y < bounds.y ||
           tf.position.y > bounds.y + bounds.height){
            entity.removeAll();
            this.getEngine().removeEntity(entity);
        }
    }
}
