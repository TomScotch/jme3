package mygame;

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
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
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import com.jme3.scene.control.CameraControl;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication implements ActionListener {

    private static Main app;

    private BulletAppState bas;
    private PointLight sunLight;
    private CameraControl cc;

    private Spatial jupiter;
    private Spatial ship;
    private Spatial earth;
    private Node moon;
    private Spatial moonGeom;
    private Spatial mercurius;
    private Spatial mars;
    private Spatial neptune;
    private Spatial saturn;
    private Spatial uranus;
    private Spatial venus;

    private float cycle1 = 0;
    private float cycle2 = 0;
    private float cycle3 = 0;
    private float cycle4 = 0;
    private float cycle5 = 0;
    private float cycle6 = 0;
    private float cycle7 = 0;
    private float cycle8 = 0;

    float Sun_Durchmesser = 1300000;
    float Sun_Masse = 1.9884f;

    //Durchmesser
    //Masse
    float Merkur_Orbitalgeschwindigkeit = 47.8725f;
    float Merkur_Durchmesser = 4879;
    float Merkur_Masse = 0.33022f;
    float Merkur_Rotationsgeschwindigkeit = 4.25f;
    float Merkur_Abstand = 58;

    float Venus_Orbitalgeschwindigkeit = 35.0214f;
    float Venus_Durchmesser = 12104;
    float Venus_Masse = 4.8685f;
    float Venus_Rotationsgeschwindigkeit = -10.36f;
    float Venus_Abstand = 108;

    float Erde_Orbitalgeschwindigkeit = 29.7859f;
    float Erde_Durchmesser = 12742;
    float Erde_Masse = 5.9737f;
    float Erde_Rotationsgeschwindigkeit = 11.18f;
    float Erde_Abstand = 150;

    float Mars_Orbitalgeschwindigkeit = 24.1309f;
    float Mars_Durchmesser = 6779;
    float Mars_Masse = 0.64185f;
    float Mars_Rotationsgeschwindigkeit = 5.02f;
    float Mars_Abstand = 228;

    float Jupiter_Orbitalgeschwindigkeit = 13.0697f;
    float Jupiter_Durchmesser = 139822;
    float Jupiter_Masse = 1898.7f;
    float Jupiter_Rotationsgeschwindigkeit = 59.54f;
    float Jupiter_Abstand = 778;

    float Saturn_Orbitalgeschwindigkeit = 9.6724f;
    float Saturn_Durchmesser = 116464;
    float Saturn_Masse = 568.51f;
    float Saturn_Rotationsgeschwindigkeit = 35.49f;
    float Saturn_Abstand = 1427;

    float Uranus_Orbitalgeschwindigkeit = 6.8352f;
    float Uranus_Durchmesser = 50724;
    float Uranus_Masse = 86.849f;
    float Uranus_Rotationsgeschwindigkeit = -21.29f;
    float Uranus_Abstand = 2884;

    float Neptun_Orbitalgeschwindigkeit = 5.4778f;
    float Neptun_Durchmesser = 49244;
    float Neptun_Masse = 102.44f;
    float Neptun_Rotationsgeschwindigkeit = 23.71f;
    float Neptun_Abstand = 4509;

    public static void main(String[] args) {

        app = new Main();
        AppSettings settings = new AppSettings(true);

        try {
            settings.load("settings");
            app.setShowSettings(false);
        } catch (BackingStoreException ex) {
            app.setShowSettings(true);
        }

        app.setSettings(settings);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        createPhysics();
        createSpace();
        createSun();
        createStars();
        createShip();
        createViewPort();
        createInput();
    }

    private void createViewPort() {

        viewPort.getCamera().setFrustumFar(9999);
        viewPort.getCamera().onFrameChange();
    }

    private void createInput() {

        cam.getLocation().addLocal(0, 100, -200);
        getFlyByCamera().setMoveSpeed(350);
        inputManager.setCursorVisible(false);
        inputManager.addMapping("SPACE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "SPACE");
    }

    private void createPhysics() {

        bas = new BulletAppState();
        bas.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        bas.initialize(stateManager, this);
        stateManager.attach(bas);
        bas.getPhysicsSpace().setGravity(Vector3f.ZERO);
        bas.setDebugEnabled(false);
    }

    private void createShip() {

        ship = assetManager.loadModel("Models/ship.j3o");
        cc = new CameraControl(cam);
        cc.setControlDir(CameraControl.ControlDirection.CameraToSpatial);
        ship.addControl(cc);
        rootNode.attachChild(ship);
    }

    private void createMoons() {
        moon = new Node("MoonNode");
        Node en = (Node) earth;
        en.attachChild(moon);
        moonGeom = assetManager.loadModel("Models/moon.j3o");
        moonGeom.center();
        moonGeom.setLocalTranslation(0, 0, 2);
        moon.attachChild(moonGeom);
    }

    private void createStars() {

        Sphere neptune_sphere = new Sphere(64, 64, Neptun_Durchmesser);
        neptune = new Geometry("Neptun", neptune_sphere);
        neptune.setMaterial(assetManager.loadMaterial("Materials/Generated/neptune.j3m"));
        neptune.addControl(new RigidBodyControl(Neptun_Masse));
        PhysicsSpace.getPhysicsSpace().add(neptune);
        rootNode.attachChild(neptune);

        Sphere saturn_sphere = new Sphere(64, 64, Saturn_Durchmesser);
        saturn = new Geometry("Saturn", saturn_sphere);
        saturn.setMaterial(assetManager.loadMaterial("Materials/Generated/saturn.j3m"));
        saturn.addControl(new RigidBodyControl(Saturn_Masse));
        PhysicsSpace.getPhysicsSpace().add(saturn);
        rootNode.attachChild(saturn);

        Sphere earth_sphere = new Sphere(64, 64, Erde_Durchmesser);
        earth = new Geometry("Erde", earth_sphere);
        earth.setMaterial(assetManager.loadMaterial("Materials/Generated/earth.j3m"));
        earth.addControl(new RigidBodyControl(Erde_Masse));
        PhysicsSpace.getPhysicsSpace().add(earth);
        rootNode.attachChild(earth);

        Sphere jupiter_sphere = new Sphere(64, 64, Jupiter_Durchmesser);
        jupiter = new Geometry("Jupiter", jupiter_sphere);
        jupiter.setMaterial(assetManager.loadMaterial("Materials/Generated/jupiter.j3m"));
        jupiter.addControl(new RigidBodyControl(Jupiter_Masse));
        PhysicsSpace.getPhysicsSpace().add(jupiter);
        rootNode.attachChild(jupiter);

        Sphere mars_sphere = new Sphere(64, 64, Mars_Durchmesser);
        mars = new Geometry("Mars", mars_sphere);
        mars.setMaterial(assetManager.loadMaterial("Materials/Generated/mars.j3m"));
        mars.addControl(new RigidBodyControl(Mars_Masse));
        PhysicsSpace.getPhysicsSpace().add(mars);
        rootNode.attachChild(mars);

        Sphere merkur_sphere = new Sphere(64, 64, Merkur_Durchmesser);
        mercurius = new Geometry("Merkur", merkur_sphere);
        mercurius.setMaterial(assetManager.loadMaterial("Materials/Generated/mercurius.j3m"));
        mercurius.addControl(new RigidBodyControl(Merkur_Masse));
        PhysicsSpace.getPhysicsSpace().add(mercurius);
        rootNode.attachChild(mercurius);

        Sphere uranus_sphere = new Sphere(64, 64, Uranus_Durchmesser);
        uranus = new Geometry("Uranus", uranus_sphere);
        uranus.setMaterial(assetManager.loadMaterial("Materials/Generated/uranus.j3m"));
        uranus.addControl(new RigidBodyControl(Uranus_Masse));
        PhysicsSpace.getPhysicsSpace().add(uranus);
        rootNode.attachChild(uranus);

        Sphere venus_sphere = new Sphere(64, 64, Venus_Durchmesser);
        venus = new Geometry("Venus", venus_sphere);
        venus.setMaterial(assetManager.loadMaterial("Materials/Generated/venus.j3m"));
        venus.addControl(new RigidBodyControl(Venus_Masse));
        PhysicsSpace.getPhysicsSpace().add(venus);
        rootNode.attachChild(venus);
    }

    private void createSpace() {

        Texture west = assetManager.loadTexture("Textures/space/left(uity right).png");
        Texture east = assetManager.loadTexture("Textures/space/right(unity left).png");
        Texture north = assetManager.loadTexture("Textures/space/back.png");
        Texture south = assetManager.loadTexture("Textures/space/middle.png");
        Texture up = assetManager.loadTexture("Textures/space/up.png");
        Texture down = assetManager.loadTexture("Textures/space/down.png");
        Spatial space = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        space.setQueueBucket(RenderQueue.Bucket.Sky);
        rootNode.attachChild(space);
    }

    private void createSun() {

        Geometry sunGeom = new Geometry("Sun", new Sphere(24, 24, 24));
        Material sunMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sunMat.setColor("Color", ColorRGBA.Yellow);
        sunGeom.setMaterial(sunMat);
        sunGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sunGeom);

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        material.setFloat("Softness", 3f);
        ParticleEmitter sunFire = new ParticleEmitter("Fire", ParticleMesh.Type.Triangle, 30);
        sunFire.setMaterial(material);
        sunFire.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.1f));
        sunFire.setImagesX(2);
        sunFire.setImagesY(2);
        sunFire.setEndColor(ColorRGBA.Red);
        sunFire.setStartColor(ColorRGBA.Orange);
        sunFire.setStartSize(24 * 2);
        sunFire.setEndSize(24 * 1.5f);
        sunFire.setGravity(0, -3, 0);
        sunFire.setInWorldSpace(false);
        sunFire.setLowLife(0.75f);
        sunFire.setHighLife(1.5f);
        sunFire.setParticlesPerSec(30);
        sunFire.setNumParticles(120);
        sunFire.setShadowMode(RenderQueue.ShadowMode.Off);
        sunFire.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(sunFire);

        sunLight = new PointLight();
        sunLight.setPosition(Vector3f.ZERO);
        sunLight.setColor(ColorRGBA.White);
        sunLight.setRadius(99999);
        rootNode.addLight(sunLight);
    }

    @Override
    public void simpleUpdate(float tpf) {

        cycle2 += Jupiter_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        jupiter.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle2) * 1200), 0, FastMath.cos(cycle2) * 1200));
        jupiter.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Jupiter_Rotationsgeschwindigkeit * tpf / 50, 0));

        cycle3 += Mars_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        mars.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle3) * 700), 0, FastMath.cos(cycle3) * 800));
        mars.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Mars_Rotationsgeschwindigkeit * tpf / 50, 0));

        cycle4 += Merkur_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        mercurius.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle4) * Merkur_Abstand), 0, FastMath.cos(cycle4) * Merkur_Abstand));
        mercurius.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Merkur_Rotationsgeschwindigkeit * tpf / 50, 0));

        cycle1 += Erde_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        earth.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle1) * Erde_Abstand, 0, FastMath.cos(cycle1) * Erde_Abstand));
        earth.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Erde_Rotationsgeschwindigkeit * tpf / 50, 0));

        cycle5 += Venus_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        venus.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle5) * Venus_Abstand, 0, FastMath.cos(cycle5) * Venus_Abstand));
        venus.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Venus_Rotationsgeschwindigkeit * tpf / 50, 0));

        cycle6 += Uranus_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        uranus.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle6) * Uranus_Abstand, 0, FastMath.cos(cycle6) * Uranus_Abstand));
        uranus.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Uranus_Rotationsgeschwindigkeit * tpf / 50, 0));

        cycle7 += Neptun_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        neptune.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle7) * Neptun_Abstand, 0, FastMath.cos(cycle7) * Neptun_Abstand));
        neptune.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Neptun_Rotationsgeschwindigkeit * tpf / 50, 0));

        cycle8 += Saturn_Orbitalgeschwindigkeit * tpf / 50 % FastMath.TWO_PI;
        saturn.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle8) * Saturn_Abstand, 0, FastMath.cos(cycle8) * Saturn_Abstand));
        saturn.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Saturn_Rotationsgeschwindigkeit * tpf / 50, 0));

        // moon.rotate(0, 0.003f, 0);
        // moonGeom.rotate(0, 0.07f, 0);
    }

    @Override
    @SuppressWarnings("null")
    public void onAction(String name, boolean isPressed, float tpf) {

        if (name.equals("SPACE") && !isPressed) {
            cc.setEnabled(!cc.isEnabled());
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //
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
}
