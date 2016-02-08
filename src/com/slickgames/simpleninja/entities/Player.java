package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.main.Game;

public class Player extends B2DSprite {
    public boolean running, idling, jumping, attacking, attacked;
    TextureRegion[] run, idle, jump, attack;
    private int numCrystals;
    private int totalCrystals;


    public Player(Body body) {
        super(body);

        Texture runningAnimation = Game.game.getAssetManager().get("res/images/simple_run.png");
        Texture attackingAnimation = Game.game.getAssetManager().get("res/images/simple_attack.png");
        Texture idlingAnimation = Game.game.getAssetManager().get("res/images/simple_idle.png");
        run = TextureRegion.split(runningAnimation, 54, 42)[0];
        idle = TextureRegion.split(idlingAnimation, 54, 42)[0];
        jump = TextureRegion.split(runningAnimation, 54, 42)[0];
        attack = TextureRegion.split(attackingAnimation, 54, 42)[0];
        setAnimation(idle, 1 / 7f);
    }

    @Override
    public void update(float dt) {

    }

    public void collectCrystal() {
        numCrystals++;
    }

    public void toggleAnimation(String animation) {
        switch (animation) {
            case "run":
                running = true;
                idling = false;
                jumping = false;
                attacking = false;
                setAnimation(run, 1 / 16f);
                break;
            case "idle":
                running = false;
                idling = true;
                jumping = false;
                attacking = false;
                setAnimation(idle, 1 / 7f);
                break;
            case "jump":
                running = false;
                idling = false;
                jumping = true;
                attacking = false;
                setAnimation(jump, 1 / 2f);
                break;
            case "attack":
                running = false;
                idling = false;
                jumping = false;
                attacking = true;
                setAnimation(attack, 1 / 32f);
        }

    }

    public boolean isRunning() {
        return running;
    }

    public boolean isIdle() {
        return idling;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean b) {
        attacking = b;
    }

    public int getNumCrystal() {
        return numCrystals;
    }

    public int getTotalCrystals() {
        return totalCrystals;
    }

    public void setTotalCrystals(int i) {
        totalCrystals = i;
    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {
        if (health <= 0) {
            kill();
        }
        if (attacked) {
            animation.update(dt + (dt - lastAttack));
            attacked = false;
        } else
            animation.update(dt);
    }

    public void kill() {
        replace();
        health = MAX_HEALTH;
    }

    private void replace() {
        this.body.setTransform(new Vector2(0, 6), body.getAngle());
    }
}
