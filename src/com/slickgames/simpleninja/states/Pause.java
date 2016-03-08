package com.slickgames.simpleninja.states;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyInputProcessor;
import com.slickgames.simpleninja.main.Game;

public class Pause extends GameState {


    private final TextButton quitButton, reset;
    private final TextField cmd;
    BitmapFont font = new BitmapFont();
    Skin skin;
    Table table;
    InputMultiplexer im;
    private ExtendViewport viewPort;

    public Pause(GameStateManager gsm) {
        super(gsm);
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
        game.viewPort.setCamera(cam);
        game.stage = new Stage(game.viewPort);
        skin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));

        cmd = new TextField("", skin);
        cmd.setMessageText(";)");
        
        BitmapFont bt = new BitmapFont();
        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);
        Label.LabelStyle ls = new Label.LabelStyle(bt, Color.WHITE);
        Label label = new Label("Pause", ls);
        reset = new TextButton("Reset", skin);
        quitButton = new TextButton("Menu", skin);

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
        table.add(reset);
        table.row();
        table.add(quitButton);
        table.row();
        table.add(cmd);

        game.stage.addActor(table);

        // handle input

        Gdx.input.setInputProcessor(gsm.game().stage);
        
    }

    @Override
    public void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.setState(GameStateManager.PLAY);
            Gdx.input.setInputProcessor(new MyInputProcessor());
            game.viewPort.setCamera(game.getCamera());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
//            String[] c = cmd.getText().split("(?<=[a-zA-Z])(?=[1])|(?<=\\d)(?=\\D)");
            String c = cmd.getText();
            Pattern p = Pattern.compile(" *([a-zA-Z]+) ?([0-9]+(\\.[0-9]+)?)?");

            Matcher m = p.matcher(c);

            String param1 = "";
            String param2 = "";
            if (m.find()) {
                param1 = m.group(1);
                if (m.groupCount() > 1)
                    param2 = m.group(2);
            }
            cmd.setText("");
            switch (param1) {
                case "tdb":
                    gsm.debug = !gsm.debug;
                    break;
                case "setSpeed":
                    gsm.play.player.setMaxSpeed(Float.parseFloat(param2));
                    break;
                case "tai":
                    gsm.play.enemyAi = !gsm.play.enemyAi;
                    break;
                case "fling":
                    gsm.play.player.getBody().applyLinearImpulse(121, 0, 0, 0, true);
                    break;
                case "tip":
                    gsm.play.ignorePlayer = !gsm.play.ignorePlayer;
                    break;
                case "addEnemy":
                	gsm.play.createEnemy(Integer.parseInt(param2));
                	break;
                default:
                    cmd.setText("Error");
            }
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
