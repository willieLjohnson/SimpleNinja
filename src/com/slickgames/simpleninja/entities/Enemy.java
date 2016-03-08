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
import com.slickgames.simpleninja.states.Play;

public class Enemy extends B2DSprite {
    public static final float MAX_SPEED = 1.5f;
    private final int GUARD = 0;
    private final int CHASE = 1;
    private final int FIND = 2;
    public boolean running, idling, jumping, attacking, attacked, ignorePlayer;
    public int state = 0;
    public int id;
    TextureRegion[] run, idle, jump, attack;
    Vector2 target = new Vector2();
    Vector2 collision = new Vector2();
    Vector2 normal = new Vector2();
    Fixture cFix;
    ShapeRenderer sr;
    Play play;
    private int numCrystals;
    private int totalCrystals;
    private boolean enemySpotted;
    private float currentTime;
    private float lastSeen;
    private float lastSwitch;
    private boolean bounced;

    public Enemy(Body body, Play aPlay, int aId) {
        super(body);
        id = aId;
        play = aPlay;
        body.setUserData(this);
        play.enemies.add(this);

        Texture runningAnimation = Game.game.getAssetManager().get("res/images/enemy_run.png");
        Texture attackingAnimation = Game.game.getAssetManager().get("res/images/enemy_attack.png");
        Texture idlingAnimation = Game.game.getAssetManager().get("res/images/enemy_idle.png");
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
        if (health <= 0) {
            kill();
            System.out.println("ded");
        }

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
            if (!running && !attacking)
                toggleAnimation("run");
        } else {
            toggleAnimation("idle");
        }
    }

    public void seek(Body player, World world, MyContactListener cl) {
        target.set(player.getPosition());
        RayCastCallback callback = (fixture, point, normal1, fraction) -> {
            cFix = fixture;
            collision.set(point);
            Enemy.this.normal.set(normal1).add(point);
            if (cFix.equals(player))
                return 1;
            else
                return .5f;
        };
        world.rayCast(callback, this.body.getPosition(), target);


        if (cFix.getBody().equals(player) && cl.isPlayerSpotted(dir) && !play.ignorePlayer) {
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
            case GUARD:
                if (currentTime - lastSwitch < 3000000000f) {
                    if (cl.isCollidingWall() && !bounced) {
                        bounced = true;
                        dir *= -1;
                        lastSwitch = currentTime;
                    }
                    if (currentTime - lastSwitch > 1000000000f)
                        bounced = false;
                    if (Math.abs(body.getLinearVelocity().x) < .5f)
                        body.applyForceToCenter(32f * dir, 0, true);
                } else if (currentTime - lastSwitch < 7500000000f)
                    body.applyForceToCenter(0, 0, true);
                else {
                    lastSwitch = currentTime;
                }
                break;

            case CHASE:
                dir = (target.x < body.getPosition().x ? -1 : 1);
                if (Math.abs((target.x + target.y) - (body.getPosition().x + body.getPosition().y)) <= .14f && !attacking) {
                    toggleAnimation("attack");
                }
                if (Math.abs(body.getLinearVelocity().x) < MAX_SPEED) body.applyForceToCenter(16f * dir, 0, true);
                if (body.getLinearVelocity().y == 0 && target.y > body.getPosition().y) {
                    body.applyLinearImpulse(0, 2, 0, 0, true);
                }
                break;

            case FIND:
                if (Math.abs(body.getLinearVelocity().x) < MAX_SPEED) {
                    body.applyForceToCenter(32f * dir, 0, true);
                }
                if (cl.isCollidingWall())
                    dir *= -1;
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


    @Override
    public void playerUpdate(float dt, float lastAttack) {
    }

    @Override
    public void kill() {
        replace();
        health = MAX_HEALTH;
    }

    private void replace() {
        this.body.setTransform(new Vector2(5, 8), body.getAngle());
        state = 0;
    }
}
