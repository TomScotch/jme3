package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public abstract class FlipFlopControl extends AbstractControl {

    private int flipflop = 1;
    private boolean paused = false;

    @Override
    protected void controlUpdate(float tpf) {

        if (!paused) {
            if (flipflop < 3) {
                flipflop += 1;
            } else {
                flipflop = 1;
            }

            if ((flipflop % 2) == 0) {
                action(true);
            } else {
                action(false);
            }
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    abstract void action(boolean flipflop);
}
