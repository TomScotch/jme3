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
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.TextureKey;

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
    private final DirectionalLight sun;
    private final Spatial terrain;
    private final static Node pivot = new Node();
    private final playerControl playerControl;
    private boolean isRunning = false;
    private FilterPostProcessor processor;
    //
    private final int shadowmapSize = 512;
    private final AudioNode bgm;
    private final boolean bgmOn = false;
    private final int timeDelay = 12;
    private final int bgmVolume = 8;
    private final int anisotrpy_samples = 4;
    private final boolean globalLightning = true;

    public GameRunningState(SimpleApplication app) {

        System.out.println("Game State is being constructed");

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

//      SKYBOX
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        sky.setLocalTranslation(0, -1000, 0);
        localRootNode.attachChild(sky);

        this.viewPort.getCamera().setLocation(new Vector3f(0, 8, -10));
        this.viewPort.getCamera().lookAtDirection(Vector3f.ZERO, Vector3f.UNIT_XYZ);
//      PLAYER
        playerControl = new playerControl(app, bulletAppState, localRootNode);
        Node player = new Node("playerNode");
        player.addControl(playerControl);
        localRootNode.attachChild(player);

//      SUN
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0, 0, 0));
        sun.setColor(ColorRGBA.White);
        localRootNode.addLight(sun);

//      TERRAIN
        terrain = assetManager.loadModel("Scenes/terrain.j3o");
        terrain.setLocalTranslation(0, 1.45f, 0);
        terrain.addControl(new RigidBodyControl(0));
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        bulletAppState.getPhysicsSpace().addAll(terrain);
        localRootNode.attachChild(terrain);

//      BGM
        Node bgmNode = (Node) terrain;
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
                9f);

//      LIGHT AND SHADOWS
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmapSize, 1);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

//      ANISOTROPY
        AssetEventListener asl = new AssetEventListener() {
            @Override
            public void assetRequested(AssetKey key) {
                if (key.getExtension().equals("png") || key.getExtension().equals("jpg") || key.getExtension().equals("dds")) {
                    TextureKey tkey = (TextureKey) key;
                    tkey.setAnisotropy(anisotrpy_samples);
                }
            }

            @Override
            public void assetLoaded(AssetKey key) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        assetManager.addAssetEventListener(asl);

        addHostile();
    }

    private void addHostile() {
        //      HOSTILE
        Node enemyNode = new Node();
        Spatial hostile = assetManager.loadModel("Models/hostile/Demon/demon.j3o");
        enemyNode.attachChild(hostile);
        enemyNode.setName("demon");
        hostile.setName("demon");
        //hostile.scale(3.75f);
        hostile.setLocalTranslation(0, -0.2f, 0);
        EntityControl npcCon = new EntityControl();
        hostile.addControl(npcCon);
        hostile.setShadowMode(RenderQueue.ShadowMode.Cast);
        BetterCharacterControl priestControl = new BetterCharacterControl(3, 6, 100);
        enemyNode.addControl(priestControl);
        //priestControl.setSpatial(priestNode);
        localRootNode.attachChild(enemyNode);
        bulletAppState.getPhysicsSpace().add(priestControl);
        priestControl.warp(new Vector3f(5, 5, 5));
        npcCon.setAnim("IdleHeadTilt", LoopMode.Loop);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        System.out.println("Game State is being initialized");
        viewPort.setBackgroundColor(backgroundColor);
        processor = (FilterPostProcessor) assetManager.loadAsset("Filters/myFilter.j3f");
        viewPort.addProcessor(processor);
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

        inputManager.addMapping("debug",
                new KeyTrigger(KeyInput.KEY_Q));

        inputManager.addListener(actionListener, "debug");
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {

            switch (binding) {

                case "debug":
                    if (value) {
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

            if (globalLightning) {

                pivot.rotate((FastMath.QUARTER_PI * tpf) / timeDelay, 0, 0);
                sun.setDirection(pivot.getLocalRotation().getRotationColumn(2));
                float z = pivot.getLocalRotation().getRotationColumn(2).getZ();

                if (z > 0.75f) {
                    System.out.println("Morning");
                    sun.setEnabled(true);
                    sun.setColor(ColorRGBA.White);
                    terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
                }
                if (z < 0.75f && z > 0.50f) {
                    System.out.println("Late Morning");
                    sun.setColor(ColorRGBA.LightGray);
                }
                if (z < 0.5f && z > 0.25f) {
                    System.out.println("Noon");
                    sun.setColor(ColorRGBA.Gray);
                }
                if (z < 0.25f && z > 0.1f) {
                    System.out.println("Early Evening");
                    sun.setColor(ColorRGBA.DarkGray);
                }
                if (z < 0.1f) {
                    System.out.println("Evening");
                    sun.setEnabled(false);
                    terrain.setShadowMode(RenderQueue.ShadowMode.Off);
                }
            }
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        System.out.println("Game State is being attached");
        playerControl.setEnabled(true);
        bgm.play();
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        setupKeys();
        setIsRunning(true);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        System.out.println("Game State is being detached");
        playerControl.setEnabled(false);
        bgm.stop();
        viewPort.removeProcessor(processor);
        inputManager.removeListener(actionListener);
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
