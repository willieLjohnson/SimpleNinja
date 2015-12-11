package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener {

	private int numFootContacts;
	private Array<Body> bodiesToRemove;
	private boolean detectRight;
	private boolean detectLeft;

	public MyContactListener() {
		super();
		bodiesToRemove = new Array<Body>();
	}

	// called when two fixtures start collide
	@Override
	public void beginContact(Contact c) {
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();

		if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
			numFootContacts++;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
			numFootContacts++;
		}

		if (fa.getUserData() != null && fa.getUserData().equals("crystal")) {
			bodiesToRemove.add(fa.getBody());
		}
		if (fb.getUserData() != null && fb.getUserData().equals("crystal")) {
			bodiesToRemove.add(fb.getBody());
		}

		if (fa.getUserData() != null && fa.getUserData().equals("visionRight")) {
			detectRight = true;

		}
		if (fb.getUserData() != null && fb.getUserData().equals("visionRight")) {
			detectRight = true;

		}

		if (fa.getUserData() != null && fa.getUserData().equals("visionLeft")) {
			detectLeft = true;

		}
		if (fb.getUserData() != null && fb.getUserData().equals("visionLeft")) {
			detectLeft = true;
		}
	}

	// called when two fixtures no longer collide
	@Override
	public void endContact(Contact c) {
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();

		if (fa == null || fb == null)
			return;
		if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
			numFootContacts--;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
			numFootContacts--;
		}
		if (fa.getUserData() != null && fa.getUserData().equals("visionRight")) {
			detectRight = false;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("visionRight")) {
			detectRight = false;
		}
		if (fa.getUserData() != null && fa.getUserData().equals("visionLeft")) {
			detectLeft = false;

		}
		if (fb.getUserData() != null && fb.getUserData().equals("visionLeft")) {
			detectLeft = false;
		}
	}

	public boolean isPlayerOnGround() {
		return numFootContacts > 0;
	}

	public boolean isPlayerSpotted(int n) {
		return n == 1 ? detectRight : detectLeft;
	}

	public Array<Body> getBodiesToRemove() {
		return bodiesToRemove;
	}

	// collision detection
	// presolve
	// collision handling
	// postolve
	@Override
	public void preSolve(Contact c, Manifold m) {
	}

	@Override
	public void postSolve(Contact c, ContactImpulse m) {
	}

}
