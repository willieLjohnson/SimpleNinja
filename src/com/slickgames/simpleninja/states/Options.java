package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.main.Game;

/**
 * Created by Administrator on 1/11/2016.
 */
public class Options extends GameState {
    TextButton graphics;
    Skin Lskin;
    Skin skin;
    TextureRegion[] background;
    Animation menuAnimation;
    private TextButton ResizeButton;
    private Slider Resize;
    private Table table;
    private Sprite animationSprite;

    public Options(GameStateManager gsm) {
        super(gsm);

        game.viewPort = new ExtendViewport(Game.V_WIDTH * Game.SCALE, Game.V_HEIGHT * Game.SCALE, cam);
        Lskin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));
        skin = new Skin(Gdx.files.internal("res/font/uiskin.json"));

        table = new Table();
        BitmapFont bt = new BitmapFont();
        //  labels
        Label.LabelStyle ls = new Label.LabelStyle(bt, Color.BLACK);
        Label label = new Label("Difficulty", ls);
        Label label2 = new Label("Easy    Normal   Hard", ls);
        //buttons
        // garphics
        HorizontalGroup group = new HorizontalGroup();
        final Button tab1 = new TextButton("Tab1", skin, "toggle");
        final Button tab2 = new TextButton("Tab2", skin, "toggle");
        final Button tab3 = new TextButton("Tab3", skin, "toggle");
        group.addActor(tab1);
        group.addActor(tab2);
        group.addActor(tab3);
        table.add(group);
        table.row();
        graphics = new TextButton("Graphics", Lskin);// need to make a better skin
        Stack content = new Stack();
        final Image content1 = new Image(skin.newDrawable("white", 1,0,0,1));
        final Image content2 = new Image(skin.newDrawable("white", 0,1,0,1));
        final Image content3 = new Image(skin.newDrawable("white", 0,0,1,1));
        content.addActor(content1);
        content.addActor(content2);
        content.addActor(content3);
        table.add(content).expand().fill();
        table.row();
        ChangeListener tab_listener = new ChangeListener(){
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                content1.setVisible(tab1.isChecked());
                content2.setVisible(tab2.isChecked());
                content3.setVisible(tab3.isChecked());
            }
        };
        tab1.addListener(tab_listener);
        tab2.addListener(tab_listener);
        tab3.addListener(tab_listener);

        // Let only one tab button be checked at a time
        ButtonGroup tabs = new ButtonGroup();
        tabs.setMinCheckCount(1);
        tabs.setMaxCheckCount(1);
        tabs.add(tab1);
        tabs.add(tab2);
        tabs.add(tab3);

        graphics.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                event.stop();
            }
        });
        //skin

        skin.add("knob", new Texture(Gdx.files.internal("res/Style/Knode.png")));
        skin.add("bgs", new Texture(Gdx.files.internal("res/Style/diffculty.png")));

        //table

        table.setFillParent(true);
//        table.setWidth(game.stage.getWidth());
//        table.setHeight(game.stage.getHeight());

        table.align(Align.center);
         //slider
        Slider.SliderStyle ResizeStyle = new Slider.SliderStyle();
        ResizeStyle.background = skin.getDrawable("bgs");
        ResizeStyle.knob= skin.getDrawable("knob");
        Resize = new Slider(0, 2, 1, false, ResizeStyle);
        Resize.setVisible(true);

        table.padTop(20);
        //graphics


        table.add(graphics);
        table.row();
        //diffculy slider
        table.add(label);
        table.row();
        table.add(label2);
        table.row();
        table.add(Resize);
        table.row();

        //stage

        game.stage.addActor(table);
        //aimations and backgroud
        menuAnimation = new Animation();
        background = TextureRegion.split(Game.game.getAssetManager().get("res/menu/optBack.png"), 500,500)[0];
        menuAnimation.setFrames(background, 1f);
        animationSprite = new Sprite(menuAnimation.getFrame());
        animationSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

    }
}
