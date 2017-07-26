package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class SSAO extends AbstractControl {

    private float intensity = 2f;//1.2f

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public SSAO(AssetManager assetManager) {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter ssaoFilter = new SSAOFilter(5.1f, 1.2f, 0.2f, 0.1f);
        ssaoFilter.setApproximateNormals(true);
        ssaoFilter.setIntensity(intensity);
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

}
