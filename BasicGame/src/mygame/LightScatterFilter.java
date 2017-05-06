package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class LightScatterFilter extends AbstractControl {

    private final LightScatteringFilter sunlight;
    private final GlobalLightingControl glc;
    private boolean dynamicLightScatter;
    private final FilterPostProcessor fpp;

    public LightScatterFilter(ViewPort vp, AssetManager am, GlobalLightingControl glc) {
        this.glc = glc;
        fpp = new FilterPostProcessor(am);
        sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
        sunlight.setLightDensity(0.5f);
        sunlight.setNbSamples(5);
        fpp.addFilter(sunlight);
        vp.addProcessor(fpp);
        dynamicLightScatter = true;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {
            if (glc.getIsSun()) {
                sunlight.setLightDensity(0.4f);

                if (isDynamicLightScatter()) {
                    sunlight.setLightPosition(glc.getSunPosition());
                }
            } else {
                sunlight.setLightDensity(0);
                if (isDynamicLightScatter()) {
                    sunlight.setLightPosition(new Vector3f(0, -1, 0));
                }
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

    public boolean isDynamicLightScatter() {
        return dynamicLightScatter;
    }

    public void setDynamicLightScatter(boolean dynamicLightScatter) {
        this.dynamicLightScatter = dynamicLightScatter;
    }

}
