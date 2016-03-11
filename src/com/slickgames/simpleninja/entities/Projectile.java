package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.main.Game;
import com.slickgames.simpleninja.states.Play;

/**
 * Created by Administrator on 2/10/2016.
 */
public class Projectile extends B2DSprite {
    private float velocity;
    public Projectile(Body body, Play play, float aVelocity) {
        super(body);
        velocity = aVelocity;
        Texture tex = Game.game.getAssetManager().get("res/images/kuni.png");
        TextureRegion[] sprites = TextureRegion.split(tex, 314, 236)[0];

        setAnimation(sprites, 1 / 12f);
        body.setUserData("proj" + play.projectiles.size);
        play.projectiles.add(this);
    }

    @Override
    public void update(float dt) {
        animation.update(dt);

        if (Math.abs(body.getLinearVelocity().x) < 1)
            body.applyLinearImpulse(velocity, 0, 0, 0, false);
    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {

    }

    @Override
    public void kill() {

    }


}
