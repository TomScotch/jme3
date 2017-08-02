package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.LightControl;
import com.jme3.system.JmeContext;

public class PlayerControl extends AbstractControl {

    private final SkeletonControl skelCon;
    private final AudioNode footsteps;
    // private final AudioNode hit;

    private final ViewPort viewPort;
    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;
    private boolean chaseEnabled = true;
    private final float jump_Speed = 750;
    private final CameraNode camNode;
    private final float gravity = 0;
    private final float playerMass = 175;
    private final float chaseCamRotationSpeed = 0.375f;
    private final SpotLight lamp;

    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 0);
    private final float move_speed = 0.1f;
    private final float strafe_speed = 0.125f;
    private final float rotationSpeed = 0.005f;
    private boolean attacking = false;
    private float attackTimer = 0f;
    private final float attackTime = 1.5f;
    private final float playerDmg = 30f;
    private final Spatial model;
    private BetterCharacterControl physicsCharacter;
    private final Node characterNode;
    boolean rotate = false;
    private final ChaseCamera chaseCam;
    private final float flashLightStrength = 1.25f;
    private final float flashLightSpotRange = 33;
    private final int outerLamp = 30;
    private final int innerLamp = 15;

    boolean leftRotate = false, rightRotate = false, leftStrafe = false, rightStrafe = false, forward = false, backward = false;
    private final Node localRootNode;
    private final AnimControl aniCon;
    private final float scale = 0.45f;
    private final int maxDistance = 30;
    private boolean rotateAround = false;
    private float idleCounter = 0;
    private final JmeContext context;
    private final float idleTimeOutValue = 90f;

    public PlayerControl(SimpleApplication app, BulletAppState bulletState, Node localRootNode) {

        this.context = app.getContext();
        this.viewPort = app.getViewPort();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.bulletAppState = bulletState;
        this.localRootNode = localRootNode;

        characterNode = new Node("player");
        chaseCam = new ChaseCamera(app.getCamera(), characterNode, inputManager);
        chaseCam.setChasingSensitivity(1);
        chaseCam.setTrailingEnabled(false);
        chaseCam.setSmoothMotion(false);
        chaseCam.setMinDistance(3f);
        chaseCam.setLookAtOffset(new Vector3f(0, 5f, 0));
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setRotationSpeed(chaseCamRotationSpeed);
        chaseCam.setDownRotateOnCloseViewOnly(true);
        chaseCam.setMaxVerticalRotation(FastMath.QUARTER_PI);
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        chaseCam.setMaxDistance(maxDistance);
        chaseCam.setDefaultDistance(chaseCam.getMaxDistance() / 2);
        chaseCam.setDefaultVerticalRotation(FastMath.INV_PI - FastMath.ONE_THIRD);
        camNode = new CameraNode("Camera Node", app.getCamera());
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        characterNode.attachChild(camNode);
        camNode.setLocalTranslation(new Vector3f(0, 4.4f, 0)); //-18f
        camNode.setEnabled(false);
        lamp = new SpotLight();
        lamp.setSpotRange(flashLightSpotRange);
        lamp.setSpotInnerAngle(innerLamp * FastMath.DEG_TO_RAD);
        lamp.setSpotOuterAngle(outerLamp * FastMath.DEG_TO_RAD);
        lamp.setColor(ColorRGBA.Orange.mult(flashLightStrength));
        lamp.setFrustumCheckNeeded(true);
        lamp.setIntersectsFrustum(true);
        lamp.setEnabled(false);
        this.localRootNode.addLight(lamp);
        LightControl lc = new LightControl(lamp, LightControl.ControlDirection.SpatialToLight);
        Node lightNode = new Node("");
        lightNode.addControl(lc);
        characterNode.attachChild(lightNode);
        lightNode.getLocalTranslation().addLocal(0, 2.79f, 1.87f);
        model = assetManager.loadModel("Models/npc/berzerker.j3o");
        model.scale(scale);
        characterNode.attachChild(model);
        model.setShadowMode(RenderQueue.ShadowMode.Cast);
        model.setLocalTranslation(0, 4, 0);
        Node n = (Node) model;
        Node n1 = (Node) n.getChild("anim");
        aniCon = n1.getControl(AnimControl.class);
        skelCon = n1.getControl(SkeletonControl.class);
        skelCon.setHardwareSkinningPreferred(false);
        physicsCharacter = new BetterCharacterControl(1f, 6, playerMass);
        physicsCharacter.setEnabled(false);
        physicsCharacter.warp(new Vector3f(0, 2, 0));
        physicsCharacter.setJumpForce(new Vector3f(0, jump_Speed, 0));
        physicsCharacter.setGravity(new Vector3f(0, gravity, 0));
        characterNode.addControl(physicsCharacter);
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        this.localRootNode.attachChild(characterNode);
        doAnim("player", "Idle", LoopMode.Loop);
        characterNode.setQueueBucket(RenderQueue.Bucket.Opaque);

        setupKeys();

        if (aniCon.getClass() == null) {
            aniCon.createChannel();
            aniCon.getChannel(0).setAnim("idle");
            aniCon.getChannel(0).setLoopMode(LoopMode.Loop);
        }

        localRootNode.addControl(new CameraCollisionControl(bulletAppState, app.getCamera(), localRootNode, this));
        footsteps = new AudioNode(assetManager, "Sound/Effects/Foot steps.ogg", AudioData.DataType.Buffer);
        footsteps.setLooping(false);
        footsteps.setPositional(false);
        footsteps.setVolume(3f);
        localRootNode.attachChild(footsteps);

        /*        hit = new AudioNode(assetManager, "Sound/Effects/Bang.wav", AudioData.DataType.Buffer);
        hit.setLooping(false);
        hit.setPositional(false);
        hit.setVolume(4);
        localRootNode.attachChild(hit);*/
        // TangentBinormalGenerator.generate(this.model);
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {
            idleCounter = 0;
            switch (binding) {
                case "flashlight":
                    if (value && isEnabled()) {
                        getLamp().setEnabled(!lamp.isEnabled());
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
                    if (value && isEnabled()) {
                        makeChase();
                    }

                    break;
                case "changeFPS":
                    if (value && isEnabled()) {
                        if (context.getSettings().getFrameRate() == 30) {
                            context.getSettings().setFrameRate(60);
                        } else {
                            context.getSettings().setFrameRate(30);
                        }
                        context.restart();
                    }
                    break;
                case "Walk Forward":
                    if (value && isEnabled()) {
                        doAnim("player", "Walk", LoopMode.Loop);
                    } else {
                        doAnim("player", "Idle", LoopMode.Loop);
                    }
                    forward = value;

                    break;
                case "Walk Backward":
                    if (value && isEnabled()) {
                        doAnim("player", "Walk", LoopMode.Loop);
                    } else {
                        doAnim("player", "Idle", LoopMode.Loop);
                    }
                    backward = value;

                    break;
                case "Jump":
                    if (value) {
                        if (getPhysicsCharacter().isOnGround()) {
                            getPhysicsCharacter().jump();
                        }
                    }

                    break;
                default:

                    break;
            }

        }
    };

    public void makeChase() {

        chaseEnabled = !chaseEnabled;

        if (!chaseEnabled) {
            chaseCam.setDragToRotate(true);
        } else {
            chaseCam.setDragToRotate(false);
        }
    }

    public ChaseCamera getChaseCam() {
        return chaseCam;
    }

    public void removeChaseCam() {
        characterNode.removeControl(chaseCam);
    }

    public void attachChaseCam() {
        characterNode.addControl(chaseCam);
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
        inputManager.addMapping("changeFPS",
                new KeyTrigger(KeyInput.KEY_T));

        inputManager.addListener(actionListener, "leftRotate", "rightRotate");
        inputManager.addListener(actionListener, "Strafe Left", "Strafe Right");
        inputManager.addListener(actionListener, "Rotate Left", "Rotate Right");
        inputManager.addListener(actionListener, "Walk Forward", "Walk Backward");
        inputManager.addListener(actionListener, "chase");
        inputManager.addListener(actionListener, "changeFPS");
        inputManager.addListener(actionListener, "Jump", "Shoot", "flashlight");

    }

    public void makeRotateAround(Boolean bol) {

        rotateAround = bol;

        if (rotateAround == false) {
            model.setCullHint(Spatial.CullHint.Dynamic);
            camNode.setEnabled(false);
            if (!chaseEnabled) {
                chaseCam.setDragToRotate(true);
            } else {
                chaseCam.setDragToRotate(false);
            }
        } else {
            model.setCullHint(Spatial.CullHint.Always);
            camNode.setEnabled(true);
            chaseCam.setDragToRotate(false);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {

            checkIdleforPlayer();

            if (attackTimer <= 0) {
                if (attacking) {
                    attack();
                }
            }

            if (attackTimer > 0) {
                attackTimer -= tpf;
            }

            if (model.getWorldTranslation().y < -300f) {
                getPhysicsCharacter().warp(new Vector3f(0, 2, 0));
            }

            Vector3f camDir = viewPort.getCamera().getDirection().normalizeLocal();
            Vector3f camLeft = viewPort.getCamera().getLeft().divide(strafe_speed);

            camDir.y = 0;
            camLeft.y = 0;

            if (idleCounter >= idleTimeOutValue) {
                if (!rotateAround) {
                    System.out.println("start idleing");
                    makeRotateAround(true);
                }
            }

            if (idleCounter == 0) {
                if (rotateAround) {
                    System.out.println("stop idleing");
                    makeRotateAround(false);
                }
            }
            if (idleCounter <= idleTimeOutValue) {
                idleCounter += tpf;
            }

            if (chaseEnabled) {
                viewDirection.set(camDir);
            }

            if (rotateAround) {
                viewDirection.addLocal(camLeft.mult((rotationSpeed * 1.5f) * tpf));
            }

            walkDirection.set(0, 0, 0);

            if (leftStrafe) {
                footsteps.play();
                walkDirection.addLocal(camLeft);
            } else if (rightStrafe) {
                footsteps.play();
                walkDirection.addLocal(camLeft.negate());
            }

            getPhysicsCharacter().setWalkDirection(walkDirection);

            if (forward) {
                footsteps.play();
                walkDirection.addLocal(model.getWorldRotation().getRotationColumn(2).normalizeLocal().divide(move_speed));
            } else if (backward) {
                footsteps.play();
                walkDirection.addLocal(model.getWorldRotation().getRotationColumn(2).normalize().negate().divide(move_speed));
            }

            if (!forward && !backward && !rightStrafe && !leftStrafe) {
                footsteps.stop();
            }

            getPhysicsCharacter().setViewDirection(viewDirection);
            getPhysicsCharacter().setWalkDirection(walkDirection);

            if (chaseCam.getDistanceToTarget() <= chaseCam.getMinDistance()) {
                model.setCullHint(Spatial.CullHint.Always);
                lamp.setDirection(viewPort.getCamera().getDirection());
            } else {
                if (!rotateAround) {
                    model.setCullHint(Spatial.CullHint.Dynamic);
                }
                lamp.setDirection(model.getWorldRotation().getRotationColumn(2));
            }
        }
    }

    private void hit(final String name) {
        @SuppressWarnings("Convert2Lambda")
        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {
                if (spat.getName() != null) {
                    if (spat.getName().equals(name)) {
                        if (spat.getControl(EntityControl.class) != null) {
                            spat.getControl(EntityControl.class).hit(playerDmg, "player");
                        }
                    }
                }
            }
        };

        localRootNode.depthFirstTraversal(visitor);
    }

    private void doAnim(final String name, final String animation, final LoopMode lop) {
        @SuppressWarnings("Convert2Lambda")
        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {

                if (spat.getName() != null) {
                    if (spat.getName().equals(name)) {

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
                }
            }
        };
        localRootNode.depthFirstTraversal(visitor);
    }

    private void checkIdleforPlayer() {

        if (aniCon.getChannel(0).getAnimationName().equals("Attack")) {
            if (aniCon.getChannel(0).getTime() == aniCon.getChannel(0).getAnimMaxTime()) {
                aniCon.clearChannels();
                aniCon.createChannel();
                aniCon.getChannel(0).setAnim("Idle");
                aniCon.getChannel(0).setLoopMode(LoopMode.Loop);
            }
        }
    }

    private void attack() {

        if (attackTimer <= 0) {
            //hit.play();

            doAnim("player", "Attack", LoopMode.DontLoop);
            attackTimer = attackTime;

            Ray ray1 = new Ray(model.getWorldTranslation(), physicsCharacter.getViewDirection());
            CollisionResults results1 = new CollisionResults();
            localRootNode.collideWith(ray1, results1);

            if (results1.size() > 1) {
                String target = results1.getCollision(1).getGeometry().getParent().getParent().getParent().getName();
                if (!target.equals("terrain")) {
                    if (!target.equals("")) {
                        if (model.getWorldTranslation().distance(results1.getCollision(1).getGeometry().getWorldTranslation()) < 10) {
                            hit(target);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

    public BetterCharacterControl getPhysicsCharacter() {
        return physicsCharacter;
    }

    public void setPhysicsCharacter(BetterCharacterControl physicsCharacter) {
        this.physicsCharacter = physicsCharacter;
    }

    public boolean isChaseEnabled() {
        return chaseEnabled;
    }

    public SpotLight getLamp() {
        return lamp;
    }

    public boolean isRotating() {
        return rotateAround;
    }
}
