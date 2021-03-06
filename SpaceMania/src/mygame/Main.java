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

    private float rotationDamp = 5;
    private float orbitalDamp = 2000;
    private float sizeRatio = 1500;

    private float cycle1 = 0;
    private float cycle2 = 0;
    private float cycle3 = 0;
    private float cycle4 = 0;
    private float cycle5 = 0;
    private float cycle6 = 0;
    private float cycle7 = 0;
    private float cycle8 = 0;

    float Sun_Durchmesser = 1300000 / sizeRatio / 4;
    float Sun_Masse = 1.9884f;

    float Merkur_Orbitalgeschwindigkeit = 47.8725f;
    float Merkur_Durchmesser = 4879 / sizeRatio;
    float Merkur_Masse = 0.33022f;
    float Merkur_Rotationsgeschwindigkeit = 4.25f;
    float Merkur_Abstand = 58 + (Sun_Durchmesser * 2);

    float Venus_Orbitalgeschwindigkeit = 35.0214f;
    float Venus_Durchmesser = 12104 / sizeRatio;
    float Venus_Masse = 4.8685f;
    float Venus_Rotationsgeschwindigkeit = -10.36f;
    float Venus_Abstand = 108 + (Sun_Durchmesser * 2);

    float Erde_Orbitalgeschwindigkeit = 29.7859f;
    float Erde_Durchmesser = 12742 / sizeRatio;
    float Erde_Masse = 5.9737f;
    float Erde_Rotationsgeschwindigkeit = 11.18f;
    float Erde_Abstand = 150 + (Sun_Durchmesser * 2);

    float Mars_Orbitalgeschwindigkeit = 24.1309f;
    float Mars_Durchmesser = 6779 / sizeRatio;
    float Mars_Masse = 0.64185f;
    float Mars_Rotationsgeschwindigkeit = 5.02f;
    float Mars_Abstand = 228 + (Sun_Durchmesser * 2);

    float Jupiter_Orbitalgeschwindigkeit = 13.0697f;
    float Jupiter_Durchmesser = 139822 / sizeRatio;
    float Jupiter_Masse = 1898.7f;
    float Jupiter_Rotationsgeschwindigkeit = 59.54f;
    float Jupiter_Abstand = 778 + (Sun_Durchmesser * 2);

    float Saturn_Orbitalgeschwindigkeit = 9.6724f;
    float Saturn_Durchmesser = 116464 / sizeRatio;
    float Saturn_Masse = 568.51f;
    float Saturn_Rotationsgeschwindigkeit = 35.49f;
    float Saturn_Abstand = 1427 + (Sun_Durchmesser * 2);

    float Uranus_Orbitalgeschwindigkeit = 6.8352f;
    float Uranus_Durchmesser = 50724 / sizeRatio;
    float Uranus_Masse = 86.849f;
    float Uranus_Rotationsgeschwindigkeit = -21.29f;
    float Uranus_Abstand = 2884 + (Sun_Durchmesser * 2);

    float Neptun_Orbitalgeschwindigkeit = 5.4778f;
    float Neptun_Durchmesser = 49244 / sizeRatio;
    float Neptun_Masse = 102.44f;
    float Neptun_Rotationsgeschwindigkeit = 23.71f;
    float Neptun_Abstand = 4509 + (Sun_Durchmesser * 2);

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
        app.setDisplayFps(true);
        app.setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        createSun();
        createPhysics();
        createSpace();
        createPlanets();
        createShip();
        createViewPort();
        createInput();
    }

    private void createViewPort() {

        viewPort.getCamera().setFrustumFar(9999);
        viewPort.getCamera().onFrameChange();
    }

    private void createInput() {

        cam.getLocation().addLocal(0, 0, -1000);
        getFlyByCamera().setMoveSpeed(350);

        inputManager.setCursorVisible(false);

        inputManager.addMapping("SPACE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "SPACE");
        inputManager.addMapping("0", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addListener(this, "0");
        inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addListener(this, "1");
        inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(this, "2");
        inputManager.addMapping("3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addListener(this, "3");
        inputManager.addMapping("4", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addListener(this, "4");
        inputManager.addMapping("5", new KeyTrigger(KeyInput.KEY_5));
        inputManager.addListener(this, "5");
        inputManager.addMapping("6", new KeyTrigger(KeyInput.KEY_6));
        inputManager.addListener(this, "6");
        inputManager.addMapping("7", new KeyTrigger(KeyInput.KEY_7));
        inputManager.addListener(this, "7");
        inputManager.addMapping("8", new KeyTrigger(KeyInput.KEY_8));
        inputManager.addListener(this, "8");
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
//TODO:
    }

    private void createPlanets() {

        Sphere neptune_sphere = new Sphere(128, 128, Neptun_Durchmesser);
        neptune = new Geometry("Neptun", neptune_sphere);
        neptune.setMaterial(assetManager.loadMaterial("Materials/Generated/neptune.j3m"));
        neptune.addControl(new RigidBodyControl(Neptun_Masse));
        neptune.rotate(90, 0, 0);
        PhysicsSpace.getPhysicsSpace().add(neptune);
        rootNode.attachChild(neptune);

        Sphere saturn_sphere = new Sphere(128, 128, Saturn_Durchmesser);
        saturn = new Geometry("Saturn", saturn_sphere);
        saturn.setMaterial(assetManager.loadMaterial("Materials/Generated/saturn.j3m"));
        saturn.rotate(90, 0, 0);
        saturn.addControl(new RigidBodyControl(Saturn_Masse));
        PhysicsSpace.getPhysicsSpace().add(saturn);
        rootNode.attachChild(saturn);

        Sphere earth_sphere = new Sphere(128, 128, Erde_Durchmesser);
        earth = new Geometry("Erde", earth_sphere);
        earth.setMaterial(assetManager.loadMaterial("Materials/Generated/earth.j3m"));
        earth.rotate(90, 0, 0);
        earth.addControl(new RigidBodyControl(Erde_Masse));
        PhysicsSpace.getPhysicsSpace().add(earth);
        rootNode.attachChild(earth);

        Sphere jupiter_sphere = new Sphere(128, 128, Jupiter_Durchmesser);
        jupiter = new Geometry("Jupiter", jupiter_sphere);
        jupiter.setMaterial(assetManager.loadMaterial("Materials/Generated/jupiter.j3m"));
        jupiter.rotate(90, 0, 0);
        jupiter.addControl(new RigidBodyControl(Jupiter_Masse));
        PhysicsSpace.getPhysicsSpace().add(jupiter);
        rootNode.attachChild(jupiter);

        Sphere mars_sphere = new Sphere(128, 128, Mars_Durchmesser);
        mars = new Geometry("Mars", mars_sphere);
        mars.setMaterial(assetManager.loadMaterial("Materials/Generated/mars.j3m"));
        mars.rotate(90, 0, 0);
        mars.addControl(new RigidBodyControl(Mars_Masse));
        PhysicsSpace.getPhysicsSpace().add(mars);
        rootNode.attachChild(mars);

        Sphere merkur_sphere = new Sphere(128, 128, Merkur_Durchmesser);
        mercurius = new Geometry("Merkur", merkur_sphere);
        mercurius.setMaterial(assetManager.loadMaterial("Materials/Generated/mercurius.j3m"));
        mercurius.rotate(90, 0, 0);
        mercurius.addControl(new RigidBodyControl(Merkur_Masse));
        PhysicsSpace.getPhysicsSpace().add(mercurius);
        rootNode.attachChild(mercurius);

        Sphere uranus_sphere = new Sphere(128, 128, Uranus_Durchmesser);
        uranus = new Geometry("Uranus", uranus_sphere);
        uranus.setMaterial(assetManager.loadMaterial("Materials/Generated/uranus.j3m"));
        uranus.rotate(90, 0, 0);
        uranus.addControl(new RigidBodyControl(Uranus_Masse));
        PhysicsSpace.getPhysicsSpace().add(uranus);
        rootNode.attachChild(uranus);

        Sphere venus_sphere = new Sphere(128, 128, Venus_Durchmesser);
        venus = new Geometry("Venus", venus_sphere);
        venus.setMaterial(assetManager.loadMaterial("Materials/Generated/venus.j3m"));
        venus.rotate(90, 0, 0);
        venus.addControl(new RigidBodyControl(Venus_Masse));
        PhysicsSpace.getPhysicsSpace().add(venus);
        rootNode.attachChild(venus);
    }

    private void createSpace() {

        Texture west = assetManager.loadTexture("Textures/space/right(unity left).png");
        Texture east = assetManager.loadTexture("Textures/space/left(uity right).png");
        Texture north = assetManager.loadTexture("Textures/space/back.png");
        Texture south = assetManager.loadTexture("Textures/space/middle.png");
        Texture up = assetManager.loadTexture("Textures/space/up.png");
        Texture down = assetManager.loadTexture("Textures/space/down.png");
        Spatial space = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(space);
    }

    private void createSun() {

        Geometry sunGeom = new Geometry("Sun", new Sphere(24, 24, Sun_Durchmesser));
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
        sunFire.setStartSize(Sun_Durchmesser * 2);
        sunFire.setEndSize(Sun_Durchmesser * 1.5F);
        sunFire.setInWorldSpace(false);
        sunFire.setLowLife(0.75f);
        sunFire.setHighLife(1.5f);
        sunFire.setParticlesPerSec(30);
        sunFire.setNumParticles(120);
        sunFire.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sunFire);

        sunLight = new PointLight();
        sunLight.setPosition(Vector3f.ZERO);
        sunLight.setColor(ColorRGBA.White);
        sunLight.setRadius(9999);
        rootNode.addLight(sunLight);
    }

    @Override
    public void simpleUpdate(float tpf) {

        cycle2 += Jupiter_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        jupiter.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle2) * Jupiter_Abstand), 0, FastMath.cos(cycle2) * Jupiter_Abstand));
        jupiter.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Jupiter_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));

        cycle3 += Mars_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        mars.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle3) * Mars_Abstand), 0, FastMath.cos(cycle3) * Mars_Abstand));
        mars.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Mars_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));

        cycle4 += Merkur_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        mercurius.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f((FastMath.sin(cycle4) * Merkur_Abstand), 0, FastMath.cos(cycle4) * Merkur_Abstand));
        mercurius.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Merkur_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));

        cycle1 += Erde_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        earth.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle1) * Erde_Abstand, 0, FastMath.cos(cycle1) * Erde_Abstand));
        earth.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Erde_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));

        cycle5 += Venus_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        venus.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle5) * Venus_Abstand, 0, FastMath.cos(cycle5) * Venus_Abstand));
        venus.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Venus_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));

        cycle6 += Uranus_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        uranus.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle6) * Uranus_Abstand, 0, FastMath.cos(cycle6) * Uranus_Abstand));
        uranus.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Uranus_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));

        cycle7 += Neptun_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        neptune.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle7) * Neptun_Abstand, 0, FastMath.cos(cycle7) * Neptun_Abstand));
        neptune.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Neptun_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));

        cycle8 += Saturn_Orbitalgeschwindigkeit / orbitalDamp * tpf % FastMath.TWO_PI;
        saturn.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.sin(cycle8) * Saturn_Abstand, 0, FastMath.cos(cycle8) * Saturn_Abstand));
        saturn.getControl(RigidBodyControl.class).setAngularVelocity(new Vector3f(0, Saturn_Rotationsgeschwindigkeit * tpf * rotationDamp, 0));
    }

    @Override
    @SuppressWarnings("null")
    public void onAction(String name, boolean isPressed, float tpf) {

        if (name.equals("SPACE") && !isPressed) {
            cc.setEnabled(!cc.isEnabled());
        }
        if (name.equals("0") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.CameraToSpatial);
            cc.getSpatial().removeControl(cc);
            ship.addControl(cc);
        }
        if (name.equals("1") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            mercurius.addControl(cc);
        }
        if (name.equals("2") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            venus.addControl(cc);
        }
        if (name.equals("3") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            earth.addControl(cc);
        }
        if (name.equals("4") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            mars.addControl(cc);
        }
        if (name.equals("5") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            jupiter.addControl(cc);
        }
        if (name.equals("6") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            saturn.addControl(cc);
        }
        if (name.equals("7") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            neptune.addControl(cc);
        }
        if (name.equals("8") && !isPressed) {
            cc.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
            cc.getSpatial().removeControl(cc);
            uranus.addControl(cc);
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
