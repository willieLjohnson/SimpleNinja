package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.slickgames.simpleninja.SimpleNinja;

public abstract class GameState implements Screen {

    public SimpleNinja game;
    public OrthographicCamera cam;
    SpriteBatch sb;
    Stage stage;

    GameState(SimpleNinja game) {
        this.game = game;
        sb = new SpriteBatch();
        Viewport viewPort = new ExtendViewport(SimpleNinja.V_WIDTH, SimpleNinja.V_HEIGHT, cam);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, SimpleNinja.V_WIDTH, SimpleNinja.V_HEIGHT);
        viewPort.setCamera(cam);
        stage = new Stage(viewPort);
    }

    abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render();

    public abstract void dispose();

    public SimpleNinja getGame() {
        return game;
    }
}
