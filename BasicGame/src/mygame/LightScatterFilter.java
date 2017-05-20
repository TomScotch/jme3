package mygame;

import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class LightScatterFilter extends AbstractControl {

    private final LightScatteringFilter sunlight;
    private GlobalLightingControl glc;
    private boolean dynamicLightScatter;

    public LightScatterFilter(FilterPostProcessor fpp) {

        sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
        sunlight.setLightDensity(0.45f);
        sunlight.setNbSamples(9);
        fpp.addFilter(sunlight);
        dynamicLightScatter = true;
    }

    public LightScatterFilter(FilterPostProcessor fpp, GlobalLightingControl glc) {

        this.glc = glc;

        sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
        sunlight.setLightDensity(0.45f);
        sunlight.setNbSamples(9);
        fpp.addFilter(sunlight);
        dynamicLightScatter = true;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled() && dynamicLightScatter) {
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
