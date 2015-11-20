package com.slickgames.simpleninja.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {

	private TextureRegion[] frames;
	private float time;
	private float delay;
	private int currentFrame;
	private int timesPlayed;

	public Animation() {
	}

	public Animation(TextureRegion[] frames) {
		this(frames, 1 / 12f);
	}

	public Animation(TextureRegion[] frames, float delay) {
		setFrames(frames, delay);
	}

	public void setFrames(TextureRegion[] frames, float delay) {
		this.frames = frames;
		this.delay = delay;
		time = 0;
		currentFrame = 0;
		timesPlayed = 0;
	}

	public void update(float dt) {
		if (delay <= 0)
			return;
		time += dt;
		while (time >= delay) {
			step();
		}
	}

	private void step() {
		time -= delay;
		currentFrame++;
		if (currentFrame == frames.length) {
			currentFrame = 0;
			timesPlayed++;
		}
	}

	public boolean getFinished() {
		System.out.println(currentFrame + ". " + frames.length);
		return (currentFrame+1 == frames.length ? true : false);
	}

	public TextureRegion getFrame() {
		return frames[currentFrame];
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public int getTimesPlayed() {
		return timesPlayed;
	}

	public void setSpeed(float speed) {
		this.delay = speed;
	}
}
