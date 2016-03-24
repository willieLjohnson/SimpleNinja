package com.slickgames.simpleninja.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyInput;


public class Game implements ApplicationListener {
    public static final String TITLE = "Simple Ninja v2.0";
    public static final int V_WIDTH = 1366 / 4;
    public static final int V_HEIGHT = 720 / 4;
    public static final int SCALE = 2;
    public static final float STEP = 1 / 60f;
    public static Game game;
    public Stage stage;
    public AssetManager assets;
    public Viewport viewPort;
    private float accum;
    private SpriteBatch sb;
    private OrthographicCamera cam;
    private GameStateManager gsm;

    @Override
    public void create() {
        game = this;


        //load assets
        assets = new AssetManager();

        //player assets
        assets.load("res/images/simple_run.png", Texture.class);
        assets.load("res/images/simple_attack.png", Texture.class);
        assets.load("res/images/simple_idle.png", Texture.class);
        assets.load("res/images/simple_block.png", Texture.class);
        assets.load("res/images/simple_throw2.png", Texture.class);
        assets.load("res/images/simple_throw1.png", Texture.class);


        //enemy assets
        assets.load("res/images/enemy_idle.png", Texture.class);
        assets.load("res/images/enemy_attack.png", Texture.class);
        assets.load("res/images/enemy_run.png", Texture.class);

        //misc
        assets.load("res/images/crystal.png", Texture.class);
        assets.load("res/images/hud.png", Texture.class);
        assets.load("res/images/throw_knife2.png", Texture.class);
        assets.load("res/images/throw_knife1.png", Texture.class);


        //main menu
        assets.load("res/menu/waterfall_animation.Png", Texture.class);
        assets.load("res/music/waterfall_music.mp3", Music.class);
        assets.load("res/menu/treeBob_animation.png", Texture.class);


        //sfx
        assets.load("res/sfx/hit/hita .wav", Sound.class);
        assets.load("res/sfx/hit/hit2.wav", Sound.class);
        assets.load("res/sfx/hit/hit3.wav", Sound.class);

        //Options
        assets.load("res/Style/Knode.png", Texture.class);
        assets.load("res/Style/onAndoff Slider.jpg", Texture.class);

        // reports progress for loading all assets
        while (!assets.update()) {
            System.out.println(assets.getProgress() * 100 + "%");
        }
        System.out.println(assets.getProgress() * 100 + "%");
        System.out.println(assets.getAssetNames() + " 100% loaded");

        sb = new SpriteBatch();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);


        viewPort = new ExtendViewport(Game.V_WIDTH, Game.V_HEIGHT, cam);
        stage = new Stage(viewPort);
        viewPort.apply();

        gsm = new GameStateManager(this);

    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(40 / 255f, 38 / 255f, 33 / 255f, 1f);

        gsm.update(STEP);
        gsm.render();
        MyInput.update();
    }

    @Override
    public void pause() {


    }

    @Override
    public void resume() {


    }

    @Override
    public void dispose() {


    }

    public AssetManager getAssetManager() {
        return assets;
    }

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public OrthographicCamera getCamera() {
        return cam;
    }

}
