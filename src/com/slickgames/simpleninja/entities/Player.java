package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.slickgames.simpleninja.main.Game;
import com.slickgames.simpleninja.states.Play;

public class Player extends B2DSprite {
    public boolean shooting, running, idling, jumping, attacking, attacked, blocking,throwing;
    TextureRegion[] run, idle, jump, attack, shoot, block, throwProj;
    private int numCrystals;
    private int totalCrystals;
    public double penelty;


    public Player(Body body, Play play) {
        super(body, play);
        Texture runningAnimation = Game.game.getAssetManager().get("res/images/simple_run.png");
        Texture attackingAnimation = Game.game.getAssetManager().get("res/images/simple_attack.png");
        Texture idlingAnimation = Game.game.getAssetManager().get("res/images/simple_idle.png");
        Texture blockingAnimation = Game.game.getAssetManager().get("res/images/simple_block.png");
        Texture throwingAnimation = Game.game.getAssetManager().get("res/images/simple_throw1.png");

        run = TextureRegion.split(runningAnimation, 54, 42)[0];
        idle = TextureRegion.split(idlingAnimation, 54, 42)[0];
        jump = run;
        attack = TextureRegion.split(attackingAnimation, 54, 42)[0];
        shoot = TextureRegion.split(attackingAnimation, 54, 42)[0];
        block = TextureRegion.split(blockingAnimation, 54, 42)[0];
        throwProj = TextureRegion.split(throwingAnimation, 54, 42)[0];
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
                setAnimation(attack, 1 / 32f);
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
        penelty = (getMaxStamina() - stamina)/10;

        if (health <= 0) {
            kill();
        }
        if (attacked) {
            animation.update(dt + (dt - lastAttack));
            attacked = false;
        } else
            animation.update(dt);

        if (TimeUtils.nanoTime() - lastAttack > 650000000f && stamina < getMaxStamina()) {
            stamina+=1;
        }
        System.out.println(penelty);
    }

    public void kill() {
        replace();
        health = MAX_HEALTH;
    }

    private void replace() {
        this.body.setTransform(new Vector2(0, 7), body.getAngle());
    }
}
