package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainPatch;
import java.io.File;
import java.io.IOException;

public class GameRunningState extends AbstractAppState {

    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final AssetManager assetManager;
    private final Node localRootNode = new Node("Game Screen RootNode");
    private final Node localGuiNode = new Node("Game Screen GuiNode");
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;
    private final ColorRGBA backgroundColor = ColorRGBA.BlackNoAlpha;

    private final PlayerControl playerControl;
    private boolean isRunning = false;

    private final AudioNode bgm = new AudioNode();
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

    private static FilterPostProcessor fpp;

    public GameRunningState(SimpleApplication app, Boolean fogEnabled, Boolean bloomEnabled, Boolean lightScatterEnabled, Boolean anisotropyEnabled, Boolean waterPostProcessing, Boolean shadows, Boolean globalLightningEnabled) {

        System.out.println("Game State is being constructed");

        this.fogEnabled = fogEnabled;
        this.bloomEnabled = bloomEnabled;
        this.lightScatterEnabled = lightScatterEnabled;
        this.anisotropyEnabled = anisotropyEnabled;
        this.waterPostProcessing = waterPostProcessing;
        this.globalLightningEnabled = globalLightningEnabled;
        this.shadows = shadows;

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
        bulletAppState.setDebugEnabled(false);

//      CAMERA        
        this.viewPort.getCamera().setLocation(new Vector3f(0, 8, -10));
        this.viewPort.getCamera().lookAtDirection(Vector3f.ZERO, Vector3f.UNIT_XYZ);

//      TERRAIN
        localRootNode.addControl(new Terrain(assetManager, bulletAppState, localRootNode, viewPort));

//      PLAYER
        playerControl = new PlayerControl(app, bulletAppState, localRootNode);
        Node player = new Node("playerNode");
        player.addControl(playerControl);
        localRootNode.attachChild(player);

//      SUN
        Node sunNode = new Node("sunNode");
        glc = new GlobalLightingControl(viewPort, assetManager, playerControl.getLamp(), localRootNode);
        sunNode.addControl(glc);
        localRootNode.attachChild(sunNode);
        glc.setGlobalLightning(globalLightningEnabled);

//      SKY
        localRootNode.addControl(new SkyControl(assetManager, glc, localRootNode));

//      Bloom
        if (bloomEnabled) {
            if (localRootNode.getControl(BloomPostFilter.class) == null) {
                localRootNode.addControl(new BloomPostFilter(fpp));
            }
        }

//      LightScatter
        if (lightScatterEnabled) {

            if (localRootNode.getControl(LightScatterFilter.class) == null) {
                localRootNode.addControl(new LightScatterFilter(fpp, glc));
            }
        }

//      FOG
        if (fogEnabled) {
            if (localRootNode.getControl(FogPostFilter.class) == null) {
                localRootNode.addControl(new FogPostFilter(fpp));
            }
        }

//      WATER
        if (localRootNode.getControl(WaterPostFilter.class) == null) {
            localRootNode.addControl(new WaterPostFilter(fpp, true, true, true, true, true, true, true));
        }

//      BGM
        // Node bgmNode = (Node) localRootNode.getChild("terrain");
        //  bgm = (AudioNode) bgmNode.getChild("AudioNode");

        /*        bgm.setVolume(bgm.getVolume() / bgmVolume);
        if (bgmOn == false) {
        bgm.setVolume(0);
        }
        bgm.setLooping(bgmOn);*/
//      TEST GUI TEXT
        displayText("Game running",
                new Vector2f(10, 20),
                1.8f,
                ColorRGBA.Blue,
                4f);

//      ANISOTROPY
        if (anisotropyEnabled) {
            localRootNode.addControl(new AnisotropyControl(assetManager, 2));
        }

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
    }

    public Node getLocalRoot() {
        return localRootNode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        super.initialize(stateManager, app);

        System.out.println("Game State is being initialized");

        viewPort.setBackgroundColor(backgroundColor);
        inputManager.setCursorVisible(false);
    }

    private void displayText(String txt, Vector2f pos, float size, ColorRGBA color, float lifetime) {

        BitmapFont guiFont = assetManager.loadFont(
                "Interface/Fonts/Default.fnt");
        BitmapText displaytext = new BitmapText(guiFont);

        TimedActionControl tc = new TimedActionControl(lifetime) {
            @Override
            void action() {
                this.spatial.removeFromParent();
            }
        };

        displaytext.setText(txt);
        displaytext.setColor(color);
        displaytext.setSize(guiFont.getCharSet().getRenderedSize() * size);
        displaytext.move(pos.x, displaytext.getLineHeight() + pos.y, 0);
        localGuiNode.attachChild(displaytext);
        displaytext.addControl(tc);
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
    }

    private void treeoutroot(Node node) {

        int n = node.getQuantity();

        String name = node.getName();

        if (n > 0) {
            System.out.println("+ " + name + " " + n);
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

                case "treeoutroot":
                    if (value && isRunning) {
                        treeoutroot(localRootNode);
                    }
                    break;

                case "debug":
                    if (value && isRunning) {
                        bulletAppState.setDebugEnabled(!bulletAppState.isDebugEnabled());
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
        }
    }

    public void attachLocalRootNode() {
        if (!rootNode.hasChild(localRootNode)) {
            rootNode.attachChild(localRootNode);
        }
    }

    public void attachLocalGuiNode() {
        if (!guiNode.hasChild(localGuiNode)) {
            guiNode.attachChild(localGuiNode);
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        System.out.println("Game State is being attached");
        playerControl.setEnabled(true);
        //bgm.play();

        if (waterPostProcessing) {
            if (localRootNode.getControl(WaterPostFilter.class) != null) {
                //localRootNode.getControl(WaterPostFilter.class).start();
            }
        }

        if (shadows) {
            if (!viewPort.getProcessors().contains(glc.getSlsr())) {
                viewPort.addProcessor(glc.getSlsr());
            }
        }

        if (!viewPort.getProcessors().contains(fpp)) {
            viewPort.addProcessor(fpp);
        }

        attachLocalGuiNode();
        attachLocalRootNode();

        setIsRunning(true);

    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        System.out.println("Game State is being detached");
        //localRootNode.getControl(WaterPostFilter.class).stop();
        playerControl.setEnabled(false);
        //bgm.stop();

        if (viewPort.getProcessors().contains(fpp)) {
            viewPort.removeProcessor(fpp);
        }

        if (shadows) {
            if (viewPort.getProcessors().contains(glc.getSlsr())) {
                viewPort.removeProcessor(glc.getSlsr());
            }
        }

        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
        setIsRunning(false);
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

}
