package com.roaringcatgames.ld34.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.roaringcatgames.ld34.VolcanoGame;
import com.roaringcatgames.ld34.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = Constants.height;
		config.width = Constants.width;
		new LwjglApplication(new VolcanoGame(), config);
	}
}
