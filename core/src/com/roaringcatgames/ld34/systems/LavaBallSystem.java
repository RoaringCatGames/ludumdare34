package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.components.LavaBallComponent;

/**
 * Created by barry on 12/12/15 @ 12:00 PM.
 */
public class LavaBallSystem extends IteratingSystem {

    private Array<Entity> lavaBalls;

    public LavaBallSystem() {
        super(Family.all(LavaBallComponent.class).get());
        lavaBalls = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        //lavaBalls.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //lavaBalls.add(entity);
    }
}
