package com.slickgames.simpleninja.main;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Launcher {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.vSyncEnabled = true;
        cfg.title = SimpleNinja.TITLE + " " + SimpleNinja.GAME_VERSION;
        cfg.addIcon("res/images/SimpleNinja1_0.png", Files.FileType.Internal);
        cfg.width = SimpleNinja.V_WIDTH * SimpleNinja.SCALE;
        cfg.height = SimpleNinja.V_HEIGHT * SimpleNinja.SCALE;
        new LwjglApplication(new SimpleNinja(), cfg);
    }
}
 