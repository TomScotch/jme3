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

public class simpleWaterControl extends AbstractControl {

    private final SimpleWaterProcessor waterProcessor;

    private float waterSpeed = 0.015f;
    private float waterStrength = 0.05f;
    private int waterDepth = 75;

    public simpleWaterControl(SimpleApplication app, Node localRootNode) {

        waterProcessor = new SimpleWaterProcessor(app.getAssetManager());
        waterProcessor.setReflectionScene(localRootNode);
        waterProcessor.setWaterDepth(waterDepth);
        waterProcessor.setDistortionScale(waterStrength);
        waterProcessor.setWaveSpeed(waterSpeed);
        Vector3f waterLocation = new Vector3f(0, -4f, 0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        waterProcessor.setRenderSize(256, 256);

        Quad quad = new Quad(4096, 4096);
        quad.scaleTextureCoordinates(new Vector2f(6f, 6f));
        app.getViewPort().addProcessor(waterProcessor);
        Geometry water = new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-1024, -4f, 1024);
        water.setShadowMode(ShadowMode.Off);
        water.setMaterial(waterProcessor.getMaterial());
        localRootNode.attachChild(water);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        waterProcessor.setWaterDepth(waterDepth);
        waterProcessor.setDistortionScale(waterStrength);
        waterProcessor.setWaveSpeed(waterSpeed);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public float getWaterSpeed() {
        return waterSpeed;
    }

    public void setWaterSpeed(float waterSpeed) {
        this.waterSpeed = waterSpeed;
    }

    public float getWaterStrength() {
        return waterStrength;
    }

    public void setWaterStrength(float waterStrength) {
        this.waterStrength = waterStrength;
    }

    public int getWaterDepth() {
        return waterDepth;
    }

    public void setWaterDepth(int waterDepth) {
        this.waterDepth = waterDepth;
    }

    /**
     * @return the waterProcessor
     */
    public SimpleWaterProcessor getWaterProcessor() {
        return waterProcessor;
    }
}
