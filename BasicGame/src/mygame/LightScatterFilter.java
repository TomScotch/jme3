package mygame;

import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class LightScatterFilter extends AbstractControl {

    private final GlobalLightingControl glc;
    
    private final LightScatteringFilter sunlight;
    private final boolean dynamicLightScatter;
    private final float lightScatterFilterDensity = 0.45f;//1.4f
    private final int lightScatterFiltersamples = 9;//50

    public LightScatterFilter(FilterPostProcessor fpp, GlobalLightingControl glc, boolean dynamicLightScatter) {

        this.glc = glc;
        sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
        sunlight.setLightDensity(lightScatterFilterDensity);
        sunlight.setNbSamples(lightScatterFiltersamples);
        fpp.addFilter(sunlight);
        this.dynamicLightScatter = dynamicLightScatter;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (glc.getIsSun()) {
            if (dynamicLightScatter) {
                sunlight.setLightPosition(glc.getSunPosition());
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }
}
