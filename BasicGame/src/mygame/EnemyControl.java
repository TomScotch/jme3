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

    private final MotionPath path1;
    private final MotionPath path2;
    private final MotionPath path3;
    private MotionEvent motionControl1;
    private MotionEvent motionControl2;
    private MotionEvent motionControl3;
    private boolean playing1;
    private boolean playing2;
    private boolean playing3;
    private final Node localRoot;
    private final AssetManager assetManager;

    private final PlayerControl pc;
    private EntityControl ec1;
    private EntityControl ec2;
    private EntityControl ec3;

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
        path1 = new MotionPath();
        path2 = new MotionPath();
        path3 = new MotionPath();

        path1.addWayPoint(new Vector3f(0, 2.f, 30));
        path1.addWayPoint(new Vector3f(15, 0.35f, 7.5f));
        path1.addWayPoint(new Vector3f(-15, 2.2f, 7.5f));
        path1.addWayPoint(new Vector3f(0, 2.f, 30));

        //  path1.enableDebugShape(assetManager, localRoot);
        path2.addWayPoint(new Vector3f(-35, 5f, 30));
        path2.addWayPoint(new Vector3f(-50, 5.15f, 7.5f));
        path2.addWayPoint(new Vector3f(-20, 2, 7.5f));
        path2.addWayPoint(new Vector3f(-35, 5f, 30));

        //  path2.enableDebugShape(assetManager, localRoot);
        path3.addWayPoint(new Vector3f(0, 1f, 30));
        path3.addWayPoint(new Vector3f(35, 0.5f, 30));
        path3.addWayPoint(new Vector3f(35, 0.25f, 7.5f));
        path3.addWayPoint(new Vector3f(0, 1, 30));

        // path3.enableDebugShape(assetManager, localRoot);
        addHostille();
    }

    private void setMotionPath(String name, MotionPath path, Spatial spa) {

        switch (name) {

            case "demon":
                motionControl1 = new MotionEvent(spa, path);
                motionControl1.setDirectionType(MotionEvent.Direction.PathAndRotation);
                motionControl1.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.TWO_PI, Vector3f.UNIT_Y));
                motionControl1.setInitialDuration(10);
                motionControl1.setSpeed(0.5f);
                break;
            case "forestmonster":
                motionControl2 = new MotionEvent(spa, path);
                motionControl2.setDirectionType(MotionEvent.Direction.PathAndRotation);
                motionControl2.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.TWO_PI, Vector3f.UNIT_Y));
                motionControl2.setInitialDuration(10);
                motionControl2.setSpeed(0.25f);
                break;
            case "spider":
                motionControl3 = new MotionEvent(spa, path);
                motionControl3.setDirectionType(MotionEvent.Direction.PathAndRotation);
                motionControl3.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.TWO_PI, Vector3f.UNIT_Y));
                motionControl3.setInitialDuration(10);
                motionControl3.setSpeed(0.75f);
                break;
        }

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (countEnemys() <= 0) {
            addHostille();
        }

        if (ec1 != null) {
            playing1 = !ec1.isFighting();
            if (playing1) {
                motionControl1.play();
                if (ec1.getAnimControl() != null) {
                    if (ec1.getAnimControl().getChannel(0) != null) {
                        if (!ec1.getAnimControl().getChannel(0).getAnimationName().equals("Walk")) {
                            ec1.setAnim("Walk", LoopMode.Loop);
                        }
                    }
                }
            } else {
                motionControl1.pause();
            }
        }

        if (ec2 != null) {
            playing2 = !ec2.isFighting();
            if (playing2) {
                motionControl2.play();
                if (ec2.getAnimControl() != null) {
                    if (ec2.getAnimControl().getChannel(0) != null) {
                        if (!ec2.getAnimControl().getChannel(0).getAnimationName().equals("Walk")) {
                            ec2.setAnim("Walk", LoopMode.Loop);
                        }
                    }
                }
            } else {
                motionControl2.pause();
            }
        }

        if (ec3 != null) {
            playing3 = !ec3.isFighting();
            if (playing3) {
                motionControl3.play();
                if (ec3.getAnimControl() != null) {
                    if (ec3.getAnimControl().getChannel(0) != null) {
                        if (!ec3.getAnimControl().getChannel(0).getAnimationName().equals("Walk")) {
                            ec3.setAnim("Walk", LoopMode.Loop);
                        }
                    }
                }
            } else {
                motionControl3.pause();
            }
        }
    }

    private void addHostille() {
        Spatial demon = assetManager.loadModel("Models/hostile/demon/demon.j3o");
        ec1 = new EntityControl(assetManager, demon, "demon", new Vector3f(-10, 0, -10), pc);
        demon.setLocalTranslation(new Vector3f(10, 1.75f, 10));
        demon.addControl(ec1);
        localRoot.attachChild(demon);
        setMotionPath("demon", path1, demon);
        playing1 = true;

        Spatial forestmonster = assetManager.loadModel("Models/hostile/forestmonster/forestmonster.j3o");
        ec2 = new EntityControl(assetManager, forestmonster, "forestmonster", new Vector3f(-10, 0, 10), pc);
        forestmonster.addControl(ec2);
        localRoot.attachChild(forestmonster);
        setMotionPath("forestmonster", path2, forestmonster);
        playing2 = true;

        Spatial spider = assetManager.loadModel("Models/hostile/spider/spider.j3o");
        ec3 = new EntityControl(assetManager, spider, "spider", new Vector3f(-10, 0, -10), pc);
        spider.addControl(ec3);
        localRoot.attachChild(spider);
        playing3 = true;
        setMotionPath("spider", path3, spider);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

}
