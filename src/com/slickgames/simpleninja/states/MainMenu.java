package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.main.Game;

public class MainMenu extends GameState {
    Animation menuAnimation;
    TextureRegion[] menuTexReg;
    int menuVariant = 1;
    Music mainMenuMusic;
    private TextButton OptionsButton;
    private Skin skin;
    private Table table;
    private TextButton startButton;
    private TextButton quitButton;
    private Sprite animationSprite;

    public MainMenu(GameStateManager gsm) {
        super(gsm);

        skin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));

        BitmapFont bt = new BitmapFont();
        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);
        Label.LabelStyle ls = new Label.LabelStyle(bt, Color.GRAY);
        Label label = new Label("Simple Ninja 1.0", ls);
        startButton = new TextButton("Start", skin);
        OptionsButton = new TextButton("Options", skin);
        quitButton = new TextButton("Quit", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.setState(GameStateManager.PLAY);
                event.stop();
            }
        });

        OptionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.setState(GameStateManager.Option);
                event.stop();
            }
        });
        table.padTop(20);
        table.add(label);
        table.row();
        table.add(startButton);
        table.row();
        table.add(OptionsButton);
        table.row();
        table.add(quitButton);

        game.stage.addActor(table);

        // animations
        menuAnimation = new Animation();
        if (menuVariant == 1) {
            mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("res/music/waterfall_music.mp3"));

            menuTexReg = TextureRegion.split(Game.game.getAssetManager().get("res/menu/waterfall_animation.Png"), 500, 475)[0];
            menuAnimation.setFrames(menuTexReg, 1 / 25f);
        }
        if (menuVariant == 2) {
            //mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("res/music/treeBob_music.mp3"));

            menuTexReg = TextureRegion.split(Game.game.getAssetManager().get("res/menu/Treebob.Png"), 500, 475)[0];
            menuAnimation.setFrames(menuTexReg, 1 / 1f);
        }
        animationSprite = new Sprite(menuAnimation.getFrame());
        animationSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // handle input
        InputMultiplexer im = new InputMultiplexer(gsm.game().stage);
        Gdx.input.setInputProcessor(im);

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
        animationSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        animationSprite.flip(true, false);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.begin();
        animationSprite.draw(sb);
        sb.end();
        gsm.game().stage.act(Gdx.graphics.getDeltaTime());
        gsm.game().stage.draw();
    }


    @Override
    public void dispose() {
        game.stage.dispose();
        mainMenuMusic.dispose();
    }
}