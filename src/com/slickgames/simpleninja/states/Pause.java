package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyInputProcessor;
import com.slickgames.simpleninja.main.Game;

public class Pause extends GameState {


    private final TextButton debug, quitButton, reset;
    BitmapFont font = new BitmapFont();
    Skin skin;
    Table table;
    InputMultiplexer im;


    public Pause(GameStateManager gsm) {
        super(gsm);
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
        game.stage = new Stage(new FitViewport(Game.V_WIDTH, Game.V_HEIGHT));
        skin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));

        BitmapFont bt = new BitmapFont();
        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);
        Label.LabelStyle ls = new Label.LabelStyle(bt, Color.WHITE);
        Label label = new Label("Pause", ls);
        debug = new TextButton("Debug", skin);
        reset = new TextButton("Reset", skin);
        quitButton = new TextButton("Menu", skin);
        debug.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.debug = !gsm.debug;
                event.stop();
            }
        });
        reset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.play = new Play(gsm);
                gsm.setState(GameStateManager.PLAY);
                event.stop();
            }
        });

        /* Currently Broken
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.stage = new Stage(new FitViewport(Game.V_WIDTH, Game.V_HEIGHT));
                gsm.setState(GameStateManager.MAIN_MENU);
                event.stop();
            }
        });
        */
        table.padTop(20);
        table.add(label);
        table.row();
        table.add(debug);
        table.row();
        table.add(reset);
        table.row();
        table.add(quitButton);

        game.stage.addActor(table);

        // handle input

        Gdx.input.setInputProcessor(gsm.game().stage);
    }

    @Override
    public void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.setState(GameStateManager.PLAY);
            Gdx.input.setInputProcessor(new MyInputProcessor());
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render() {
        gsm.play.render();
        gsm.game().stage.act(Gdx.graphics.getDeltaTime());
        gsm.game().stage.draw();

    }

    @Override
    public void dispose() {

    }
}
