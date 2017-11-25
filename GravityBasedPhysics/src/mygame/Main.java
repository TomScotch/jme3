package mygame;

import com.jme3.app.SimpleApplication;
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
    private Geometry core;

    public static void main(String[] args) {

        Main app = new Main();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        app.setDisplayFps(true);
        app.start();
    }

    @Override
    public void simpleInitApp() {

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

        Sphere a = new Sphere(64, 64, 14);
        core = new Geometry("Box", a);
        Material matA = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matA.setColor("Color", ColorRGBA.randomColor());
        core.setMaterial(matA);

        RigidBodyControl rbcA = new RigidBodyControl(999999999);
        core.addControl(rbcA);
        bas.getPhysicsSpace().add(core);

        rootNode.attachChild(core);

        for (int x = 0; x < 8; x++) {
            rootNode.attachChild(addBox(100));
        }

        getFlyByCamera().setMoveSpeed(50);
        getCamera().getLocation().set(0, 0, 60);
    }

    @Override
    public void simpleUpdate(float tpf) {
        core.getControl(RigidBodyControl.class).setLinearVelocity(Vector3f.ZERO);
        core.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, 0.25f, 0));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //
    }

    private Geometry addBox(float mass) {
        Box a = new Box(1, 1, 1);
        Geometry geomA = new Geometry("Box", a);
        geomA.move(new Vector3f(0, 12, 0));
        Material matA = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matA.setColor("Color", ColorRGBA.randomColor());
        geomA.setMaterial(matA);
        RigidBodyControl rbcA = new RigidBodyControl(mass);
        geomA.addControl(rbcA);
        bas.getPhysicsSpace().add(geomA);
        rbcA.setGravity(Vector3f.ZERO);
        rbcA.setAngularDamping(0);
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
    }
}
