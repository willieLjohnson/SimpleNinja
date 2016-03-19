package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.main.Game;
import com.slickgames.simpleninja.states.Play;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;


public abstract class B2DSprite extends Sprite{

    protected Body body;
    protected Animation animation;
    protected float width;
    protected float height;
    protected int dir = 1;
    protected float MAX_SPEED = 2f;
    protected int MAX_HEALTH = 20;
    protected double MAX_STAMINA = 200;
    public int health = MAX_HEALTH;
    public double stamina = MAX_STAMINA;
    public Play play;


    public B2DSprite(Body aBody, Play aPlay) {
        body = aBody;
        animation = new Animation();
        play = aPlay;

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

    public void setMaxSpeed(float maxSpeed) {
        this.MAX_SPEED = maxSpeed;
    }

    public void damage(int dmg) {
        health -= dmg;
        ParticleEffect bloodSplat = new ParticleEffect();
        bloodSplat.load(Gdx.files.internal("res/particles/blood_splat"), Gdx.files.internal("res/particles"));
        bloodSplat.setPosition(this.getPosition().x * PPM - this.getWidth() / 10,
                this.getPosition().y * PPM - this.getHeight() / 4);
        bloodSplat.start();
        play.bloodParts.add(bloodSplat);
    }

    public abstract void kill();

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public double getMaxStamina() {
        return MAX_STAMINA;
    }
}
