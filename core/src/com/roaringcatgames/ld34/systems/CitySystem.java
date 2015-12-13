package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.GameScreen;
import com.roaringcatgames.ld34.components.BuildingComponent;

/**
 * Created by barry on 12/13/15 @ 6:37 PM.
 */
public class CitySystem extends IteratingSystem {

    private GameScreen game;

    private Array<Entity> buildings;
    public CitySystem(GameScreen game){
        super(Family.all(BuildingComponent.class).get());
        this.game = game;
        buildings = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(buildings.size == 0){
            game.doEvent("GAMEOVER");
        }

        buildings.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        buildings.add(entity);
    }
}
