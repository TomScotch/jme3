package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
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
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
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
    private final ColorRGBA backgroundColor = ColorRGBA.BlackNoAlpha;
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;
    private boolean isRunning = false;
    private final Spatial model;
    private FilterPostProcessor processor;
    private final DirectionalLight sun;
    private final Spatial terrain;
    private BetterCharacterControl physicsCharacter;
    private final Node characterNode;
    boolean rotate = false;
    private final ChaseCamera chaseCam;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 0);
    boolean leftRotate = false, rightRotate = false, leftStrafe = false, rightStrafe = false, forward = false, backward = false;
    private final static Node pivot = new Node();
    private boolean chaseEnabled = true;
    private final float move_speed = 0.1f;
    private final float strafe_speed = 0.1f;
    private final float jump_Speed = 25f;
    private final int shadowmapSize = 512;
    private final float rotationSpeed = 0.005f;
    private final CameraNode camNode;
    private final AudioNode bgm;
    private boolean attacking = false;
    private float attackTimer = 0f;
    private final float attackTime = 1.5f;
    private String collisionTarget = "";
    private final float playerDmg = 30f;
    private final boolean bgmOn = false;
    private final int timeDelay = 240;
    private final float gravity = 50;
    private final float playerMass = 2.5f;
    private final float chaseCamRotationSpeed = 0.5f;
    private final int bgmVolume = 8;
    private final SpotLight lamp;
    private final GhostControl ghostControl;

    private final int anisotrpy_samples = 4;

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
        characterNode = new Node("player");
        model = assetManager.loadModel("Models/npc/knight.j3o");
        model.scale(0.45f);
        characterNode.attachChild(model);
        model.setShadowMode(RenderQueue.ShadowMode.Cast);
        model.setLocalTranslation(0, 4.15f, 0);
        physicsCharacter = new BetterCharacterControl(3, 6, playerMass);
        physicsCharacter.warp(new Vector3f(0, 6, 0));
        physicsCharacter.setJumpForce(new Vector3f(0, jump_Speed, 0));
        physicsCharacter.setGravity(new Vector3f(0, gravity, 0));
        characterNode.addControl(physicsCharacter);
        physicsCharacter.setSpatial(characterNode);
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        Node ghostNode = new Node("PlayerGhostNode");
        ghostControl = new GhostControl(new BoxCollisionShape(new Vector3f(2f, 0.1f, 3f)));
        bulletAppState.getPhysicsSpace().add(ghostControl);
        ghostNode.addControl(ghostControl);
        characterNode.attachChild(ghostNode);
        ghostNode.setLocalTranslation(0, 3, 5);
        localRootNode.attachChild(characterNode);

        doAnim("player", "Idle", LoopMode.Loop);

//      SUN
        sun = new DirectionalLight();
        sun.setDirection(model.getWorldTranslation());
        sun.setColor(ColorRGBA.Orange);
        localRootNode.addLight(sun);

//      FLASHLIGHT        
        lamp = new SpotLight();
        lamp.setSpotRange(50);
        lamp.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD);
        lamp.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD);
        lamp.setColor(ColorRGBA.White.mult(flashLightStrength));
        lamp.setEnabled(false);
        localRootNode.addLight(lamp);

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

//      ChaseCamera
        chaseCam = new ChaseCamera(app.getCamera(), characterNode, inputManager);
        chaseCam.setChasingSensitivity(1);
        chaseCam.setTrailingEnabled(false);
        chaseCam.setSmoothMotion(false);
        chaseCam.setDefaultDistance(7.5f);
        chaseCam.setMinDistance(3f);
        chaseCam.setLookAtOffset(new Vector3f(0, 5f, 0));
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setRotationSpeed(chaseCamRotationSpeed);
        chaseCam.setDownRotateOnCloseViewOnly(true);
        chaseCam.setMaxVerticalRotation(FastMath.QUARTER_PI);
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        camNode = new CameraNode("Camera Node", app.getCamera());
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        characterNode.attachChild(camNode);
        camNode.setLocalTranslation(new Vector3f(0, 4.4f, -18f));
        camNode.lookAt(characterNode.getLocalTranslation(), Vector3f.UNIT_Y);
        camNode.setEnabled(false);

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
//      HOSTILE
        Node priestNode = new Node();
        Spatial hostile = assetManager.loadModel("Models/hostile/Demon/demon.j3o");
        priestNode.attachChild(hostile);
        priestNode.setName("demon");
        hostile.setName("demon");
        //hostile.scale(3.75f);
        hostile.setLocalTranslation(0, -0.2f, 0);
        EntityControl npcCon = new EntityControl();
        hostile.addControl(npcCon);
        hostile.setShadowMode(RenderQueue.ShadowMode.Cast);
        BetterCharacterControl priestControl = new BetterCharacterControl(3, 6, 100);
        priestNode.addControl(priestControl);
        priestControl.setSpatial(priestNode);
        localRootNode.attachChild(priestNode);
        bulletAppState.getPhysicsSpace().add(priestControl);
        priestControl.warp(new Vector3f(5, 5, 5));
        doAnim("demon", "IdleHeadTilt", LoopMode.Loop);
    }
    private final float flashLightStrength = 1.3f;

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
        inputManager.addMapping("flashlight",
                new KeyTrigger(KeyInput.KEY_F));
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
        inputManager.addListener(actionListener, "Jump", "Shoot", "flashlight");
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {

            switch (binding) {
                case "flashlight":
                    if (value) {
                        lamp.setEnabled(!lamp.isEnabled());
                    }
                    break;
                case "rightRotate":
                    rightRotate = value;
                    break;
                case "leftRotate":
                    leftRotate = value;
                    break;
                case "Shoot":
                    attacking = value;
                    break;
                case "Strafe Left":
                    leftStrafe = value;
                    if (value) {
                        doAnim("player", "Walk", LoopMode.Loop);
                    } else {
                        doAnim("player", "Idle", LoopMode.Loop);
                    }
                    break;
                case "Strafe Right":
                    rightStrafe = value;
                    if (value) {
                        doAnim("player", "Walk", LoopMode.Loop);
                    } else {
                        doAnim("player", "Idle", LoopMode.Loop);
                    }
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
                        doAnim("player", "Walk", LoopMode.Loop);
                    } else {
                        doAnim("player", "Idle", LoopMode.Loop);
                    }
                    forward = value;
                    break;
                case "Walk Backward":
                    if (value) {
                        doAnim("player", "Walk", LoopMode.Loop);
                    } else {
                        doAnim("player", "Idle", LoopMode.Loop);
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

    private void attack() {

        if (attackTimer <= 0) {

            if (!collisionTarget.equals("")) {
                hit(collisionTarget);
            }
            doAnim("player", "Attack", LoopMode.DontLoop);
            attackTimer = attackTime;
        }
    }

    @Override
    public void update(float tpf) {

        if (getIsRunning()) {

            super.update(tpf);

            if (ghostControl.getOverlappingObjects().size() > 1) {
                Node overlap = (Node) ghostControl.getOverlapping(1).getUserObject();
                if (!overlap.getName().equals("terrain") && !overlap.getName().equals("")) {
                    collisionTarget = overlap.getName();
                }
            } else {
                collisionTarget = "";
            }

            if (attacking) {
                attack();
            }

            checkIdleforPlayer();

            if (attackTimer > 0) {
                attackTimer -= 1 * tpf;
            }

            if (model.getWorldTranslation().y < -35) {
                physicsCharacter.warp(new Vector3f(5, 5, 5));
            }

            pivot.rotate((FastMath.QUARTER_PI * tpf) / timeDelay, 0, 0);
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

        lamp.setPosition(characterNode.getLocalTranslation());
        lamp.getPosition().addLocal(0, 3, 0);
        lamp.setDirection(viewPort.getCamera().getDirection());

        if (chaseCam.getDistanceToTarget() <= chaseCam.getMinDistance()) {
            model.setCullHint(Spatial.CullHint.Always);
        } else {
            model.setCullHint(Spatial.CullHint.Dynamic);
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        System.out.println("Game State is being attached");
        bgm.play();
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        setupKeys();
        setIsRunning(true);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        System.out.println("Game State is being detached");
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

    private void hit(String name) {
        SceneGraphVisitor visitor = (Spatial spat) -> {
            if (spat.getName().equals(name)) {
                Node n = (Node) spat;
                Spatial child = n.getChild(name);
                if (child != null) {
                    child.getControl(EntityControl.class).hit(playerDmg, "player");
                }
            }
        };
        localRootNode.depthFirstTraversal(visitor);
    }

    private void doAnim(String name, String animation, LoopMode lop) {

        SceneGraphVisitor visitor = (Spatial spat) -> {
            if (spat.getName().equals(name)) {
                Node n = (Node) spat;
                Node n1 = (Node) n.getChild("anim");
                AnimControl aniCon = n1.getControl(AnimControl.class);
                if (aniCon.getClass() != null) {
                    aniCon.clearChannels();
                    aniCon.createChannel();
                    aniCon.getChannel(0).setAnim(animation);
                    aniCon.getChannel(0).setLoopMode(lop);
                } else {
                    aniCon.createChannel();
                    aniCon.getChannel(0).setAnim(animation);
                    aniCon.getChannel(0).setLoopMode(lop);
                }
            }
        };
        localRootNode.depthFirstTraversal(visitor);
    }

    private void checkIdleforPlayer() {

        Node n = (Node) model;
        Node n1 = (Node) n.getChild("anim");
        AnimControl aniCon = n1.getControl(AnimControl.class);
        if (aniCon.getChannel(0).getAnimationName().equals("Attack")) {
            if (aniCon.getChannel(0).getTime() == aniCon.getChannel(0).getAnimMaxTime()) {
                aniCon.clearChannels();
                aniCon.createChannel();
                aniCon.getChannel(0).setAnim("Idle");
                aniCon.getChannel(0).setLoopMode(LoopMode.Loop);
            }
        }
    }
}
