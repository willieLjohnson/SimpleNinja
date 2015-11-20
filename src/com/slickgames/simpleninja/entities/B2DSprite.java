package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.handlers.Animation;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;


public class B2DSprite {
    protected Body body;
    protected Animation animation;
    protected float width;
    protected float height;

    public B2DSprite(Body body) {
        this.body = body;
        animation = new Animation();
    }

    public void setAnimation(TextureRegion[] reg, float delay) {
        animation.setFrames(reg, delay);
        height = reg[0].getRegionHeight();
        width = reg[0].getRegionWidth();
    }

    public void udpate(float dt) {
        animation.update(dt);
    }

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
}
