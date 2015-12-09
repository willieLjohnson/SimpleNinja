package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.slickgames.simpleninja.main.Game;

public class Enemy extends B2DSprite {
    private int numCrystals;
    private int totalCrystals;
    public boolean running, idling, jumping, attacking, attacked;
    public static final float MAX_SPEED = 5f;
    TextureRegion[] run, idle, jump, attack;

    private boolean enemySpotted;

    Vector2 vision = new Vector2(), target = new Vector2(), collision = new Vector2(), normal = new Vector2();
    Fixture cFix;
    ShapeRenderer sr;

    public Enemy(Body body) {
        super(body);

        Texture runningAnimation = Game.game.getAssetManager().get("res/images/simple_runAll.png");
        Texture attackingAnimation = Game.game.getAssetManager().get("res/images/simple_attackAll.png");
        Texture idlingAnimation = Game.game.getAssetManager().get("res/images/enemy_idleAll.png");
        run = TextureRegion.split(runningAnimation, 54, 42)[0];
        idle = TextureRegion.split(idlingAnimation, 54, 42)[0];
        jump = TextureRegion.split(runningAnimation, 54, 42)[0];
        attack = TextureRegion.split(attackingAnimation, 54, 42)[0];
        setAnimation(idle, 1 / 7f);

    }

    @Override
    public void update(float dt) {

        animation.update(dt);
    }

    public void seek(Body player, World world) {

        target.set(player.getPosition());

        RayCastCallback callback = (fixture, point, normal1, fraction) -> {
            cFix = fixture;
            collision.set(point);
            Enemy.this.normal.set(normal1).add(point);
            return -1;
        };

        world.rayCast(callback, this.body.getPosition(), target);

        if (Math.abs(this.body.getPosition().x - target.x) <= .35f
                && Math.abs(this.body.getPosition().y - target.y) <= .2f) {
            if (!attacking)
                toggleAnimation("attack");
        } else if (Math.abs(this.body.getLinearVelocity().x) > .05f) {
            if (!running)
                toggleAnimation("run");
        } else {
            if (!idling)
                toggleAnimation("idle");
        }
        if (Math.abs(this.body.getPosition().x - target.x) <= 1) {
            enemySpotted = true;
        } else if (Math.abs(this.body.getPosition().x - target.x) >= 1.2) {
            enemySpotted = false;
        }
        if (enemySpotted) {
            if (cFix.getBody().equals(player))
            if (Math.abs(this.body.getLinearVelocity().x) <= 1f) {
                this.body.applyLinearImpulse(target.x < this.getPosition().x ? -.3f : .3f, 0f, 0, 0, true);
            }
            if (this.body.getLinearVelocity().x <= 0f) {

                if (!this.getAnimation().getFrame().isFlipX()) {
                    this.getAnimation().getFrame().flip(true, false);
                }
            } else {
                if (this.getAnimation().getFrame().isFlipX()) {
                    this.getAnimation().getFrame().flip(true, false);
                }
            }
        }
    }

    public void collectCrystal() {
        numCrystals++;
    }

    public Vector2 getVectors(String t) {
        switch (t) {
            case "p1":
                return this.body.getPosition();
            case "p2":
                return target;
            case "c":
                return collision;
            case "n":
                return normal;
            default:
                return collision;
        }
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

    public void setAttacking(boolean b) {
        attacking = b;
    }

    public boolean isAttacking() {
        return attacking;
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
    }
}
