package mygame;

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.setDisplayStatView(false);
        app.setShowSettings(false);
        app.setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        app.start();
    }

    private BulletAppState bas;
    private final int size = 7;
    private Geometry core;

    @Override
    public void simpleInitApp() {

        inputManager.setCursorVisible(false);

        bas = new BulletAppState();
        bas.initialize(stateManager, this);
        stateManager.attach(bas);
        bas.getPhysicsSpace().setGravity(Vector3f.ZERO);

        core = addBox(50000);
        core.move(size / 2, size / 2, size / 2);
        rootNode.attachChild(core);

        for (int x = 0; x < size; x++) {

            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    Geometry geom = addBox(5);
                    geom.move(x, y, z);
                    rootNode.attachChild(geom);
                }
            }
        }

        getCamera().getLocation().set(0, 0, 50);
        getCamera().lookAt(core.getLocalTranslation(), Vector3f.UNIT_XYZ);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //
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

}
