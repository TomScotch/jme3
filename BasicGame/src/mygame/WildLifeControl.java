package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;

public class WildLifeControl extends AbstractControl {

    @Override
    protected void controlUpdate(float tpf) {

        spatial.move(0, 0, 15 * tpf);

        float x = spatial.getWorldTranslation().getX();
        float y = spatial.getWorldTranslation().getZ();
        float z = spatial.getWorldTranslation().getZ();

        if (getRandomNumberInRange(1, 2) > 1) {
            if (y < 150) {
                spatial.getWorldTranslation().addLocal(0, 1.5f * tpf, 0);
            } else {
                spatial.getWorldTranslation().subtractLocal(0, 2.5f * tpf, 0);
            }
        } else {
            if (y > 100) {
                spatial.getWorldTranslation().subtractLocal(0, 1.5f * tpf, 0);
            } else {
                spatial.getWorldTranslation().addLocal(0, 2.5f * tpf, 0);
            }
        }
        if (x > 2048) {
            killBird();
        }

        if (x < -2048) {
            killBird();
        }

        if (z > 2048) {
            killBird();
        }

        if (z < -2048) {
            killBird();
        }

    }

    public void killBird() {
        this.spatial.removeFromParent();
        this.spatial.removeControl(this);
        this.spatial = null;
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

    public SkeletonControl getSkeletonControl() {
        Node n = (Node) this.spatial;
        Node e = (Node) n.getChild("anim");
        return e.getControl(SkeletonControl.class);
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }
}
