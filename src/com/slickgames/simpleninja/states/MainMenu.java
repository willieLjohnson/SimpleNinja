package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.slickgames.simpleninja.handlers.GameStateManager;

/**
 * Created by Administrator on 12/8/2015.
 */
public class MainMenu extends GameState {
    private Skin skin;
//    private Stage stage;

    private Table table;
    private TextButton startButton;
    private TextButton quitButton;

    private SpriteBatch batch;
    private Sprite sprite;

    public MainMenu(GameStateManager gsm) {
        super(gsm);
//        stage = new Stage(new FitViewport(Game.V_WIDTH,Game.V_HEIGHT));
        skin = new Skin(Gdx.files.internal("res/maps/uiskin.json"));
        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center | Align.center);
        startButton = new TextButton("Start", skin);
        quitButton = new TextButton("Quit", skin);
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
        InputMultiplexer im = new InputMultiplexer(gsm.game().stage);
        Gdx.input.setInputProcessor(im);

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
        gsm.game().stage.act(Gdx.graphics.getDeltaTime());
        gsm.game().stage.draw();

    }


    public void dispose() {
        game.stage.dispose();
    }
}