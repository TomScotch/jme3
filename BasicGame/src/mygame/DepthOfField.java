package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.JmeContext;

public class DepthOfField extends AbstractControl {

    private final DepthOfFieldFilter dofFilter;
    private final JmeContext context;
    private final Camera cam;

    public DepthOfField(FilterPostProcessor fpp, JmeContext context, ViewPort vp, AssetManager assetManager) {

        this.context = context;
        this.cam = vp.getCamera();

        dofFilter = new DepthOfFieldFilter();
        dofFilter.setFocusDistance(0);
        dofFilter.setFocusRange(50);
        dofFilter.setBlurScale(1);
        fpp.addFilter(dofFilter);
    }

    @Override
    protected void controlUpdate(float tpf) {

        Node n = (Node) this.spatial;
        Spatial s = n.getChild("terrainNode");
        if (s != null) {
            Vector3f origin = cam.getWorldCoordinates(new Vector2f(context.getSettings().getWidth() / 2, context.getSettings().getHeight() / 2), 0.0f);
            Vector3f direction = cam.getWorldCoordinates(new Vector2f(context.getSettings().getWidth() / 2, context.getSettings().getHeight() / 2), 0.3f);
            direction.subtractLocal(origin).normalizeLocal();
            Ray ray = new Ray(origin, direction);
            CollisionResults results = new CollisionResults();
            int numCollisions = s.collideWith(ray, results);
            if (numCollisions > 0) {
                CollisionResult hit = results.getClosestCollision();
                dofFilter.setFocusDistance(hit.getDistance() / 10.0f);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
