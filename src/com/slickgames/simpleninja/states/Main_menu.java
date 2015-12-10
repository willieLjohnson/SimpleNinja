package com.slickgames.simpleninja.states;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyActor;
import com.slickgames.simpleninja.handlers.MyInput;
import com.slickgames.simpleninja.handlers.MyInputProcessor;
import com.slickgames.simpleninja.main.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
/**
 * Created by Administrator on 12/8/2015.
 */
public  class Main_menu extends ApplicationAdapter {
    private Skin skin;
    private Stage stage;

    private Table table;
    private TextButton startButton;
    private TextButton quitButton;

    private SpriteBatch batch;
    private Sprite sprite;;

    public void create () {
        stage = new Stage(new FitViewport(Game.V_WIDTH,Game.V_HEIGHT));
        table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);

        table.setPosition(0,Gdx.graphics.getHeight());
        startButton = new TextButton("New Game",skin);
        quitButton = new TextButton("Quit Game",skin);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.setState(GameStateManager.PLAY);
                event.stop();
            }
        });

        table.padTop(30);

        table.add(startButton).padBottom(30);

        table.row();
        table.add(quitButton);

        stage.addActor(table);
        batch = new SpriteBatch();
        sprite = new Sprite(new Texture(Gdx.files.internal("res/maps/Main1.gif")));
        sprite.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        InputMultiplexer im = new InputMultiplexer(stage);
        Gdx.input.setInputProcessor(im);



        //assets.load("res/maps/Main1.gif", Texture.class);
       // assets.load("res/maps/Main2.gif", Texture.class);
    }

    public void resize (int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        sprite.draw(batch);
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }



    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }


    public void dispose() {
        stage.dispose();
    }}