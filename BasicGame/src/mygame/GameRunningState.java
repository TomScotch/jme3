package mygame;

import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.water.SimpleWaterProcessor;
import de.lessvoid.nifty.controls.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameRunningState extends AbstractAppState {

    private final Spatial teapot;
    private final BitmapText ch;

    public AudioNode getLightRain() {
        return lightRain;
    }

    public AudioNode getNormalRain() {
        return normalRain;
    }

    public AudioNode getHeavyRain() {
        return heavyRain;
    }

    private BitmapText healthText;
    private BitmapFont guiFont;
    private boolean noWide;

    public void setConsole(Console console) {
        this.console = console;
    }

    public Console getConsole() {
        return console;
    }

    private Console console;

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    private final Camera cam2;
    private final AudioNode amb;
    private final AudioNode amb1;
    private final AudioNode amb2;

    private final AudioNode lightRain;
    private final AudioNode normalRain;
    private final AudioNode heavyRain;

    private final int ambienceVolume = 3;

    private WeatherControl weatherControl;
    private final SkyControl sc;
    private final Terrain terrainControl;

    private final ViewPort view2;

    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final AssetManager assetManager;
    private Node localRootNode = new Node("Game Screen RootNode");
    private final Node localGuiNode = new Node("Game Screen GuiNode");
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;

    private final PlayerControl playerControl;
    private boolean isRunning = false;

    private boolean bgmOn = false;
    private int bgmVolume = 8;
    private int anisotrpy_samples = 4;
    private GlobalLightingControl glc;

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
    // private final SSAO ssao;
    private BitmapText hudText;
    protected boolean isTimeDemo = false;
    private List<Float> fps;
    private BitmapText hudText2;
    private Vector2f minMaxFps;
    private final AppStateManager stateManager;
    private EnemyControl enemyControl;
    private float health = 100;
    private final int birdLimit = 29;

    public GameRunningState(SimpleApplication app, Boolean fogEnabled, Boolean bloomEnabled, Boolean lightScatterEnabled, Boolean anisotropyEnabled, Boolean waterPostProcessing, Boolean shadows, Boolean globalLightningEnabled) {

        System.out.println("Game State is being constructed");
        this.console = mygame.Main.getConsole();
        this.fogEnabled = fogEnabled;
        this.bloomEnabled = bloomEnabled;
        this.lightScatterEnabled = lightScatterEnabled;
        this.anisotropyEnabled = anisotropyEnabled;
        this.waterPostProcessing = waterPostProcessing;
        this.globalLightningEnabled = globalLightningEnabled;
        this.shadows = shadows;
        this.weatherEnabled = true;
        this.stateManager = app.getStateManager();

//      CONSTRUKTOR
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();

        fps = new ArrayList<>();

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
//      FOV

        String xy = String.valueOf(viewPort.getCamera().getWidth()) + "x" + String.valueOf(viewPort.getCamera().getHeight());

        noWide = false;

        switch (xy) {
            case "320x240":
                noWide = true;
                break;
            case "640x480":
                noWide = true;
                break;
            case "800x600":
                noWide = true;
                break;
            case "1024x768":
                noWide = true;
                break;
            case "1152x864":
                noWide = true;
                break;
            case "1280x1024":
                noWide = true;
                break;
            case "1360x1024":
                noWide = true;
                break;
            case "1400x1050":
                noWide = true;
                break;
            case "1600x1200":
                noWide = true;
                break;
        }

//      PLAYER
        playerControl = new PlayerControl(app, bulletAppState, localRootNode, noWide);
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
                weatherControl = new WeatherControl(glc, assetManager, localRootNode, terrainControl.getHeightmap());
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

        //Depth of Field
        dof = new DepthOfField(fpp, app.getContext(), viewPort, assetManager);
        localRootNode.addControl(dof);

//      Bloom
        if (bloomEnabled) {
            if (localRootNode.getControl(BloomPostFilter.class) == null) {
                localRootNode.addControl(new BloomPostFilter(fpp));
            }
        }

        //Screen Space Ambient Occlusion
        //ssao = new SSAO(assetManager, fpp);
        //localRootNode.addControl(ssao);
        //Second Camera View
        cam2 = app.getCamera().clone();
        cam2.setViewPort(0f, 0.5f, 0f, 0.5f);
        cam2.setLocation(new Vector3f(-0.10947256f, 25.5760219f, 4.81758f));
        cam2.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));

        view2 = app.getRenderManager().createMainView("Bottom Left", cam2);
        view2.setClearFlags(true, true, true);
        //view2.attachScene(localRootNode);
        view2.setEnabled(false);

        //Audio
        lightRain = new AudioNode(assetManager, "audio/rain/light_rain.ogg", DataType.Stream);
        normalRain = new AudioNode(assetManager, "audio/rain/moderate_rain.ogg", DataType.Stream);
        heavyRain = new AudioNode(assetManager, "audio/rain/heavy_rain.ogg", DataType.Stream);

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

        enemyControl = new EnemyControl(glc, assetManager, localRootNode, bulletAppState, playerControl);
        limit = getRandomNumberInRange(15, 45);

        teapot = assetManager.loadModel("Models/alternativeScene.j3o");
        teapot.setName("scene");
        teapot.scale(5);
        teapot.setLocalTranslation(0, 2, 0);
        RigidBodyControl rb1 = new RigidBodyControl(0);
        teapot.addControl(rb1);
        rb1.setFriction(0.9f);

        app.getCamera().setFrustum(app.getCamera().getFrustumNear(), app.getCamera().getFrustumFar() * 2, app.getCamera().getFrustumLeft(), app.getCamera().getFrustumRight(), app.getCamera().getFrustumTop(), app.getCamera().getFrustumBottom());
        app.getCamera().update();

        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        ch = new BitmapText(guiFont, false);
        ch.setName("crosshair");
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                app.getContext().getSettings().getWidth() / 2 - ch.getLineWidth() / 2, app.getContext().getSettings().getHeight() / 2 + ch.getLineHeight() / 2, 0);
    }

    private void setupHudText() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        hudText = new BitmapText(assetManager.loadFont("Interface/Fonts/Console.fnt"), false);
        hudText.setSize(assetManager.loadFont("Interface/Fonts/Console.fnt").getCharSet().getRenderedSize() * 1.75f);      // font size
        hudText.setColor(ColorRGBA.Blue);
        hudText.setText("          ");
        hudText.setLocalTranslation(hudText.getLineWidth() * 2.5f, hudText.getLineHeight(), 0); // position
        hudText.setCullHint(Spatial.CullHint.Always);
        localGuiNode.attachChild(hudText);

        hudText2 = new BitmapText(assetManager.loadFont("Interface/Fonts/Console.fnt"), false);
        hudText2.setSize(assetManager.loadFont("Interface/Fonts/Console.fnt").getCharSet().getRenderedSize() * 2.25f);      // font size
        hudText2.setColor(ColorRGBA.Red);
        hudText2.setText("... : ...");
        hudText2.setLocalTranslation((viewPort.getCamera().getWidth() / 2) - (hudText2.getLineWidth() / 2), hudText2.getLineHeight() * 3, 0); // position
        // hudText2.setAlpha(-2);
        localGuiNode.attachChild(hudText2);

        healthText = new BitmapText(assetManager.loadFont("Interface/Fonts/Roboto.fnt"), false);
        healthText.setSize(assetManager.loadFont("Interface/Fonts/Roboto.fnt").getCharSet().getRenderedSize() * 3.2f);
        healthText.setColor(ColorRGBA.Green);
        Integer i = (int) playerControl.getHealth();
        healthText.setText(i.toString());
        healthText.setLocalTranslation(healthText.getSize(), viewPort.getCamera().getHeight() - healthText.getHeight(), 0);
        guiNode.attachChild(healthText);
        healthText.setCullHint(Spatial.CullHint.Never);
    }

    public void addListener() {
        inputManager.addListener(actionListener, "changeLevel");
        inputManager.addListener(actionListener, "write");
        inputManager.addListener(actionListener, "treeoutroot");
        inputManager.addListener(actionListener, "debug");
        inputManager.addListener(actionListener, "switchCam");
        inputManager.addListener(actionListener, "delayUp");
        inputManager.addListener(actionListener, "delayDown");
        inputManager.addListener(actionListener, "timeDemo");
    }

    public void removeListener() {
        inputManager.removeListener(actionListener);
    }

    public final void attachBird(int cl) {

        for (int i = 1; i < cl; i++) {
            Spatial bird = assetManager.loadModel("Models/wildlife/Bird.j3o");

            bird.setLocalTranslation(getRandomNumberInRange(-512, 512), getRandomNumberInRange(100, 150), getRandomNumberInRange(-512, 512));

            //bird.lookAt(new Vector3f(getRandomNumberInRange(0, 32), 0, getRandomNumberInRange(0, 32)), Vector3f.UNIT_Y);
            WildLifeControl wildlifeControl = new WildLifeControl(glc);

            bird.addControl(wildlifeControl);
            wildlifeControl.getSkeletonControl().setHardwareSkinningPreferred(false);
            bird.setName("bird");
            bird.setShadowMode(RenderQueue.ShadowMode.Cast);
            localRootNode.attachChild(bird);
            wildlifeControl.setAnim("fly", LoopMode.Loop);
            // bird.scale(getRandomNumberInRange(0, 1) - 0.5f);
        }
    }

    public Node getLocalRoot() {
        return localRootNode;
    }

    public void setLocalRoot(Node n) {
        this.localRootNode = n;
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
                localRootNode.addControl(new WaterPostFilter(fpp, glc, true, false, false, false, false, false, true, true));
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

        inputManager.addMapping("changeLevel",
                new KeyTrigger(KeyInput.KEY_NUMPAD5));

        inputManager.addMapping("switchCam",
                new KeyTrigger(KeyInput.KEY_P));

        inputManager.addMapping("delayUp",
                new KeyTrigger(KeyInput.KEY_UP));

        inputManager.addMapping("delayDown",
                new KeyTrigger(KeyInput.KEY_DOWN));

        inputManager.addMapping("write",
                new KeyTrigger(KeyInput.KEY_F9));

        inputManager.addMapping("treeoutroot",
                new KeyTrigger(KeyInput.KEY_O));

        inputManager.addMapping("debug",
                new KeyTrigger(KeyInput.KEY_Q));

        inputManager.addMapping("timeDemo",
                new KeyTrigger(KeyInput.KEY_F10));
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
            playerControl.setIdleCounter(0);
            switch (binding) {

                case "changeLevel":
                    if (value && isRunning) {
                        if (!isTimeDemo) {
                            if (localRootNode.getChild("scene") != null) {
                                teapot.removeFromParent();
                                bulletAppState.getPhysicsSpace().addAll(terrainControl.getTerrain());
                                bulletAppState.getPhysicsSpace().removeAll(teapot);
                                playerControl.getPhysicsCharacter().warp(new Vector3f(0, 3.5f, 0));
                                localRootNode.attachChild(terrainControl.getTerrain());
                                enemyControl = new EnemyControl(glc, assetManager, localRootNode, bulletAppState, playerControl);
                                localRootNode.addControl(enemyControl);
                                enemyControl.setEnabled(true);
                                //   enemyControl.remAllEnemys();
                            } else if (localRootNode.hasChild(terrainControl.getTerrain())) {
                                terrainControl.getTerrain().removeFromParent();
                                localRootNode.attachChild(teapot);
                                playerControl.getPhysicsCharacter().warp(new Vector3f(0, 6, 0));
                                bulletAppState.getPhysicsSpace().addAll(teapot);
                                bulletAppState.getPhysicsSpace().removeAll(terrainControl.getTerrain());
                                enemyControl.remAllEnemys();
                                enemyControl.setEnabled(false);
                                localRootNode.removeControl(enemyControl);
                                //enemyControl = null;
                            }
                        }
                    }
                    break;

                case "timeDemo":
                    if (value && isRunning) {
                        if (!isTimeDemo) {
                            if (stateManager.getState(VideoRecorderAppState.class) == null) {
                                isTimeDemo = true;
                                System.out.println("Running Timedemo");
                                fps = new ArrayList<>();
                            }
                        }
                    }
                    break;

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

                case "delayUp":
                    if (value && isRunning) {

                        glc.setTimeDelay((int) glc.getTimeDelay() * 2);
                        hudText.setText("delay : " + glc.getTimeDelay());
                        if (glc.getTimeDelay() >= 65536) {
                            glc.setTimeDelay(65536);
                        }
                        hudText.setCullHint(Spatial.CullHint.Never);
                        hudText.addControl(new TimedActionControl(3f) {
                            @Override
                            void action() {
                                hudText.setCullHint(Spatial.CullHint.Always);
                            }
                        });

                    }
                    break;
                case "delayDown":
                    if (value && isRunning) {

                        glc.setTimeDelay((int) glc.getTimeDelay() / 2);
                        hudText.setText("delay : " + glc.getTimeDelay());
                        if (glc.getTimeDelay() <= 4) {
                            glc.setTimeDelay(8);
                        }
                        hudText.setCullHint(Spatial.CullHint.Never);
                        hudText.addControl(new TimedActionControl(3f) {
                            @Override
                            void action() {
                                hudText.setCullHint(Spatial.CullHint.Always);
                            }
                        });

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

            if (glc.isNight()) {

                glc.getDlsr().setShadowIntensity(0);

            } else {

                glc.getDlsr().setShadowIntensity(0.30f);
            }

            if (playerControl != null) {
                if (!playerControl.isDead()) {
                    if (playerControl.isRotating()) {
                        ch.removeFromParent();
                    }
                    if (playerControl.getChaseCam().getDistanceToTarget() <= playerControl.getChaseCam().getMinDistance()) {

                        if (stateManager.getState(VideoRecorderAppState.class) == null) {
                            if (!playerControl.isRotating()) {
                                localGuiNode.attachChild(ch);
                            } else {
                                ch.removeFromParent();
                            }

                        } else {
                            ch.removeFromParent();
                        }
                    } else {
                        ch.removeFromParent();
                    }

                    health = playerControl.getHealth();
                    Integer i = (int) playerControl.getHealth();

                    if (health <= 100 && health >= 75) {
                        healthText.setColor(ColorRGBA.Green);
                    }
                    if (health < 75 && health >= 50) {
                        healthText.setColor(ColorRGBA.Yellow);
                    }

                    if (health < 50 && health >= 25) {
                        healthText.setColor(ColorRGBA.Orange);
                    }

                    if (health < 25) {
                        healthText.setColor(ColorRGBA.Red);
                        localRootNode.getControl(PosterizationFilterControl.class).setEnabled(true);
                        localRootNode.getControl(PosterizationFilterControl.class).setStrength(1.25f);
                    }

                    healthText.setText(i.toString());
                } else {
                    healthText.setText("dead");
                }
            }

            if (isTimeDemo) {
                playerControl.setRotationModifier(12.f);
                playerControl.setIdleCounter(90);
                if (fps.size() < 500) {
                    hudText2.setText(" timedemo : " + fps.size() + " - 500");
                    fps.add(1 / tpf);
                } else {
                    isTimeDemo = false;
                    playerControl.setRotationModifier(0);
                    playerControl.setIdleCounter(0);
                    for (int i = 1; i < fps.size(); i++) {
                        for (int j = 0; j < fps.size(); j++) {
                            if (fps.get(j) < fps.get(i)) {
                                Float temp = fps.get(i);
                                fps.set(i, fps.get(j));
                                fps.set(j, temp);
                            }
                        }
                    }
                    float t = 0;
                    for (float f : fps) {
                        t += f;
                    }

                    minMaxFps = new Vector2f(fps.get(fps.size() - 1), fps.get(0));

                    hudText2.setText("min : " + (int) minMaxFps.getX() + " max : " + (int) minMaxFps.getY() + " avg : " + (int) (t / fps.size()));
                    System.out.println("finished Timedemo");
                    System.out.println(hudText2.getText());

                    if (hudText2.getControl(TimedActionControl.class) != null) {
                        hudText2.removeControl(TimedActionControl.class);
                    }

                    hudText2.addControl(new TimedActionControl(15) {
                        @Override
                        void action() {
                            if (!isTimeDemo) {
                                if (fps.size() >= 500) {
                                    hudText2.setText("... : ...");
                                }
                            }
                        }
                    });
                }
            }

            /*            if (hudText.getAlpha() >= -2 && hudText.getAlpha() <= 2) {
            hudText.setAlpha(hudText.getAlpha() + (1 / tpf));
            }*/
            if (globalLightningEnabled) {
                if (view2.isEnabled()) {
                    cam2.setLocation(glc.getSunPosition());
                    cam2.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));
                    cam2.lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
                    cam2.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
                }
            }

            if (stateManager.getState(VideoRecorderAppState.class) != null) {
                counter += (tpf * 20); // / glc.getTimeDelay()
            } else {
                counter += tpf; // / glc.getTimeDelay()
            }

            if (counter >= limit) {
                counter = 0;
                int nl = getRandomNumberInRange(15, 45);
                nl = nl + (glc.getTimeDelay() / 500);
                limit = nl;
                int bc = 0;
                for (Spatial child : localRootNode.getChildren()) {
                    if (localRootNode.getChildren() != null) {
                        if (child.getName() != null) {
                            if (child.getName().equals("bird")) {
                                bc += 1;
                            }
                        }
                    }
                }

                if (bc <= birdLimit) {
                    attachBird(getRandomNumberInRange(1, ((birdLimit) - bc) + 1));
                }
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

        setupHudText();
        hudText2.setText("... : ...");
        glc.setEnabled(true);
        playerControl.setEnabled(true);
        sc.setEnabled(true);
        weatherControl.setEnabled(true);
        localRootNode.getControl(PosterizationFilterControl.class).setEnabled(true);
        localRootNode.getControl(PosterizationFilterControl.class).setStrength(1.75f);

        if (shadows) {
            if (!viewPort.getProcessors().contains(glc.getSlsr())) {
                viewPort.addProcessor(glc.getSlsr());
            }
            if (!viewPort.getProcessors().contains(glc.getDlsr())) {
                viewPort.addProcessor(glc.getDlsr());
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
        addListener();
        setupKeys();
        playerControl.setupListener();
        playerControl.setupMappings();
        view2.attachScene(localRootNode);
        if (localRootNode.hasChild(terrainControl.getTerrain())) {
            localRootNode.addControl(enemyControl);
            enemyControl.setEnabled(true);
        }
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        System.out.println("Game State is being detached");
        setIsRunning(false);
        stateDetach();
    }

    public void stateDetach() {

        if (localGuiNode.hasChild(ch)) {
            ch.removeFromParent();
        }

        hudText.removeFromParent();
        hudText2.removeFromParent();
        healthText.removeFromParent();
        hudText2.setText("... : ...");

        amb.stop();
        amb1.stop();
        amb2.stop();

        removeMappings();
        removeListener();

        playerControl.removeMappings();
        playerControl.removeListeners();

        view2.setEnabled(false);
        view2.detachScene(localRootNode);
        localRootNode.getControl(PosterizationFilterControl.class).setEnabled(false);
        playerControl.setEnabled(false);

        sc.setEnabled(false);
        glc.setEnabled(false);

        if (weatherControl != null) {
            weatherControl.setEnabled(false);
        }

        if (viewPort.getProcessors().contains(fpp)) {
            viewPort.removeProcessor(fpp);
        }

        if (viewPort.getProcessors().contains(glc.getDlsr())) {
            viewPort.removeProcessor(glc.getDlsr());
        }
        if (viewPort.getProcessors().contains(glc.getSlsr())) {
            viewPort.removeProcessor(glc.getSlsr());
        }

        if (!waterPostProcessing) {
            simpleWaterControl control = localRootNode.getControl(simpleWaterControl.class);
            if (control != null) {
                if (viewPort.getProcessors().contains(control.getWaterProcessor())) {
                    SimpleWaterProcessor swp = control.getWaterProcessor();
                    if (swp != null) {
                        viewPort.removeProcessor(swp);
                    }
                }
            }
        }
        enemyControl.setEnabled(false);
        localRootNode.removeControl(enemyControl);
        dettachLocalRootNode();
        detachLocalGuiNode();
    }

    private void removeMappings() {

        inputManager.deleteMapping("write");
        inputManager.deleteMapping("treeoutroot");
        inputManager.deleteMapping("debug");
        inputManager.deleteMapping("switchCam");
        inputManager.deleteMapping("delayUp");
        inputManager.deleteMapping("delayDown");
        inputManager.deleteMapping("timeDemo");
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

    public PlayerControl getPlayerCOntrol() {
        return playerControl;
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }

    public AudioNode getAudioNode(String path) {
        return (AudioNode) new AudioNode(assetManager, path, AudioData.DataType.Buffer);
    }
};
