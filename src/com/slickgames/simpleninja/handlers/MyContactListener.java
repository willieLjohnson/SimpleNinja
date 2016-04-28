package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.slickgames.simpleninja.entities.Enemy;
import com.slickgames.simpleninja.entities.Projectile;
import com.slickgames.simpleninja.states.Play;

public class MyContactListener implements ContactListener {

	public Array<Enemy> enemiesHit;
	private int numFootContacts;
	public Array<Body> bodiesToRemove;
	private boolean detectRight;
	private boolean detectLeft;
	private boolean withinRange;
	private boolean wallCollision;
	private boolean wallRun;
	private boolean enemyHit;
	private Play play;
	private Array<Enemy> attackers;
	public Array<Enemy> enemiesShot;

	public MyContactListener(Play aPlay) {
		super();
		play = aPlay;
		bodiesToRemove = new Array<Body>();
		enemiesHit = new Array<Enemy>();
		enemiesShot = new Array<Enemy>();
		attackers = new Array<Enemy>();
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
			if (e.getBody().getUserData().equals(fa.getBody().getUserData())) {
				if (fa.getUserData() != null && fa.getUserData().equals("visionRight" + e.id))
					e.detectRight = true;
				if (fa.getUserData() != null && fa.getUserData().equals("visionLeft" + e.id))
					e.detectLeft = true;
				if (fa.getUserData() != null && fa.getUserData().equals("range" + e.id))
					e.withinRange = true;
				if (fa.getUserData() != null && fa.getUserData().equals("wallcollision" + e.id))
					e.wallCollision = true;
				if (fa.getUserData() != null && fa.getUserData().equals("enemyHitBox" + e.id)) {

					if (fb.getUserData().equals("attackRange")) {
						enemiesHit.add(e);
					}
					try {
						if (play.projectiles.contains((Projectile) fb.getBody().getUserData(), true)) {
							enemiesShot.add(e);
							if (!bodiesToRemove.contains(fb.getBody(), true))bodiesToRemove.add(fb.getBody());
						}
					} catch (java.lang.ClassCastException f) {
						System.out.println("nah");
					}
				}
				if (fa.getUserData() != null && fa.getUserData().equals("Efoot" + e.id))
					e.numFootContacts++;
				if (fa.getUserData() != null && fa.getUserData().equals("enemyAttackRange" + e.id)) {
					if (!attackers.contains(e, true)) {
						attackers.add(e);
						e.playerAttackable = true;
					}
				}
			}
			if (e.getBody().getUserData().equals(fb.getBody().getUserData())) {
				if (fb.getUserData() != null && fb.getUserData().equals("visionRight" + e.id))
					e.detectRight = true;
				if (fb.getUserData() != null && fb.getUserData().equals("visionLeft" + e.id))
					e.detectLeft = true;
				if (fb.getUserData() != null && fb.getUserData().equals("range" + e.id))
					e.withinRange = true;
				if (fb.getUserData() != null && fb.getUserData().equals("wallcollision" + e.id))
					e.wallCollision = true;

				//check if enemy is hit by projectile or player's sword
				if (fb.getUserData() != null && fb.getUserData().equals("enemyHitBox" + e.id)) {
					if (fa.getUserData().equals("attackRange")) {
						enemiesHit.add(e);
					}
					try {
						if (play.projectiles.contains((Projectile) fa.getBody().getUserData(), true)) {
							enemiesShot.add(e);
							if (!bodiesToRemove.contains(fa.getBody(), true))bodiesToRemove.add(fa.getBody());
						}
					} catch (java.lang.ClassCastException f) {
					}
				}
				if (fb.getUserData() != null && fb.getUserData().equals("Efoot" + e.id))
					e.numFootContacts++;
				if (fb.getUserData() != null && fb.getUserData().equals("enemyAttackRange" + e.id)) {
					if (!attackers.contains(e, true)) {
						attackers.add(e);
						e.playerAttackable = true;
					}
				}
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
		if (fb.getUserData() != null && fb.getUserData().equals("playerHitBox")) {
			Enemy n = (Enemy) fa.getBody().getUserData();
			n.playerAttackable = false;
			attackers.removeIndex(attackers.indexOf(n, true));
		}

		// enemy senses
		for (Enemy e : play.enemies) {
			if (e.getBody().getUserData().equals(fa.getBody().getUserData())) {
				if (fa.getUserData() != null && fa.getUserData().equals("visionRight" + e.id))
					e.detectRight = false;
				if (fa.getUserData() != null && fa.getUserData().equals("visionLeft" + e.id))
					e.detectLeft = false;
				if (fa.getUserData() != null && fa.getUserData().equals("range" + e.id))
					e.withinRange = false;
				if (fa.getUserData() != null && fa.getUserData().equals("wallcollision" + e.id))
					e.wallCollision = false;
				if (fa.getUserData() != null && fa.getUserData().equals("enemyHitBox" + e.id)) {
					if (enemiesHit.contains(e, true))
						enemiesHit.removeIndex(enemiesHit.indexOf(e, true));
					if (enemiesShot.contains(e, true))
						enemiesShot.removeIndex(enemiesShot.indexOf(e, true));
				}
				if (fa.getUserData() != null && fa.getUserData().equals("Efoot" + e.id))
					e.numFootContacts--;
				if (fa.getUserData() != null && fa.getUserData().equals("enemyAttackRange" + e.id)) {
					e.playerAttackable = false;
					attackers.removeIndex(attackers.indexOf(e, true));
				}
			}
			if (e.getBody().getUserData().equals(fb.getBody().getUserData())) {
				if (fb.getUserData() != null && fb.getUserData().equals("visionRight" + e.id))
					e.detectRight = false;
				if (fb.getUserData() != null && fb.getUserData().equals("visionLeft" + e.id))
					e.detectLeft = false;
				if (fb.getUserData() != null && fb.getUserData().equals("range" + e.id))
					e.withinRange = false;
				if (fb.getUserData() != null && fb.getUserData().equals("wallcollision" + e.id))
					e.wallCollision = false;

				if (fb.getUserData() != null && fb.getUserData().equals("enemyHitBox" + e.id)) {
					if (enemiesHit.contains(e, true))
						enemiesHit.removeIndex(enemiesHit.indexOf(e, true));
					if (enemiesShot.contains(e, true))
						enemiesShot.removeIndex(enemiesShot.indexOf(e, true));
				}
				if (fb.getUserData() != null && fb.getUserData().equals("Efoot" + e.id))
					e.numFootContacts--;
				if (fb.getUserData() != null && fb.getUserData().equals("enemyAttackRange" + e.id)) {
					e.playerAttackable = false;
					attackers.removeIndex(attackers.indexOf(e, true));
				}
			}
		}

	}

	public boolean isPlayerOnGround() {
		return numFootContacts > 0;
	}

	public Array<Body> getBodiesToRemove() {
		return bodiesToRemove;
	}

	public Array<Enemy> getEnemiesHit() {
		return enemiesHit;
	}

	public Array<Enemy> getAttackers() {
		return attackers;
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
