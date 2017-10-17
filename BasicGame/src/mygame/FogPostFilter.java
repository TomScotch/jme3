package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class FogPostFilter extends AbstractControl {

    public FogFilter getFog() {
        return fog;
    }

    public void setFog(FogFilter fog) {
        this.fog = fog;
    }

    private FogFilter fog;

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
        fog.setFogColor(ColorRGBA.Gray);
        fog.setFogDistance(0);
        fog.setFogDensity(0);
        fpp.addFilter(fog);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

}
