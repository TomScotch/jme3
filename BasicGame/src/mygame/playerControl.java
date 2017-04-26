package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
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

public class playerControl extends AbstractControl {

    private final ViewPort viewPort;
    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;
    private boolean chaseEnabled = true;
    private final float jump_Speed = 25f;
    private final CameraNode camNode;
    private final float gravity = 50;
    private final float playerMass = 2.5f;
    private final float chaseCamRotationSpeed = 0.5f;
    private final SpotLight lamp;
    private final GhostControl ghostControl;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 0);
    private final float move_speed = 0.1f;
    private final float strafe_speed = 0.1f;
    private final float rotationSpeed = 0.005f;
    private boolean attacking = false;
    private float attackTimer = 0f;
    private final float attackTime = 1.5f;
    private String collisionTarget = "";
    private final float playerDmg = 30f;
    private final Spatial model;
    private BetterCharacterControl physicsCharacter;
    private final Node characterNode;
    boolean rotate = false;
    private final ChaseCamera chaseCam;
    private final float flashLightStrength = 1.3f;
    boolean leftRotate = false, rightRotate = false, leftStrafe = false, rightStrafe = false, forward = false, backward = false;
    private final Node localRootNode;
    private final AnimControl aniCon;

    public playerControl(SimpleApplication app, BulletAppState bulletState, Node localRootNode) {

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
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        characterNode.attachChild(camNode);
        camNode.setLocalTranslation(new Vector3f(0, 4.4f, -18f));
        camNode.lookAt(characterNode.getLocalTranslation(), Vector3f.UNIT_Y);
        camNode.setEnabled(false);
        lamp = new SpotLight();
        lamp.setSpotRange(50);
        lamp.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD);
        lamp.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD);
        lamp.setColor(ColorRGBA.White.mult(flashLightStrength));
        lamp.setEnabled(false);
        this.localRootNode.addLight(lamp);
        model = assetManager.loadModel("Models/npc/knight.j3o");
        model.scale(0.45f);
        characterNode.attachChild(model);
        model.setShadowMode(RenderQueue.ShadowMode.Cast);
        model.setLocalTranslation(0, 4.15f, 0);
        Node n = (Node) model;
        Node n1 = (Node) n.getChild("anim");
        aniCon = n1.getControl(AnimControl.class);
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
        this.localRootNode.attachChild(characterNode);
        doAnim("player", "Idle", LoopMode.Loop);
        setupKeys();
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {

            switch (binding) {
                case "flashlight":
                    if (value && isEnabled()) {
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
                    if (value && isEnabled()) {
                        camNode.setEnabled(!camNode.isEnabled());
                        chaseCam.setEnabled(!chaseCam.isEnabled());
                        chaseEnabled = !chaseEnabled;
                        chaseCam.setDragToRotate(!chaseCam.isDragToRotate());
                    }
                    break;

                case "Walk Forward":
                    if (value&& isEnabled()) {
                        doAnim("player", "Walk", LoopMode.Loop);
                    } else {
                        doAnim("player", "Idle", LoopMode.Loop);
                    }
                    forward = value;
                    break;
                case "Walk Backward":
                    if (value&& isEnabled()) {
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

        inputManager.addListener(actionListener, "leftRotate", "rightRotate");
        inputManager.addListener(actionListener, "Strafe Left", "Strafe Right");
        inputManager.addListener(actionListener, "Rotate Left", "Rotate Right");
        inputManager.addListener(actionListener, "Walk Forward", "Walk Backward");
        inputManager.addListener(actionListener, "chase");
        inputManager.addListener(actionListener, "Jump", "Shoot", "flashlight");

        if (aniCon.getClass() == null) {
            aniCon.createChannel();
            aniCon.getChannel(0).setAnim("idle");
            aniCon.getChannel(0).setLoopMode(LoopMode.Loop);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {

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

            lamp.setPosition(characterNode.getLocalTranslation());
            lamp.getPosition().addLocal(0, 3, 0);
            lamp.setDirection(viewPort.getCamera().getDirection());

            if (chaseCam.getDistanceToTarget() <= chaseCam.getMinDistance()) {
                model.setCullHint(Spatial.CullHint.Always);
            } else {
                model.setCullHint(Spatial.CullHint.Dynamic);
            }
        }
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

            if (!collisionTarget.equals("")) {
                hit(collisionTarget);
            }
            doAnim("player", "Attack", LoopMode.DontLoop);
            attackTimer = attackTime;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }
}
