package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;

public class Main extends SimpleApplication implements ActionListener {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    private BulletAppState bas;

    @Override
    public void simpleInitApp() {
        bas = new BulletAppState();
        bas.setDebugEnabled(true);
        stateManager.attach(bas);
        bas.initialize(stateManager, this);
        inputManager.addListener(this, "fire");
        inputManager.addMapping("fire", new KeyTrigger(KeyInput.KEY_SPACE));
    }

    @Override
    public void simpleUpdate(float tpf) {
        //
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("fire") && !isPressed) {
            fireArrow();
        }
    }

    private void fireArrow() {
        Arrow arrow = new Arrow(new Vector3f(0, 6, -10), new Vector3f(0.5f, 0.5f, 0.0f).mult(50));
        Material matBullet = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matBullet.setBoolean("UseMaterialColors", true);
        matBullet.setColor("Ambient", ColorRGBA.Green);
        matBullet.setColor("Diffuse", ColorRGBA.Green);
        arrow.setMaterial(matBullet);
        rootNode.attachChild(arrow);
        bas.getPhysicsSpace().add(arrow);
    }

    public class Arrow extends Node {

        public Arrow(Vector3f location, Vector3f velocity) {
            Box arrowBody = new Box(0.3f, 4f, 0.3f);
            Geometry geometry = new Geometry("bullet", arrowBody);
            geometry.setLocalTranslation(0, -4f, 0);
            this.setLocalTranslation(location);
            SphereCollisionShape arrowHeadCollision = new SphereCollisionShape(0.5f);
            RigidBodyControl rigidBody = new RigidBodyControl(arrowHeadCollision, 1f);
            rigidBody.setLinearVelocity(velocity);
            addControl(rigidBody);
            addControl(new ArrowFacingControl());
        }
    }

    public class ArrowFacingControl extends AbstractControl {

        Vector3f directions;

        @Override
        protected void controlUpdate(float tpf) {
            directions = spatial.getControl(RigidBodyControl.class).getLinearVelocity().normalize();
            spatial.rotateUpTo(directions);
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            //
        }
    }
}
