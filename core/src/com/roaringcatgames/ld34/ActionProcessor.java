package com.roaringcatgames.ld34;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by barry on 12/12/15 @ 2:20 PM.
 */
public class ActionProcessor extends InputAdapter {

    private class KeyState{
        public boolean isPressed = false;
        public boolean isJustReleased = false;
    }

    private static ArrayMap<Integer, KeyState> keyStates = new ArrayMap<>();

    private Array<Integer> leftKeys;
    private Array<Integer> rightKeys;
    private static boolean isLeftPressed = false;
    private static boolean isRightPressed = false;
    private static boolean isLeftReleased = false;
    private static boolean isRightReleased = false;

    public static boolean isKeyDown(int key){
        if(keyStates.containsKey(key)){
            return keyStates.get(key).isPressed;
        }
        return false;
    }

    public static boolean isKeyJustReleased(int key){
        if(keyStates.containsKey(key)){
            return keyStates.get(key).isJustReleased;
        }
        return false;
    }

    public static void clear(){

        for(int key : keyStates.keys()){
            keyStates.get(key).isJustReleased = false;
        }
    }

    public ActionProcessor(int...keys){
        leftKeys = new Array<Integer>();
        rightKeys = new Array<Integer>();

        for(int key:keys){
            keyStates.put(key, new KeyState());
        }
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keyStates.containsKey(keycode)){
            keyStates.get(keycode).isPressed = true;
        }

        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {

        if(keyStates.containsKey(keycode)) {
            keyStates.get(keycode).isPressed = false;
            keyStates.get(keycode).isJustReleased = true;
        }

        return super.keyDown(keycode);
    }
}
