package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.main.SimpleNinja;

public class MainMenu extends GameState {

    private Animation menuAnimation;
    private Sprite animationSprite;
    private Music mainMenuMusic;

    public MainMenu(SimpleNinja game) {
        super(game);

        Skin skin = new Skin(Gdx.files.internal("res/ui/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/ui/uiskin.atlas")));

        Table table = new Table();
        table.setFillParent(true);

        float dp = Gdx.graphics.getDensity();

        // text button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("default-round");
        textButtonStyle.down = skin.getDrawable("default-round-down");
        textButtonStyle.pressedOffsetX = 1 * dp;
        textButtonStyle.pressedOffsetY = -1 * dp;
        textButtonStyle.font = getGame().getFont("med");
        textButtonStyle.fontColor = Color.BLACK;

        Label heading = new Label(SimpleNinja.TITLE, new Label.LabelStyle(game.getFont("med"), Color.WHITE));
        heading.setFontScale(1.2f);

        TextButton startButton = new TextButton("Start", textButtonStyle);
        TextButton optionsButton = new TextButton("Options", textButtonStyle);
        TextButton quitButton = new TextButton("Quit", textButtonStyle);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setPlay(new Play(game));
                game.setScreen(game.getPlay());
                event.stop();
            }
        });
        startButton.pad(12 * dp);

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Options(game));
                event.stop();
            }
        });
        optionsButton.pad(12 * dp);

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });
        quitButton.pad(12 * dp);

        table.add(heading);
        table.getCell(heading).spaceBottom(25 * dp);
        table.row();
        table.add(startButton);
        table.getCell(startButton).spaceBottom(10 * dp);
        table.row();
        table.add(optionsButton);
        table.getCell(optionsButton).spaceBottom(10 * dp);
        table.row();
        table.add(quitButton);

        stage.addActor(table);

        // animations
        menuAnimation = new Animation();

        mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("res/music/waterfallMusic.mp3"));

        TextureRegion[] menuTexReg = TextureRegion.split(game.getAssetManager().get("res/menu/waterfallBackground.Png"), 500, 475)[0];
        menuAnimation.setFrames(menuTexReg, 1 / 25f);

        animationSprite = new Sprite(menuAnimation.getFrame());

        // handle input
        Gdx.input.setInputProcessor(stage);

        // sfx
        mainMenuMusic.play();
        mainMenuMusic.setLooping(true);
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void update(float dt) {
        menuAnimation.update(dt);
        animationSprite = new Sprite(menuAnimation.getFrame());
        animationSprite.setSize(SimpleNinja.V_WIDTH, SimpleNinja.V_HEIGHT);
        animationSprite.flip(true, false);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        animationSprite.draw(sb);
        sb.end();

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
        mainMenuMusic.dispose();
        stage.dispose();
    }
}