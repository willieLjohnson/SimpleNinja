package com.slickgames.simpleninja;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.slickgames.simpleninja.handlers.MyInput;
import com.slickgames.simpleninja.handlers.font.SmartFontGenerator;
import com.slickgames.simpleninja.states.GameState;
import com.slickgames.simpleninja.states.MainMenu;
import com.slickgames.simpleninja.states.Play;

public class SimpleNinja extends Game {
	public static final int V_WIDTH = 1366 / 4;
	public static final int V_HEIGHT = 720 / 4;
	public static final String TITLE = "Simple Ninja";
	public static final double GAME_VERSION = 1;
	public static final int SCALE = 2;

	private static final float STEP = 1 / 60f;

	public boolean debug = false;

	private BitmapFont fontSmall, fontMedium, fontLarge;
	private AssetManager assets;
	private double difficulty = 1; // .5 CHEEZ, 1 normal, 2 hard?
	private Play play;

	@Override
	public void create() {
		// fonts
		SmartFontGenerator fontGen = new SmartFontGenerator();
		FileHandle exoFile = Gdx.files.local("ui/acknowtt.ttf");
		fontSmall = fontGen.createFont(exoFile, "exo-small", 12);
		fontMedium = fontGen.createFont(exoFile, "exo-medium", 24);
		fontLarge = fontGen.createFont(exoFile, "exo-large", 120);

		//load assets
		assets = new AssetManager();

		//player assets
		assets.load("images/simple_run.png", Texture.class);
		assets.load("images/simple_attack.png", Texture.class);
		assets.load("images/simple_idle.png", Texture.class);

		assets.load("images/simple_block.png", Texture.class);
		assets.load("images/simple_throw2.png", Texture.class);
		assets.load("images/simple_throw1.png", Texture.class);

		//enemy assets
		assets.load("images/enemy_idle.png", Texture.class);
		assets.load("images/enemy_attack.png", Texture.class);
		assets.load("images/enemy_run.png", Texture.class);

		//enemy variants
//        assets.load("images/big_enemy_idle.png", Texture.class);
//        assets.load("images/big_enemy_attack.png", Texture.class);
//        assets.load("images/big_enemy_run.png", Texture.class);

		//misc
		assets.load("images/crystal.png", Texture.class);
		assets.load("images/throw_knife.png", Texture.class);

		//main menu
		assets.load("menu/waterfallBackground.Png", Texture.class);
		assets.load("music/waterfallMusic.mp3", Music.class);
		assets.load("menu/twilightBackground.png", Texture.class);

		//sfx
		assets.load("sfx/hit/hita .wav", Sound.class);
		assets.load("sfx/hit/hit2.wav", Sound.class);
		assets.load("sfx/hit/hit3.wav", Sound.class);
		assets.load("sfx/simpleStep1.wav", Sound.class);
		assets.load("sfx/simpleStep2.wav", Sound.class);

		//Options
		assets.load("ui/knob.png", Texture.class);
		assets.load("ui/difficulty.png", Texture.class);
		assets.load("menu/optBack.png", Texture.class);

		// reports progress for loading all assets
		while (!assets.update()) {
			System.out.println(assets.getProgress() * 100 + "%");
		}
		System.out.println(assets.getProgress() * 100 + "%");
		System.out.println(assets.getAssetNames() + " 100% loaded");

		setScreen(new MainMenu(this));
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(40 / 255f, 38 / 255f, 33 / 255f, 1f);

		((GameState) getScreen()).update(STEP);
		((GameState) getScreen()).render();

		MyInput.update();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public AssetManager getAssetManager() {
		return assets;
	}

	public BitmapFont getFont(String type) {
		switch (type) {
			case "small":
				return fontSmall;
			case "med":
				return fontMedium;
			case "big":
				return fontLarge;
			default:
				return fontMedium;
		}
	}

	public double getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(double d) {
		difficulty = d;
	}

	public Play getPlay() {
		return play;
	}

	public void setPlay(Play play) {
		this.play = play;
	}
}
