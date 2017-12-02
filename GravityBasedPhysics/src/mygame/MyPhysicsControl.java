package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

public class MyPhysicsControl extends AbstractControl {

    ArrayList<Spatial> children = new ArrayList<>();

    @Override
    protected void controlUpdate(float tpf) {

        for (Spatial child : this.spatial.getParent().getChildren()) {
            try {
                if (this.spatial.getControl(RigidBodyControl.class).getMass() < child.getControl(RigidBodyControl.class).getMass() | child.getControl(RigidBodyControl.class).getMass() == 0) {
                    if (!children.contains(child)) {
                        children.add(child);
                    }
                }
            } catch (Exception e) {
            }
        }

        for (int cA = 0; cA < children.size(); cA++) {
            for (int cB = 0; cB < children.size(); cB++) {
                Float massA = children.get(cA).getControl(RigidBodyControl.class).getMass();
                Float massB = children.get(cB).getControl(RigidBodyControl.class).getMass();
                if (massA > massB | massA == 0) {
                    Spatial temp = children.get(cA);
                    children.set(children.indexOf(children.get(cA)), children.get(cB));
                    children.set(children.indexOf(children.get(cB)), temp);
                }
            }
        }

        if (children.size() > 0) {
            this.spatial.getControl(RigidBodyControl.class).setGravity(children.get(0).getWorldTranslation().subtract(this.spatial.getWorldTranslation()).mult(25));
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }
}
