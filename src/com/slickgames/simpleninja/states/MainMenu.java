package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.main.Game;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;

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
    private Sprite sprite;
    Animation animation;
    TextureRegion[] MainMenu1S;
    int mainmenu = 1;
    Music MainMusic;

    public MainMenu(GameStateManager gsm) {
        super(gsm);

        //stage/////////////////////////
//        stage = new Stage(new FitViewport(Game.V_WIDTH,Game.V_HEIGHT));
        skin = new Skin(Gdx.files.internal("res/maps/uiskin.json"));

        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.right);
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
            Texture Mainmenu1 = Game.game.getAssetManager().get("res/maps/Main1.Png");
            MainMenu1S = TextureRegion.split(Mainmenu1, 500, 475)[0];
            animation.setFrames(MainMenu1S, 1 / 25f);
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

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.begin();
        sb.draw(animation.getFrame(), 0, 0);
        sb.end();
        gsm.game().stage.act(Gdx.graphics.getDeltaTime());
        gsm.game().stage.draw();


    }


    public void dispose() {
        game.stage.dispose();
        MainMusic.dispose();
    }
}