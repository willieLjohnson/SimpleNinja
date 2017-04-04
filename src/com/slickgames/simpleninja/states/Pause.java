package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slickgames.simpleninja.entities.Enemy;
import com.slickgames.simpleninja.handlers.MyInputProcessor;
import com.slickgames.simpleninja.main.SimpleNinja;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pause extends GameState {

    private final TextButton quitButton, resetButton;
    private final TextField cmd;

    public Pause(SimpleNinja game) {
        super(game);

        cam = new OrthographicCamera();

        cam.setToOrtho(false, SimpleNinja.V_WIDTH, SimpleNinja.V_HEIGHT);
        viewPort.setCamera(cam);
        stage = new Stage(viewPort);

        Skin skin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));
        Table table = new Table();
        table.setFillParent(true);

        Label heading = new Label("Pause", new Label.LabelStyle(game.fontMedium, Color.WHITE));
        heading.setFontScale(1.2f);

        // font and density
        float dp = Gdx.graphics.getDensity();

        // text button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("default-round");
        textButtonStyle.down = skin.getDrawable("default-round-down");
        textButtonStyle.pressedOffsetX = 1 * dp;
        textButtonStyle.pressedOffsetY = -1 * dp;
        textButtonStyle.font = game.fontMedium;
        textButtonStyle.fontColor = Color.BLACK;

        // cmd style
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = skin.getDrawable("textfield");
        textFieldStyle.selection = skin.getDrawable("selection");
        textFieldStyle.font = game.fontSmall;
        textFieldStyle.fontColor = Color.WHITE;

        resetButton = new TextButton("Reset", textButtonStyle);
        quitButton = new TextButton("Menu", textButtonStyle);

        cmd = new TextField("", textFieldStyle);
        cmd.setWidth(SimpleNinja.V_WIDTH);
        cmd.setMessageText("Type Command");

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.play.dispose();
                game.play = new Play(game);
                game.setScreen(game.play);
                event.stop();
            }
        });
        resetButton.pad(12*dp);

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
                event.stop();
            }
        });

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

        stage.addActor(table);
        stage.addActor(cmd);

        // handle input
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.play);
            Gdx.input.setInputProcessor(new MyInputProcessor());
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
                    game.debug = !game.debug;
                    break;
                case "ss":
                    game.play.player.setMaxSpeed(Float.parseFloat(param2));
                    break;
                case "tai":
                    game.play.enemyAi = !game.play.enemyAi;
                    game.play.createEnemy(200000);
                    break;
                case "fling":
                    game.play.player.getBody().applyLinearImpulse(121, 0, 0, 0, true);
                    break;
                case "tip":
                    game.play.ignorePlayer = !game.play.ignorePlayer;
                    break;
                case "ae":
                    game.play.createEnemy(Integer.parseInt(param2));
                    break;
                case "kaf":
                    for (Enemy e: game.play.enemies) {
                        game.play.cl.bodiesToRemove.add(e.getBody());
                    }
                    game.play.enemies.clear();
                    break;
                case "op":
                    game.play.player.op = !game.play.player.op;
                    break;
                case "proj":
                    game.play.player.ammo= 30;
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
        game.play.render();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
