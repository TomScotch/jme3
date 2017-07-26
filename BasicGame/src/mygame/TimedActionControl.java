package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public abstract class TimedActionControl extends AbstractControl {

    private float counter = 0;
    private final float defaultCountDown = 30;
    private float countDown = 0;
    private boolean paused = false;

    public TimedActionControl() {
        countDown = defaultCountDown;
    }

    public TimedActionControl(float countDown) {
        this.countDown = countDown;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (!paused) {
            counter += tpf;
            if (counter >= countDown) {
                action();
                this.spatial.removeControl(this);
            }
        }
    }

    public void reset() {
        counter = 0;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setCountDown(float countDown) {
        this.countDown = countDown;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public float getCounter() {
        return counter;
    }

    public float getCountDown() {
        return countDown;
    }

    abstract void action();
}
