package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.handlers.Animation;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;


public abstract class B2DSprite {
    protected Body body;
    protected Animation animation;
    protected float width;
    protected float height;
    protected int dir = 1;
    protected float MAX_SPEED = 2f;
    protected int MAX_HEALTH = 20;
    public int health = MAX_HEALTH;

    public B2DSprite(Body body) {
        this.body = body;
        animation = new Animation();

    }

    public void setAnimation(TextureRegion[] reg, float delay) {
        animation.setFrames(reg, delay);
        height = reg[0].getRegionHeight();
        width = reg[0].getRegionWidth();
    }

    public abstract void update(float dt);

    public abstract void playerUpdate(float dt, float lastAttack);

    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(animation.getFrame(), body.getPosition().x * PPM - width / 2,
                body.getPosition().y * PPM - height / 2);
        sb.end();
    }

    public Animation getAnimation() {
        return animation;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int x) {
        dir = x;
    }

    public float getMaxSpeed() {
        return MAX_SPEED;
    }

    public void damage(int dmg) {
        health -= dmg;
        System.out.println(health);
    }

    public abstract void kill();

    public void setMaxSpeed(float maxSpeed) {
        this.MAX_SPEED = maxSpeed;
    }
}
