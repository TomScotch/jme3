package mygame;

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bas;
    private final int size = 32;
    private Geometry core;

    public static void main(String[] args) {

        Main app = new Main();
        app.setDisplayStatView(true);
        app.setShowSettings(true);
        app.setDisplayFps(true);
        app.setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        doInit();
    }

    private void doInit() {
        inputManager.addMapping("record", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addListener(this, "record");
        inputManager.addMapping("restart", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "restart");
        inputManager.addMapping("debug", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addListener(this, "debug");

        inputManager.setCursorVisible(false);

        bas = new BulletAppState();
        bas.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        bas.initialize(stateManager, this);
        stateManager.attach(bas);
        bas.getPhysicsSpace().setGravity(Vector3f.ZERO);
        bas.setDebugEnabled(true);

        Sphere a = new Sphere(32, 32, 12);
        core = new Geometry("Box", a);
        Material matA = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matA.setColor("Color", ColorRGBA.randomColor());
        core.setMaterial(matA);
        RigidBodyControl rbcA = new RigidBodyControl(9999999);
        core.addControl(rbcA);
        bas.getPhysicsSpace().add(core);
        rbcA.setFriction(999);
        core.move(size / 2, size / 2, size / 2);
        core.center();
        rootNode.attachChild(core);

        for (int x = 0; x < size; x++) {
            Geometry geom = addBox(5);
            geom.move(Vector3f.ZERO);
            rootNode.attachChild(geom);
        }

        getFlyByCamera().setMoveSpeed(30);
        getCamera().getLocation().set(0, 0, 60);
        getCamera().lookAt(core.getLocalTranslation(), Vector3f.UNIT_XYZ);

    }

    @Override
    public void simpleUpdate(float tpf) {
        core.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(tpf * 100, 0, 0));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //
    }

    private Geometry addBox(int mass) {
        Box a = new Box(1, 1, 1);
        Geometry geomA = new Geometry("Box", a);
        Material matA = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matA.setColor("Color", ColorRGBA.randomColor());
        geomA.setMaterial(matA);
        RigidBodyControl rbcA = new RigidBodyControl(mass);
        geomA.addControl(rbcA);
        bas.getPhysicsSpace().add(geomA);
        MyPhysicsControl mpcA = new MyPhysicsControl();
        geomA.addControl(mpcA);
        return geomA;
    }

    @Override
    @SuppressWarnings("null")
    public void onAction(String name, boolean isPressed, float tpf) {

        if (name.equals("debug") && !isPressed) {
            if (bas.isDebugEnabled()) {
                bas.setDebugEnabled(false);
            } else {
                bas.setDebugEnabled(true);
            }
        }

        if (name.equals("record") && !isPressed) {
            if (stateManager.hasState(stateManager.getState(VideoRecorderAppState.class))) {
                stateManager.detach(stateManager.getState(VideoRecorderAppState.class));
            } else {
                stateManager.attach(new VideoRecorderAppState(1, 30));
            }
        }

        if (name.equals("restart") && !isPressed) {
            bas.setEnabled(false);
            bas.cleanup();
            rootNode.detachAllChildren();
            if (stateManager.getState(VideoRecorderAppState.class) != null) {
                stateManager.detach(stateManager.getState(VideoRecorderAppState.class));
            }
            inputManager.removeListener(this);
            System.gc();
            this.restart();
            doInit();
        }
    }
}
