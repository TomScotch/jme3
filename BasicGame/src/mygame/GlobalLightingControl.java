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
    private final ParticleEmitter fire;
    private float rotation;
    private final SpotLight dummySpotLight;

    private final Node localRootNode;
    private final ViewPort vp;

    public DirectionalLight getSun() {
        return sun;
    }

    private final Node pivot = new Node();
    private int timeDelay = 32;// INSTANE=32  to REALISTIC = 8192  
    private boolean isSun = true;
    private final SpotLight sl;
    private final DirectionalLight sun;
    private final Node pivotSun;
    private final float sunHeight = 300f;
    private final int sunSize = 82;
    private final Geometry sphereGeo;
    private final SpotLightShadowRenderer slsr;
    private final DirectionalLightShadowRenderer dlsr;
    private final int shadowmapSize = 1024;
    private boolean globalLightning = true;

    private boolean morning = true;
    private boolean day = false;
    private boolean evening = false;
    private boolean night = false;

    public GlobalLightingControl(ViewPort vp, AssetManager assetManager, SpotLight sl, Node localRootNode) {

        this.sl = sl;
        this.localRootNode = localRootNode;
        dummySpotLight = new SpotLight(Vector3f.ZERO, Vector3f.ZERO);
        this.vp = vp;

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
        fire.setEndColor(ColorRGBA.Red);//new ColorRGBA(1f, 0f, 0f, 1f)
        fire.setStartColor(ColorRGBA.Orange);//new ColorRGBA(1f, 1f, 0f, 0.5f)
        fire.setStartSize(sunSize * 2);
        fire.setEndSize(sunSize * 1.5f);
        fire.setGravity(0, -3, 0);
        fire.setInWorldSpace(false);
        fire.setLowLife(0.75f);
        fire.setHighLife(1.5f);
        fire.setParticlesPerSec(30);
        fire.setNumParticles(120);
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

        sunMat.setColor("Color", ColorRGBA.Orange.addLocal(ColorRGBA.Red));
        sun.setColor(ColorRGBA.Orange);

    }

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
                rotation = tpf / timeDelay;
                fire.rotate(0, 0, rotation);
                pivot.rotate(rotation, 0, 0);
                float z = sphereGeo.getWorldTranslation().getZ();
                float y = sphereGeo.getWorldTranslation().getY();

                if (isSun) {
                    sun.setEnabled(true);
                } else {
                    sun.setEnabled(false);
                }

                sun.setDirection(pivot.getLocalRotation().getRotationColumn(2));

                if (y > -(sunSize / 1.1f) && z < -222) {
                    //morning
                    if (isSun) {
                        sun.getColor().interpolateLocal(ColorRGBA.White, (tpf / timeDelay));/// 0.0005f
                    }
                    morning = true;
                    day = false;
                    evening = false;
                    night = false;
                    if (isSun == false) {
                        if (sl != null) {
                            slsr.setShadowIntensity(0.25f);
                        }
                        isSun = true;
                        System.out.println("Sun is Up");
                        sun.setColor(ColorRGBA.Orange);
                        fire.setEnabled(true);
                        sun.setEnabled(true);
                    }

                }

                if (y > 422 && z > 0) {
                    //day
                    morning = false;
                    day = true;
                    evening = false;
                    night = false;

                    if (sl != null) {
                        slsr.setShadowIntensity(0.35f);
                    }
                    if (isSun) {
                        sun.getColor().interpolateLocal(ColorRGBA.Red, (tpf / timeDelay) / 2);/// 0.0005f
                    }
                }

                if (y > 90 && z > 233) {
                    //evening
                    morning = false;
                    day = false;
                    evening = true;
                    night = false;
                    if (isSun) {
                        sun.getColor().interpolateLocal(ColorRGBA.Blue, (tpf / timeDelay) / 3);/// 0.0005f
                    }
                }

                if (y < -(sunSize * 1.1f) && z > 444) {
                    //night
                    morning = false;
                    day = false;
                    evening = false;
                    night = true;
                    if (isSun == true) {
                        sun.setEnabled(false);
                        fire.setEnabled(false);
                        fire.killAllParticles();
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
            if (!isNight() && !isMorning()) {
                vp.getBackgroundColor().interpolateLocal(ColorRGBA.Black, (tpf / getTimeDelay()) * 3);
            }
            if (isMorning()) {
                vp.getBackgroundColor().interpolateLocal(ColorRGBA.Blue.add(ColorRGBA.White), (tpf / getTimeDelay()) * 3);
            }
        } else {
            System.out.println("glc stopped");
        }

        if (localRootNode.getControl(LightScatterFilter.class) != null) {
            if (night) {
                localRootNode.getControl(LightScatterFilter.class).setEnabled(false);
            } else {
                localRootNode.getControl(LightScatterFilter.class).setEnabled(true);
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

    public float getRotation() {
        return rotation;
    }

    public ColorRGBA getBackgroundColor() {
        return vp.getBackgroundColor();
    }

    public DirectionalLightShadowRenderer getDlsr() {
        return dlsr;
    }
}
