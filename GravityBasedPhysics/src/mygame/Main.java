package mygame;

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bas;
    private Geometry core;
    private PointLight sun;
    private Spatial night;
    private ParticleEmitter fire;

    public static void main(String[] args) {

        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setFullscreen(true);
        settings.setResolution(1280, 1024);
        settings.setGammaCorrection(true);
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        app.setDisplayFps(true);
        app.setLostFocusBehavior(LostFocusBehavior.Disabled);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        inputManager.setCursorVisible(false);

        inputManager.addMapping("debug", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addListener(this, "debug");

        bas = new BulletAppState();
        bas.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        bas.initialize(stateManager, this);
        stateManager.attach(bas);
        bas.getPhysicsSpace().setGravity(Vector3f.ZERO);

        Sphere a = new Sphere(24, 24, 12);
        core = new Geometry("Box", a);
        core.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        Material matA = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matA.setBoolean("UseMaterialColors", true);
        matA.setColor("Ambient", ColorRGBA.randomColor());
        matA.setColor("Diffuse", ColorRGBA.randomColor());
        core.setMaterial(matA);
        rootNode.attachChild(core);
        core.setLocalTranslation(0, 0, 100);
        RigidBodyControl rbcA = new RigidBodyControl(999999999);
        core.addControl(rbcA);
        bas.getPhysicsSpace().add(core);
        rbcA.setGravity(Vector3f.ZERO);

        getFlyByCamera().setMoveSpeed(50);
        getCamera().getLocation().set(0, 25, 125);

        sun = new PointLight();
        sun.setPosition(Vector3f.ZERO);
        sun.setColor(ColorRGBA.White);
        sun.setRadius(999);
        rootNode.addLight(sun);

        PointLightShadowRenderer dlsr = new PointLightShadowRenderer(assetManager, 2048);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
        dlsr.setShadowCompareMode(CompareMode.Hardware);
        dlsr.setEdgesThickness(5);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        viewPort.addProcessor((FilterPostProcessor) assetManager.loadAsset("Filters/MyFilter.j3f"));

        Texture west = assetManager.loadTexture("Textures/left(uity right).png");
        Texture east = assetManager.loadTexture("Textures/right(unity left).png");
        Texture north = assetManager.loadTexture("Textures/back.png");
        Texture south = assetManager.loadTexture("Textures/middle.png");
        Texture up = assetManager.loadTexture("Textures/up.png");
        Texture down = assetManager.loadTexture("Textures/down.png");
        night = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(night);

        Geometry sunGeom = new Geometry("Sun", new Sphere(24, 24, 24));
        Material sunMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sunMat.setColor("Color", ColorRGBA.Yellow);
        sunGeom.setMaterial(sunMat);
        sunGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sunGeom);

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        material.setFloat("Softness", 3f);
        fire = new ParticleEmitter("Fire", ParticleMesh.Type.Triangle, 30);
        fire.setMaterial(material);
        fire.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.1f));
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setEndColor(ColorRGBA.Red);
        fire.setStartColor(ColorRGBA.Orange);
        fire.setStartSize(24 * 2);
        fire.setEndSize(24 * 1.5f);
        fire.setGravity(0, -3, 0);
        fire.setInWorldSpace(false);
        fire.setLowLife(0.75f);
        fire.setHighLife(1.5f);
        fire.setParticlesPerSec(30);
        fire.setNumParticles(120);
        fire.setShadowMode(RenderQueue.ShadowMode.Off);
        fire.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(fire);

        for (int x = 0; x < 6; x++) {
            rootNode.attachChild(addBox(250));//250
        }

        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }
    float cycle = 0;

    @Override
    public void simpleUpdate(float tpf) {

        core.getControl(RigidBodyControl.class).setLinearVelocity(Vector3f.ZERO);
        core.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, 0.075f, 0));
        cycle += 0.0001f % FastMath.TWO_PI;
        core.setLocalTranslation(new Vector3f(FastMath.sin(cycle) * 100, 0, FastMath.cos(cycle) * 100));
        core.getControl(RigidBodyControl.class).setPhysicsLocation(core.getLocalTranslation());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //
    }

    private Geometry addBox(float mass) {

        Box a = new Box(1, 1, 1);
        Geometry geomA = new Geometry("Box", a);
        geomA.move(new Vector3f(0, 0, 100));

        Material matA = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matA.setBoolean("UseMaterialColors", true);
        matA.setColor("Diffuse", ColorRGBA.randomColor());
        geomA.setMaterial(matA);

        RigidBodyControl rbcA = new RigidBodyControl(mass);
        geomA.addControl(rbcA);
        bas.getPhysicsSpace().add(geomA);
        rbcA.setGravity(Vector3f.ZERO);
        rbcA.setAngularDamping(9999);
        rbcA.setLinearDamping(9999);

        MyPhysicsControl mpcA = new MyPhysicsControl();
        geomA.addControl(mpcA);

        geomA.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        return geomA;
    }

    @Override
    @SuppressWarnings("null")
    public void onAction(String name, boolean isPressed, float tpf) {

        if (name.equals("debug") && !isPressed) {
            if (bas.isDebugEnabled()) {
                bas.setDebugEnabled(false);
            } else {
                bas.setDebugEnabled(true);
            }
        }
    }
}
