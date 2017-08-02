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
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.JmeContext;

public class DepthOfField extends AbstractControl {

    private final DepthOfFieldFilter dofFilter;
    private final JmeContext context;
    private final Camera cam;

    private float focusDistance = 10; // 10f
    private final float range = 50; // 50f
    private final float scale = 1.2f; // 1.4f

    public DepthOfField(FilterPostProcessor fpp, JmeContext context, ViewPort vp, AssetManager assetManager) {

        this.context = context;
        this.cam = vp.getCamera();

        dofFilter = new DepthOfFieldFilter();
        dofFilter.setFocusRange(range);
        dofFilter.setBlurScale(scale);
        fpp.addFilter(dofFilter);
    }

    @Override
    protected void controlUpdate(float tpf) {

        Vector3f origin = cam.getWorldCoordinates(new Vector2f(context.getSettings().getWidth() / 2, context.getSettings().getHeight() / 2), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f(context.getSettings().getWidth() / 2, context.getSettings().getHeight() / 2), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        int numCollisions = this.spatial.collideWith(ray, results);
        if (numCollisions > 0) {
            CollisionResult hit = results.getClosestCollision();
            dofFilter.setFocusDistance(hit.getDistance() / focusDistance);

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public float getRange() {
        return range;
    }

    public float getScale() {
        return scale;
    }

    public float getFocusDistance() {
        return focusDistance;
    }

    public void setFocusDistance(float focusDistance) {
        this.focusDistance = focusDistance;
    }
}
