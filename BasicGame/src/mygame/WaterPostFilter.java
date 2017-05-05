package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.water.WaterFilter;

public class WaterPostFilter extends AbstractControl {

    private float time = 0.0f;
    private float waterHeight = -0.1f;
    private final float initialWaterHeight = -0.9f;
    private final WaterFilter water;
    private final GlobalLightingControl control;

    public WaterPostFilter(AssetManager assetManager, ViewPort viewPort) {

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        control = this.spatial.getControl(GlobalLightingControl.class);
        water = new WaterFilter((Node) spatial, control.getSunDirection());
        water.setWaterHeight(initialWaterHeight);

        fpp.addFilter(water);
        viewPort.addProcessor(fpp);
    }

    @Override
    protected void controlUpdate(float tpf) {
        time += tpf;
        waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(initialWaterHeight + waterHeight);
        water.getLightDirection().set(control.getSunDirection());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
