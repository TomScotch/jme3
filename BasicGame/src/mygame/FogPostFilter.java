package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class FogPostFilter extends AbstractControl {

    private final FogFilter fog;

    public void setFogColor(ColorRGBA color) {
        fog.setFogColor(color);

    }

    public void setFogDistance(float distance) {
        fog.setFogDistance(distance);

    }

    public void setFogDensity(float density) {
        fog.setFogDensity(density);
    }

    public FogPostFilter(FilterPostProcessor fpp) {

        fog = new FogFilter();
        fog.setFogColor(ColorRGBA.White);
        fog.setFogDistance(999);
        fog.setFogDensity(0.1f);
        fpp.addFilter(fog);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

}
