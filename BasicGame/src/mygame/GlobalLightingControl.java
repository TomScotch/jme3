package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowRenderer;

public class GlobalLightingControl extends AbstractControl {

    private final Node localRootNode;
    private final static Node pivot = new Node();
    private int timeDelay = 12;
    private boolean isSun = true;
    private final SpotLight sl;
    private final DirectionalLight sun;
    private final Node pivotSun;
    private final float sunHeight = 300f;
    private final int sunSize = 64;
    private final Geometry sphereGeo;
    private final SpotLightShadowRenderer slsr;
    private final SpotLight dummySpotLight;
    private final DirectionalLightShadowRenderer dlsr;
    private final int shadowmapSize = 256;
    private boolean globalLightning = true;

    public GlobalLightingControl(ViewPort vp, AssetManager assetManager, SpotLight sl, Node localRootNode) {

        this.localRootNode = localRootNode;
        //Player FlashLight
        this.sl = sl;

        dummySpotLight = new SpotLight(Vector3f.ZERO, Vector3f.ZERO);

        //PointLightSunPivotNode
        pivot.getWorldTranslation().set(0, 0, 0);
        pivotSun = new Node();
        pivot.attachChild(pivotSun);
        pivotSun.getLocalTranslation().addLocal(0, sunHeight, 0);
        localRootNode.attachChild(pivot);

        //Sun Sphere
        Sphere sphereMesh = new Sphere(sunSize, sunSize, sunSize);
        sphereGeo = new Geometry("", sphereMesh);
        sphereGeo.setMaterial(assetManager.loadMaterial("Common/Materials/WhiteColor.j3m"));
        sphereGeo.getLocalTranslation().addLocal(0, (-sunHeight * FastMath.QUARTER_PI), (-sunHeight * FastMath.HALF_PI));
        pivotSun.attachChild(sphereGeo);
        sphereGeo.setShadowMode(RenderQueue.ShadowMode.Off);

        //Sun
        sun = new DirectionalLight();
        sun.setColor(ColorRGBA.Orange);
        localRootNode.addLight(sun);

        //Point Light Shadow Renderer
        LightList localLightList = localRootNode.getLocalLightList();
        for (Light light : localLightList) {
            if (light.getClass() == PointLight.class) {
                PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, shadowmapSize);
                plsr.setLight((PointLight) light);
                vp.addProcessor(plsr);
            }
        }

        //Directional Light Shadow Renderer
        dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmapSize, 1);
        dlsr.setLight(sun);
        vp.addProcessor(dlsr);

        //Spot Light Shadow Renderer
        slsr = new SpotLightShadowRenderer(assetManager, shadowmapSize);
        slsr.setLight(dummySpotLight);
        slsr.setShadowCompareMode(CompareMode.Software);
        slsr.setShadowIntensity(0.45f);
        slsr.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
        slsr.setEdgesThickness(10);
        //vp.addProcessor(slsr);
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (this.isEnabled()) {

            if (slsr.getShadowCompareMode() == CompareMode.Software) {
                slsr.setShadowCompareMode(CompareMode.Hardware);
            }

            if (this.isEnabled()) {
                if (sl.isEnabled()) {
                    slsr.setLight(sl);
                } else {
                    slsr.setLight(dummySpotLight);
                }
            }

            if (globalLightning) {

                pivot.rotate((FastMath.QUARTER_PI * tpf) / timeDelay, 0, 0);

                if (isSun) {
                    sun.setDirection(pivot.getLocalRotation().getRotationColumn(2));
                } else {
                    sun.setDirection(new Vector3f(0, 1, 0));
                }

                float z = pivot.getLocalRotation().getRotationColumn(2).getZ();

                if (z > 0.99f) {

                    sun.getColor().interpolateLocal(ColorRGBA.White, 0.01f / timeDelay);

                    if (isSun == false) {
                        if (sl.isEnabled()) {
                            slsr.setShadowIntensity(0.35f);
                        }
                        localRootNode.addLight(sun);
                        sun.setColor(ColorRGBA.Orange);
                        isSun = true;
                    }
                }

                if (z < -0.25f && z > -0.99f) {
                    sun.getColor().interpolateLocal(ColorRGBA.Blue, 0.01f / timeDelay);
                    if (sl.isEnabled()) {
                        slsr.setShadowIntensity(0.25f);
                    }
                }

                if (z < -0.999f) {

                    if (isSun == true) {
                        localRootNode.removeLight(sun);
                        isSun = false;
                        if (sl.isEnabled()) {
                            slsr.setShadowIntensity(0.5f);
                        }
                    }
                }
            } else {
                sun.setDirection(new Vector3f(-5, -5, -5));
                sun.setColor(ColorRGBA.White);
            }
        }
    }

    public boolean isGlobalLightning() {
        return globalLightning;
    }

    public void setGlobalLightning(boolean globalLightning) {
        this.globalLightning = globalLightning;
    }

    public int getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public Vector3f getSunPosition() {
        return sphereGeo.getWorldTranslation();
    }

    public Vector3f getSunDirection() {
        return sun.getDirection();
    }

    public float getTimingValue() {
        return pivot.getLocalRotation().getRotationColumn(2).getZ();
    }

    public boolean getIsSun() {
        return isSun;
    }

    public SpotLightShadowRenderer getSlsr() {
        return slsr;
    }
}
