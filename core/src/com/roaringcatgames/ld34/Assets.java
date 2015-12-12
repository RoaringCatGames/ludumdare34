package com.roaringcatgames.ld34;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by barry on 12/9/15 @ 11:17 PM.
 */
public class Assets {

    public static AssetManager am;

    public static AssetManager load(){

        am = new AssetManager();
        am.load(LOADING_ATLAS, TEXTURE_ATLAS);
        am.finishLoading();
        am.load(ANI_ATLAS, TEXTURE_ATLAS);
        am.load(TITLE_SONG, MUSIC);
//        am.load(SPRITE_ATLAS, TEXTURE_ATLAS);
//        am.load(FONT, BITMAP_FONT);

        return am;
    }

    /***
     * SAFE IMMEDIATELY after am.load() is called
     * @return AtlasRegions for the loading animation.
     */
    public static Array<TextureAtlas.AtlasRegion> getLoadingFrames(){
        return am.get(LOADING_ATLAS, TEXTURE_ATLAS).findRegions("loading");
    }
    public static Array<TextureAtlas.AtlasRegion> getLavaBallFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("lavaball/lavaball");
    }
    public static Array<TextureAtlas.AtlasRegion> getLavaBallExplodingFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("lavaball/lavaball");
    }

    public static Music getTitleMusic(){
        return am.get(TITLE_SONG, MUSIC);
    }

    private static Class<TextureAtlas> TEXTURE_ATLAS = TextureAtlas.class;
    private static Class<Music> MUSIC = Music.class;
    private static Class<BitmapFont> BITMAP_FONT = BitmapFont.class;
    private static Class<Sound> SOUND = Sound.class;

    private static final String FONT = "fonts/courier-new-bold-32.fnt";
    private static final String LOADING_ATLAS = "animations/loading.atlas";
    private static final String ANI_ATLAS = "animations/animations.atlas";
    private static final String TITLE_SONG = "music/title-music.mp3";
    private static final String SPRITE_ATLAS = "sprites/sprites.atlas";


    public static TextureRegion splashScreen;
}
