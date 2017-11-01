package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public abstract class FlipFlopControl extends AbstractControl {

    private float flipflop = 0;
    private boolean paused = false;
    private final float delay;

    public FlipFlopControl(float delay) {
        this.delay = delay;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (!paused) {
            if (flipflop < delay) {
                flipflop += tpf;
            } else {
                action();
                flipflop = 0;
            }
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

    abstract void action();
}
