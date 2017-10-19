package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.water.WaterFilter;

public class WaterPostFilter extends AbstractControl {

    private final GlobalLightingControl glc;

    private float timeWater = 0.0f;
    private float waterHeight = -5f;
    private final float initialWaterHeight = -7f;
    private final WaterFilter water;
    private final boolean dynamicWater;
    private final boolean dynamicLighting;

    public WaterPostFilter(FilterPostProcessor fpp, GlobalLightingControl glc, boolean specular, boolean hqshore, boolean caustics, boolean foam, boolean refraction, boolean ripples, boolean dynamicLight, boolean dynamicWater) {

        this.glc = glc;
        water = new WaterFilter((Node) spatial, new Vector3f(0, 0, 0));
        water.setWaterHeight(initialWaterHeight);
        this.dynamicLighting = dynamicLight;
        this.dynamicWater = dynamicWater;
        water.setUseSpecular(specular);
        water.setUseHQShoreline(hqshore);
        water.setUseCaustics(caustics);
        water.setUseFoam(foam);
        water.setUseRefraction(refraction);
        water.setUseRipples(ripples);
        fpp.addFilter(water);
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {

            timeWater += tpf;

            if (dynamicWater) {
                waterHeight = (float) Math.cos(((timeWater * 0.6f) % FastMath.TWO_PI)) * 1.5f;
                water.setWaterHeight(initialWaterHeight + waterHeight);
            }

            if (dynamicLighting) {
                if (!glc.isNight()) {
                    water.setDeepWaterColor(glc.getBackgroundColor());
                    water.setWaterColor(ColorRGBA.Blue);
                    water.setLightColor(glc.getSun().getColor());
                    water.getLightDirection().set(glc.getSunDirection());
                } else {
                    water.setDeepWaterColor(ColorRGBA.Black);
                    water.setLightColor(ColorRGBA.Black);
                    water.setLightDirection(new Vector3f(0, 1, 0));
                }
            }

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }
}
