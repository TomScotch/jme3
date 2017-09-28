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
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Quad;
import com.jme3.system.JmeContext;
import com.jme3.util.TangentBinormalGenerator;

public class PlayerControl extends AbstractControl {

    private boolean noWide;

    public float getRotationModifier() {
        return rotationModifier;
    }

    public void setRotationModifier(float rotationModifier) {
        this.rotationModifier = rotationModifier;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    private final SkeletonControl skelCon;
    private final AudioNode footsteps;
    private final AudioNode hit;

    private final ViewPort viewPort;
    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;
    private boolean chaseEnabled = true;

    private final CameraNode camNode;
    private final float gravity = -9.81f;
    private final float playerMass = 75f;
    private final float jump_Speed = 500f;
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
    public final Node characterNode;
    boolean rotate = false;
    private final ChaseCamera chaseCam;
    private final float flashLightStrength = 1.5f;
    private final float flashLightSpotRange = 25;
    private final float outerLamp = 15 * FastMath.PI;
    private final float innerLamp = 15 * FastMath.HALF_PI;
    private final ColorRGBA flashLightColor = ColorRGBA.Orange.mult(flashLightStrength).addLocal(ColorRGBA.LightGray);

    boolean leftRotate = false, rightRotate = false, leftStrafe = false, rightStrafe = false, forward = false, backward = false;
    private final Node localRootNode;
    private final AnimControl aniCon;
    private final float scale = 0.45f;
    private final int maxDistance = 30;
    private boolean rotateAround = false;
    private float idleCounter = 0;
    private final JmeContext context;
    private final float idleTimeOutValue = 90f;

    private float health = 100;
    private boolean dead = false;
    private final float armor = 10;
    private float deadDelay = 3f;
    private final Geometry healthbar;
    private boolean showHealthBar = false;
    private float underAttackTimer = 0f;
    private boolean underAttack = false;
    private final float underAttackTimerVal = 9f;
    private float rotationModifier = 0;

    public PlayerControl(SimpleApplication app, BulletAppState bulletState, Node localRootNode, boolean noWide) {

        this.context = app.getContext();
        this.viewPort = app.getViewPort();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.noWide = noWide;
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
        lamp.setSpotRange(flashLightSpotRange * flashLightStrength);
        lamp.setSpotInnerAngle(innerLamp * FastMath.DEG_TO_RAD);
        lamp.setSpotOuterAngle(outerLamp * FastMath.DEG_TO_RAD);
        lamp.setColor(flashLightColor);
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
        physicsCharacter.warp(new Vector3f(5, 2, -10));
        physicsCharacter.setJumpForce(new Vector3f(0, jump_Speed, 0));
        physicsCharacter.setGravity(new Vector3f(0, gravity, 0));
        physicsCharacter.setPhysicsDamping(1);
        characterNode.addControl(physicsCharacter);
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        this.localRootNode.attachChild(characterNode);
        doAnim("player", "Idle", LoopMode.Loop);
        characterNode.setQueueBucket(RenderQueue.Bucket.Opaque);

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

        hit = new AudioNode(assetManager, "Sound/Effects/Bang.wav", AudioData.DataType.Buffer);
        hit.setLooping(false);
        hit.setPositional(false);
        hit.setVolume(4);
        localRootNode.attachChild(hit);

        TangentBinormalGenerator.generate(this.model);
        BillboardControl billboard = new BillboardControl();
        healthbar = new Geometry("healthbar", new Quad(4f, 0.2f));
        Material mathb = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mathb.setColor("Color", ColorRGBA.Red);
        healthbar.setMaterial(mathb);
        characterNode.attachChild(healthbar);
        healthbar.setCullHint(Spatial.CullHint.Always);
        healthbar.getLocalScale().setX((health + 1) / 75);
        healthbar.center();
        healthbar.move(5, 9, 0);
        healthbar.addControl(billboard);
        characterNode.getLocalTranslation().addLocal(60, 3, -10);
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String binding, boolean value, float tpf) {
            idleCounter = 0;
            switch (binding) {
                case "showHealthBar":
                    if (value && isEnabled()) {
                        showHealthBar = !showHealthBar;
                    }
                    break;
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
                    if (attackTimer <= 0) {
                        if (value) {
                            doAnim("player", "Walk", LoopMode.Loop);
                        } else {
                            doAnim("player", "Idle", LoopMode.Loop);
                        }
                    }

                    break;
                case "Strafe Right":
                    rightStrafe = value;
                    if (attackTimer <= 0) {
                        if (value) {
                            doAnim("player", "Walk", LoopMode.Loop);
                        } else {
                            doAnim("player", "Idle", LoopMode.Loop);
                        }
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
                    if (attackTimer <= 0) {
                        if (value && isEnabled()) {
                            doAnim("player", "Walk", LoopMode.Loop);
                        } else {
                            doAnim("player", "Idle", LoopMode.Loop);
                        }
                    }
                    forward = value;

                    break;
                case "Walk Backward":
                    if (attackTimer <= 0) {
                        if (value && isEnabled()) {
                            doAnim("player", "Walk", LoopMode.Loop);
                        } else {
                            doAnim("player", "Idle", LoopMode.Loop);
                        }
                    }
                    backward = value;

                    break;

                case "changeFOV":
                    if (value) {

                        if (noWide) {
                            viewPort.getCamera().resize(viewPort.getCamera().getWidth() + (viewPort.getCamera().getWidth() / 3), viewPort.getCamera().getHeight(), true);
                        } else {
                            viewPort.getCamera().resize(viewPort.getCamera().getWidth(), viewPort.getCamera().getHeight(), true);
                        }
                        noWide = !noWide;
                    }
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

    public void setupListener() {

        inputManager.addListener(actionListener, "showHealthBar");
        inputManager.addListener(actionListener, "leftRotate", "rightRotate");
        inputManager.addListener(actionListener, "Strafe Left", "Strafe Right");
        inputManager.addListener(actionListener, "Rotate Left", "Rotate Right");
        inputManager.addListener(actionListener, "Walk Forward", "Walk Backward");
        inputManager.addListener(actionListener, "chase");
        inputManager.addListener(actionListener, "changeFPS");
        inputManager.addListener(actionListener, "changeFOV");
        inputManager.addListener(actionListener, "Jump", "Shoot", "flashlight");
    }

    public void removeListeners() {
        inputManager.removeListener(actionListener);
    }

    public void removeMappings() {
        inputManager.deleteMapping("flashlight");
        inputManager.deleteMapping("leftRotate");
        inputManager.deleteMapping("rightRotate");
        inputManager.deleteMapping("Strafe Left");
        inputManager.deleteMapping("Strafe Right");
        inputManager.deleteMapping("chase");
        inputManager.deleteMapping("Walk Forward");
        inputManager.deleteMapping("Walk Backward");
        inputManager.deleteMapping("Jump");
        inputManager.deleteMapping("Shoot");
        inputManager.deleteMapping("changeFOV");
        inputManager.deleteMapping("changeFPS");
        inputManager.deleteMapping("showHealthBar");
    }

    public void setupMappings() {

        inputManager.addMapping("showHealthBar",
                new KeyTrigger(KeyInput.KEY_B));
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
        inputManager.addMapping("changeFOV",
                new KeyTrigger(KeyInput.KEY_L));
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
    private float hitAnimationDelay = 0;

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {

            if (!dead) {

                underAttackTimer -= tpf;

                underAttack = underAttackTimer > 0;

                if (!underAttack) {
                    if (getHealth() < 100) {
                        if ((getHealth() + 10) < 100) {
                            underAttackTimer = underAttackTimerVal / 2;
                            setHealth(getHealth() + 10);
                        } else {
                            setHealth(100);
                        }
                    }
                }

                if (showHealthBar) {
                    healthbar.setCullHint(Spatial.CullHint.Never);
                } else {
                    healthbar.setCullHint(Spatial.CullHint.Always);
                }

                checkIdleforPlayer();

                if (attackTimer <= 0) {
                    if (attacking) {
                        attack();
                    }
                }
                if (health <= 0) {

                    dead = true;
                    // doAnim("player", "Idle", LoopMode.DontLoop);
                }
                if (hitAnimationDelay > 0) {
                    hitAnimationDelay -= tpf;
                    if (hitAnimationDelay <= 0) {
                        // doAnim("player", "Idle", LoopMode.Loop);
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
                    viewDirection.addLocal(camLeft.mult((rotationSpeed * (1.5f + rotationModifier)) * tpf));
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
            //dead
            if (deadDelay <= 0) {

                bulletAppState.getPhysicsSpace().remove(physicsCharacter);
                this.spatial.removeControl(BetterCharacterControl.class);
                this.spatial.removeFromParent();
                this.spatial.removeControl(this);

            }

            if (dead) {
                healthbar.getLocalScale().setX(0);
                if (deadDelay >= 3f) {
                    System.out.println("YOU ARE DEAD");
                    inputManager.clearMappings();
                    doAnim("player", "Die", LoopMode.DontLoop);
                }
                deadDelay -= tpf;
            }
        }
    }

    private void attack(final String name) {
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

            Ray ray1 = new Ray(model.getWorldTranslation(), physicsCharacter.getViewDirection()); //model.getWorldTranslation()
            CollisionResults results1 = new CollisionResults();
            localRootNode.collideWith(ray1, results1);

            if (results1.size() > 1) {

                Geometry g = results1.getCollision(1).getGeometry();

                if (g != null) {

                    Node n = g.getParent().getParent().getParent();

                    if (n != null) {

                        String target = n.getName();
                        if (target != null) {
                            if (!target.equals("")) {
                                if (!target.equals("terrain")) {
                                    if (model.getWorldTranslation().distance(results1.getCollision(1).getGeometry().getWorldTranslation()) < 25) {
                                        attack(target);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                ray1 = new Ray(viewPort.getCamera().getLocation(), viewPort.getCamera().getDirection());
                results1 = new CollisionResults();
                localRootNode.collideWith(ray1, results1);
                if (results1.size() > 1) {

                    Geometry g = results1.getCollision(1).getGeometry();

                    if (g != null) {

                        Node n = g.getParent().getParent().getParent();

                        if (n != null) {

                            String target = n.getName();
                            if (target != null) {
                                if (!target.equals("")) {
                                    if (!target.equals("terrain")) {
                                        if (model.getWorldTranslation().distance(results1.getCollision(1).getGeometry().getWorldTranslation()) < 25) {
                                            attack(target);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    ray1 = new Ray(model.getWorldTranslation(), viewPort.getCamera().getDirection());
                    results1 = new CollisionResults();
                    localRootNode.collideWith(ray1, results1);
                    if (results1.size() > 1) {

                        Geometry g = results1.getCollision(1).getGeometry();

                        if (g != null) {

                            Node n = g.getParent().getParent().getParent();

                            if (n != null) {

                                String target = n.getName();
                                if (target != null) {
                                    if (!target.equals("")) {
                                        if (!target.equals("terrain")) {
                                            if (model.getWorldTranslation().distance(results1.getCollision(1).getGeometry().getWorldTranslation()) < 25) {
                                                attack(target);
                                            }
                                        }
                                    }
                                }
                            }
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

    public boolean hit(float dmg, String name) {

        if (!dead) {
            if ((dmg - armor) > 0) {
                health -= (dmg - armor);
                underAttackTimer = underAttackTimerVal;
                healthbar.getLocalScale().setX((health + 1) / 75);
                healthbar.center();
                healthbar.move((health / 200) + 1, 9, 0);
                hitAnimationDelay = 1.5f;
                // doAnim("player", "Hit", LoopMode.Loop);
                System.out.println("ouch" + dmg + " damage " + " from " + name + "");
                PointLight shine = new PointLight();
                shine.setPosition(Vector3f.ZERO);
                shine.setColor(ColorRGBA.Red);
                characterNode.addLight(shine);
                shine.setRadius(42);
                this.characterNode.addControl(new TimedActionControl(0.30f) {
                    @Override
                    void action() {
                        characterNode.removeLight(shine);
                    }
                });

                hit.play();
                /*                if (health >= 0) {
                 characterNode.addControl(new ShowDamage(assetManager, Float.toString(dmg), (Node) characterNode));
                 }*/
            }
        }
        return dead;
    }

    public void setIdleCounter(float value) {
        this.idleCounter = value;
    }
}
