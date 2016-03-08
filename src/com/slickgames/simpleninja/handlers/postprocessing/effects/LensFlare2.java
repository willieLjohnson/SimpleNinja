/*******************************************************************************
 * Copyright 2012 tsagrista
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.slickgames.simpleninja.handlers.postprocessing.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.slickgames.simpleninja.handlers.postprocessing.PostProcessor;
import com.slickgames.simpleninja.handlers.postprocessing.PostProcessorEffect;
import com.slickgames.simpleninja.handlers.postprocessing.filters.Bias;
import com.slickgames.simpleninja.handlers.postprocessing.filters.Blur;
import com.slickgames.simpleninja.handlers.postprocessing.filters.Blur.BlurType;
import com.slickgames.simpleninja.handlers.postprocessing.filters.Combine;
import com.slickgames.simpleninja.handlers.postprocessing.filters.Lens2;
import com.slickgames.simpleninja.handlers.postprocessing.utils.PingPongBuffer;

/** Pseudo lens flare implementation. This is a post-processing effect entirely, no need for light positions or anything. It
 * includes ghost generation, halos, chromatic distortion and blur.
 * @author Toni Sagrista */
public final class LensFlare2 extends PostProcessorEffect {
    private PingPongBuffer pingPongBuffer;
    private Lens2 lens;
    private Blur blur;
    private Bias bias;
    private Combine combine;
    private Settings settings;
    private boolean blending = false;
    private int sfactor, dfactor;
    public LensFlare2(int fboWidth, int fboHeight) {
        pingPongBuffer = PostProcessor.newPingPongBuffer(fboWidth, fboHeight, PostProcessor.getFramebufferFormat(), false);

        lens = new Lens2(fboWidth, fboHeight);
        blur = new Blur(fboWidth, fboHeight);
        bias = new Bias();
        combine = new Combine();

        setSettings(new Settings("default", 2, -0.9f, 1f, 1f, 0.7f, 1f, 8, 0.5f));
    }

    @Override
    public void dispose() {
        combine.dispose();
        bias.dispose();
        blur.dispose();
        pingPongBuffer.dispose();
    }

    public void setBaseIntesity(float intensity) {
        combine.setSource1Intensity(intensity);
    }

    public void setFlareIntesity(float intensity) {
        combine.setSource2Intensity(intensity);
    }

    public void setHaloWidth(float haloWidth) {
        lens.setHaloWidth(haloWidth);
    }

    public void setLensColorTexture(Texture tex) {
        lens.setLensColorTexture(tex);
    }

    public void enableBlending(int sfactor, int dfactor) {
        this.blending = true;
        this.sfactor = sfactor;
        this.dfactor = dfactor;
    }

    public void disableBlending() {
        this.blending = false;
    }

    public float getBias() {
        return bias.getBias();
    }

    public void setBias(float b) {
        bias.setBias(b);
    }

    public float getBaseIntensity() {
        return combine.getSource1Intensity();
    }

    public float getBaseSaturation() {
        return combine.getSource1Saturation();
    }

    public void setBaseSaturation(float saturation) {
        combine.setSource1Saturation(saturation);
    }

    public float getFlareIntensity() {
        return combine.getSource2Intensity();
    }

    public float getFlareSaturation() {
        return combine.getSource2Saturation();
    }

    public void setFlareSaturation(float saturation) {
        combine.setSource2Saturation(saturation);
    }

    public int getGhosts() {
        return (int) lens.getGhosts();
    }

    public void setGhosts(int ghosts) {
        lens.setGhosts(ghosts);
    }

    public boolean isBlendingEnabled() {
        return blending;
    }

    public int getBlendingSourceFactor() {
        return sfactor;
    }

    public int getBlendingDestFactor() {
        return dfactor;
    }

    public BlurType getBlurType() {
        return blur.getType();
    }

    public void setBlurType(BlurType type) {
        blur.setType(type);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;

        // setup threshold filter
        setBias(settings.flareBias);

        // setup combine filter
        setBaseIntesity(settings.baseIntensity);
        setBaseSaturation(settings.baseSaturation);
        setFlareIntesity(settings.flareIntensity);
        setFlareSaturation(settings.flareSaturation);

        // setup blur filter
        setBlurPasses(settings.blurPasses);
        setBlurAmount(settings.blurAmount);
        setBlurType(settings.blurType);

        setGhosts(settings.ghosts);
    }

    public int getBlurPasses() {
        return blur.getPasses();
    }

    public void setBlurPasses(int passes) {
        blur.setPasses(passes);
    }

    public float getBlurAmount() {
        return blur.getAmount();
    }

    public void setBlurAmount(float amount) {
        blur.setAmount(amount);
    }

    @Override
    public void render(final FrameBuffer src, final FrameBuffer dest) {
        Texture texsrc = src.getColorBufferTexture();

        boolean blendingWasEnabled = PostProcessor.isStateEnabled(GL20.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        pingPongBuffer.begin();
        {
            // apply bias
            bias.setInput(texsrc).setOutput(pingPongBuffer.getSourceBuffer()).render();

            lens.setInput(pingPongBuffer.getSourceBuffer()).setOutput(pingPongBuffer.getResultBuffer()).render();

            pingPongBuffer.set(pingPongBuffer.getResultBuffer(), pingPongBuffer.getSourceBuffer());

            // blur pass
            blur.render(pingPongBuffer);
        }
        pingPongBuffer.end();

        if (blending || blendingWasEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }

        if (blending) {
            Gdx.gl.glBlendFunc(sfactor, dfactor);
        }

        restoreViewport(dest);

        // mix original scene and blurred threshold, modulate via
        combine.setOutput(dest).setInput(texsrc, pingPongBuffer.getResultTexture()).render();
    }

    @Override
    public void rebind() {
        blur.rebind();
        bias.rebind();
        combine.rebind();
        pingPongBuffer.rebind();
    }

    public static class Settings {
        public final String name;

        public final BlurType blurType;
        public final int blurPasses; // simple blur
        public final float blurAmount; // normal blur (1 pass)
        public final float flareBias;

        public final float flareIntensity;
        public final float flareSaturation;
        public final float baseIntensity;
        public final float baseSaturation;

        public final int ghosts;
        public final float haloWidth;

        public Settings(String name, BlurType blurType, int blurPasses, float blurAmount, float flareBias, float baseIntensity,
                        float baseSaturation, float flareIntensity, float flareSaturation, int ghosts, float haloWidth) {
            this.name = name;
            this.blurType = blurType;
            this.blurPasses = blurPasses;
            this.blurAmount = blurAmount;

            this.flareBias = flareBias;
            this.baseIntensity = baseIntensity;
            this.baseSaturation = baseSaturation;
            this.flareIntensity = flareIntensity;
            this.flareSaturation = flareSaturation;

            this.ghosts = ghosts;
            this.haloWidth = haloWidth;
        }

        // simple blur
        public Settings(String name, int blurPasses, float flareBias, float baseIntensity, float baseSaturation,
                        float flareIntensity, float flareSaturation, int ghosts, float haloWidth) {
            this(name, BlurType.Gaussian5x5b, blurPasses, 0, flareBias, baseIntensity, baseSaturation, flareIntensity,
                    flareSaturation, ghosts, haloWidth);
        }

        public Settings(Settings other) {
            this.name = other.name;
            this.blurType = other.blurType;
            this.blurPasses = other.blurPasses;
            this.blurAmount = other.blurAmount;

            this.flareBias = other.flareBias;
            this.baseIntensity = other.baseIntensity;
            this.baseSaturation = other.baseSaturation;
            this.flareIntensity = other.flareIntensity;
            this.flareSaturation = other.flareSaturation;

            this.ghosts = other.ghosts;
            this.haloWidth = other.haloWidth;

        }
    }
}
