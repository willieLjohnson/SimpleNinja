package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.slickgames.simpleninja.main.Game;

public class Player extends B2DSprite {
	private int numCrystals;
	private int totalCrystals;
	public boolean running, idling, jumping, attacking;
	public static final float MAX_SPEED = 3f;
	TextureRegion[] run, idle, jump,attack;

	public Player(Body body) {
		super(body);

		Texture runningAnimation = Game.game.getAssetManager().get("res/images/simple_runAll.png");
		Texture attackingAnimation = Game.game.getAssetManager().get("res/images/simple_attackAll.png");
		Texture idlingAnimation = Game.game.getAssetManager().get("res/images/simple_idleAll.png");
		run = TextureRegion.split(runningAnimation, 54, 42)[0];
		idle = TextureRegion.split(idlingAnimation, 54, 42)[0];
		jump = TextureRegion.split(runningAnimation, 54, 42)[0];
		attack = TextureRegion.split(attackingAnimation, 54, 42)[0];
		setAnimation(idle, 1 / 7f);
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
			attacking = false;
			setAnimation(run, 1 / 18f);
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
			setAnimation(attack, 1 / 14f);
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

}
