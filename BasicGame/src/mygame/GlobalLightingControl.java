package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
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
import com.jme3.shadow.SpotLightShadowRenderer;

public class GlobalLightingControl extends AbstractControl {

    private final Material sunMat;
    private final ViewPort vp;
    private final ParticleEmitter fire;

    public DirectionalLight getSun() {
        return sun;
    }

    private final Node localRootNode;
    private final Node pivot = new Node();
    private int timeDelay = 24;// INSTANE=24  to REALISTIC = 8192  
    private boolean isSun = true;
    private final SpotLight sl;
    private final DirectionalLight sun;
    private final Node pivotSun;
    private final float sunHeight = 300f;
    private final int sunSize = 82;
    private final Geometry sphereGeo;
    private final SpotLightShadowRenderer slsr;
    private final SpotLight dummySpotLight;
    private final DirectionalLightShadowRenderer dlsr;
    private final int shadowmapSize = 1024;
    private boolean globalLightning = true;

    private boolean morning = true;
    private boolean day = false;
    private boolean evening = false;
    private boolean night = false;

    public GlobalLightingControl(ViewPort vp, AssetManager assetManager, SpotLight sl, Node localRootNode) {

        this.localRootNode = localRootNode;
        //Player FlashLight
        this.sl = sl;
        this.vp = vp;
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

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        material.setFloat("Softness", 3f); // 

        //Fire
        fire = new ParticleEmitter("Fire", ParticleMesh.Type.Triangle, 30);
        fire.setMaterial(material);
        fire.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.1f));
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f));
        fire.setStartSize(sunSize * 2);
        fire.setEndSize(0.01f);
        fire.setGravity(0, -0.3f, 0);
        fire.setInWorldSpace(false);
        fire.setLowLife(0.4f);
        fire.setHighLife(2f);
        fire.setParticlesPerSec(25);
        fire.setNumParticles(100);
        fire.setShadowMode(RenderQueue.ShadowMode.Off);
        fire.setQueueBucket(RenderQueue.Bucket.Transparent);
        localRootNode.attachChild(fire);

        sunMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sunMat.setColor("Color", ColorRGBA.Orange);
        sphereGeo.setMaterial(sunMat);//assetManager.loadMaterial("Common/Materials/WhiteColor.j3m")
        sphereGeo.getLocalTranslation().addLocal(0, (-sunHeight * FastMath.QUARTER_PI), (-sunHeight * FastMath.HALF_PI));
        pivotSun.attachChild(sphereGeo);
        sphereGeo.setShadowMode(RenderQueue.ShadowMode.Off);
        //sphereGeo.setQueueBucket(RenderQueue.Bucket.Translucent);

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
        dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmapSize, 2);
        dlsr.setLight(sun);
        dlsr.setShadowCompareMode(CompareMode.Hardware);
        dlsr.setShadowIntensity(0.30f);
        dlsr.setEdgesThickness(5);
        vp.addProcessor(dlsr);

        //Spot Light Shadow Renderer
        slsr = new SpotLightShadowRenderer(assetManager, shadowmapSize);
        slsr.setLight(dummySpotLight);
        slsr.setShadowCompareMode(CompareMode.Hardware);
        slsr.setShadowIntensity(0.30f);
        slsr.setEdgesThickness(5);

    }

    private ColorRGBA tmp = ColorRGBA.Orange;

    @Override
    protected void controlUpdate(float tpf) {

        if (this.isEnabled()) {

            if (sl.isEnabled()) {
                slsr.setLight(sl);
            } else {
                slsr.setLight(dummySpotLight);
            }

            if (globalLightning) {

                fire.setLocalTranslation(sphereGeo.getWorldTranslation());
                fire.rotate(0, tpf, 0);

                final float rotation = tpf / timeDelay ;//(FastMath.QUARTER_PI * tpf) / timeDelay

                pivot.rotate(rotation, 0, 0);

                if (isSun) {
                    sphereGeo.rotate(0, tpf, 0);
                    sun.setDirection(pivot.getLocalRotation().getRotationColumn(2));

                } else {
                    sun.setDirection(new Vector3f(0, 1, 0));
                }

                float z = pivot.getLocalRotation().getRotationColumn(2).getZ();

                //morning
                if (z > 0.99f) {

                    /*                    if (!vp.getProcessors().contains(dlsr)) {
                    vp.addProcessor(dlsr);
                    }*/
                    morning = true;
                    day = false;
                    evening = false;
                    night = false;

                    if (isSun == false) {
                        if (sl != null) {
                            slsr.setShadowIntensity(0.25f);
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
                    fire.setEndColor(sun.getColor());
                    fire.setStartColor(tmp);
                }

                //day
                if (z < -0.36f && z > -0.99f) {

                    /*                    if (!vp.getProcessors().contains(dlsr)) {
                    vp.addProcessor(dlsr);
                    }*/
                    morning = false;
                    day = true;
                    evening = false;
                    night = false;

                    tmp.interpolateLocal(ColorRGBA.Red, tpf / timeDelay / 1.25f);
                    tmp.interpolateLocal(ColorRGBA.Orange, tpf / timeDelay / 1.25f);
                    sunMat.setColor("Color", tmp);

                    if (sun.getColor().getBlue() < 0.5f) {
                        sun.getColor().interpolateLocal(ColorRGBA.Blue, ((tpf / timeDelay) / 1.25f));
                    } else {
                        sun.getColor().b = 0.5f;
                    }
                    if (sl != null) {
                        slsr.setShadowIntensity(0.35f);
                    }
                }

                if (z < -0.38f && z > -0.99f) {

                    morning = false;
                    day = false;
                    evening = true;
                    night = false;
                }

                //night
                if (z < -0.999f) {

                    /*                    if (vp.getProcessors().contains(dlsr)) {
                    vp.removeProcessor(dlsr);
                    }*/
                    morning = false;
                    day = false;
                    evening = false;
                    night = true;

                    if (isSun == true) {
                        tmp = ColorRGBA.Orange;
                        localRootNode.removeLight(sun);
                        isSun = false;
                        System.out.println("Sun is Down");
                        if (sl != null) {
                            slsr.setShadowIntensity(0.45f);
                        }
                    }
                }
            } else {
                sun.setDirection(new Vector3f(-5, -5, -5));
                sun.setColor(ColorRGBA.White);
                fire.removeFromParent();
                sphereGeo.removeFromParent();
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

    public boolean isMorning() {
        return morning;
    }

    public boolean isDay() {
        return day;
    }

    public boolean isEvening() {
        return evening;
    }

    public boolean isNight() {
        return night;
    }
}
