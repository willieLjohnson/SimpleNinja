package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.main.SimpleNinja;
import com.slickgames.simpleninja.states.Play;

public class Projectile extends B2DSprite {
    private int id;
    private float life;

    public Projectile(Body body, Play aPlay, float velocity) {
        super(body, aPlay);
        Texture tex = game.getAssetManager().get("res/images/throw_knife.png");
        TextureRegion[] sprites = TextureRegion.split(tex, 8, 2)[0];
        body.getFixtureList().first().setDensity(1);
        setAnimation(sprites, 1 / 12f);
        body.setUserData(this);
        id = play.projectiles.size;
        body.setLinearVelocity(velocity, 1.5f);
        body.getFixtureList().first().setFriction(1000);
        play.projectiles.add(this);
        this.setOriginCenter();
    }

    @Override
    public void update(float dt) {
        animation.update(dt);
        life += 1;
        body.setAngularVelocity(life > 25 ? 0 : 50);
        if (life > 25) {
            body.setTransform(getPosition(), 67.5f);
        }
        if (!body.isAwake()) {
            play.cl.bodiesToRemove.add(this.body);
        }
    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {

    }

    public int getId() {
        return id;
    }

}

