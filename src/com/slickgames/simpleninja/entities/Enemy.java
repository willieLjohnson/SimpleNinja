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
import com.slickgames.simpleninja.main.SimpleNinja;
import com.slickgames.simpleninja.states.Play;

public class Enemy extends B2DSprite {
    public static final float MAX_SPEED = 2f; //  * ((float) play.game.getDifficulty())
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
    private int numCrystals;
    private int totalCrystals;
    private boolean enemySpotted;
    private float currentTime;
    private float lastSeen;
    private float lastSwitch;
    private boolean bounced;
    public boolean playerAttackable;
    private boolean swinging;
    public float charge;
    private int attackFrame;
    public boolean detectRight;
    public boolean detectLeft;
    public boolean withinRange;
    public boolean wallCollision;
    private boolean charging;
    public int numFootContacts;
    private boolean aggressive = false;
    public Texture runningAnimation, attackingAnimation, idlingAnimation;
    private double damage = 5 * play.game.getDifficulty();

    public Enemy(Body body, Play play, int aId) {
        super(body, play);
        id = aId;
        body.setUserData(this);
        play.enemies.add(this);

        runningAnimation = play.game.getAssetManager().get("res/images/enemy_run.png");
        attackingAnimation = play.game.getAssetManager().get("res/images/enemy_attack.png");
        idlingAnimation = play.game.getAssetManager().get("res/images/enemy_idle.png");
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
            swinging = false;
            attacking = false;
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
            if (!attacking && !idling)
                toggleAnimation("idle");
        }
    }

    public void seek(Body player, World world) {
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

        if (!aggressive)
            if (cFix.getBody().equals(player) && isPlayerSpotted() && !play.ignorePlayer) {
                state = CHASE;
                lastSeen = currentTime;
            } else if (state == CHASE && (!withinRange && !cFix.getBody().equals(player))) {
                if (currentTime - lastSeen > 2000000000f) {
                    state = FIND;
                }
            } else {
                if (currentTime - lastSeen > 5000000000f) {
                    state = GUARD;
                }
            }
        else
            state = CHASE;
        switch (state) {
            case GUARD:
                if (currentTime - lastSwitch < 3000000000f) {
                    if (wallCollision && !bounced) {
                        bounced = true;
                        dir *= -1;
                        lastSwitch = currentTime;
                    } else if (currentTime - lastSwitch > 1000000000f)
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

                // if the player is in range
                if (playerAttackable) {
                    // charge attack
                    if (!charging) {
                        charge = currentTime;
                        charging = true;
                    }

                    //if not, keep moving
                } else if (!charging) {
                    attacking = false;

                    if (Math.abs(body.getLinearVelocity().x) < MAX_SPEED) {
                        body.applyForceToCenter((MAX_SPEED * 8) * dir, 0, true);
                    }
                    if ((isEnemyOnGround() && (target.y - body.getPosition().y) > .5) && body.getLinearVelocity().y < 1) {
                        body.applyLinearImpulse(0, (target.y - body.getPosition().y) * 4, 0, 0, true);
                    }

                }

                // if done charging and not already attacking, attack.
                if ((charging && currentTime - charge > 200000000f)) {
                    if (!attacking)
                        toggleAnimation("attack");
                    swinging = true;
                }

                // one attack is every 4 frames, so reset attack.
                if ((getAnimation().getCurrentFrame() + 1) % 4 == 0 && attacking) {
                    attacked = true;
                    swinging = false;
                    charging = false;
                    charge = 0;
                    attacking = false;
                } else {
                    attacked = false;
                }
                break;

            case FIND:
                if (Math.abs(body.getLinearVelocity().x) < MAX_SPEED) {
                    body.applyForceToCenter(32f * dir, 0, true);
                }
                if (wallCollision)
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
                setAnimation(attack, 1 / 10f);
        }

    }

    @Override
    public void playerUpdate(float dt, float lastAttack) {
    }

    @Override
    public void kill() {
        // replace();
        health = MAX_HEALTH;
        play.player.stamina += 20;
        play.cl.bodiesToRemove.add(body);
    }

    private void replace() {
        this.body.setTransform(new Vector2(5, 8), body.getAngle());
        state = 0;
    }
    public boolean isPlayerSpotted() {
        return dir == 1 ? detectRight : detectLeft;
    }

    public boolean isEnemyOnGround() {
        return numFootContacts > 0;
    }

    public double getDamage() {
        return damage;
    }
}
