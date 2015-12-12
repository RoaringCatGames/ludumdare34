package com.roaringcatgames.ld34;

import com.badlogic.gdx.ScreenAdapter;

/**
 * Created by barry on 12/12/15 @ 10:57 AM.
 *
 * Lazy initialized Screens will wait until the render loop
 * calls one time before
 */
public abstract class LazyInitScreen extends ScreenAdapter {

    protected boolean isInitialized = false;

    abstract void init();
    abstract void update(float detltaChange);

    @Override
    public void render(float delta) {
        if(!isInitialized) {
            init();
            isInitialized = true;
        }

        update(delta);
    }
}
