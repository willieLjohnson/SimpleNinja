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
 public int id;
    public Projectile(Body body, Play aPlay, float velocity) {
        super(body,aPlay);

        Texture tex = Game.game.getAssetManager().get("res/images/throw_knife1.png");
        TextureRegion[] sprites = TextureRegion.split(tex,8, 2)[0];
        body.getFixtureList().first().setDensity(1);
        setAnimation(sprites, 1 / 12f);
        body.setUserData(this);
        id = play.projectiles.size;
        body.setLinearVelocity(velocity,1.5f);
        play.projectiles.add(this);
    }

    @Override
    public void update(float dt) {
        animation.update(dt);
    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {

    }

    @Override
    public void kill() {

    }


}
