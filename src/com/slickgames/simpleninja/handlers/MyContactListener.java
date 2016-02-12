package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.slickgames.simpleninja.entities.Enemy;
import com.slickgames.simpleninja.states.Play;

public class MyContactListener implements ContactListener {

    private int numFootContacts;
    private Array<Body> bodiesToRemove;
    public Array<Enemy> enemiesHit;
    private boolean detectRight;
    private boolean detectLeft;
    private boolean withinRange;
    private boolean wallCollision;
    private boolean wallRun;
    private boolean enemyHit;
    private Play play;


    public MyContactListener(Play aPlay) {
        super();
        play = aPlay;
        bodiesToRemove = new Array<Body>();
        enemiesHit = new Array<Enemy>();
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

        /// enemy senses
        for (Enemy e : play.enemies) {
            if (fa.getUserData() != null && fa.getUserData().equals("visionRight" + e.id))
                detectRight = true;
            if (fb.getUserData() != null && fb.getUserData().equals("visionRight" + e.id))
                detectRight = true;
            if (fa.getUserData() != null && fa.getUserData().equals("visionLeft" + e.id))
                detectLeft = true;
            if (fb.getUserData() != null && fb.getUserData().equals("visionLeft" + e.id))
                detectLeft = true;

            if (fb.getUserData() != null && fb.getUserData().equals("range" + e.id))
                withinRange = true;
            if (fa.getUserData() != null && fa.getUserData().equals("range" + e.id))
                withinRange = true;

            if (fb.getUserData() != null && fb.getUserData().equals("wallcollision" + e.id))
                wallCollision = true;
            if (fa.getUserData() != null && fa.getUserData().equals("wallcollision" + e.id))
                wallCollision = true;

            if (fa.getUserData() != null && fa.getUserData().equals("enemyHitBox" + e.id)) {
                enemiesHit.add(e);
            }
            if (fb.getUserData() != null && fb.getUserData().equals("enemyHitBox" + e.id)) {
                enemiesHit.add(e);
            }

        }
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

        // enemy senses
        for (Enemy e : play.enemies) {
            if (fa.getUserData() != null && fa.getUserData().equals("visionRight" + e.id))
                detectRight = false;
            if (fb.getUserData() != null && fb.getUserData().equals("visionRight" + e.id))
                detectRight = false;
            if (fa.getUserData() != null && fa.getUserData().equals("visionLeft" + e.id))
                detectLeft = false;
            if (fb.getUserData() != null && fb.getUserData().equals("visionLeft" + e.id))
                detectLeft = false;

            if (fb.getUserData() != null && fb.getUserData().equals("range" + e.id))
                withinRange = false;
            if (fa.getUserData() != null && fa.getUserData().equals("range" + e.id))
                withinRange = false;

            if (fb.getUserData() != null && fb.getUserData().equals("wallcollision" + e.id))
                wallCollision = false;
            if (fa.getUserData() != null && fa.getUserData().equals("wallcollision" + e.id))
                wallCollision = false;

            if (fa.getUserData() != null && fa.getUserData().equals("enemyHitBox" + e.id)) {
                enemiesHit.removeIndex(enemiesHit.indexOf(e,true));
            }
            if (fb.getUserData() != null && fb.getUserData().equals("enemyHitBox" + e.id)) {
                enemiesHit.removeIndex(enemiesHit.indexOf(e,true));
            }
        }

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

    public Array<Enemy> getEnemiesHit() {
        return enemiesHit;
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

    public boolean isEnemyHit(int id) {
        return enemyHit;
    }

}
