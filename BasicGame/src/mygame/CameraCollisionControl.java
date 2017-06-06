package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class CameraCollisionControl extends AbstractControl {

    private final Camera cam;
    private final Node localRootNode;
    private final PlayerControl pc;

    public CameraCollisionControl(BulletAppState bulletAppState, Camera cam, Node localRootNode, PlayerControl pc) {
        this.cam = cam;
        this.localRootNode = localRootNode;
        this.pc = pc;
    }

    @Override
    protected void controlUpdate(float tpf) {

        Ray ray1 = new Ray(cam.getLocation(), cam.getDirection());
        CollisionResults results1 = new CollisionResults();
        localRootNode.collideWith(ray1, results1);
        if (results1.size() > 0) {
            if (results1.getClosestCollision().getGeometry().getName().contains("terrain")) {
                setNewCamPos();
            } else {
                pc.getChaseCam().setMaxDistance(50);
            }
        } else {
            pc.getChaseCam().setMaxDistance(50);
        }
    }

    public void setNewCamPos() {
        if (pc.getChaseCam() != null) {
            pc.getChaseCam().setMaxDistance(pc.getChaseCam().getMaxDistance() - 25);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
    }
}
