package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
//import com.jme3.audio.AudioData;
//import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.AmbientLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;

public class EntityControl extends AbstractControl {

    private float damage = 20;
    private final PlayerControl pc;
    private final Geometry healthbar;
    private final AmbientLight lamp;
    private final ParticleEmitter hit;

    public boolean isFighting() {
        return fighting;
    }

    public void setFighting(boolean fighting) {
        this.fighting = fighting;
    }

    private float health = 100;
    private final float armor = 10;
    private boolean dead = false;
    private float hitAnimationDelay = 0;
    private float deadDelay = 3f;
    private final float fleeDistance = 40;
    private String targetName = "";
    private Spatial targetSpatial;
    private final AssetManager assetManager;
    public int mass = 1000;
    //private final AudioNode hit;
    private boolean fighting = false;

    public EntityControl(AssetManager assetManager, Spatial hostile, String name, Vector3f pos, PlayerControl pc) {

        this.spatial = hostile;
        this.assetManager = assetManager;
        this.pc = pc;
        hostile.setShadowMode(RenderQueue.ShadowMode.Cast);

        switch (spatial.getName()) {
            case "bear":
                damage = 0;
                break;
            case "spider":
                damage = 15;
                break;
            default:
                damage = 20;
                break;
        }

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture("Textures/blood/blooddrop1.png"));
        material.setFloat("Softness", 3f); // 
        hit = new ParticleEmitter("Hit", ParticleMesh.Type.Triangle, 16);
        hit.setMaterial(material);
        hit.setShape(new EmitterSphereShape(Vector3f.ZERO, 2.5f));
        // hit.setImagesX(2);
        // hit.setImagesY(2); // 2x2 texture animation
        hit.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f)); // red
        hit.setStartColor(new ColorRGBA(1f, 0f, 0f, 1f)); // red
        hit.setStartSize(0.5f);
        hit.setEndSize(1f);
        hit.setGravity(0, 5, 3);
        hit.setLowLife(3f);
        hit.setHighLife(3f);
        hit.setLocalTranslation(0, 2f, 0);
        hit.setParticlesPerSec(0);
        hit.setQueueBucket(RenderQueue.Bucket.Opaque);
        Node n = (Node) this.spatial;
        n.attachChild(hit);

        /*        bcc = new BetterCharacterControl(3, 7, mass);
        bcc.setSpatial(hostile);
        hostile.addControl(bcc);
        bulletState.getPhysicsSpace().add(bcc);s
        bcc.warp(new Vector3f(pos));*/
        setAnim("Walk", LoopMode.Loop);
        getSkeletonControl().setHardwareSkinningPreferred(false);
        this.spatial.setQueueBucket(RenderQueue.Bucket.Opaque);

        // TangentBinormalGenerator.generate(this.spatial);

        /*        hit = new AudioNode(assetManager, "audio/creature-growl01.wav", AudioData.DataType.Buffer);
        hit.setLooping(false);
        hit.setPositional(false);
        hit.setVolume(2);
        Node localRootNode = (Node) this.spatial.getParent();
        localRootNode.attachChild(hit);*/
        this.spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        // this.spatial.lookAt(new Vector3f(1, 0, 0), Vector3f.UNIT_X);
        lamp = new AmbientLight(ColorRGBA.Red);
        // lamp.setPosition(new Vector3f(0, 5, 1));
        //  lamp.setColor(ColorRGBA.Red);
        //lamp.setRadius(75);
        lamp.setEnabled(false);
        spatial.addLight(lamp);
        //spatial.addLight(lamp);

        BillboardControl billboard = new BillboardControl();
        healthbar = new Geometry("healthbar", new Quad(4f, 0.2f));
        Material mathb = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mathb.setColor("Color", ColorRGBA.Red);
        healthbar.setMaterial(mathb);
        n.attachChild(healthbar);
        healthbar.getLocalScale().setX((health + 1) / 75);
        healthbar.center();
        healthbar.move(5, 9, 0);
        healthbar.addControl(billboard);

        if (name.equals("demon")) {
            this.spatial.scale(1.2f);
        }
        if (name.equals("bear")) {
            this.spatial.getWorldScale().addLocal(2, 2, 2);
            this.spatial.getWorldTranslation().addLocal(0, 4, 0);
        }

    }
    private float attackTimer = 0f;
    private final float attackTime = 1.5f;
    private boolean attacking = false;

    @Override
    protected void controlUpdate(float tpf) {

        if (!dead) {

            if (attackTimer <= 0) {
                if (attacking) {
                    attack();
                }
            }
            if (attackTimer > 0) {
                attackTimer -= tpf;
            }
            if (!targetName.equals("")) {
                fighting = true;
                Node n = (Node) this.spatial;
                targetSpatial = n.getParent().getChild(targetName);
                Vector3f a = targetSpatial.getWorldTranslation();
                Vector3f b = this.spatial.getWorldTranslation();

                float distance = a.distance(b);
                if (distance > fleeDistance / 1.5f) {
                    targetName = "";
                    fighting = false;
                } else {
                    //  spatial.lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
                    //   spatial.lookAt(Vector3f.ZERO, Vector3f.UNIT_Z);
                    spatial.lookAt(targetSpatial.getWorldTranslation(), Vector3f.UNIT_Y);
                }
            } else {
                fighting = false;
            }
            attacking = fighting;

            if (health < 0) {

                dead = true;

                if (!spatial.getName().equals("bear")) {
                    setAnim("Idle", LoopMode.DontLoop);
                } else {
                    setAnim("Attack", LoopMode.DontLoop);
                }
            }

            if (hitAnimationDelay > 0) {
                hitAnimationDelay -= tpf;
                if (hitAnimationDelay <= 0) {
                    if (!spatial.getName().equals("bear")) {
                        setAnim("Idle", LoopMode.DontLoop);
                    } else {
                        setAnim("Attack", LoopMode.DontLoop);
                    }
                }
            }
        }

        if (deadDelay <= 0) {

            /*            BetterCharacterControl control = this.spatial.
            getControl(BetterCharacterControl.class);
            control.getPhysicsSpace().remove(control);
            this.spatial.removeControl(BetterCharacterControl.class);*/
            this.spatial.removeFromParent();
            this.spatial.removeControl(this);
        }

        if (dead) {
            healthbar.getLocalScale().setX(0);
            if (deadDelay >= 3f) {
                deadParticles(20);
                if (this.spatial.getName().equals("spider")) {
                    this.spatial.rotate(-90, 0, 0);
                }
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

        if (this.spatial != null) {
            Node n = (Node) this.spatial;
            if (n.getChild("anim") != null) {
                Node e = (Node) n.getChild("anim");
                return e.getControl(AnimControl.class);
            }
        }
        return null;
    }

    public final void setAnim(String name, LoopMode mode) {

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
                healthbar.getLocalScale().setX((health + 1) / 75);
                healthbar.center();
                healthbar.move(-(((health + 1) / 75) * 2), 9, 0);
                hitAnimationDelay = 1.5f;
                if (spatial.getName().equals("bear")) {
                    setAnim("Walk", LoopMode.Loop);
                } else {
                    setAnim("Hit", LoopMode.Loop);
                }

                lamp.setEnabled(true);
                this.spatial.addControl(new TimedActionControl(0.4f) {
                    @Override
                    void action() {
                        lamp.setEnabled(false);
                    }
                });
                // hit.play();
                hitParticles();
                /*                if (health >= 0) {
                this.spatial.addControl(new ShowDamage(assetManager, Float.toString(dmg), (Node) this.spatial));
                }*/
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    private void hitParticles() {
        hit.emitParticles(15);
    }

    private void deadParticles(int num) {
        Node n = (Node) this.spatial;
        Node n1 = (Node) n.getChild("death");
        ParticleEmitter child = (ParticleEmitter) n1.getChild("emitter");
        child.setParticlesPerSec(num);
    }

    private void attack() {

        if (attackTimer <= 0) {
            setAnim("Attack", LoopMode.DontLoop);
            attackTimer = attackTime;

            if (pc.hit(damage, this.spatial.getName())) {
                fighting = false;
                attacking = false;
                targetName = "";
            }

        }
    }
}
