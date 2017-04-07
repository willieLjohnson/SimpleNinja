package com.slickgames.simpleninja.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.slickgames.simpleninja.SimpleNinja;

public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.vSyncEnabled = true;
		cfg.title = SimpleNinja.TITLE + " " + SimpleNinja.GAME_VERSION;
		cfg.addIcon("images/SimpleNinja1_0.png", Files.FileType.Internal);
		cfg.width = SimpleNinja.V_WIDTH * SimpleNinja.SCALE;
		cfg.height = SimpleNinja.V_HEIGHT * SimpleNinja.SCALE;
		new LwjglApplication(new SimpleNinja(), cfg);
	}
}
