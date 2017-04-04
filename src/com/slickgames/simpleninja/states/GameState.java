package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.slickgames.simpleninja.main.SimpleNinja;

public abstract class GameState implements Screen{

    public SimpleNinja game;

    protected SpriteBatch sb;
    protected OrthographicCamera cam;
    protected Viewport viewPort;
    protected Stage stage;

    protected GameState(SimpleNinja game) {
        this.game = game;
        sb = game.getSpriteBatch();
        cam = game.getCamera();
        viewPort = game.getViewPort();
        stage = game.getStage();

    }

    public abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render();

    public abstract void dispose();
}
