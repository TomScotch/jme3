package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class WildLifeControl extends AbstractControl {

    @Override
    protected void controlUpdate(float tpf) {
        spatial.move(0, 0, 15*tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

    public AnimControl getAnimControl() {
        Node n = (Node) this.spatial;
        Node e = (Node) n.getChild("anim");
        return e.getControl(AnimControl.class);
    }

    public void setAnim(String name, LoopMode mode) {

        if (getAnimControl().getClass() != null) {
            getAnimControl().clearChannels();
            getAnimControl().createChannel();
            getAnimControl().getChannel(0).setAnim(name);
            getAnimControl().getChannel(0).setLoopMode(mode);
        } else {
            getAnimControl().createChannel();
            getAnimControl().getChannel(0).setAnim(name);
            getAnimControl().getChannel(0).setLoopMode(mode);
        }
    }
}
