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
    private CollisionResults results;
    private final PlayerControl pc;

    public CameraCollisionControl(BulletAppState bulletAppState, Camera cam, Node localRootNode, PlayerControl pc) {
        this.cam = cam;
        this.localRootNode = localRootNode;
        this.pc = pc;
    }

    @Override
    protected void controlUpdate(float tpf) {

        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        results = new CollisionResults();
        localRootNode.collideWith(ray, results);
        if (results.size() > 0) {
            if (results.getClosestCollision().getGeometry().getName().contains("terrain")) {
                setNewCamPos();
            } else {
                if (pc.getChaseCam().getMaxDistance() != 40) {
                    pc.getChaseCam().setMaxDistance(40);
                }
            }
        } else {
            if (pc.getChaseCam().getMaxDistance() != 40) {
                pc.getChaseCam().setMaxDistance(40);
            }
        }
    }

    public void setNewCamPos() {
        pc.getChaseCam().setMaxDistance(pc.getChaseCam().getMaxDistance() - 10);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
    }
}
