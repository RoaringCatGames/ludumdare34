package com.roaringcatgames.ld34.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by barry on 12/13/15 @ 11:22 AM.
 */
public class ArmySpawnerComponent implements Component {

    public int direction = 1;
    public boolean isActive = false;
    public float intervalSeconds = 5f;
    public float lastSpawnTime = 0f;
    public float elapsedTime = 0f;

    public static ArmySpawnerComponent create(){
        return new ArmySpawnerComponent();
    }

    public ArmySpawnerComponent setDirection(int dir){
        this.direction = dir;
        return this;
    }

    public ArmySpawnerComponent setActive(boolean isActive){
        this.isActive = isActive;
        return this;
    }

    public ArmySpawnerComponent setIntervalSeconds(float seconds){
        this.intervalSeconds = seconds;
        return this;
    }

    public ArmySpawnerComponent setLastSpawnTime(float time){
        this.lastSpawnTime = time;
        return this;
    }

    public ArmySpawnerComponent setElapsedTime(float time){
        this.elapsedTime = time;
        return this;
    }
}
