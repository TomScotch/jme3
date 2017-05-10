package mygame;

import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SafeArrayList;

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

    private final playerControl playerControl;
    private boolean isRunning = false;

    private final AudioNode bgm;
    private boolean bgmOn = false;
    private int bgmVolume = 8;
    private int anisotrpy_samples = 2;
    private final GlobalLightingControl glc;
    private SafeArrayList<SceneProcessor> processors;

    private boolean bloomEnabled;
    private boolean fogEnabled;
    private boolean lightScatterEnabled;
    private boolean anisotropyEnabled;
    private boolean waterPostProcessing;

    public GameRunningState(SimpleApplication app) {

        System.out.println("Game State is being constructed");

        fogEnabled = false;
        bloomEnabled = false;
        lightScatterEnabled = false;
        anisotropyEnabled = false;
        waterPostProcessing = true;

//      CONSTRUKTOR
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();

//      PHYSICS STATE
        bulletAppState = new BulletAppState();
        app.getStateManager().attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

//      CAMERA        
        this.viewPort.getCamera().setLocation(new Vector3f(0, 8, -10));
        this.viewPort.getCamera().lookAtDirection(Vector3f.ZERO, Vector3f.UNIT_XYZ);

//      PLAYER
        playerControl = new playerControl(app, bulletAppState, localRootNode);
        Node player = new Node("playerNode");
        player.addControl(playerControl);
        localRootNode.attachChild(player);

//      SUN
        Node sunNode = new Node("sunNode");
        glc = new GlobalLightingControl(viewPort, assetManager, localRootNode);
        sunNode.addControl(glc);
        localRootNode.attachChild(sunNode);

//      LightScatter
        if (lightScatterEnabled) {
            localRootNode.addControl(new LightScatterFilter(viewPort, assetManager, glc));
        }

//      SKY
        localRootNode.addControl(new SkyControl(assetManager, glc));

//      FOG
        if (fogEnabled) {
            localRootNode.addControl(new FogPostFilter(assetManager, viewPort));
        }

//      WATER
        localRootNode.addControl(new WaterPostFilter(assetManager, viewPort, glc));

//      Bloom
        if (bloomEnabled) {
            localRootNode.addControl(new BloomPostFilter(assetManager, viewPort));
        }

//      TERRAIN
        localRootNode.addControl(new Terrain(assetManager, bulletAppState, localRootNode));

//      BGM
        Node bgmNode = (Node) localRootNode.getChild("terrain");
        bgm = (AudioNode) bgmNode.getChild("AudioNode");
        bgm.setVolume(bgm.getVolume() / bgmVolume);
        if (bgmOn == false) {
            bgm.setVolume(0);
        }
        bgm.setLooping(bgmOn);

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

        addHostile("Models/hostile/Demon/demon.j3o", new Vector3f(-12, 0, 12));
        addHostile("Models/hostile/forestmonster/forest-monster.j3o", new Vector3f(12, 0, 12));
        addHostile("Models/hostile/Spider/spider.j3o", new Vector3f(12, 0, -12));
    }

    protected final void addHostile(String name, Vector3f pos) {
        //      HOSTILE
        Node enemyNode = new Node();
        Spatial hostile = assetManager.loadModel(name);
        enemyNode.attachChild(hostile);
        enemyNode.setName(name);
        hostile.setName(name);
        //hostile.scale(3.75f);
        //hostile.setLocalTranslation(pos);
        EntityControl npcCon = new EntityControl();
        hostile.addControl(npcCon);
        hostile.setShadowMode(RenderQueue.ShadowMode.Cast);
        BetterCharacterControl bcc = new BetterCharacterControl(3, 7, 3);
        bcc.setSpatial(hostile);
        hostile.addControl(bcc);
        localRootNode.attachChild(enemyNode);
        bulletAppState.getPhysicsSpace().add(bcc);
        bcc.warp(new Vector3f(pos));
        npcCon.setAnim("Idle", LoopMode.Loop);
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

        inputManager.addMapping("treeoutroot",
                new KeyTrigger(KeyInput.KEY_O));

        inputManager.addListener(actionListener, "treeoutroot");

        inputManager.addMapping("debug",
                new KeyTrigger(KeyInput.KEY_Q));

        inputManager.addListener(actionListener, "debug");
    }

    private void treeoutroot(Node node) {
        int n = node.getQuantity();
        if (n > 0) {
            System.out.println("+" + node.getName() + " " + n);
        } else {
            System.out.println("+" + node.getName());
        }
        node.getChildren().stream().filter((spat) -> (spat.getClass() == Node.class | spat.getClass() == Spatial.class)).map((spat) -> {
            int c = spat.getParent().getChildIndex(spat);
            if (c > 0) {
                System.out.println(c + "-" + spat.getName());
            } else {
                System.out.println("-" + spat.getName());
            }
            return spat;
        }).map((spat) -> (Node) spat).forEachOrdered((children) -> {
            children.getChildren().stream().map((child) -> (Node) child).forEachOrdered((spn) -> {
                treeoutroot(spn);
            });
        });

    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {

            switch (binding) {

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
        bgm.play();

        if (waterPostProcessing) {
            localRootNode.getControl(WaterPostFilter.class).start();
        }
        attachLocalGuiNode();
        attachLocalRootNode();
        setupKeys();
        setIsRunning(true);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        System.out.println("Game State is being detached");
        localRootNode.getControl(WaterPostFilter.class).stop();
        playerControl.setEnabled(false);
        bgm.stop();
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
