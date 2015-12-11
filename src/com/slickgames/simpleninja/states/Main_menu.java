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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
public class Main_menu extends GameState {
    private Skin skin;
//    private Stage stage;

    private Table table;
    //    private TextButton startButton;
//    private TextButton quitButton;
    private Button startButton;
    private Button quitButton;
    private SpriteBatch batch;
    private Sprite sprite;

    public Main_menu(GameStateManager gsm) {
        super(gsm);
//        stage = new Stage(new FitViewport(Game.V_WIDTH,Game.V_HEIGHT));
        table = new Table();
        table.setWidth(game.stage.getWidth());
        table.align(Align.center | Align.top);

        table.setPosition(0, Gdx.graphics.getHeight());
        startButton = new Button();
        quitButton = new Button();
        startButton.setName("Start");
        quitButton.setName("quit");
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

        game.stage.addActor(table);
        batch = new SpriteBatch();
        sprite = new Sprite(new Texture(Gdx.files.internal("res/maps/Main1.gif")));
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        InputMultiplexer im = new InputMultiplexer(game.stage);
//        Gdx.input.setInputProcessor(im);


        //assets.load("res/maps/Main1.gif", Texture.class);
        // assets.load("res/maps/Main2.gif", Texture.class);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.begin();
        sprite.draw(sb);
        sb.end();
        game.stage.act(Gdx.graphics.getDeltaTime());
        game.stage.draw();

    }


    public void dispose() {
        game.stage.dispose();
    }
}