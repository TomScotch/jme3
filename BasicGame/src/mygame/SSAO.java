package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class SSAO extends AbstractControl {

    private final SSAOFilter ssaoFilter;

    private float sampleRadius = 0.5f; // 5.1f 12.94f, 
    private float intensity = 0.5f; // 1.2f 43.92f, 
    private float scale = 0.2f;// 0.2f 0.33f, 
    private float bias = 0.3f; // 0.1f 0.61f
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
