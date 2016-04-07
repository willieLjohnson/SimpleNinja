package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.handlers.GameStateManager;

/**
 * Created by Administrator on 1/11/2016.
 */
public class Options extends GameState {
    Skin skin;
    Animation menuAnimation;
    private TextButton ResizeButton;
    private Slider Resize;
    private Table table;
    private Sprite animationSprite;

    public Options(GameStateManager gsm) {
        super(gsm);
        //  skin = new Skin(Gdx.files.internal("res/font/uiskin.json"), new TextureAtlas(Gdx.files.internal("res/font/uiskin.atlas")));
        //  skin=new Skin("konbs",new Texture(Gdx.files.internal("")));
        skin = new Skin();
        table = new Table();
        table.setFillParent(true);
        table.setWidth(game.stage.getWidth());
        table.align(Align.center);
//      ResizeButton = new TextButton("Resize", skin);
//      Resize = new Slider(0,1,1,false,SliderTesture);
        // skin = new Skin();
        skin.add("knob", new Texture(Gdx.files.internal("res/Style/Knode.png")));
        skin.add("bgs", new Texture(Gdx.files.internal("res/Style/onAndoff Slider.jpg")));
        Slider.SliderStyle ResizeStyle = new Slider.SliderStyle();
        ResizeStyle.background = skin.getDrawable("bgs");
        Resize = new Slider(0, 1, 1, false, ResizeStyle);
        Resize.setVisible(true);
        table.padTop(20);
        table.add(Resize);
        table.row();
        game.stage.addActor(table);

    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.game().stage.act(Gdx.graphics.getDeltaTime());
        gsm.game().stage.draw();
    }

    @Override
    public void dispose() {

    }
}
