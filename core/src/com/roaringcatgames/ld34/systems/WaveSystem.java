package com.roaringcatgames.ld34.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.ld34.Assets;
import com.roaringcatgames.ld34.GameScreen;
import com.roaringcatgames.ld34.components.ArmySpawnerComponent;
import com.roaringcatgames.ld34.components.ArmyUnitComponent;

/**
 * Created by barry on 12/14/15 @ 7:06 PM.
 */
public class WaveSystem extends IteratingSystem {


    private boolean isWaving = false;
    private boolean isWaiting = false;
    private float elapsedWaveTime = 0f;
    private float timeBetweenWaves = 0f;
    private int wave = 1;

    Music marchingMusic;

    GameScreen game;
    private boolean isInitialized = false;

    private ComponentMapper<ArmySpawnerComponent> asm;
    private Array<Entity> units;
    private Array<Entity> spawners;

    public WaveSystem(GameScreen game){
        super(Family.one(ArmyUnitComponent.class, ArmySpawnerComponent.class).get());
        asm = ComponentMapper.getFor(ArmySpawnerComponent.class);
        this.game = game;
        units = new Array<>();
        spawners = new Array<>();
        marchingMusic = Assets.getMarching();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(!isInitialized){
            isWaving = true;
            game.doEvent("STARTWAVE");
            marchingMusic.play();
            marchingMusic.setLooping(true);
            marchingMusic.setVolume(0.75f);
            isInitialized = true;
        }

        if(isWaiting){
            timeBetweenWaves += deltaTime;
            if(timeBetweenWaves >= 5f){
                isWaiting = false;
                timeBetweenWaves = 0f;
                game.doEvent("STARTWAVE");
                marchingMusic.play();
                marchingMusic.setLooping(true);
                marchingMusic.setVolume(0.75f);
            }
        }

        if(isWaving && !isWaiting){
            elapsedWaveTime += deltaTime;
            float timeToKeepWaving = wave != 3 ? 60 : 71;
            if(elapsedWaveTime >= timeToKeepWaving){
                for(Entity spawner:spawners){
                    asm.get(spawner).isActive = false;
                }
                if(units.size == 0) {
                    Gdx.app.log("Wave System", "Wave " + wave + " finished");
                    elapsedWaveTime = 0f;
                    marchingMusic.stop();
                    wave++;
                    if(wave > 3){
                        game.doEvent("BEGINENDING");
                        isWaving = false;
                    }else {
                        isWaiting = true;
                        game.doEvent("STOPWAVE");

                    }
                }
            }
        }

        units.clear();
        spawners.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ArmySpawnerComponent asc = asm.get(entity);
        if(asc == null){
            units.add(entity);
        }else{
            spawners.add(entity);
        }

    }
}
