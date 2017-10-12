package mygame;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class BloomPostFilter extends AbstractControl {

    private final BloomFilter bloom;

    private float density = 2f;//2
    private float sampling = 1f;//1
    private float blurScale = 1.5f;//1.5f
    private float exposurePower = 5f;//5
    private float cutOff = 0.1f; // 0.1 - 1.0

    public BloomPostFilter(FilterPostProcessor fpp) {

        bloom = new BloomFilter(BloomFilter.GlowMode.SceneAndObjects);
        bloom.setExposureCutOff(cutOff);
        bloom.setBloomIntensity(density);
        bloom.setDownSamplingFactor(sampling);
        bloom.setBlurScale(blurScale);
        bloom.setExposurePower(exposurePower);
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
        this.density = bloomIntensity;
        bloom.setBloomIntensity(density);
    }

    public void setBlurScale(float blurScale) {
        this.blurScale = blurScale;
        bloom.setBlurScale(blurScale);
    }

    public void setSampling(float sampling) {
        bloom.setDownSamplingFactor(sampling);
        this.sampling = sampling;
    }

    public void setExposurePower(float exposurePower) {
        this.exposurePower = exposurePower;
        bloom.setExposurePower(exposurePower);
    }

    public void setCutOff(float cutOff) {
        bloom.setExposureCutOff(cutOff);
        this.cutOff = cutOff;
    }
}
