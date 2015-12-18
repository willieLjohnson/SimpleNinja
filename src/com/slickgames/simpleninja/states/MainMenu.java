package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
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

/**
 * Created by Administrator on 12/8/2015.
 */
public class MainMenu extends GameState {
    private TextButton OptionsButton;
    private Skin skin;
//    private Stage stage;

    private Table table;
    private TextButton startButton;
    private TextButton quitButton;

    private SpriteBatch batch;
    private Sprite waterfall;
    Animation animation;
    TextureRegion[] MainMenu1S;
    int mainmenu = 1;
    Music MainMusic;

    public MainMenu(GameStateManager gsm) {
        super(gsm);

        //stage/////////////////////////
//        stage = new Stage(new FitViewport(Game.V_WIDTH,Game.V_HEIGHT));
        skin = new Skin(Gdx.files.internal("res/maps/uiskin.json") , new TextureAtlas(Gdx.files.internal("res/maps/uiskin.atlas")));

        BitmapFont bt = new BitmapFont();
        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);
        Label.LabelStyle ls = new Label.LabelStyle(bt, Color.WHITE);
        Label label = new Label("Simple Ninja 1.0", ls);
        startButton = new TextButton("Start", skin);
        OptionsButton = new TextButton("Option", skin);
        quitButton = new TextButton("Quit", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.setState(GameStateManager.PLAY);
                event.stop();
            }
        });

        table.padTop(30);
        table.add(label);
        table.row();
        table.add(startButton);
        table.row();
        table.add(OptionsButton);
        table.row();
        table.add(quitButton);
        //sounds//////////////////////////////////

        game.stage.addActor(table);
//        //animation//////////////////////////////
        animation = new Animation();
        if (mainmenu == 1) {
            MainMusic = Gdx.audio.newMusic(Gdx.files.internal("res/music/MainMusic.mp3"));

            MainMenu1S = TextureRegion.split(Game.game.getAssetManager().get("res/maps/Main1.Png"), 500, 475)[0];
            animation.setFrames(MainMenu1S, 1 / 25f);
            waterfall = new Sprite(animation.getFrame());
            waterfall.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        }
// if (mainmenu==1){
//            Texture Mainmenu1 = Game.game.getAssetManager().get("res/maps/test two.png");
//            MainMenu1S = TextureRegion.split(Mainmenu1, 375, 354)[0];
//            animation.setFrames(MainMenu1S, 1/1f);
//        }

        //input///////////////////////////////
        InputMultiplexer im = new InputMultiplexer(gsm.game().stage);
        Gdx.input.setInputProcessor(im);
        MainMusic.play();
        MainMusic.setLooping(true);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        animation.update(dt);
        waterfall = new Sprite(animation.getFrame());
        waterfall.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.begin();
        waterfall.draw(sb);
        sb.end();
        gsm.game().stage.act(Gdx.graphics.getDeltaTime());
        gsm.game().stage.draw();
    }


    @Override
    public void dispose() {
        game.stage.dispose();
        MainMusic.dispose();
    }
}