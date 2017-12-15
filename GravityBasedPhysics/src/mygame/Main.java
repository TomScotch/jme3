package mygame;

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

public class Main extends SimpleApplication implements ActionListener {

    private static Main app;
    private float cycle = 0;
    private float cycle2 = 0;
    private BulletAppState bas;
    private Geometry core;
    private PointLight sun;
    private Spatial night;
    private ParticleEmitter fire;
    private Geometry moonGeom;
    private Node moon;
    private Spatial jupiter;
    private Spatial ship;

    public static void main(String[] args) {

        app = new Main();
        AppSettings settings = new AppSettings(true);

        try {
            settings.load("settings");
            app.setShowSettings(false);
        } catch (BackingStoreException ex) {
            app.setShowSettings(true);
        }

        app.setDisplayStatView(false);
        app.setDisplayFps(false);
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

        Sphere a = new Sphere(64, 64, 12);
        core = new Geometry("Box", a);
        core.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        Material matA = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matA.setBoolean("UseMaterialColors", true);
        matA.setColor("Diffuse", ColorRGBA.randomColor());
        core.setMaterial(matA);
        rootNode.attachChild(core);
        core.setLocalTranslation(0, 0, 100);

        RigidBodyControl rbcA = new RigidBodyControl(999);
        core.addControl(rbcA);
        bas.getPhysicsSpace().add(core);
        rbcA.setGravity(Vector3f.ZERO);

        getFlyByCamera().setMoveSpeed(50);
        getCamera().getLocation().set(0, 25, 125);

        sun = new PointLight();
        sun.setPosition(Vector3f.ZERO);
        sun.setColor(ColorRGBA.White);
        sun.setRadius(888);
        rootNode.addLight(sun);

        PointLightShadowRenderer dlsr = new PointLightShadowRenderer(assetManager, 1024);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        dlsr.setShadowCompareMode(CompareMode.Software);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

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

        moon = new Node("MoonNode");
        rootNode.attachChild(moon);
        moonGeom = new Geometry("Sun", new Sphere(24, 24, 2));
        Material moonMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        moonMat.setBoolean("UseMaterialColors", true);
        moonMat.setColor("Diffuse", ColorRGBA.White);
        moonGeom.setMaterial(moonMat);
        moonGeom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        moon.attachChild(moonGeom);
        moonGeom.setLocalTranslation(15, 0, 15);

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

        jupiter = assetManager.loadModel("Models/jupiter.blend.j3o");
        rootNode.attachChild(jupiter);
        PhysicsSpace.getPhysicsSpace().add(jupiter);

        for (int x = 0; x < 1; x++) {
            rootNode.attachChild(addBox(250));//250
        }

        ship = assetManager.loadModel("Models/starship/4206d04471674c3ca70b1ab40e1d2b45.fbx.j3o");
        ship.scale(0.25f);
        ship.rotate(180, -270, 90);
        BetterCharacterControl bcc = new BetterCharacterControl(25, 10, 100);
        ship.setLocalTranslation(getCamera().getLocation());
        ship.addControl(bcc);
        bcc.setSpatial(ship);
        CameraControl cc = new CameraControl(cam);
        cc.setControlDir(CameraControl.ControlDirection.CameraToSpatial);
        ship.addControl(cc);
        rootNode.attachChild(ship);
        PhysicsSpace.getPhysicsSpace().add(ship);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    public enum Res {

        A(640),
        B(800),
        C(1024),
        D(1360),
        E(1920);

        private int res;

        private Res() {
            this.res = 640;
        }

        private Res(int res) {
            this.res = res;
        }

        public void setRes(int res) {
            this.res = res;
        }

        public int getRes() {
            return res;
        }
    }

    @Override
    public void simpleUpdate(float tpf) {

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {

                ship.setLocalTranslation(getCamera().getLocation().mult(3));

                moon.setLocalTranslation(core.getLocalTranslation());
                moon.rotate(0, 0.003f, 0);
                moonGeom.rotate(0, 0.03f, 0);

                jupiter.getControl(RigidBodyControl.class).setLinearVelocity(Vector3f.ZERO);
                jupiter.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, 0.00002f, 0));

                core.getControl(RigidBodyControl.class).setLinearVelocity(Vector3f.ZERO);
                core.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, 0.075f, 0));

                cycle += 0.000125f % FastMath.TWO_PI;
                cycle2 += 0.00001f % FastMath.TWO_PI;

                core.setLocalTranslation(new Vector3f(FastMath.sin(cycle) * 100, 0, FastMath.cos(cycle) * 100));
                jupiter.setLocalTranslation(new Vector3f((FastMath.sin(cycle2) * 500), 0, FastMath.cos(cycle2) * 550).negate());

                core.getControl(RigidBodyControl.class).setPhysicsLocation(core.getLocalTranslation());
                jupiter.getControl(RigidBodyControl.class).setPhysicsLocation(jupiter.getLocalTranslation());

                return null;
            }
        });
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

        geomA.setLocalTranslation(jupiter.getLocalTranslation());

        RigidBodyControl rbcA = new RigidBodyControl(mass);
        geomA.addControl(rbcA);
        bas.getPhysicsSpace().add(geomA);
        rbcA.setGravity(Vector3f.ZERO);
//        rbcA.setAngularDamping(99);
        rbcA.setLinearDamping(250);
//        rbcA.setFriction(0);
        rbcA.setAngularFactor(0);
//        rbcA.setRestitution(0);

        MyPhysicsControl mpcA = new MyPhysicsControl();
        geomA.addControl(mpcA);

        geomA.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        return geomA;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            app.getContext().getSettings().save("settings");
        } catch (BackingStoreException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
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
