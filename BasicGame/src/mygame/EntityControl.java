package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
//import com.jme3.audio.AudioData;
//import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.queue.RenderQueue;

public class EntityControl extends AbstractControl {

    private float health = 100;
    private final float armor = 10;
    private boolean dead = false;
    private float hitAnimationDelay = 0;
    private float deadDelay = 3f;
    private final float fleeDistance = 40;
    private String targetName = "";
    private Spatial targetSpatial;
    private final BetterCharacterControl bcc;
    private final AssetManager assetManager;
    public int mass = 1000;
    //private final AudioNode hit;

    public EntityControl(AssetManager assetManager, Spatial hostile, BulletAppState bulletState, String name, Vector3f pos) {

        this.spatial = hostile;
        this.assetManager = assetManager;
        hostile.setShadowMode(RenderQueue.ShadowMode.Cast);
        bcc = new BetterCharacterControl(3, 7, mass);
        bcc.setSpatial(hostile);
        hostile.addControl(bcc);
        bulletState.getPhysicsSpace().add(bcc);
        bcc.warp(new Vector3f(pos));
        setAnim("Idle", LoopMode.Loop);
        getSkeletonControl().setHardwareSkinningPreferred(false);

        /*        hit = new AudioNode(assetManager, "audio/creature-growl01.wav", AudioData.DataType.Buffer);
        hit.setLooping(false);
        hit.setPositional(false);
        hit.setVolume(2);
        Node localRootNode = (Node) this.spatial.getParent();
        localRootNode.attachChild(hit);*/
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (!dead) {

            if (!targetName.equals("")) {
                Node n = (Node) this.spatial;
                targetSpatial = n.getParent().getChild(targetName);
                Vector3f a = targetSpatial.getWorldTranslation();
                Vector3f b = this.spatial.getWorldTranslation();
                bcc.setViewDirection(a.subtract(b));
                float distance = a.distance(b);
                if (distance > fleeDistance) {
                    targetName = "";
                    bcc.setViewDirection(new Vector3f(0, 0, 0));
                }
            }

            if (health < 0) {
                dead = true;
                setAnim("Idle", LoopMode.DontLoop);
            }

            if (hitAnimationDelay > 0) {
                hitAnimationDelay -= tpf;
                if (hitAnimationDelay <= 0) {
                    setAnim("Idle", LoopMode.Loop);
                }
            }
        }

        if (deadDelay <= 0) {

            BetterCharacterControl control = this.spatial.
                    getControl(BetterCharacterControl.class);
            control.getPhysicsSpace().remove(control);
            this.spatial.removeControl(BetterCharacterControl.class);
            this.spatial.removeFromParent();
            this.spatial.removeControl(this);
        }

        if (dead) {
            if (deadDelay >= 3f) {
                deadParticles(4);
                setAnim("Dying", LoopMode.DontLoop);
            }
            deadDelay -= tpf;
        }

    }

    private SkeletonControl getSkeletonControl() {
        Node n = (Node) this.spatial;
        Node e = (Node) n.getChild("anim");
        return e.getControl(SkeletonControl.class);
    }

    public AnimControl getAnimControl() {
        Node n = (Node) this.spatial;
        Node e = (Node) n.getChild("anim");
        return e.getControl(AnimControl.class);
    }

    private void setAnim(String name, LoopMode mode) {

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

    public void hit(float dmg, String name) {
        if (!dead) {
            targetName = name;
            if ((dmg - armor) > 0) {
                health -= (dmg - armor);
                hitAnimationDelay = 1.5f;
                setAnim("Hit", LoopMode.Loop);
                // hit.play();
                hitParticles();
                this.spatial.addControl(new ShowDamage(assetManager, Float.toString(dmg), (Node) this.spatial));
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    private void hitParticles() {
        Node n = (Node) this.spatial;
        Node n1 = (Node) n.getChild("hit");
        ParticleEmitter child = (ParticleEmitter) n1.getChild("emitter");
        child.emitAllParticles();
    }

    private void deadParticles(int num) {
        Node n = (Node) this.spatial;
        Node n1 = (Node) n.getChild("death");
        ParticleEmitter child = (ParticleEmitter) n1.getChild("emitter");
        child.setParticlesPerSec(num);
    }
}
