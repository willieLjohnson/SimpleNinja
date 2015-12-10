package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Administrator on 12/9/2015.
 */
public class MyActor extends Actor {
    Sprite sprite = new Sprite(new Texture(Gdx.files.internal("res/maps/Main1.gif")));
}
