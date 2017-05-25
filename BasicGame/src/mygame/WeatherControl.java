package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class WeatherControl extends AbstractControl {

    private final AssetManager am;
    private boolean isRaining;
    private boolean isSnowing;
    private boolean isStorming;

    public WeatherControl(AssetManager am) {
        this.am = am;
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
