package mygame;

import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.water.SimpleWaterProcessor;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GameRunningState extends AbstractAppState {

    private final Camera cam2;
    private final AudioNode amb;
    private final AudioNode amb1;
    private final AudioNode amb2;
    private final int ambienceVolume = 3;

    private WeatherControl weatherControl;
    private final SkyControl sc;
    private final Terrain terrainControl;

    private final ViewPort view2;

    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final AssetManager assetManager;
    private final Node localRootNode = new Node("Game Screen RootNode");
    private final Node localGuiNode = new Node("Game Screen GuiNode");
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;

    private final PlayerControl playerControl;
    private boolean isRunning = false;

    private boolean bgmOn = false;
    private int bgmVolume = 8;
    private int anisotrpy_samples = 4;
    private final GlobalLightingControl glc;

    private boolean bloomEnabled;
    private boolean fogEnabled;
    private boolean lightScatterEnabled;
    private boolean anisotropyEnabled;
    private boolean waterPostProcessing;
    private final boolean globalLightningEnabled;
    private final boolean shadows;

    private final FilterPostProcessor fpp;
    private boolean weatherEnabled;

    private float counter = 0;
    private float limit = 0;
    private final DepthOfField dof;
    private final SSAO ssao;

    public GameRunningState(SimpleApplication app, Boolean fogEnabled, Boolean bloomEnabled, Boolean lightScatterEnabled, Boolean anisotropyEnabled, Boolean waterPostProcessing, Boolean shadows, Boolean globalLightningEnabled) {

        System.out.println("Game State is being constructed");

        this.fogEnabled = fogEnabled;
        this.bloomEnabled = bloomEnabled;
        this.lightScatterEnabled = lightScatterEnabled;
        this.anisotropyEnabled = anisotropyEnabled;
        this.waterPostProcessing = waterPostProcessing;
        this.globalLightningEnabled = globalLightningEnabled;
        this.shadows = shadows;
        this.weatherEnabled = true;

//      CONSTRUKTOR
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();

        fpp = new FilterPostProcessor(assetManager);

//      PHYSICS STATE
        bulletAppState = new BulletAppState();
        app.getStateManager().attach(bulletAppState);
        bulletAppState.setEnabled(false);
        bulletAppState.setDebugEnabled(false);

//      CAMERA        
        this.viewPort.getCamera().setLocation(new Vector3f(0, 8, -10));

//      TERRAIN
        terrainControl = new Terrain(assetManager, bulletAppState, localRootNode, viewPort);
        Node terrainNode = new Node("terrainNode");
        terrainNode.addControl(terrainControl);
        localRootNode.attachChild(terrainNode);

//      PLAYER
        playerControl = new PlayerControl(app, bulletAppState, localRootNode);
        playerControl.getPhysicsCharacter().setEnabled(false);
        Node player = new Node("playerNode");
        player.addControl(playerControl);
        localRootNode.attachChild(player);
        localRootNode.getControl(CameraCollisionControl.class).setEnabled(false);

//      SUN
        Node sunNode = new Node("sunNode");
        glc = new GlobalLightingControl(viewPort, assetManager, playerControl.getLamp(), localRootNode);
        sunNode.addControl(glc);
        localRootNode.attachChild(sunNode);
        glc.setGlobalLightning(this.globalLightningEnabled);

//      SKY
        sc = new SkyControl(assetManager, glc, localRootNode);
        localRootNode.addControl(sc);

//      Bloom
        if (bloomEnabled) {
            if (localRootNode.getControl(BloomPostFilter.class) == null) {
                localRootNode.addControl(new BloomPostFilter(fpp));
            }
        }

//      LightScatter
        if (lightScatterEnabled) {

            if (localRootNode.getControl(LightScatterFilter.class) == null) {
                localRootNode.addControl(new LightScatterFilter(fpp, glc, true));
            }
        }

//      FOG
        if (fogEnabled) {
            if (localRootNode.getControl(FogPostFilter.class) == null) {
                localRootNode.addControl(new FogPostFilter(fpp));
            }
        }

        //      Weather
        if (weatherEnabled) {
            if (localRootNode.getControl(WeatherControl.class) == null) {
                weatherControl = new WeatherControl(assetManager, localRootNode, viewPort.getCamera(),terrainControl.getHeightmap());
                weatherControl.setEnabled(false);
                localRootNode.addControl(weatherControl);
            }
        }

//      ANISOTROPY
        if (anisotropyEnabled) {
            localRootNode.addControl(new AnisotropyControl(assetManager, 2));
        }

//      PosterizationFilter
        localRootNode.addControl(new PosterizationFilterControl(fpp));

        //DOF
        dof = new DepthOfField(fpp, app.getContext(), viewPort, assetManager);
        localRootNode.addControl(dof);

        //SSAO
        ssao = new SSAO(assetManager);
        localRootNode.addControl(ssao);

//      HOSTILE
        Spatial demon = assetManager.loadModel("Models/hostile/demon/demon.j3o");
        EntityControl ec1 = new EntityControl(assetManager, demon, bulletAppState, "demon", new Vector3f(10, 0, -10));
        demon.addControl(ec1);
        localRootNode.attachChild(demon);

        Spatial forestmonster = assetManager.loadModel("Models/hostile/forestmonster/forestmonster.j3o");
        EntityControl ec2 = new EntityControl(assetManager, forestmonster, bulletAppState, "forestmonster", new Vector3f(-10, 0, 10));
        forestmonster.addControl(ec2);
        localRootNode.attachChild(forestmonster);

        Spatial spider = assetManager.loadModel("Models/hostile/spider/spider.j3o");
        EntityControl ec3 = new EntityControl(assetManager, spider, bulletAppState, "spider", new Vector3f(-10, 0, -10));
        spider.addControl(ec3);
        localRootNode.attachChild(spider);
        setupKeys();

        //Second Camera View
        cam2 = app.getCamera().clone();
        cam2.setViewPort(0f, 0.5f, 0f, 0.5f);
        cam2.setLocation(new Vector3f(-0.10947256f, 25.5760219f, 4.81758f));
        cam2.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));

        view2 = app.getRenderManager().createMainView("Bottom Left", cam2);
        view2.setClearFlags(true, true, true);
        view2.attachScene(localRootNode);
        view2.setEnabled(false);

        //Audio
        amb = new AudioNode(assetManager, "audio/ambience-creepyatmosfear.wav", DataType.Stream);
        amb.setLooping(true);
        amb.setPositional(false);
        amb.setVolume(ambienceVolume - 1.5f);
        localRootNode.attachChild(amb);

        amb1 = new AudioNode(assetManager, "audio/Ocean Waves.ogg", DataType.Stream);
        amb1.setLooping(true);
        amb1.setPositional(false);
        amb1.setVolume(ambienceVolume - 1.5f);
        localRootNode.attachChild(amb1);

        amb2 = new AudioNode(assetManager, "audio/Nature.ogg", DataType.Stream);
        amb2.setLooping(true);
        amb2.setPositional(false);
        amb2.setVolume(ambienceVolume + 1.5f);
        localRootNode.attachChild(amb2);

        limit = getRandomNumberInRange(15, 45);
    }

    public final void attachBird() {

        int cl = getRandomNumberInRange(6, 24);

        for (int i = 1; i < cl; i++) {
            Spatial bird = assetManager.loadModel("Models/wildlife/Bird.j3o");

            bird.setLocalTranslation(getRandomNumberInRange(-512, 512), getRandomNumberInRange(100, 150), getRandomNumberInRange(-512, 512));

            //bird.lookAt(new Vector3f(getRandomNumberInRange(0, 32), 0, getRandomNumberInRange(0, 32)), Vector3f.UNIT_Y);
            WildLifeControl wildlifeControl = new WildLifeControl();

            bird.addControl(wildlifeControl);
            wildlifeControl.getSkeletonControl().setHardwareSkinningPreferred(false);
            localRootNode.attachChild(bird);
            wildlifeControl.setAnim("fly", LoopMode.Loop);
        }
    }

    public Node getLocalRoot() {
        return localRootNode;
    }

    public void switchSecondView() {

        view2.setEnabled(!view2.isEnabled());
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        super.initialize(stateManager, app);

        System.out.println("Game State is being initialized");

        int nl = getRandomNumberInRange(15, 45);

        if (limit == 0) {
            limit = nl;
        }

        inputManager.setCursorVisible(false);

        bulletAppState.setEnabled(true);

        //      WATER
        if (waterPostProcessing) {
            if (localRootNode.getControl(WaterPostFilter.class) == null) {
                localRootNode.addControl(new WaterPostFilter(fpp, true, true, true, true, true, true, true));
            }
        } else {
            if (localRootNode.getControl(simpleWaterControl.class) == null) {
                localRootNode.addControl(new simpleWaterControl((SimpleApplication) app, localRootNode));
            }
        }

        localRootNode.getControl(CameraCollisionControl.class).setEnabled(true);

        if (weatherEnabled) {
            weatherControl.setEnabled(true);
        }

        playerControl.getPhysicsCharacter().setEnabled(true);
        amb.play();
        amb1.play();
        amb2.play();
    }

    private void setupKeys() {

        inputManager.addMapping("write",
                new KeyTrigger(KeyInput.KEY_F9));

        inputManager.addListener(actionListener, "write");

        inputManager.addMapping("treeoutroot",
                new KeyTrigger(KeyInput.KEY_O));

        inputManager.addListener(actionListener, "treeoutroot");

        inputManager.addMapping("debug",
                new KeyTrigger(KeyInput.KEY_Q));

        inputManager.addListener(actionListener, "debug");

        inputManager.addMapping("switchCam",
                new KeyTrigger(KeyInput.KEY_P));

        inputManager.addListener(actionListener, "switchCam");
    }

    private void treeoutroot(Node node) {

        int n = node.getQuantity();

        String name = node.getName();

        if (n > 0) {
            System.out.print("+ " + name + " " + n);
        } else {
            System.out.println(name);
        }

        for (Spatial spat : node.getChildren()) {
            int c = spat.getParent().getChildIndex(spat);
            if (c > 0) {
                System.out.println("-" + name + " " + c);
            } else {
                System.out.println(name);
            }
            if (!spat.getClass().equals(TerrainPatch.class)) {
                if (!spat.getClass().equals(Geometry.class)) {
                    if (!spat.getClass().equals(ParticleEmitter.class)) {
                        treeoutroot((Node) spat);
                    }
                }
            }
        }
    }

    public Node treeOutOuter(Spatial spat) {

        int c = spat.getParent().getChildIndex(spat);

        if (spat.getClass().equals(Spatial.class) | spat.getClass().equals(Node.class)) {
            if (spat.getName() != null) {
                if (!spat.getName().equals("null")) {
                    if (c > 0) {
                        System.out.println("-" + spat.getName() + " " + c);
                    } else {
                        System.out.println(spat.getName());
                    }
                }
            }
        }
        return (Node) spat;
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {

            switch (binding) {

                case "write":
                    if (value && isRunning) {
                        write(localRootNode, "localRootNode");
                    }
                    break;

                case "switchCam":
                    if (value && isRunning) {
                        switchSecondView();
                    }
                    break;

                case "treeoutroot":
                    if (value && isRunning) {
                        treeoutroot(localRootNode);
                    }
                    break;

                case "debug":
                    if (value && isRunning) {
                        getBulletAppState().setDebugEnabled(!bulletAppState.isDebugEnabled());
                    }
                    break;
            }

        }

    };

    private void write(Node node, String name) {

        String userHome = System.getProperty("user.home");
        BinaryExporter exporter = BinaryExporter.getInstance();
        File file = new File(userHome + "/" + name + ".j3o");

        playerControl.removeChaseCam();

        try {
            exporter.save(node, file);
        } catch (IOException ex) {
            System.out.println("Failed to save node!");
            // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to save node!", ex);
        }
        playerControl.attachChaseCam();
    }

    @Override
    public void update(float tpf) {

        if (isRunning) {

            super.update(tpf);

            if (globalLightningEnabled) {
                if (view2.isEnabled()) {
                    cam2.setLocation(glc.getSunPosition());
                    cam2.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));
                    cam2.lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
                    cam2.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
                }
            }

            counter += tpf;

            if (counter >= limit) {
                counter = 0;
                int nl = getRandomNumberInRange(15, 45);
                limit = nl;
                attachBird();
            }
        }
    }

    public void attachLocalRootNode() {
        if (!rootNode.hasChild(localRootNode)) {
            rootNode.attachChild(localRootNode);
        }
    }

    public void dettachLocalRootNode() {
        if (rootNode.hasChild(localRootNode)) {
            localRootNode.removeFromParent();
        }
    }

    public void attachLocalGuiNode() {
        if (!guiNode.hasChild(localGuiNode)) {
            guiNode.attachChild(localGuiNode);
        }
    }

    public void detachLocalGuiNode() {
        if (guiNode.hasChild(localGuiNode)) {
            guiNode.removeFromParent();
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {

        System.out.println("Game State is being attached");

        stateAttach();
        setIsRunning(true);

    }

    public void stateAttach() {
        playerControl.setEnabled(true);
        sc.setEnabled(true);
        glc.setEnabled(true);
        weatherControl.setEnabled(true);
        localRootNode.getControl(PosterizationFilterControl.class).setEnabled(true);
        localRootNode.getControl(PosterizationFilterControl.class).setStrength(1.75f);

        if (shadows) {
            if (!viewPort.getProcessors().contains(glc.getSlsr())) {
                viewPort.addProcessor(glc.getSlsr());
            }
        }

        if (!viewPort.getProcessors().contains(fpp)) {
            viewPort.addProcessor(fpp);
        }

        if (!waterPostProcessing) {
            if (localRootNode.getControl(simpleWaterControl.class) != null) {
                viewPort.addProcessor((SimpleWaterProcessor) localRootNode.getControl(simpleWaterControl.class).getWaterProcessor());
            }
        }
        attachLocalGuiNode();
        attachLocalRootNode();
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        System.out.println("Game State is being detached");
        stateDetach();
        setIsRunning(false);
    }

    public void stateDetach() {

        amb.stop();
        amb1.stop();
        amb2.stop();

        view2.setEnabled(false);
        localRootNode.getControl(PosterizationFilterControl.class).setEnabled(false);
        playerControl.setEnabled(false);
        sc.setEnabled(false);
        glc.setEnabled(false);
        weatherControl.setEnabled(false);
        if (viewPort.getProcessors().contains(fpp)) {
            viewPort.removeProcessor(fpp);
        }

        if (shadows) {
            if (viewPort.getProcessors().contains(glc.getSlsr())) {
                viewPort.removeProcessor(glc.getSlsr());
            }
        }

        if (!waterPostProcessing) {
            if (viewPort.getProcessors().contains(localRootNode.getControl(simpleWaterControl.class).getWaterProcessor())) {
                SimpleWaterProcessor swp = localRootNode.getControl(simpleWaterControl.class).getWaterProcessor();
                viewPort.removeProcessor(swp);
            }
        }

        dettachLocalRootNode();
        detachLocalGuiNode();
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isBgmOn() {
        return bgmOn;
    }

    public void setBgmOn(boolean bgmOn) {
        this.bgmOn = bgmOn;
    }

    public int getBgmVolume() {
        return bgmVolume;
    }

    public void setBgmVolume(int bgmVolume) {
        this.bgmVolume = bgmVolume;
    }

    public int getAnisotrpy_samples() {
        return anisotrpy_samples;
    }

    public void setAnisotrpy_samples(int anisotrpy_samples) {
        this.anisotrpy_samples = anisotrpy_samples;
    }

    public boolean isBloomEnabled() {
        return bloomEnabled;
    }

    public void setBloomEnabled(boolean bloomEnabled) {
        this.bloomEnabled = bloomEnabled;
    }

    public boolean isFogEnabled() {
        return fogEnabled;
    }

    public void setFogEnabled(boolean fogEnabled) {
        this.fogEnabled = fogEnabled;
    }

    public boolean isLightScatterEnabled() {
        return lightScatterEnabled;
    }

    public void setLightScatterEnabled(boolean lightScatterEnabled) {
        this.lightScatterEnabled = lightScatterEnabled;
    }

    public boolean isAnisotropyEnabled() {
        return anisotropyEnabled;
    }

    public void setAnisotropyEnabled(boolean anisotropyEnabled) {
        this.anisotropyEnabled = anisotropyEnabled;
    }

    public boolean isWaterPostProcessing() {
        return waterPostProcessing;
    }

    public void setWaterPostProcessing(boolean waterPostProcessing) {
        this.waterPostProcessing = waterPostProcessing;
    }

    public boolean isWeatherEnabled() {
        return weatherEnabled;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public void setWeatherEnabled(boolean weatherEnabled) {
        this.weatherEnabled = weatherEnabled;
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }
};
