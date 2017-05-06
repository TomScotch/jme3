package mygame;

import com.jme3.asset.AssetManager;
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
    private float waterHeight = -2f;
    private final float initialWaterHeight = -2f;
    private final WaterFilter water;
    private final GlobalLightingControl glc;
    private boolean dynamicWater;
    private boolean dynamicLighting;
    private final ViewPort viewPort;
    private final FilterPostProcessor fpp;

    public WaterPostFilter(AssetManager assetManager, ViewPort viewPort, GlobalLightingControl glc) {
        this.glc = glc;
        this.viewPort = viewPort;
        fpp = new FilterPostProcessor(assetManager);
        water = new WaterFilter((Node) spatial, new Vector3f(0, 0, 0));
        water.setWaterHeight(initialWaterHeight);
        water.setUseSpecular(false);
        water.setUseHQShoreline(false);
        fpp.addFilter(water);
        //viewPort.addProcessor(fpp);
        dynamicLighting = false;
        dynamicWater = false;
    }

    public void start() {
        viewPort.addProcessor(fpp);
    }

    public void stop() {
        viewPort.removeProcessor(fpp);
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
