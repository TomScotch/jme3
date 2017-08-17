package mygame;

import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class EnemyControl extends AbstractControl {

    private final MotionPath path;
    private MotionEvent motionControl;
    private boolean playing;
    private final Node localRoot;
    private final AssetManager assetManager;
    private EntityControl ec3;
    private final PlayerControl pc;

    public int countEnemys() {
        int c = 0;
        for (Spatial child : localRoot.getChildren()) {
            if (localRoot.getChildren() != null) {
                if (child.getName() != null) {
                    if (child.getName().equals("spider")) {
                        c += 1;
                    }
                    if (child.getName().equals("forestmonster")) {
                        c += 1;
                    }
                    if (child.getName().equals("demon")) {
                        c += 1;
                    }
                }
            }
        }
        return c;
    }

    public EnemyControl(AssetManager assetManager, Node localRoot, BulletAppState bulletAppState, PlayerControl pc) {

        this.localRoot = localRoot;
        this.assetManager = assetManager;
        this.pc = pc;
        path = new MotionPath();
        path.addWayPoint(new Vector3f(10, 1.75f, 0));
        path.addWayPoint(new Vector3f(-40, 4, 0));
        path.addWayPoint(new Vector3f(-40, 4.5f, 40));
        path.addWayPoint(new Vector3f(10, 1.75f, 0));
        // path.enableDebugShape(assetManager, localRoot);
        addHostille();
    }

    private void setMotionPath(Spatial spider) {
        motionControl = new MotionEvent(spider, path);
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.TWO_PI, Vector3f.UNIT_Y));
        motionControl.setInitialDuration(10);
        motionControl.setSpeed(0.1f);
        playing = true;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (countEnemys() <= 0) {
            addHostille();
        }

        if (ec3 != null) {
            playing = !ec3.isFighting();
        }

        if (playing) {
            motionControl.play();
            if (!ec3.getAnimControl().getChannel(0).getAnimationName().equals("walk")) {
                ec3.setAnim("walk", LoopMode.Loop);
            }

        } else {
            motionControl.pause();
        }
    }

    private void addHostille() {
        /*        Spatial demon = assetManager.loadModel("Models/hostile/demon/demon.j3o");
        EntityControl ec1 = new EntityControl(assetManager, demon, "demon", new Vector3f(10, 0, -10));
        demon.addControl(ec1);
        localRoot.attachChild(demon);
        
        Spatial forestmonster = assetManager.loadModel("Models/hostile/forestmonster/forestmonster.j3o");
        EntityControl ec2 = new EntityControl(assetManager, forestmonster, "forestmonster", new Vector3f(-10, 0, 10));
        forestmonster.addControl(ec2);
        localRoot.attachChild(forestmonster);*/

        Spatial spider = assetManager.loadModel("Models/hostile/spider/spider.j3o");
        ec3 = new EntityControl(assetManager, spider, "spider", new Vector3f(-10, 0, -10), pc);
        spider.addControl(ec3);
        localRoot.attachChild(spider);

        setMotionPath(spider);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
