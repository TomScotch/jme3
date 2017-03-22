package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class GameRunningState extends AbstractAppState implements AnimEventListener {

    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final AssetManager assetManager;
    private Node localRootNode = new Node("Game Screen RootNode");
    private final Node localGuiNode = new Node("Game Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.BlackNoAlpha;
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;
    private boolean isRunning = false;
    private final Spatial model;
    private FilterPostProcessor processor;
    private final DirectionalLight sun;
    private final Spatial terrain;
    private AnimChannel channel;
    private final AnimControl control;
    private CharacterControl physicsCharacter;
    private final Node characterNode;
    boolean rotate = false;
    private final ChaseCamera chaseCam;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 0);
    boolean leftRotate = false, rightRotate = false, leftStrafe = false, rightStrafe = false, forward = false, backward = false;

    private boolean chaseEnabled = true;

    private final float move_speed = 3.5f;
    private final float strafe_speed = 10f;
    private final float jump_Speed = 30f;

    private final int shadowmapSize = 512;
    private final int anisotrpy_samples = 8;
    private final float rotationSpeed = 2.5f;
    private final CameraNode camNode;

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

//      PLAYER MODEL
        model = assetManager.loadModel("Models/girl/girl.j3o");
        model.setShadowMode(RenderQueue.ShadowMode.Cast);
        physicsCharacter = new CharacterControl(new CapsuleCollisionShape(0.75f, 3.5f), 0.1f);//
        physicsCharacter.setJumpSpeed(jump_Speed);
        physicsCharacter.setMaxSlope(0);
        model.setLocalTranslation(0, -0.5f, 0);
        characterNode = new Node("character node");
        characterNode.setLocalTranslation(0, 5, 0);
        characterNode.addControl(physicsCharacter);
        physicsCharacter.setSpatial(characterNode);
        physicsCharacter.setUseViewDirection(true);
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        localRootNode.attachChild(characterNode);
        characterNode.attachChild(model);

//      SUN
        sun = new DirectionalLight();
        sun.setDirection(model.getWorldTranslation());
        localRootNode.addLight(sun);

//      TERRAIN
        terrain = assetManager.loadModel("Scenes/terrain.j3o");
        terrain.setLocalTranslation(0, 1.45f, 0);
        terrain.addControl(new RigidBodyControl(0));
        terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        bulletAppState.getPhysicsSpace().addAll(terrain);
        localRootNode.attachChild(terrain);

//      ChaseCamera
        chaseCam = new ChaseCamera(app.getCamera(), characterNode, inputManager);
        chaseCam.setChasingSensitivity(1);
        chaseCam.setTrailingEnabled(false);
        chaseCam.setSmoothMotion(false);
        chaseCam.setDefaultDistance(7.5f);
        chaseCam.setLookAtOffset(new Vector3f(0, 3.2f, 0));
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setRotationSpeed(0.5f);
        chaseCam.setDownRotateOnCloseViewOnly(true);
        chaseCam.setMaxVerticalRotation(FastMath.QUARTER_PI);
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        camNode = new CameraNode("Camera Node", app.getCamera());
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        characterNode.attachChild(camNode);
        camNode.setLocalTranslation(new Vector3f(0, 7.5f, -10));
        camNode.lookAt(characterNode.getLocalTranslation(), Vector3f.UNIT_Y);
        camNode.setEnabled(false);

//      TEST GUI TEXT
        loadHintText("Game running", "gametext");

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

//      ANIMATION CHANNEL AND CONTROL
        Node n = (Node) model;
        Node n1 = (Node) n.getChild("player");

        control = n1.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
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

    private void loadHintText(String txt, String name) {

        BitmapFont guiFont = assetManager.loadFont(
                "Interface/Fonts/Default.fnt");
        BitmapText displaytext = new BitmapText(guiFont);
        displaytext.setName(name);
        displaytext.setSize(guiFont.getCharSet().getRenderedSize());
        displaytext.move(10, displaytext.getLineHeight() + 20, 0);
        displaytext.setText(txt);
        localGuiNode.attachChild(displaytext);
    }

    private void setupKeys() {

        inputManager.addMapping("leftRotate",
                new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("rightRotate",
                new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("Strafe Left",
                new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Strafe Right",
                new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("debug",
                new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("chase",
                new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addListener(actionListener, "leftRotate", "rightRotate");
        inputManager.addListener(actionListener, "Strafe Left", "Strafe Right");
        inputManager.addListener(actionListener, "Rotate Left", "Rotate Right");
        inputManager.addListener(actionListener, "Walk Forward", "Walk Backward");
        inputManager.addListener(actionListener, "debug", "chase");
        inputManager.addListener(actionListener, "Jump", "Shoot");
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {

            switch (binding) {
                case "rightRotate":
                    rightRotate = value;
                    break;
                case "leftRotate":
                    leftRotate = value;
                    break;
                case "Shoot":
                    localRootNode.depthFirstTraversal(visitor);
                    break;
                case "Strafe Left":
                    leftStrafe = value;
                    break;
                case "Strafe Right":
                    rightStrafe = value;
                    break;
                case "chase":
                    if (value) {
                        camNode.setEnabled(!camNode.isEnabled());
                        chaseCam.setEnabled(!chaseCam.isEnabled());
                        chaseEnabled = !chaseEnabled;
                        chaseCam.setDragToRotate(!chaseCam.isDragToRotate());
                    }
                    break;
                case "debug":
                    if (value) {
                        bulletAppState.setDebugEnabled(!bulletAppState.isDebugEnabled());
                    }
                    break;
                case "Walk Forward":
                    if (value) {
                        channel.setAnim("cammina");
                        channel.setLoopMode(LoopMode.Loop);
                    } else {
                        channel.setAnim("cammina");
                        channel.setLoopMode(LoopMode.DontLoop);
                        channel.setTime(0);
                    }
                    forward = value;
                    break;
                case "Walk Backward":
                    if (value) {
                        channel.setAnim("cammina");
                        channel.setLoopMode(LoopMode.Loop);
                    } else {
                        channel.setAnim("cammina");
                        channel.setLoopMode(LoopMode.DontLoop);
                        channel.setTime(0);
                    }
                    backward = value;
                    break;
                case "Jump":
                    physicsCharacter.jump();
                    break;
                default:
                    break;
            }

        }
    };
    Node pivot = new Node();
    int c = 0;

    @Override
    public void update(float tpf) {

        if (getIsRunning()) {

            super.update(tpf);

            if (c++ >= 90) {
                c = 0;
                localGuiNode.detachChildNamed("gametext");
                localGuiNode.detachChildNamed("visitor");
            }

            pivot.rotate((FastMath.QUARTER_PI * tpf) / 15, 0, 0);
            sun.setDirection(pivot.getLocalRotation().getRotationColumn(2));

            Vector3f camDir = viewPort.getCamera().getDirection().normalizeLocal();
            Vector3f camLeft = viewPort.getCamera().getLeft().divide(strafe_speed);

            camDir.y = 0;
            camLeft.y = 0;

            if (!chaseEnabled) {
                if (rightRotate) {
                    viewDirection.addLocal(camLeft.mult(rotationSpeed).negate());
                }
                if (leftRotate) {
                    viewDirection.addLocal(camLeft.mult(rotationSpeed));
                }
            } else {
                viewDirection.set(camDir);
            }

            physicsCharacter.setViewDirection(viewDirection);

            walkDirection.set(0, 0, 0);

            if (leftStrafe) {
                walkDirection.addLocal(camLeft);
            } else if (rightStrafe) {
                walkDirection.addLocal(camLeft.negate());
            }

            if (forward) {
                walkDirection.addLocal(model.getWorldRotation().getRotationColumn(2).normalizeLocal().divide(move_speed));
            } else if (backward) {
                walkDirection.addLocal(model.getWorldRotation().getRotationColumn(2).normalize().negate().divide(move_speed));
            }

            physicsCharacter.setWalkDirection(walkDirection);

        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        System.out.println("Game State is being attached");
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        setupKeys();
        setIsRunning(true);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        System.out.println("Game State is being detached");
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

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        //control.getSkeleton().resetAndUpdate();
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        //channel.reset(false);
    }

    SceneGraphVisitor visitor = (Spatial spat) -> {
        if (spat.getControl(AnimControl.class) != null) {
            try {
                loadHintText(spat.getName(), "visitor");
            } catch (Exception err) {
                System.err.println(err);
            }
        }
    };
}
