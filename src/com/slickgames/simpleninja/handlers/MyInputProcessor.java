package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import org.lwjgl.input.Mouse;

public class MyInputProcessor extends InputAdapter {


    /////////// Keyboard Inputs

    @Override
    public boolean keyDown(int k) {
        if (k == Keys.A) {
            MyInput.setKey(MyInput.LEFT, true);
        }
        if (k == Keys.D) {
            MyInput.setKey(MyInput.RIGHT, true);
        }
        if (k == Keys.SPACE) {
            MyInput.setKey(MyInput.JUMP, true);
        }
        if (k == Keys.ESCAPE) {
            MyInput.setKey(MyInput.RESET, true);
        }

        return true;
    }

    @Override
    public boolean keyUp(int k) {
        if (k == Keys.A) {
            MyInput.setKey(MyInput.LEFT, false);
        }
        if (k == Keys.D) {
            MyInput.setKey(MyInput.RIGHT, false);
        }
        if (k == Keys.SPACE) {
            MyInput.setKey(MyInput.JUMP, false);
        }
        if (k == Keys.ESCAPE) {
            MyInput.setKey(MyInput.RESET, false);
        }
        return true;
    }

    /////////// Mouse Inputs

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT)
        {
            MyInput.setKey(MyInput.ATTACK, true);
        }
        return true;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT)
        {
            MyInput.setKey(MyInput.ATTACK, false);
        }
        return true;
    }
}
