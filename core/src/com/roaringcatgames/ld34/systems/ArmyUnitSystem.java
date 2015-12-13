package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.GameScreen;
import com.roaringcatgames.ld34.components.ArmyUnitComponent;
import com.roaringcatgames.ld34.components.BoundsComponent;
import com.roaringcatgames.ld34.components.BuildingComponent;
import com.roaringcatgames.ld34.components.VelocityComponent;

/**
 * Created by barry on 12/13/15 @ 4:42 PM.
 */
public class ArmyUnitSystem extends IteratingSystem {

    private GameScreen game;
    private Array<Entity> units;
    private Array<Entity> buildings;

    private ComponentMapper<BoundsComponent> bm;
    private ComponentMapper<VelocityComponent> vm;
    private ComponentMapper<ArmyUnitComponent> am;
    private ComponentMapper<BuildingComponent> bdm;

    public ArmyUnitSystem(GameScreen game){
        super(Family.one(ArmyUnitComponent.class, BuildingComponent.class).get());
        bm = ComponentMapper.getFor(BoundsComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        am = ComponentMapper.getFor(ArmyUnitComponent.class);
        bdm = ComponentMapper.getFor(BuildingComponent.class);

        this.game = game;
        units = new Array<>();
        buildings = new Array<>();
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for(Entity unit:units){
            BoundsComponent ub = bm.get(unit);
            VelocityComponent vc = vm.get(unit);
            for(Entity building:buildings){
                BoundsComponent bb = bm.get(building);
                if(bb.bounds.overlaps(ub.bounds)){
                    vc.setSpeed(0f, 0f);
                    //Get Building Health here
                    break;
                }
            }
        }

        units.clear();
        buildings.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ArmyUnitComponent ac = am.get(entity);
        if(ac != null){
            units.add(entity);
        }else{
            buildings.add(entity);
        }


    }
}
