package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
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

    private final Material sunMat;

    public DirectionalLight getSun() {
        return sun;
    }

    private final Node localRootNode;
    private final Node pivot = new Node();
    private int timeDelay = 24;// SUPERFAST=12 // FAST=24 // NORMAL= 48 // SLOW=96 //REALISTIC = 128
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

        sunMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sunMat.setColor("Color", ColorRGBA.Orange);

        //Texture sunTex = assetManager.loadTexture("Textures/ColorRamp/cloudy.png");
        //sunMat.setTexture("ColorMap", sunTex);
        sphereGeo.setMaterial(sunMat);//assetManager.loadMaterial("Common/Materials/WhiteColor.j3m")
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

    private ColorRGBA tmp = ColorRGBA.Orange;

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
                    sphereGeo.rotate(0, tpf, 0);
                    sun.setDirection(pivot.getLocalRotation().getRotationColumn(2));

                } else {
                    sun.setDirection(new Vector3f(0, 1, 0));
                }

                float z = pivot.getLocalRotation().getRotationColumn(2).getZ();

                //morning
                if (z > 0.99f) {

                    if (isSun == false) {
                        if (sl != null) {
                            slsr.setShadowIntensity(0.33f);
                        }
                        localRootNode.addLight(sun);
                        isSun = true;
                        sun.setColor(ColorRGBA.Orange);
                        tmp = ColorRGBA.Orange;
                        System.out.println("Sun is Up");
                    }

                    sun.getColor().interpolateLocal(ColorRGBA.White, ((tpf / timeDelay) / 1.25f));

                    tmp.interpolateLocal(ColorRGBA.Yellow, tpf / timeDelay / 1.25f);
                    tmp.interpolateLocal(ColorRGBA.Gray, tpf / timeDelay / 1.25f);
                    sunMat.setColor("Color", tmp);
                }

                //day
                if (z < -0.36f && z > -0.99f) {

                    tmp.interpolateLocal(ColorRGBA.Red, tpf / timeDelay / 1.25f);
                    tmp.interpolateLocal(ColorRGBA.Orange, tpf / timeDelay / 1.25f);
                    sunMat.setColor("Color", tmp);

                    if (sun.getColor().getBlue() < 0.5f) {
                        sun.getColor().interpolateLocal(ColorRGBA.Blue, ((tpf / timeDelay) / 1.25f));
                    } else {
                        sun.getColor().b = 0.5f;
                    }
                    if (sl != null) {
                        slsr.setShadowIntensity(0.66f);
                    }
                }

                //night
                if (z < -0.999f) {
                    if (isSun == true) {
                        tmp = ColorRGBA.Orange;
                        localRootNode.removeLight(sun);
                        isSun = false;
                        System.out.println("Sun is Down");
                        if (sl != null) {
                            slsr.setShadowIntensity(0.99f);
                        }
                    }
                }
            } else {
                sun.setDirection(new Vector3f(-5, -5, -5));
                sun.setColor(ColorRGBA.White);
            }
        } else {
            System.out.println("glc stopped");
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
