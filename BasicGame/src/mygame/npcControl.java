package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class npcControl extends AbstractControl {

    private float health = 100;
    private final float dmg = 15;
    private final float armor = 10;
    private final float attackDelay = 100;
    private final float speed = 100;
    private boolean dead = false;
    private float hitAnimationDelay = 0;

    private Spatial target;

    @Override
    protected void controlUpdate(float tpf) {

        if (!dead) {
            if (target != null) {
                this.spatial.lookAt(target.getWorldTranslation(), Vector3f.UNIT_Y);
            }

            if (health < 0) {
                dead = true;
                setAnim("Die", LoopMode.DontLoop);
            }

            if (hitAnimationDelay > 0) {
                hitAnimationDelay -= tpf;
                if (hitAnimationDelay <= 0) {
                    setAnim("Idle", LoopMode.Loop);
                }
            }
        }
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }

    private void setAnim(String name, LoopMode mode) {
        Node n = (Node) this.spatial;
        Node n1 = (Node) n.getChild("anim");
        AnimControl control = n1.getControl(AnimControl.class);
        control.getChannel(0).setAnim(name);
        control.getChannel(0).setLoopMode(mode);
    }

    public void hit(float dmg) {
        if (!dead) {
            if ((dmg - armor) > 0) {
                health -= (dmg - armor);
                hitAnimationDelay = 1;
                setAnim("Hit", LoopMode.DontLoop);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
