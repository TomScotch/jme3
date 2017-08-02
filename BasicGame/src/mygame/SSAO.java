package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class SSAO extends AbstractControl {

    private final SSAOFilter ssaoFilter;

    private float sampleRadius = 2.9299974f; // 2.9299974f;
    private float intensity = 32.920483f; // 32.920483f;
    private float scale = 5.8100376f; // 5.8100376f;
    private float bias = 0.091000035f; //0.091000035f;
    private boolean approximateNormals = true;

    public SSAO(AssetManager assetManager, FilterPostProcessor fpp) {

        ssaoFilter = new SSAOFilter(sampleRadius, intensity, scale, bias);
        ssaoFilter.setApproximateNormals(approximateNormals);
        fpp.addFilter(ssaoFilter);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getSampleRadius() {
        return sampleRadius;
    }

    public void setSampleRadius(float sampleRadius) {
        this.sampleRadius = sampleRadius;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }

    public boolean isApproximateNormals() {
        return approximateNormals;
    }

    public void setApproximateNormals(boolean approximateNormals) {
        this.approximateNormals = approximateNormals;
    }

}
