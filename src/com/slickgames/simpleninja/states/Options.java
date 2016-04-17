package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.main.Game;

/**
 * Created by Administrator on 1/11/2016.
 */
public class Options extends GameState {
    Skin skin;
    TextureRegion[] background;
    Animation menuAnimation;
    private TextButton ResizeButton;
    private Slider Resize;
    private Table table;
    private Sprite animationSprite;

    public Options(GameStateManager gsm) {
        super(gsm);//skin
        skin.add("knob", new Texture(Gdx.files.internal("res/Style/Knode.png")));
        skin.add("bgs", new Texture(Gdx.files.internal("res/Style/onAndoff Slider.jpg")));
        skin = new Skin();
        //table
        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);
   //slider
        Slider.SliderStyle ResizeStyle = new Slider.SliderStyle();
        ResizeStyle.background = skin.getDrawable("bgs");
        ResizeStyle.knob= skin.getDrawable("knob");
        Resize = new Slider(0, 1, 1, false, ResizeStyle);
        Resize.setVisible(true);
        table.padTop(20);
        //diffculy slider
        table.add(Resize);
        table.row();
        //stage
        game.stage.addActor(table);
        //aimations and backgroud
//        menuAnimation = new Animation();
//        background = TextureRegion.split(Game.game.getAssetManager().get("res/menu/optBack.jgp"), 1000,1000)[0];
//        menuAnimation.setFrames(background, 1f);
//        animationSprite = new Sprite(menuAnimation.getFrame());
//        animationSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
//        menuAnimation.update(dt);
//        animationSprite = new Sprite(menuAnimation.getFrame());
//        animationSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        animationSprite.flip(true, false);
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        sb.begin();
//        animationSprite.draw(sb);
//        sb.end();
        gsm.game().stage.act(Gdx.graphics.getDeltaTime());
        gsm.game().stage.draw();
    }

    @Override
    public void dispose() {

    }
}
