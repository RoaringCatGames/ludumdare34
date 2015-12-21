package com.roaringcatgames.ld34;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by barry on 12/20/15 @ 7:26 PM.
 */
public class ScreenHelper {

    public static final float PPM = 16.0f;
    public static final float MPP = 1.0f/PPM;

    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        meterDimensions.set(Gdx.graphics.getWidth()*MPP,
                Gdx.graphics.getHeight()*MPP);
        return meterDimensions;
    }

    public static Vector2 getScreenSizeInPixesl(){
        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return pixelDimensions;
    }

    public static float PixelsToMeters(float pixelValue){
        return pixelValue * PPM;
    }
}
