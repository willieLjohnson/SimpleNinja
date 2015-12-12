package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.slickgames.simpleninja.handlers.MyContactListener;
import com.slickgames.simpleninja.main.Game;

public class Enemy extends B2DSprite {
    private int numCrystals;
    private int totalCrystals;
    public boolean running, idling, jumping, attacking, attacked;
    public int state = 0;
    public static final float MAX_SPEED = 1.5f;
    TextureRegion[] run, idle, jump, attack;

    private boolean enemySpotted;

    Vector2 target = new Vector2();
    Vector2 collision = new Vector2();
    Vector2 normal = new Vector2();
    Fixture cFix;
    ShapeRenderer sr;
    private float currentTime;
    private float lastSeen;
    private float lastSwitch;

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
        currentTime = TimeUtils.nanoTime();

        if (dir == -1) {

            if (!this.getAnimation().getFrame().isFlipX()) {
                this.getAnimation().getFrame().flip(true, false);
            }
        } else {

            if (this.getAnimation().getFrame().isFlipX()) {
                this.getAnimation().getFrame().flip(true, false);
            }
        }
        if (Math.abs(body.getLinearVelocity().x) > 0f) {
            if (!running)
                toggleAnimation("run");
        } else {
            toggleAnimation("idle");
        }
    }

    public void seek(Body player, World world, MyContactListener cl) {
        target.set(player.getPosition());
        RayCastCallback callback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                cFix = fixture;
                collision.set(point);
                Enemy.this.normal.set(normal).add(point);
                if (cFix.equals(player))
                    return 1;
                else
                    return .5f;
            }

        };
        world.rayCast(callback, this.body.getPosition(), target);


        if (cFix.getBody().equals(player) && cl.isPlayerSpotted(dir)) {
            state = 1;
            lastSeen = currentTime;
        } else if (state == 1 && (!cl.isWithinRange() && !cFix.getBody().equals(player))) {
            if (currentTime - lastSeen > 2000000000f) {
                state = 2;
            }

        } else {
            if (currentTime - lastSeen > 5000000000f) {
                state = 0;
            }
        }
        switch (state) {
            case 0:
                if (currentTime - lastSwitch > 10000000000f) {
                    dir *= -1;
                    lastSwitch = currentTime;
                }
                if (Math.abs(body.getLinearVelocity().x) < .5f) {
                    body.applyForceToCenter(16f * dir, 0, true);
                }
                break;
            case 1:
                dir = (target.x < body.getPosition().x ? -1 : 1);
                if (Math.abs(body.getLinearVelocity().x) < MAX_SPEED) {
                    body.applyForceToCenter(16f * dir, 0, true);
                }
                if (body.getLinearVelocity().y <= 0 && target.y > body.getPosition().y) {
                    body.applyLinearImpulse(0, 2, 0, 0, true);
                }
                break;
            case 2:
                if (Math.abs(body.getLinearVelocity().x) < MAX_SPEED) {
                    body.applyForceToCenter(16f * dir, 0, true);
                }
        }
        // if (Math.abs(this.body.getPosition().x - target.x) <= .35f
        // && Math.abs(this.body.getPosition().y - target.y) <= .2f &&
        // cl.isPlayerSpotted(dir)) {
        // if (!attacking)
        // toggleAnimation("attack");
        // } else if (Math.abs(this.body.getLinearVelocity().x) > .05f) {
        // if (!running)
        // toggleAnimation("run");
        // } else {
        // if (!idling)
        // toggleAnimation("idle");
        // }
        //
        // if (cl.isPlayerSpotted(dir)) {
        // enemySpotted = true;
        // lastSeen = currentTime;
        // } else {
        // if (currentTime - lastSeen > 10000000000f) {
        // enemySpotted = false;
        // }
        // }
        // if (enemySpotted) {
        // dir = (target.x < body.getPosition().x ? -1 : 1);
        // if (Math.abs(this.body.getLinearVelocity().x) <= 1f) {
        // this.body.applyLinearImpulse(.3f * dir, 0f, 0, 0, true);
        // }
        // } else {
        //
        // if (currentTime - lastSwitch > 10000000000f) {
        // dir *= -1;
        // lastSwitch = currentTime;
        // }
        //
        // }
        // if (dir == -1) {
        // System.out.println("FLIP LEFT");
        // if (!this.getAnimation().getFrame().isFlipX()) {
        // this.getAnimation().getFrame().flip(true, false);
        // }
        // } else {
        // System.out.println("FLIP RIGHT");
        // if (this.getAnimation().getFrame().isFlipX()) {
        // this.getAnimation().getFrame().flip(true, false);
        // }
        // }
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
