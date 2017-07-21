package mygame;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class BloomPostFilter extends AbstractControl {

    private final BloomFilter bloom;
    private final float density = 0.5f;//2
    private final float sampling = 4;//1

    public BloomPostFilter(FilterPostProcessor fpp) {

        bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBloomIntensity(density);
        bloom.setDownSamplingFactor(sampling);
        fpp.addFilter(bloom);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

    public void setBloomIntensity(float bloomIntensity) {
        bloom.setBloomIntensity(bloomIntensity);
    }

    public void setBlurScale(float blurScale) {
        bloom.setBlurScale(blurScale);
    }

    public void setDownSamplingFactor(float downSamplingFactor) {
        bloom.setDownSamplingFactor(downSamplingFactor);
    }

    public void setExposurePower(float exposurePower) {
        bloom.setExposurePower(exposurePower);
    }

    public float getBloomIntensity() {
        return bloom.getBloomIntensity();
    }

    public float getBlurScale() {
        return bloom.getBlurScale();
    }

    public float getDownSamplingFactor() {
        return bloom.getDownSamplingFactor();
    }

    public float getExposurePower() {
        return bloom.getExposurePower();
    }
}
