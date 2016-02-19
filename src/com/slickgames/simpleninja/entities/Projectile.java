package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.main.Game;

/**
 * Created by Administrator on 2/10/2016.
 */
public class Projectile extends B2DSprite {
    public Projectile(Body body) {
        super(body);

        Texture tex = Game.game.getAssetManager().get("res/images/crystal.png");
        TextureRegion[] sprites = TextureRegion.split(tex, 16, 16)[0];

        setAnimation(sprites, 1 / 12f);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {

    }

    @Override
    public void kill() {

    }


}
