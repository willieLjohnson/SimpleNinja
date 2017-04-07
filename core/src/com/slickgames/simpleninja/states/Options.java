package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.slickgames.simpleninja.SimpleNinja;

public class Options extends GameState {
    private Sprite background;

    public Options(SimpleNinja game) {
        super(game);

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));

        float dp = Gdx.graphics.getDensity();

        // button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("default-round");
        textButtonStyle.checked = skin.getDrawable("default-round-down");
        textButtonStyle.down = skin.getDrawable("default-round-down");
        textButtonStyle.pressedOffsetX = .5f * dp;
        textButtonStyle.pressedOffsetY = -.5f * dp;
        textButtonStyle.font = getGame().getFont("med");
        textButtonStyle.fontColor = Color.BLACK;

        // label style
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.getFont("small"), Color.WHITE);

        // slider style
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        skin.add("knob", new Texture(Gdx.files.internal("ui/knob.png")));
        skin.add("bgs", new Texture(Gdx.files.internal("ui/difficulty.png")));
        sliderStyle.background = skin.getDrawable("bgs");
        sliderStyle.knob = skin.getDrawable("knob");

        // tabs
        Table table = new Table();
        table.setFillParent(true);

        table.align(Align.center| Align.top);
        table.setOrigin(0, Gdx.graphics.getHeight());

        table.setDebug(false);

        HorizontalGroup tabsGroup = new HorizontalGroup();
        Button graphicsTab = new TextButton("graphics", textButtonStyle);
        graphicsTab.pad(5 * dp);
        Button gameplayTab = new TextButton("gameplay", textButtonStyle);
        gameplayTab.pad(5 * dp);
        Button tab3 = new TextButton("Tab3", textButtonStyle);
        tab3.pad(5 * dp);

        tabsGroup.addActor(graphicsTab);
        tabsGroup.addActor(gameplayTab);
        tabsGroup.addActor(tab3);

        tabsGroup.padBottom(100 * dp);

        ButtonGroup<Button> toggle = new ButtonGroup<>(graphicsTab, gameplayTab, tab3);
        toggle.setMinCheckCount(1);
        toggle.setMaxCheckCount(1);

        table.add(tabsGroup);
        table.row();

        // gameplay tab
        Stack content = new Stack();

        Label difficultyLabel = new Label("Difficulty", labelStyle);
        Label difficultyLevels = new Label("Easy  Normal  Hard", labelStyle);

        Slider difficultySlider = new Slider(1, 3, 1, false, sliderStyle);
        difficultySlider.setVisible(true);

        VerticalGroup gameplayContent = new VerticalGroup();
        gameplayContent.addActor(difficultyLabel);
        gameplayContent.addActor(difficultyLevels);
        gameplayContent.addActor(difficultySlider);

        content.add(gameplayContent);

        table.add(content);

        // back button
        textButtonStyle.font = game.getFont("small");
        Button backButton = new TextButton("back", textButtonStyle);

        // listeners
        graphicsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                graphicsContent.setVisible(graphicsTab.isChecked());
            }
        });
        gameplayTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameplayContent.setVisible(gameplayTab.isChecked());
            }
        });
        difficultySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setDifficulty(difficultySlider.getValue());
            }
        });
        tab3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                tab3Content.setVisible(tab3.isChecked());
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
                event.stop();
            }
        });

        stage.addActor(table);
        stage.addActor(backButton);

        // background
        background = new Sprite(game.getAssetManager().get("menu/optBack.png"), 1500, 1000);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.input.setInputProcessor(stage);
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

        sb.begin();
        background.draw(sb);
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
