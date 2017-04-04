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
    Music mainMenuMusic;
    private TextButton optionsButton;
    private Skin skin;
    private Table table;
    private TextButton startButton;
    private TextButton quitButton;
    private Sprite animationSprite;

    private Label heading;

    public MainMenu(GameStateManager gsm) {
        super(gsm);

        skin = new Skin(new TextureAtlas(Gdx.files.local("res/ui/button.pack")));

        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);

        heading = new Label(Game.TITLE, new Label.LabelStyle(Game.game.fontMedium, Color.WHITE));
        heading.setFontScale(1.2f);

        float dp = Gdx.graphics.getDensity();
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("button.up");
        textButtonStyle.down = skin.getDrawable("button.down");
        textButtonStyle.pressedOffsetX = 1 * dp;
        textButtonStyle.pressedOffsetY = -1 * dp;
        textButtonStyle.font = Game.game.fontMedium;
        textButtonStyle.fontColor = Color.BLACK;

        startButton = new TextButton("Start", textButtonStyle);
        optionsButton = new TextButton("Options", textButtonStyle);
        quitButton = new TextButton("Quit", textButtonStyle);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.setState(GameStateManager.PLAY);
                dispose();
                event.stop();
            }
        });
        startButton.pad(12 * dp);

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.setState(GameStateManager.Option);
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

        game.stage.addActor(table);

        // animations
        menuAnimation = new Animation();

        mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("res/music/waterfall_music.mp3"));

        menuTexReg = TextureRegion.split(Game.game.getAssetManager().get("res/menu/waterfall_animation.Png"), 500, 475)[0];
        menuAnimation.setFrames(menuTexReg, 1 / 25f);

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
        mainMenuMusic.dispose();
        gsm.game().stage.clear();
        skin.dispose();

    }
}