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
    private final GlobalLightingControl glc;
    private float emptyNodeTimer = 0;
    private Spatial spider;
    private Spatial forestmonster;
    private TimedActionControl timedActionControl2;
    private TimedActionControl timedActionControl3;

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
                    if (child.getName().equals("bear")) {
                        c += 1;
                    }
                }
            }
        }
        return c;
    }

    public EnemyControl(GlobalLightingControl glc, AssetManager assetManager, Node localRoot, BulletAppState bulletAppState, PlayerControl pc) {

        this.localRoot = localRoot;
        this.assetManager = assetManager;
        this.pc = pc;
        this.glc = glc;

        path1 = new MotionPath();
        path2 = new MotionPath();
        path3 = new MotionPath();

        path1.addWayPoint(new Vector3f(0, 2.f, 30));
        path1.addWayPoint(new Vector3f(15, 0.35f, 7.5f));
        path1.addWayPoint(new Vector3f(-15, 2.2f, 7.5f));
        path1.addWayPoint(new Vector3f(0, 2.f, 30));

        path2.addWayPoint(new Vector3f(-35, 5f, 30));
        path2.addWayPoint(new Vector3f(-50, 5.15f, 7.5f));
        path2.addWayPoint(new Vector3f(-20, 2, 7.5f));
        path2.addWayPoint(new Vector3f(-35, 5f, 30));

        path3.addWayPoint(new Vector3f(0, 1f, 30));
        path3.addWayPoint(new Vector3f(35, 0.5f, 30));
        path3.addWayPoint(new Vector3f(35, 0.25f, 7.5f));
        path3.addWayPoint(new Vector3f(0, 1, 30));

        // path3.enableDebugShape(assetManager, localRoot);
        /*        Spatial demon = assetManager.loadModel("Models/bear/bear.j3o");
        ec1 = new EntityControl(assetManager, demon, "bear", new Vector3f(-10, 0, -10), pc);
        demon.setLocalTranslation(new Vector3f(10, 1.75f, 10));
        demon.addControl(ec1);
        localRoot.attachChild(demon);
        setMotionPath("demon", path1, demon);
        playing1 = true;*/
        forestmonster = assetManager.loadModel("Models/hostile/forestmonster/forestmonster.j3o");
        ec2 = new EntityControl(assetManager, forestmonster, "forestmonster", new Vector3f(-10, 0, 10), pc);
        forestmonster.addControl(ec2);
        localRoot.attachChild(forestmonster);
        setMotionPath("forestmonster", path2, forestmonster);
        playing2 = true;

        spider = assetManager.loadModel("Models/spider/spider.j3o");
        ec3 = new EntityControl(assetManager, spider, "spider", new Vector3f(-10, 0, -10), pc);
        spider.addControl(ec3);
        localRoot.attachChild(spider);
        setMotionPath("spider", path3, spider);
        playing3 = true;
    }

    private void setMotionPath(String name, MotionPath path, Spatial spa) {

        switch (name) {

            case "demon":
                motionControl1 = new MotionEvent(spa, path);
                motionControl1.setDirectionType(MotionEvent.Direction.PathAndRotation);
                motionControl1.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.TWO_PI, Vector3f.UNIT_Y));
                motionControl1.setInitialDuration(10);
                break;
            case "forestmonster":
                motionControl2 = new MotionEvent(spa, path);
                motionControl2.setDirectionType(MotionEvent.Direction.PathAndRotation);
                motionControl2.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.TWO_PI, Vector3f.UNIT_Y));
                motionControl2.setInitialDuration(10);
                break;
            case "spider":
                motionControl3 = new MotionEvent(spa, path);
                motionControl3.setDirectionType(MotionEvent.Direction.PathAndRotation);
                motionControl3.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.TWO_PI, Vector3f.UNIT_Y));
                motionControl3.setInitialDuration(10);
                break;
        }

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {
            if (countEnemys() < 3) {
                emptyNodeTimer += tpf;

                if (emptyNodeTimer >= 20) {
                    emptyNodeTimer = 0;
                    addHostille();
                }
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

            if (motionControl1 != null) {
                motionControl1.setSpeed(((tpf / (glc.getTimeDelay()) / 4) * 200) + 0.25f);
            }

            if (motionControl2 != null) {
                motionControl2.setSpeed(((tpf / (glc.getTimeDelay() / 4)) * 200) + 0.15f);
            }

            if (motionControl3 != null) {
                motionControl3.setSpeed(((tpf / (glc.getTimeDelay()) / 4) * 200) + 0.35f);
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
    }

    private void addHostille() {

        boolean hasSpider = false;
        boolean hasDemon = false;
        boolean hasForestmonster = false;

        for (Spatial child : localRoot.getChildren()) {
            if (localRoot.getChildren() != null) {
                if (child.getName() != null) {
                    if (child.getName().equals("spider")) {
                        hasSpider = true;

                    }
                    if (child.getName().equals("forestmonster")) {
                        hasForestmonster = true;

                    }
                    if (child.getName().equals("demon")) {
                        hasDemon = true;

                    }
                }
            }
        }

        /*        if (!hasDemon) {
        TimedActionControl timedActionControl1 = new TimedActionControl(9) {
        @Override
        void action() {
        Spatial demon = assetManager.loadModel("Models/bear/bear.j3o");
        ec1 = new EntityControl(assetManager, demon, "bear", new Vector3f(-10, 0, -10), pc);
        demon.setLocalTranslation(new Vector3f(10, 1.75f, 10));
        demon.addControl(ec1);
        localRoot.attachChild(demon);
        setMotionPath("demon", path1, demon);
        playing1 = true;
        }
        };
        localRoot.addControl(timedActionControl1);
        }*/
        if (!hasForestmonster) {
            timedActionControl2 = new TimedActionControl(6) {
                @Override
                void action() {
                    forestmonster = assetManager.loadModel("Models/hostile/forestmonster/forestmonster.j3o");
                    ec2 = new EntityControl(assetManager, forestmonster, "forestmonster", new Vector3f(-10, 0, 10), pc);
                    forestmonster.addControl(ec2);
                    localRoot.attachChild(forestmonster);
                    setMotionPath("forestmonster", path2, forestmonster);
                    playing2 = true;
                }
            };
            localRoot.addControl(timedActionControl2);
        }

        if (!hasSpider) {
            timedActionControl3 = new TimedActionControl(3) {
                @Override
                void action() {
                    spider = assetManager.loadModel("Models/spider/spider.j3o");
                    ec3 = new EntityControl(assetManager, spider, "spider", new Vector3f(-10, 0, -10), pc);
                    spider.addControl(ec3);
                    localRoot.attachChild(spider);
                    setMotionPath("spider", path3, spider);
                    playing3 = true;
                }
            };
            localRoot.addControl(timedActionControl3);
        }
    }

    public void remAllEnemys() {
        spider.removeFromParent();
        forestmonster.removeFromParent();

        if (timedActionControl2 != null) {
            localRoot.removeControl(timedActionControl2);
        }
        if (timedActionControl3 != null) {
            localRoot.removeControl(timedActionControl3);
        }

        timedActionControl2 = null;
        timedActionControl3 = null;
        localRoot.detachChild(spider);
        localRoot.detachChild(forestmonster);
        spider = (Spatial) new Node();
        forestmonster = (Spatial) new Node();

        playing2 = false;
        playing3 = false;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

}
