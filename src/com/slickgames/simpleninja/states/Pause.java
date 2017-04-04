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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slickgames.simpleninja.entities.Enemy;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyInputProcessor;
import com.slickgames.simpleninja.main.Game;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pause extends GameState {


    private final TextButton quitButton, resetButton;
    private final TextField cmd;

    InputMultiplexer im;
    private ExtendViewport viewPort;

    public Pause(GameStateManager gsm) {
        super(gsm);
        cam = new OrthographicCamera();

        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
        game.viewPort.setCamera(cam);
        game.stage = new Stage(game.viewPort);

        Skin skin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));
        Table table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);

        Label heading = new Label("Pause", new Label.LabelStyle(Game.game.fontMedium, Color.WHITE));
        heading.setFontScale(1.2f);

        // font and density
        float dp = Gdx.graphics.getDensity();

        // text button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("default-round");
        textButtonStyle.down = skin.getDrawable("default-round-down");
        textButtonStyle.pressedOffsetX = 1 * dp;
        textButtonStyle.pressedOffsetY = -1 * dp;
        textButtonStyle.font = Game.game.fontMedium;
        textButtonStyle.fontColor = Color.BLACK;

        // cmd style
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = skin.getDrawable("textfield");
        textFieldStyle.selection = skin.getDrawable("selection");
        textFieldStyle.font = Game.game.fontSmall;
        textFieldStyle.fontColor = Color.WHITE;

        skin.add("default-font", Game.game.fontMedium, BitmapFont.class);

        resetButton = new TextButton("Reset", textButtonStyle);
        quitButton = new TextButton("Menu", textButtonStyle);

        cmd = new TextField("", textFieldStyle);
        cmd.setWidth(Game.V_WIDTH);
        cmd.setMessageText("Type Command");

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.play = new Play(gsm);
                gsm.setState(GameStateManager.PLAY);
                event.stop();
            }
        });
        resetButton.pad(12*dp);

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
        quitButton.pad(12 * dp);

        table.add(heading);
        table.getCell(heading).spaceBottom(15 * dp);
        table.row();
        table.add(resetButton);
        table.getCell(resetButton).spaceBottom(10 * dp);
        table.row();
        table.add(quitButton);
        table.getCell(quitButton).spaceBottom(10 * dp);
        table.row();
//        table.add(cmd);

        game.stage.addActor(table);
        game.stage.addActor(cmd);

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
                case "ss":
                    gsm.play.player.setMaxSpeed(Float.parseFloat(param2));
                    break;
                case "tai":
                    gsm.play.enemyAi = !gsm.play.enemyAi;
                    gsm.play.createEnemy(200000);
                    break;
                case "fling":
                    gsm.play.player.getBody().applyLinearImpulse(121, 0, 0, 0, true);
                    break;
                case "tip":
                    gsm.play.ignorePlayer = !gsm.play.ignorePlayer;
                    break;
                case "ae":
                    gsm.play.createEnemy(Integer.parseInt(param2));
                    break;
                case "kaf":
                    for (Enemy e: gsm.play.enemies) {
                        gsm.play.cl.bodiesToRemove.add(e.getBody());
                    }
                    gsm.play.enemies.clear();
                    break;
                case "op":
                    gsm.play.player.op = !gsm.play.player.op;
                    break;
                case "proj":
                    gsm.play.player.ammo= 30;
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
