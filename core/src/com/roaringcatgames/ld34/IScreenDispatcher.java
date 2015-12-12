package com.roaringcatgames.ld34;

import com.badlogic.gdx.Screen;

/**
 * Created by barry on 12/8/15 @ 8:13 PM.
 */
public interface IScreenDispatcher {

    void endCurrentScreen();
    Screen getNextScreen();
}
