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
    private final float density = 0.45f;//1.4f
    private final int samples = 9;//50

    public LightScatterFilter(FilterPostProcessor fpp, boolean dynamicLightScatter) {

        sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
        sunlight.setLightDensity(density);
        sunlight.setNbSamples(samples);
        fpp.addFilter(sunlight);
        this.dynamicLightScatter = dynamicLightScatter;
    }

    public LightScatterFilter(FilterPostProcessor fpp, GlobalLightingControl glc, boolean dynamicLightScatter) {

        this.glc = glc;
        sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
        sunlight.setLightDensity(density);
        sunlight.setNbSamples(samples);
        fpp.addFilter(sunlight);
        this.dynamicLightScatter = dynamicLightScatter;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled() && dynamicLightScatter) {
            if (glc.getIsSun()) {

                if (isDynamicLightScatter()) {
                    sunlight.setLightPosition(glc.getSunPosition());
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
