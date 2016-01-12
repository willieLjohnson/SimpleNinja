package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener {

    private int numFootContacts;
    private Array<Body> bodiesToRemove;
    private boolean detectRight;
    private boolean detectLeft;
    private boolean withinRange;
    private boolean wallCollision;
    private boolean wallRun;
    private boolean enemyHit;

    public MyContactListener() {
        super();
        bodiesToRemove = new Array<Body>();
    }

    // called when two fixtures start collide
    @Override
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        /// player senses
        if (fa.getUserData() != null && fa.getUserData().equals("foot"))
            numFootContacts++;
        if (fb.getUserData() != null && fb.getUserData().equals("foot"))
            numFootContacts++;

        if (fa.getUserData() != null && fa.getUserData().equals("crystal"))
            bodiesToRemove.add(fa.getBody());
        if (fb.getUserData() != null && fb.getUserData().equals("crystal"))
            bodiesToRemove.add(fb.getBody());

        if (fa.getUserData() != null && fa.getUserData().equals("hand"))
            wallRun = true;
        if (fb.getUserData() != null && fb.getUserData().equals("hand"))
            wallRun = true;

        if (fa.getUserData() != null && fa.getUserData().equals("attackRange"))
            enemyHit = true;
        if (fb.getUserData() != null && fb.getUserData().equals("attackRange"))
            enemyHit = true;

        /// enemy senses
        if (fa.getUserData() != null && fa.getUserData().equals("visionRight"))
            detectRight = true;
        if (fb.getUserData() != null && fb.getUserData().equals("visionRight"))
            detectRight = true;
        if (fa.getUserData() != null && fa.getUserData().equals("visionLeft"))
            detectLeft = true;
        if (fb.getUserData() != null && fb.getUserData().equals("visionLeft"))
            detectLeft = true;

        if (fb.getUserData() != null && fb.getUserData().equals("range"))
            withinRange = true;
        if (fa.getUserData() != null && fa.getUserData().equals("range"))
            withinRange = true;

        if (fb.getUserData() != null && fb.getUserData().equals("wallcollision"))
            wallCollision = true;
        if (fa.getUserData() != null && fa.getUserData().equals("wallcollision"))
            wallCollision = true;
    }

    // called when two fixtures no longer collide
    @Override
    public void endContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if (fa == null || fb == null)
            return;
        // player senses
        if (fa.getUserData() != null && fa.getUserData().equals("foot"))
            numFootContacts--;
        if (fb.getUserData() != null && fb.getUserData().equals("foot"))
            numFootContacts--;
        if (fa.getUserData() != null && fa.getUserData().equals("hand"))
            wallRun = false;
        if (fb.getUserData() != null && fb.getUserData().equals("hand"))
            wallRun = false;
        if (fa.getUserData() != null && fa.getUserData().equals("attackRange"))
            enemyHit = false;
        if (fb.getUserData() != null && fb.getUserData().equals("attackRange"))
            enemyHit = false;

        // enemy senses
        if (fa.getUserData() != null && fa.getUserData().equals("visionRight"))
            detectRight = false;
        if (fb.getUserData() != null && fb.getUserData().equals("visionRight"))
            detectRight = false;
        if (fa.getUserData() != null && fa.getUserData().equals("visionLeft"))
            detectLeft = false;
        if (fb.getUserData() != null && fb.getUserData().equals("visionLeft"))
            detectLeft = false;

        if (fb.getUserData() != null && fb.getUserData().equals("range"))
            withinRange = false;
        if (fa.getUserData() != null && fa.getUserData().equals("range"))
            withinRange = false;

        if (fb.getUserData() != null && fb.getUserData().equals("wallcollision"))
            wallCollision = false;
        if (fa.getUserData() != null && fa.getUserData().equals("wallcollision"))
            wallCollision = false;

    }

    public boolean isPlayerOnGround() {
        return numFootContacts > 0;
    }

    public boolean isWithinRange() {
        return withinRange;
    }

    public boolean isCollidingWall() {
        return wallCollision;
    }

    public boolean isPlayerSpotted(int n) {
        return n == 1 ? detectRight : detectLeft;
    }

    public Array<Body> getBodiesToRemove() {
        return bodiesToRemove;
    }

    public boolean wallRun() {
        return wallRun;
    }

    @Override
    public void preSolve(Contact c, Manifold m) {
    }

    @Override
    public void postSolve(Contact c, ContactImpulse m) {
    }

    public boolean isEnemyHit() {
        return enemyHit;
    }

}
