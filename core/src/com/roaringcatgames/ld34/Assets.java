package com.roaringcatgames.ld34;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

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
        am.load(SPRITE_ATLAS, TEXTURE_ATLAS);
        am.load(TITLE_SONG, MUSIC);
        am.load(WAVE_ONE_SONG, MUSIC);
        am.load(WAVE_TWO_SONG, MUSIC);
        am.load(WAVE_THREE_SONG, MUSIC);
        am.load(SMALL_IMPACT, SOUND);
        am.load(MED_IMPACT, SOUND);
        am.load(VOLCANO_RUMBLE, SOUND);
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
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Fireball/Fireball");
    }
    public static Array<TextureAtlas.AtlasRegion> getLavaBallExplodingFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Fireball/Fireball");
    }

    public static TextureRegion getDirt(){
        return am.get(SPRITE_ATLAS, TEXTURE_ATLAS).findRegion("Dirt");
    }
    public static TextureRegion getTreeLine(){
        return am.get(SPRITE_ATLAS, TEXTURE_ATLAS).findRegion("Trees");
    }

    public static TextureRegion getBackground(){
        return am.get(SPRITE_ATLAS, TEXTURE_ATLAS).findRegion("Background");
    }
    public static TextureRegion getBackGrass(){
        return am.get(SPRITE_ATLAS, TEXTURE_ATLAS).findRegion("Grass/GrassBack");
    }
    public static TextureRegion getFrontGrass(){
        return am.get(SPRITE_ATLAS, TEXTURE_ATLAS).findRegion("Grass/GrassFront");
    }

    private static ArrayMap<String, Array<TextureAtlas.AtlasRegion>> volcanoStateFrames;
    public static ArrayMap<String, Array<TextureAtlas.AtlasRegion>> getVolcanoStateFrames(){
        if(volcanoStateFrames == null){
            volcanoStateFrames = new ArrayMap<>();
            volcanoStateFrames.put("DEFAULT", am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("volcano/VolcanoDefault"));
            volcanoStateFrames.put("FIRING", am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("volcano/VolcanoFiring"));
            volcanoStateFrames.put("CHARGING", am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("volcano/VolcanoCharge"));
            volcanoStateFrames.put("ENDING", am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("volcano/VolcanoEnd"));
        }

        return volcanoStateFrames;
    }

    public static Array<TextureAtlas.AtlasRegion> getSafeBannerFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Menu/Safe");
    }

    public static Array<TextureAtlas.AtlasRegion> getPikemanFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("army/pikeman");
    }

    public static Array<TextureAtlas.AtlasRegion> getHorsemanFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("army/Horseman");
    }
    public static Array<TextureAtlas.AtlasRegion> getHitMeBubbleFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("army/Bubble");
    }

    public static Array<TextureAtlas.AtlasRegion> getBackCloudFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("clouds/CloudBack");
    }
    public static Array<TextureAtlas.AtlasRegion> getMidBackCloudFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("clouds/CloudMidB");
    }
    public static Array<TextureAtlas.AtlasRegion> getMidFrontCloudFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("clouds/CloudMidF");
    }
    public static Array<TextureAtlas.AtlasRegion> getFrontCloudFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("clouds/CloudFront");
    }
    public static Array<TextureAtlas.AtlasRegion> getCloudPuffWhiteFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("clouds/CloudPuffWhite");
    }
    public static Array<TextureAtlas.AtlasRegion> getCloudPuffBlueFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("clouds/CloudPuffBlue");
    }

    public static TextureRegion getHoldTextRegion(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegion("Menu/Hold");
    }

    public static TextureRegion getReleaseTextRegion(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegion("Menu/Release");
    }

    public static Array<TextureAtlas.AtlasRegion> getFireTextFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Menu/Fire");
    }
    public static Array<TextureAtlas.AtlasRegion> getFFrame(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Menu/FStill");
    }
    public static Array<TextureAtlas.AtlasRegion> getFDownFrame(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Menu/F");
    }

    public static Array<TextureAtlas.AtlasRegion> getJFrame(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Menu/JStill");
    }
    public static Array<TextureAtlas.AtlasRegion> getJDownFrame(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Menu/J");
    }


    public static Array<TextureAtlas.AtlasRegion> getBuildingAFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Buildings/Building_A");
    }
    public static Array<TextureAtlas.AtlasRegion> getBuildingBFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Buildings/Building_B");
    }
    public static Array<TextureAtlas.AtlasRegion> getBuildingCFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Buildings/Building_C");
    }
    public static Array<TextureAtlas.AtlasRegion> getBuildingDFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Buildings/Building_D");
    }
    public static Array<TextureAtlas.AtlasRegion> getBuildingEFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Buildings/Building_E");
    }
    public static Array<TextureAtlas.AtlasRegion> getBuildingFFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Buildings/Building_F");
    }
    public static Array<TextureAtlas.AtlasRegion> getWallFrames(){
        return am.get(ANI_ATLAS, TEXTURE_ATLAS).findRegions("Buildings/Wall");
    }



    public static Music getTitleMusic(){
        return am.get(TITLE_SONG, MUSIC);
    }
    public static Music getWaveOneMusic(){
        return am.get(WAVE_ONE_SONG, MUSIC);
    }
    public static Music getWaveTwoMusic(){
        return am.get(WAVE_TWO_SONG, MUSIC);
    }
    public static Music getWaveThreeMusic(){
        return am.get(WAVE_THREE_SONG, MUSIC);
    }

    public static Sound getSmallImpact(){
        return am.get(SMALL_IMPACT, SOUND);
    }
    public static Sound getMediumImpact(){
        return am.get(MED_IMPACT, SOUND);
    }
    public static Sound getVolcanoRumble(){
        return am.get(VOLCANO_RUMBLE, SOUND);
    }




    private static Class<TextureAtlas> TEXTURE_ATLAS = TextureAtlas.class;
    private static Class<Music> MUSIC = Music.class;
    private static Class<BitmapFont> BITMAP_FONT = BitmapFont.class;
    private static Class<Sound> SOUND = Sound.class;

    private static final String FONT = "fonts/courier-new-bold-32.fnt";
    private static final String LOADING_ATLAS = "animations/loading.atlas";
    private static final String ANI_ATLAS = "animations/animations.atlas";
    private static final String SPRITE_ATLAS = "sprites/sprites.atlas";

    private static final String TITLE_SONG = "music/title-music.mp3";
    private static final String WAVE_ONE_SONG = "music/wave-one-music.mp3";
    private static final String WAVE_TWO_SONG = "music/wave-two-music.mp3";
    private static final String WAVE_THREE_SONG = "music/wave-three-music.mp3";
    private static final String SMALL_IMPACT = "sfx/small-impact.mp3";
    private static final String MED_IMPACT = "sfx/med-impact.mp3";
    private static final String VOLCANO_RUMBLE = "sfx/volcano-rumble.mp3";
}
