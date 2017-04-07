package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.handlers.Animation;
import com.slickgames.simpleninja.main.SimpleNinja;
import com.slickgames.simpleninja.states.Play;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;


public abstract class B2DSprite extends Sprite{
    static int MAX_HEALTH = 20;

    private static float MAX_SPEED = 2f;
    private static float MAX_STAMINA = 400;

    public boolean godMode;

    Body body;
    Animation animation;
    int dir = 1;
    Play play;
    SimpleNinja game;

    private int health = MAX_HEALTH;
    private float stamina = MAX_STAMINA;
    private float width;
    private float height;
    private Sprite sprite;

    B2DSprite(Body aBody, Play play) {
        body = aBody;
        animation = new Animation();
        this.play = play;
        game = this.play.getGame();
    }

    void setAnimation(TextureRegion[] reg, float delay) {
        animation.setFrames(reg, delay);
        height = reg[0].getRegionHeight();
        width = reg[0].getRegionWidth();
        sprite = new Sprite(reg[0]);
        setOriginCenter();
    }

    public abstract void update(float dt);

    public abstract void playerUpdate(float dt, float lastAttack);

    public void render(SpriteBatch sb) {
        sprite.setRegion(animation.getFrame());
        sprite.setPosition(body.getPosition().x * PPM - width / 2 , body.getPosition().y * PPM - height / 2);
        sprite.setRotation(body.getAngle()*PPM);

        sb.begin();
        sprite.draw(sb);
        sb.end();
    }

    public void damage(int dmg) {
        if (!godMode) {
            health -= dmg;
            ParticleEffect bloodSplat = new ParticleEffect();
            bloodSplat.load(Gdx.files.internal("res/particles/bloodSplat"), Gdx.files.internal("res/particles"));
            bloodSplat.setPosition(this.getPosition().x * PPM - this.getWidth() / 10,
                    this.getPosition().y * PPM - this.getHeight() / 4);
            bloodSplat.start();
            play.bloodParts.add(bloodSplat);
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Body getBody() {
        return body;
    }

    public static int getMaxHealth() {
        return MAX_HEALTH;
    }

    public static void setMaxHealth(int maxHealth) {
        MAX_HEALTH = maxHealth;
    }

    public static float getMaxSpeed() {
        return MAX_SPEED;
    }

    public static void setMaxSpeed(float maxSpeed) {
        MAX_SPEED = maxSpeed;
    }

    public static float getMaxStamina() {
        return MAX_STAMINA;
    }

    public static void setMaxStamina(float maxStamina) {
        MAX_STAMINA = maxStamina;
    }

    public boolean isGodMode() {
        return godMode;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getStamina() {
        return stamina;
    }

    public void addStamina(float amount) {
        stamina += amount;
    }
    public void setStamina(float stamina) {
        this.stamina = stamina;
    }
}
