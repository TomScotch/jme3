package mygame;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.water.WaterFilter;

public class WaterPostFilter extends AbstractControl {

    private float time = 0.0f;
    private float waterHeight = -12f;
    private final float initialWaterHeight = -10f;
    private final WaterFilter water;
    private GlobalLightingControl glc;
    private boolean dynamicWater;
    private boolean dynamicLighting;

    public WaterPostFilter(FilterPostProcessor fpp, GlobalLightingControl glc, boolean specular, boolean hqshore, boolean caustics, boolean foam, boolean refraction, boolean ripples, boolean dynamicLight, boolean dynamicWater) {

        this.glc = glc;
        water = new WaterFilter((Node) spatial, new Vector3f(0, 0, 0));
        water.setWaterHeight(initialWaterHeight);
        initWater(specular, hqshore, caustics, foam, refraction, ripples, dynamicLight, dynamicWater);
        fpp.addFilter(water);
    }

    public WaterPostFilter(FilterPostProcessor fpp, boolean specular, boolean hqshore, boolean caustics, boolean foam, boolean refraction, boolean ripples, boolean dynamicWater) {

        water = new WaterFilter((Node) spatial, new Vector3f(0, 0, 0));
        water.setWaterHeight(initialWaterHeight);
        initWater(specular, hqshore, caustics, foam, refraction, ripples, false, dynamicWater);
        fpp.addFilter(water);
    }

    private void initWater(boolean specular, boolean hqShore, boolean caustic, boolean foam, boolean refraction, boolean ripples, boolean dynamicLight, boolean dynamicWater) {
        water.setUseSpecular(specular);
        water.setUseHQShoreline(hqShore);
        water.setUseCaustics(caustic);
        water.setUseFoam(foam);
        water.setUseRefraction(refraction);
        water.setUseRipples(ripples);
        dynamicLighting = dynamicLight;
        this.dynamicWater = dynamicWater;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (isEnabled()) {

            time += tpf;

            if (dynamicWater) {
                waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
                water.setWaterHeight(initialWaterHeight + waterHeight);
            }

            if (dynamicLighting) {
                water.getLightDirection().set(glc.getSunDirection());
            }

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDynamicWater() {
        return dynamicWater;
    }

    public boolean isDynamicLighting() {
        return dynamicLighting;
    }

    public void setDynamicWater(boolean dynamicWater) {
        this.dynamicWater = dynamicWater;
    }

    public void setDynamicLighting(boolean dynamicLighting) {
        this.dynamicLighting = dynamicLighting;
    }
}
