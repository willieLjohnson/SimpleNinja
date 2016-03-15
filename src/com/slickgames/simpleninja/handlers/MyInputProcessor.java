package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {

    @Override
    public boolean keyDown(int k) {
        if (k == Keys.LEFT) {
            MyInput.setKey(MyInput.LEFT, true);
        } if (k == Keys.P) {
            MyInput.setKey(MyInput.SHOOT, true);
        }
        if (k == Keys.RIGHT) {
            MyInput.setKey(MyInput.RIGHT, true);
        }
        if (k == Keys.UP) {
            MyInput.setKey(MyInput.JUMP, true);
        }
        if (k == Keys.ESCAPE) {
            MyInput.setKey(MyInput.RESET, true);
        }
        if (k == Keys.SHIFT_LEFT) {
            MyInput.setKey(MyInput.WALLRUN, true);
        }
        if (k == Keys.C) {
            MyInput.setKey(MyInput.SHOOT, true);
        }
        if (k == Keys.X) {
            MyInput.setKey(MyInput.ATTACK, true);
        }
        if (k == Keys.Z) {
            MyInput.setKey(MyInput.BLOCK, true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int k) {
        if (k == Keys.LEFT) {
            MyInput.setKey(MyInput.LEFT, false);
        }
        if (k == Keys.RIGHT) {
            MyInput.setKey(MyInput.RIGHT, false);
        }
        if (k == Keys.UP) {
            MyInput.setKey(MyInput.JUMP, false);
        }
        if (k == Keys.ESCAPE) {
            MyInput.setKey(MyInput.RESET, false);
        }
        if (k == Keys.SHIFT_LEFT) {
            MyInput.setKey(MyInput.WALLRUN, false);
        }
        if (k == Keys.C) {
            MyInput.setKey(MyInput.SHOOT, false);
        }
        if (k == Keys.X) {
            MyInput.setKey(MyInput.ATTACK, false);
        }
        if (k == Keys.Z) {
            MyInput.setKey(MyInput.BLOCK, false);
        }
        return true;
    }

}
