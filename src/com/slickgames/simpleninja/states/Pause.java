package com.slickgames.simpleninja.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyInput;
import com.slickgames.simpleninja.main.Game;

public class Pause extends GameState {


    BitmapFont font = new BitmapFont();
    Button Play, Exit, Options, Load_Game;

    public Pause(GameStateManager gsm) {
        super(gsm);
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
    }

    @Override
    public void handleInput() {
        Play = new Button();
        Exit = new Button();
        Options = new Button();
        Load_Game = new Button();
//        public static final int PLAY = 1;
//        public static final int Pause =0;
        if ((MyInput.isPressed(MyInput.RESET) && (gsm.stateStautes == 0))) {
            gsm.setState(GameStateManager.PLAY);
        } else if ((MyInput.isPressed(MyInput.RESET) && (gsm.stateStautes == 1))) {
            gsm.setState(GameStateManager.PAUSE);
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

    }

    @Override
    public void render() {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        font.draw(sb, "Pause Menu", 100, 100);
        sb.end();
    }

    @Override
    public void dispose() {

    }
}
