package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.GameScreen;
import com.roaringcatgames.ld34.components.*;

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
    private ComponentMapper<HealthComponent> hm;
    private ComponentMapper<DamageComponent> dm;

    public ArmyUnitSystem(GameScreen game){
        super(Family.one(ArmyUnitComponent.class, BuildingComponent.class).get());
        bm = ComponentMapper.getFor(BoundsComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        am = ComponentMapper.getFor(ArmyUnitComponent.class);
        bdm = ComponentMapper.getFor(BuildingComponent.class);
        hm = ComponentMapper.getFor(HealthComponent.class);
        dm = ComponentMapper.getFor(DamageComponent.class);

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
            boolean isColliding = false;
            for(Entity building:buildings){
                BoundsComponent bb = bm.get(building);
                if(bb.bounds.overlaps(ub.bounds)){
                    isColliding = true;
                    vc.setSpeed(0f, 0f);
                    //Get Building Health here
                    HealthComponent buildingHealth = hm.get(building);
                    HealthComponent unitHealth = hm.get(unit);
                    DamageComponent buildingDamage = dm.get(building);
                    DamageComponent unitDamage = dm.get(unit);
                    if(buildingHealth != null){
                        float newHealth = buildingHealth.health - (unitDamage.dps*deltaTime);
                        buildingHealth.health = Math.max(newHealth, 0f);
                        float newUnitHealth = unitHealth.health - (buildingDamage.dps*deltaTime);
                        unitHealth.health = Math.max(newUnitHealth, 0f);

                        if(buildingHealth.health == 0f){
                            getEngine().removeEntity(building);
                        }
                        if(unitHealth.health == 0f){
                            getEngine().removeEntity(unit);
                        }
                    }
                    break;
                }
            }
            if(!isColliding && vc.speed.x == 0f && !unit.isScheduledForRemoval()){
                TransformComponent tc = unit.getComponent(TransformComponent.class);
                float xVel = 5f;
                if(tc.scale.x > 0){
                    xVel *= -1f;
                }
                vc.setSpeed(xVel, 0f);
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
