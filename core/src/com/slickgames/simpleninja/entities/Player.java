package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.slickgames.simpleninja.states.Play;

public class Player extends B2DSprite {

    private boolean running, idling, jumping, attacking, attacked, blocking, throwing;
    private TextureRegion[] run, idle, jump, attack, block, throwProj;
    private int numCrystals;
    private int totalCrystals;
    private double penalty;
    private int MAX_AMMO = 5;
    private int ammo = MAX_AMMO;

    public Player(Body body, Play play) {
        super(body, play);
        Texture runningAnimation = game.getAssetManager().get("images/simple_run.png");
        Texture attackingAnimation = game.getAssetManager().get("images/simple_attack.png");
        Texture idlingAnimation = game.getAssetManager().get("images/simple_idle.png");
        Texture blockingAnimation = game.getAssetManager().get("images/simple_block.png");
        Texture throwingAnimation = game.getAssetManager().get("images/simple_throw1.png");

        run = TextureRegion.split(runningAnimation, 54, 42)[0];
        idle = TextureRegion.split(idlingAnimation, 54, 42)[0];
        jump = run;
        attack = TextureRegion.split(attackingAnimation, 54, 42)[0];
        block = TextureRegion.split(blockingAnimation, 54, 42)[0];
        throwProj = TextureRegion.split(throwingAnimation, 54, 42)[0];
        setAnimation(idle, 1 / 7f);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {
        penalty = (getMaxStamina() - getStamina()) / 10;

        if (getHealth() <= 0) {
            kill();
        }
        if (attacked) {
            animation.update(dt + (dt - lastAttack));
            attacked = false;
        } else
            animation.update(dt);

        if (TimeUtils.nanoTime() - lastAttack > 650000000f && getStamina() < getMaxStamina() && play.cl.isPlayerOnGround() && !attacked) {
            addStamina(getMaxStamina() / 100);
        }
        if (running)
            animation.setDelay(Math.abs(1 / (Math.abs(body.getLinearVelocity().x * 10) - 4)) < 1 / 5f ? 1 / 16f : 1 / (Math.abs(body.getLinearVelocity().x * 10) - 4));
    }

    public void toggleAnimation(String animation) {
        switch (animation) {
            case "run":
                running = true;
                idling = false;
                jumping = false;
                blocking = false;
                attacking = false;
                throwing = false;
                setAnimation(run, 1 / 16f);
                break;
            case "idle":
                running = false;
                idling = true;
                jumping = false;
                attacking = false;
                blocking = false;
                throwing = false;
                setAnimation(idle, 1 / 7f);
                break;
            case "jump":
                running = false;
                idling = false;
                jumping = true;
                blocking = false;
                attacking = false;
                throwing = false;
                setAnimation(jump, 1 / 2f);
                break;
            case "attack":
                running = false;
                idling = false;
                jumping = false;
                attacking = true;
                blocking = false;
                throwing = false;
                setAnimation(attack, 1 / 45f);
                break;
            case "block":
                running = false;
                idling = false;
                jumping = false;
                attacking = false;
                blocking = true;
                throwing = false;
                setAnimation(block, 1 / 32f);
                break;
            case "throw":
                running = false;
                idling = false;
                jumping = false;
                attacking = false;
                blocking = false;
                throwing = true;
                setAnimation(throwProj, 1 / 20f);
        }

    }

    private void kill() {
        this.body.setTransform(new Vector2(0, 7), body.getAngle());
        setHealth(MAX_HEALTH);
    }

    public void collectCrystal() {
        numCrystals++;
        addAmmo(3);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isIdling() {
        return idling;
    }

    public void setIdling(boolean idling) {
        this.idling = idling;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isAttacked() {
        return attacked;
    }

    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean isThrowing() {
        return throwing;
    }

    public void setThrowing(boolean throwing) {
        this.throwing = throwing;
    }

    public void addAmmo(int amount) {
        ammo += amount;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

}
