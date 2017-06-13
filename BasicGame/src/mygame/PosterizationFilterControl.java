package mygame;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.PosterizationFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class PosterizationFilterControl extends AbstractControl {

    private final PosterizationFilter pf;

    public PosterizationFilterControl(FilterPostProcessor fpp) {
        pf = new PosterizationFilter();
        fpp.addFilter(pf);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (this.isEnabled()) {
            if (pf.getStrength() > 0) {
                pf.setStrength(pf.getStrength() - tpf);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

    public PosterizationFilter getPf() {
        return pf;
    }

    public float getStrength() {
        return pf.getStrength();
    }

    public void setStrength(float strength) {
        pf.setStrength(strength);
    }

    public int getNumColors() {
        return pf.getNumColors();
    }

    public void setNumColors(int numColors) {
        pf.setNumColors(numColors);
    }

    public float getGamma() {
        return pf.getGamma();
    }

    public void setGamma(float gamma) {
        pf.setGamma(gamma);
    }
}
