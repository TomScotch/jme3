package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;
import com.jme3.water.SimpleWaterProcessor;

/**
 * more simple water post processor
 *
 * @author tomscotch
 * @version 0.4
 */
public class simpleWaterControl extends AbstractControl {

    private final SimpleWaterProcessor waterProcessor;

    /**
     * Sets the speed of the wave animation.
     *
     * default = 0.05f
     */
    private float waterSpeed = 0.015f;

    /**
     * Sets the scale of distortion by the normal map.
     *
     * default = 0.2
     */
    private float waterStrength = 0.05f;

    /**
     * Higher values make the refraction texture shine through earlier.
     *
     * default = 4
     */
    private int waterDepth = 75;

    /**
     * @param app
     * @param localRootNode
     */
    public simpleWaterControl(SimpleApplication app, Node localRootNode) {

        waterProcessor = new SimpleWaterProcessor(app.getAssetManager());
        waterProcessor.setReflectionScene(localRootNode);
        waterProcessor.setWaterDepth(waterDepth);
        waterProcessor.setDistortionScale(waterStrength);
        waterProcessor.setWaveSpeed(waterSpeed);
        waterProcessor.setRenderSize(256, 256);

        Vector3f waterLocation = new Vector3f(0, -4f, 0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));

        Quad quad = new Quad(4096, 4096);
        quad.scaleTextureCoordinates(new Vector2f(6f, 6f));

        Geometry water = new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-1024, -4f, 1024);
        water.setShadowMode(ShadowMode.Off);
        water.setMaterial(waterProcessor.getMaterial());
        app.getViewPort().addProcessor(waterProcessor);
        localRootNode.attachChild(water);
    }

    @Override
    protected void controlUpdate(float tpf) {

        /**
         *
         */
        waterProcessor.setWaterDepth(waterDepth);
        waterProcessor.setDistortionScale(waterStrength);
        waterProcessor.setWaveSpeed(waterSpeed);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    /**
     * @return waterSpeed
     */
    public float getWaterSpeed() {
        return waterSpeed;
    }

    /**
     * @param waterSpeed
     */
    public void setWaterSpeed(float waterSpeed) {
        this.waterSpeed = waterSpeed;
    }

    /**
     * @return waterStrength
     */
    public float getWaterStrength() {
        return waterStrength;
    }

    /**
     * @param waterStrength
     */
    public void setWaterStrength(float waterStrength) {
        this.waterStrength = waterStrength;
    }

    /**
     * @return waterDepth
     */
    public int getWaterDepth() {
        return waterDepth;
    }

    /**
     * @param waterDepth
     */
    public void setWaterDepth(int waterDepth) {
        this.waterDepth = waterDepth;
    }

    /**
     * @return waterProcessor
     */
    public SimpleWaterProcessor getWaterProcessor() {
        return waterProcessor;
    }
}
