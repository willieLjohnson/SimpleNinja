package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.main.SimpleNinja;
import com.slickgames.simpleninja.states.Play;

public class Crystal extends B2DSprite {
    public Crystal(Body body,Play play) {
        super(body,play);

        Texture tex = play.game.getAssetManager().get("res/images/crystal.png");
        TextureRegion[] sprites = TextureRegion.split(tex, 16, 16)[0];

        setAnimation(sprites, 1 / 12f);
    }

    @Override
    public void update(float dt) {
        animation.update(dt);
    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {
        // n/a
    }

    @Override
    public void kill() {

    }
}
