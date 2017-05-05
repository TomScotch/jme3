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

    public LightScatterFilter(ViewPort vp, AssetManager am) {

        FilterPostProcessor fpp = new FilterPostProcessor(am);
        sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
        fpp.addFilter(sunlight);
        vp.addProcessor(fpp);

    }

    @Override
    protected void controlUpdate(float tpf) {
        sunlight.setLightPosition(this.spatial.getControl(GlobalLightingControl.class).getSunPosition());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

}
