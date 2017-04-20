package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class EntityControl extends AbstractControl {

    private float health = 100;
    private final float armor = 10;
    private boolean dead = false;
    private float hitAnimationDelay = 0;
    private float deadDelay = 3f;

    private String targetName = "";
    private Spatial targetSpatial;

    @Override
    protected void controlUpdate(float tpf) {

        if (!dead) {

            if (!targetName.equals("")) {
                Node n = (Node) this.spatial;
                BetterCharacterControl control = this.spatial.getParent().
                        getControl(BetterCharacterControl.class);
                targetSpatial = n.getParent().getParent().getChild(targetName);
                Vector3f a = targetSpatial.getWorldTranslation();
                Vector3f b = this.spatial.getWorldTranslation();
                control.setViewDirection(a.subtract(b));
            }

            if (health < 0) {
                dead = true;
                setAnim("FiggetIdle", LoopMode.DontLoop);
            }

            if (hitAnimationDelay > 0) {
                hitAnimationDelay -= tpf;
                if (hitAnimationDelay <= 0) {
                    setAnim("IdleHeadTilt", LoopMode.Loop);
                }
            }
        }

        if (deadDelay <= 0) {

            BetterCharacterControl control = this.spatial.getParent().
                    getControl(BetterCharacterControl.class);
            control.getPhysicsSpace().remove(control);
            this.spatial.getParent().removeControl(BetterCharacterControl.class);
            this.spatial.getParent().removeControl(FlipFlopControl.class);
            this.spatial.getParent().removeFromParent();
            this.spatial.removeControl(this);
        }

        if (dead) {
            if (deadDelay >= 3f) {
                deadParticles(400);
                this.spatial.getParent().addControl(new FlipFlopControl() {
                    @Override
                    void action(boolean flipflop) {
                        Node n = (Node)this.spatial ;
                        if (flipflop) {
                            n.getChild("anim").setCullHint(Spatial.CullHint.Always);
                        } else {
                            n.getChild("anim").setCullHint(Spatial.CullHint.Never);
                        }
                    }
                });
            }
            deadDelay -= tpf;
        }

    }

    private void setAnim(String name, LoopMode mode) {
        Node n = (Node) this.spatial.getParent();
        Node e = (Node) n.getChild("anim");
        AnimControl aniCon = e.getControl(AnimControl.class);

        if (aniCon.getClass() != null) {
            aniCon.clearChannels();
            aniCon.createChannel();
            aniCon.getChannel(0).setAnim(name);
            aniCon.getChannel(0).setLoopMode(mode);
        } else {
            aniCon.createChannel();
            aniCon.getChannel(0).setAnim(name);
            aniCon.getChannel(0).setLoopMode(mode);
        }
    }

    public void hit(float dmg, String name) {
        if (!dead) {
            targetName = name;
            if ((dmg - armor) > 0) {
                health -= (dmg - armor);
                hitAnimationDelay = 1.5f;
                setAnim("AlertToRunTransition", LoopMode.DontLoop);
                hitParticles();
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    private void hitParticles() {
        Node n = (Node) this.spatial.getParent();
        Node n1 = (Node) n.getChild("hit");
        ParticleEmitter child = (ParticleEmitter) n1.getChild("emitter");
        child.emitAllParticles();
    }

    private void deadParticles(int num) {
        Node n = (Node) this.spatial.getParent();
        Node n1 = (Node) n.getChild("death");
        ParticleEmitter child = (ParticleEmitter) n1.getChild("emitter");
        child.setParticlesPerSec(num);
    }
}
