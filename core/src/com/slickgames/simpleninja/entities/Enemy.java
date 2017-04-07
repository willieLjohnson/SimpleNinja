package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.slickgames.simpleninja.states.Play;

public class Enemy extends B2DSprite {
    private static final int GUARD = 0;
    private static final int CHASE = 1;
    private static final int FIND = 2;

    private boolean playerAttackable;
    private int id;
    private boolean detectRight;
    private boolean detectLeft;
    private boolean withinRange;
    private boolean wallCollision;
    private int numFootContacts;

    private boolean running, idling, jumping, attacking, attacked, ignorePlayer;
    private int currentState = 0;

    private TextureRegion[] run, idle, jump, attack;
    private Vector2 target = new Vector2();
    private Vector2 collision = new Vector2();
    private Vector2 normal = new Vector2();
    private Fixture cFix;
    private boolean enemySpotted;
    private float currentTime;
    private float lastSeen;
    private float lastSwitch;
    private boolean bounced;

    private boolean swinging;
    private float charge;

    private boolean charging;

    private boolean aggressive = false;
    private double damage = 5 * game.getDifficulty();

    public Enemy(Body body, Play play, int aId) {
        super(body, play);
        id = aId;
        body.setUserData(this);
        play.enemies.add(this);

        Texture runningAnimation = game.getAssetManager().get("images/enemy_run.png");
        Texture attackingAnimation = game.getAssetManager().get("images/enemy_attack.png");
        Texture idlingAnimation = game.getAssetManager().get("images/enemy_idle.png");

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
        if (getHealth() <= 0) {
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
            if (cFix.getBody().equals(player) && isPlayerSpotted() && !ignorePlayer) {
                currentState = CHASE;
                lastSeen = currentTime;
            } else if (currentState == CHASE && (!withinRange && !cFix.getBody().equals(player))) {
                if (currentTime - lastSeen > 2000000000f) {
                    currentState = FIND;
                }
            } else {
                if (currentTime - lastSeen > 5000000000f) {
                    currentState = GUARD;
                }
            }
        else
            currentState = CHASE;
        switch (currentState) {
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

                    if (Math.abs(body.getLinearVelocity().x) < getMaxSpeed()) {
                        body.applyForceToCenter((getMaxSpeed() * 8) * dir, 0, true);
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
                if (Math.abs(body.getLinearVelocity().x) < getMaxSpeed()) {
                    body.applyForceToCenter(32f * dir, 0, true);
                }
                if (wallCollision)
                    dir *= -1;
        }

    }

    private void toggleAnimation(String animation) {
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

    private void kill() {
        setHealth(MAX_HEALTH);
        play.cl.bodiesToRemove.add(body);
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

    private boolean isPlayerSpotted() {
        return dir == 1 ? detectRight : detectLeft;
    }

    private boolean isEnemyOnGround() {
        return numFootContacts > 0;
    }

    public boolean isPlayerAttackable() {
        return playerAttackable;
    }

    public void setPlayerAttackable(boolean playerAttackable) {
        this.playerAttackable = playerAttackable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDetectRight() {
        return detectRight;
    }

    public void setDetectRight(boolean detectRight) {
        this.detectRight = detectRight;
    }

    public boolean isDetectLeft() {
        return detectLeft;
    }

    public void setDetectLeft(boolean detectLeft) {
        this.detectLeft = detectLeft;
    }

    public boolean isWithinRange() {
        return withinRange;
    }

    public void setWithinRange(boolean withinRange) {
        this.withinRange = withinRange;
    }

    public boolean isWallCollision() {
        return wallCollision;
    }

    public void setWallCollision(boolean wallCollision) {
        this.wallCollision = wallCollision;
    }

    public int getNumFootContacts() {
        return numFootContacts;
    }

    public void addNumFootContacts(int amount) {
        numFootContacts += amount;
    }

    public void setNumFootContacts(int numFootContacts) {
        this.numFootContacts = numFootContacts;
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public boolean isAttacked() {
        return attacked;
    }

    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }
}
