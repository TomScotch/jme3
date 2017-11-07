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

    @Override
    public void simpleInitApp() {

        BulletAppState bas = new BulletAppState();
        bas.initialize(stateManager, this);
        stateManager.attach(bas);
        bas.getPhysicsSpace().setGravity(Vector3f.ZERO);
        Box a = new Box(1, 1, 1);
        Box b = new Box(2, 2, 2);
        Box c = new Box(3, 3, 3);
        Geometry geomA = new Geometry("BoxA", a);
        Geometry geomB = new Geometry("BoxB", b);
        Geometry geomC = new Geometry("BoxC", c);
        Material matA = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material matB = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material matC = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matA.setColor("Color", ColorRGBA.Blue);
        geomA.setMaterial(matA);
        matB.setColor("Color", ColorRGBA.Red);
        geomB.setMaterial(matB);
        matC.setColor("Color", ColorRGBA.Yellow);
        geomC.setMaterial(matC);
        rootNode.attachChild(geomA);
        rootNode.attachChild(geomB);
        rootNode.attachChild(geomC);
        geomA.move(-10, 0, -10);
        geomB.move(0, 10, -10);
        geomC.move(0, 0, -10);
        RigidBodyControl rbcA = new RigidBodyControl(5);
        RigidBodyControl rbcB = new RigidBodyControl(10);
        RigidBodyControl rbcC = new RigidBodyControl(500);
        geomA.addControl(rbcA);
        geomB.addControl(rbcB);
        geomC.addControl(rbcC);
        bas.getPhysicsSpace().add(geomA);
        bas.getPhysicsSpace().add(geomB);
        bas.getPhysicsSpace().add(geomC);
        MyPhysicsControl mpcA = new MyPhysicsControl();
        MyPhysicsControl mpcB = new MyPhysicsControl();
        MyPhysicsControl mpcC = new MyPhysicsControl();
        geomA.addControl(mpcA);
        geomB.addControl(mpcB);
        geomC.addControl(mpcC);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

}
