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
    private final boolean bgmOn = false;
    private final int bgmVolume = 8;
    private final int anisotrpy_samples = 2;
    private final GlobalLightingControl glc;
    private SafeArrayList<SceneProcessor> processors;

    private final boolean bloomEnabled;
    private final boolean fogEnabled;
    private final boolean lightScatterEnabled;
    private final boolean anisotropyEnabled;

    public GameRunningState(SimpleApplication app) {

        System.out.println("Game State is being constructed");

        fogEnabled = false;
        bloomEnabled = true;
        lightScatterEnabled = true;
        anisotropyEnabled = true;

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

//      SKY
        localRootNode.addControl(new SkyControl(assetManager, glc));

//      LightScatter
        if (lightScatterEnabled) {
            localRootNode.addControl(new LightScatterFilter(viewPort, assetManager, glc));
        }

//      FOG
        if (fogEnabled) {
            localRootNode.addControl(new FogPostFilter(assetManager, viewPort));
        }
//      Bloom
        if (bloomEnabled) {
            localRootNode.addControl(new BloomPostFilter(assetManager, viewPort));
        }

//      WATER
        localRootNode.addControl(new WaterPostFilter(assetManager, viewPort, glc));

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

        setupKeys();
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
        for (Spatial spat : node.getChildren()) {

        }

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

        if (getIsRunning()) {

            super.update(tpf);

        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        System.out.println("Game State is being attached");
        playerControl.setEnabled(true);
        bgm.play();
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        localRootNode.getControl(WaterPostFilter.class).start();
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

}
