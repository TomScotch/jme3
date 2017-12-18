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

    float Merkur_Orbitalgeschwindigkeit = 47.8725f;
    float Merkur_Durchmesser = 4879;
    float Merkur_Masse = 0.33022f;
    float Merkur_Rotationsgeschwindigkeit = 4.25f;
    float Merkur_abstand = 58;

    float Venus_Orbitalgeschwindigkeit = 35.0214f;
    float Venus_Durchmesser = 12104;
    float Venus_Masse = 4.8685f;
    float Venus_Rotationsgeschwindigkeit = -10.36f;
    float Venus_abstand = 108;

    float Erde_Orbitalgeschwindigkeit = 29.7859f;
    float Erde_Durchmesser = 12742;
    float Erde_Masse = 5.9737f;
    float Erde_Rotationsgeschwindigkeit = 11.18f;
    float Erde_abstand = 150;

    float Mars_Orbitalgeschwindigkeit = 24.1309f;
    float Mars_Durchmesser = 6779;
    float Mars_Masse = 0.64185f;
    float Mars_Rotationsgeschwindigkeit = 5.02f;
    float Mars_abstand = 228;

    float Jupiter_Orbitalgeschwindigkeit = 13.0697f;
    float Jupiter_Durchmesser = 139822;
    float Jupiter_Masse = 1898.7f;
    float Jupiter_Rotationsgeschwindigkeit = 59.54f;
    float Jupiter_abstand = 778;

    float Saturn_Orbitalgeschwindigkeit = 9.6724f;
    float Saturn_Durchmesser = 116464;
    float Saturn_Masse = 568.51f;
    float Saturn_Rotationsgeschwindigkeit = 35.49f;
    float Saturn_abstand = 1427;

    float Uranus_Orbitalgeschwindigkeit = 6.8352f;
    float Uranus_Durchmesser = 50724;
    float Uranus_Masse = 86.849f;
    float Uranus_Rotationsgeschwindigkeit = -21.29f;
    float Uranus_abstand = 2884;

    float Neptun_Orbitalgeschwindigkeit = 5.4778f;
    float Neptun_Durchmesser = 49244;
    float Neptun_Masse = 102.44f;
    float Neptun_Rotationsgeschwindigkeit = 23.71f;
    float Neptun_abstand = 4509;

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

    private void createStars() {

        neptune = assetManager.loadModel("Models/neptune.j3o");
        rootNode.attachChild(neptune);
        PhysicsSpace.getPhysicsSpace().add(neptune);

        saturn = assetManager.loadModel("Models/saturn.j3o");
        rootNode.attachChild(saturn);
        PhysicsSpace.getPhysicsSpace().add(saturn);

        uranus = assetManager.loadModel("Models/uranus.j3o");
        rootNode.attachChild(uranus);
        PhysicsSpace.getPhysicsSpace().add(uranus);

        venus = assetManager.loadModel("Models/venus.j3o");
        rootNode.attachChild(venus);
        PhysicsSpace.getPhysicsSpace().add(venus);

        earth = assetManager.loadModel("Models/earth.j3o");
        rootNode.attachChild(earth);
        PhysicsSpace.getPhysicsSpace().add(earth);

        jupiter = assetManager.loadModel("Models/jupiter.j3o");
        rootNode.attachChild(jupiter);
        PhysicsSpace.getPhysicsSpace().add(jupiter);

        moon = new Node("MoonNode");
        Node en = (Node) earth;
        en.attachChild(moon);
        moonGeom = assetManager.loadModel("Models/moon.j3o");
        moonGeom.center();
        moonGeom.setLocalTranslation(0, 0, 2);
        moon.attachChild(moonGeom);

        mercurius = assetManager.loadModel("Models/mercurius.j3o");
        rootNode.attachChild(mercurius);
        PhysicsSpace.getPhysicsSpace().add(mercurius);

        mars = assetManager.loadModel("Models/mars.j3o");
        rootNode.attachChild(mars);
        PhysicsSpace.getPhysicsSpace().add(mars);
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

        cycle2 += 0.0045f % FastMath.TWO_PI;
        jupiter.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle2) * 1200), 0, FastMath.cos(cycle2) * 1200).negate());
        jupiter.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Jupiter_Rotationsgeschwindigkeit, 0));

        cycle3 += 0.0036f % FastMath.TWO_PI;
        mars.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle3) * 700), 0, FastMath.cos(cycle3) * 800));
        mars.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Mars_Rotationsgeschwindigkeit, 0));

        cycle4 += 0.0018f % FastMath.TWO_PI;
        mercurius.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle4) * 600), 0, FastMath.cos(cycle4) * 500));
        mercurius.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Merkur_Rotationsgeschwindigkeit, 0));

        cycle1 += 0.0027f % FastMath.TWO_PI;
        earth.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle1) * 300, 0, FastMath.cos(cycle1) * 300).negate());
        earth.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Erde_Rotationsgeschwindigkeit, 0));

        cycle5 += 0.0027f % FastMath.TWO_PI;
        venus.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle5) * 300, 0, FastMath.cos(cycle5) * 300).negate());
        venus.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Venus_Rotationsgeschwindigkeit, 0));

        cycle6 += 0.0045f % FastMath.TWO_PI;
        uranus.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle6) * 300, 0, FastMath.cos(cycle6) * 300).negate());
        uranus.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Uranus_Rotationsgeschwindigkeit, 0));

        cycle7 += 0.0036f % FastMath.TWO_PI;
        neptune.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle7) * 300, 0, FastMath.cos(cycle7) * 300).negate());
        neptune.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Neptun_Rotationsgeschwindigkeit, 0));

        cycle8 += 0.0018f % FastMath.TWO_PI;
        saturn.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle8) * 300, 0, FastMath.cos(cycle8) * 300).negate());
        saturn.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Saturn_Rotationsgeschwindigkeit, 0));

        moon.rotate(0, 0.003f, 0);
        moonGeom.rotate(0, 0.07f, 0);
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
