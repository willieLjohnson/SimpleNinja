package com.slickgames.simpleninja.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyInput;
import com.slickgames.simpleninja.handlers.MyInputProcessor;

public class Game implements ApplicationListener {
    public static final String TITLE = "Simple Ninja v0.1 ALPHA";
    public static final int V_WIDTH = 1366 / 4;
    public static final int V_HEIGHT = 768/ 4;
    public static final int SCALE = 2;
    public Stage stage;
    public static final float STEP = 1 / 60f;
    public static Game game;
    public AssetManager assets;
    private float accum;
    private SpriteBatch sb;
    private OrthographicCamera cam;
    private OrthographicCamera hudCam;
    private GameStateManager gsm;

    @Override
    public void create() {
        game = this;

        Gdx.input.setInputProcessor(new MyInputProcessor());
        stage=new Stage(new FitViewport(Game.V_WIDTH, Game.V_HEIGHT));
        //load assets
        assets = new AssetManager();
        assets.load("res/images/simple_runAll.png", Texture.class);
        assets.load("res/maps/Main1.gif", Texture.class);
        assets.load("res/images/simple_attackAll.png", Texture.class);
        assets.load("res/images/simple_idleAll.png", Texture.class);
        assets.load("res/images/enemy_idleAll.png", Texture.class);
        assets.load("res/images/crystal.png", Texture.class);
        assets.load("res/images/hud.png", Texture.class);

        while (!assets.update()) {
            System.out.println(assets.getProgress()*100 + "%");
        }
        System.out.println(assets.getProgress()*100 + "%");
        System.out.println(assets.getAssetNames() + " 100% loaded");

        sb = new SpriteBatch();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);

        gsm = new GameStateManager(this);

    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void render() {
        accum += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(53 / 255f, 49 / 255f, 42 / 255f, 1f);
        while (accum >= STEP) {
            accum -= STEP;
            gsm.update(STEP);
            gsm.render();
            MyInput.update();
        }
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

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

    public OrthographicCamera getHUDCamera() {
        return hudCam;
    }

}
