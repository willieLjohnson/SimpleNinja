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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.main.SimpleNinja;

public class Options extends GameState {
    CheckBox shader;
    Skin Lskin;
    Skin skin;
    TextureRegion[] background;
    Animation menuAnimation;
    private TextButton ResizeButton;
    public Slider Diffculty;
    private Table table;
    private Sprite animationSprite;

    public Options(SimpleNinja game) {
        super(game);

        viewPort = new ExtendViewport(SimpleNinja.V_WIDTH * SimpleNinja.SCALE, SimpleNinja.V_HEIGHT * SimpleNinja.SCALE, cam);
        Lskin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));
        skin = new Skin(Gdx.files.internal("res/font/uiskin.json"));

        table = new Table();
        BitmapFont bt = new BitmapFont();
        //  labels
        Label.LabelStyle ls = new Label.LabelStyle(bt, Color.BLACK);
        Label label = new Label("Difficulty", ls);
        Label label2 = new Label("Easy    Normal   Hard", ls);
        //diffculty
        //skin
        skin.add("knob", new Texture(Gdx.files.internal("res/Style/Knode.png")));
        skin.add("bgs", new Texture(Gdx.files.internal("res/Style/diffculty.png")));


        //slider
        Slider.SliderStyle DiffcultyStyle = new Slider.SliderStyle();
        DiffcultyStyle.background = skin.getDrawable("bgs");
        DiffcultyStyle.knob= skin.getDrawable("knob");
        Diffculty = new Slider(1, 3, 1, false, DiffcultyStyle);
        Diffculty.setVisible(true);

        // garphics
        VerticalGroup group = new VerticalGroup();
        final Button Graphics = new TextButton("Graphics", skin, "toggle");
        final Button GamePlay = new TextButton("SimpleNinja Play", skin, "toggle");

        final Button tab3 = new TextButton("Tab3", skin, "toggle");

        group.addActor(Graphics);
        group.addActor(GamePlay);
        group.addActor(tab3);

        table.add(group).left();
        Stack content = new Stack();
//        final Image content1 = new Image(skin.newDrawable("white", 0,0,1,1));
//        content.addActor(content3);
        VerticalGroup GamPSet = new VerticalGroup();//gameplay
        GamPSet.addActor(label);
        GamPSet.addActor(label2);
        GamPSet.addActor(Diffculty);
        VerticalGroup GraphSet = new VerticalGroup();// graphics
        shader = new CheckBox("Shaders", Lskin);// need to make a better skin
GraphSet.addActor(shader);
        //content-actors
        content.add(GraphSet);
        content.add(GamPSet);
        //content-table
        table.add(content).center();
        table.row();
        //tab Action listener
        ChangeListener tab_listener = new ChangeListener(){
            @Override
            public void changed (ChangeEvent event, Actor actor) {
//                content1.setVisible(Graphics.isChecked());
//                content2.setVisible(GamePlay.isChecked());
//                content3.setVisible(tab3.isChecked());
                GamPSet.setVisible(GamePlay.isChecked());
                GraphSet.setVisible(shader.isChecked());
            }
        };
game.setDifficulty(Diffculty.getValue());
        Graphics.addListener(tab_listener);
        GamePlay.addListener(tab_listener);
        tab3.addListener(tab_listener);
        //D-slider
        ChangeListener Diffculy = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }

        };
        // Let only one tab button be checked at a time
        ButtonGroup tabs = new ButtonGroup();
        tabs.setMinCheckCount(1);
        tabs.setMaxCheckCount(1);
        tabs.add(Graphics);
        tabs.add(GamePlay);
        tabs.add(tab3);
        //table
        table.setFillParent(true);
        table.align(Align.center);
        table.padTop(20);
        //stage

        stage.addActor(table);
        //aimations and backgroud
        menuAnimation = new Animation();
        background = TextureRegion.split(game.getAssetManager().get("res/menu/optBack.png"), 500,500)[0];
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

    }

    @Override
    public void dispose() {

    }
}
