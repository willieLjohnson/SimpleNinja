package com.slickgames.simpleninja.entities;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Particles extends ApplicationAdapter {
    SpriteBatch batch;
    ParticleEffect pe;

    @Override
    public void create() {
        batch = new SpriteBatch();

        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("res/particles/running_dust"), Gdx.files.internal("res/particles"));
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        pe.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        pe.update(Gdx.graphics.getDeltaTime());
        batch.begin();
        pe.draw(batch);
        batch.end();
        if (pe.isComplete())
            pe.reset();
    }
}