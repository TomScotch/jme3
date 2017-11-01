package mygame;

import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.Particle;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.filters.PosterizationFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import de.lessvoid.nifty.controls.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameRunningState extends AbstractAppState {

    private FogFilter fog;
    private BitmapText healthText;
    private boolean noWide;
    private final Camera cam2;
    private final AudioNode amb;
    private final AudioNode amb1;
    private final AudioNode amb2;
    private final AudioNode lightRain;
    private final AudioNode normalRain;
    private final AudioNode heavyRain;
    private final int ambienceVolume = 3;
    private final ViewPort view2;
    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final AssetManager assetManager;
    private Node localRootNode = new Node("Game Screen RootNode");
    private final Node localGuiNode = new Node("Game Screen GuiNode");
    private final InputManager inputManager;
    private final BulletAppState bulletAppState;
    private boolean isRunning = false;
    private boolean bgmOn = false;
    private int bgmVolume = 8;
    private int anisotrpy_samples = 4;
    private boolean bloomEnabled;
    private boolean fogEnabled;
    private boolean lightScatterEnabled;
    private boolean anisotropyEnabled;
    private boolean waterPostProcessing;
    private boolean globalLightningEnabled;
    private final boolean shadows;
    private final FilterPostProcessor fpp;
    private boolean weatherEnabled;
    private float counter = 0;
    private float limit = 0;
    private final Spatial teapot;
    private final Picture pic;
    private BitmapText hudText;
    protected boolean isTimeDemo = false;
    private List<Float> fps;
    private BitmapText hudText2;
    private Vector2f minMaxFps;
    private final AppStateManager stateManager;
    private float health = 100;
    private final int birdLimit = 29;
    private BloomFilter bloom;
    private float density = 2f;//2
    private float sampling = 1f;//1
    private float blurScale = 1.5f;//1.5f
    private float exposurePower = 5f;//5
    private float cutOff = 0.1f; // 0.1 - 1.0
    private AssetEventListener asl;
    private int samples = 4;
    private final PosterizationFilter pf;
    private ParticleEmitter rain;
    private ParticleEmitter flash;
    private ParticleEmitter clouds;
    private boolean misty = false;
    private boolean clouded = false;
    private boolean raining = false;
    private boolean lightnungStrikes = false;
    private boolean clouded_low = false;
    private boolean clouded_med = false;
    private boolean clouded_high = false;
    private boolean raining_low = false;
    private boolean raining_med = false;
    private boolean raining_high = false;
    private boolean misty_low = false;
    private boolean misty_med = false;
    private boolean misty_high = false;
    private boolean lightnungStrikes_low = false;
    private boolean lightnungStrikes_med = false;
    private boolean lightnungStrikes_high = false;
    private final float fogDensity = 0.1f; // 1.3f
    private final int fogDistance = 75; // 50
    private final int cloudThickness = 350; // 400
    private final float rainStrength = 800; // 3000
    private final float rainThickness = 4000; // 6000
    private float weatherCounter = 0f;
    private float weatherLimit = 0f;
    private float flashCounter = 0f;
    private float flashLimit = 0f;
    private final int maximumWeatherLength = 8;
    private final int minimumWeatherLength = 4;
    private ParticleEmitter debrisEffect;
    private DirectionalLight sun;

    private LightScatteringFilter sunlight;

    private final float lightScatterFilterDensity = 0.45f;//1.4f
    private final int lightScatterFiltersamples = 9;//50

    private final DepthOfFieldFilter dofFilter;
    private float focusDistance = 12f; // 10f
    private float range = 55; // 50f
    private float scale = 0.8f; // 1f

    private float timeWater = 0.0f;
    private float waterHeight = -5f;
    private final float initialWaterHeight = -7f;
    private WaterFilter water;
    private boolean dynamicWater = true;
    private boolean dynamicLighting = true;

    private boolean specular = true;
    private boolean hqshore = true;
    private boolean caustics = true;
    private boolean foam = true;
    private boolean refraction = true;
    private boolean ripples = true;

    private final float ssaoSampleRadius = 0.5f; // 5.1f 12.94f, 
    private final float ssaoIntensity = 0.5f; // 1.2f 43.92f, 
    private final float ssaoScale = 0.2f;// 0.2f 0.33f, 
    private final float ssaoBias = 0.3f; // 0.1f 0.61f
    private SSAOFilter ssaoFilter;
    private boolean ssaoEnabled = false;

    private TerrainQuad terrain;
    private AbstractHeightMap heightmap;

    private final Spatial night;
    private final Spatial day;
    private final Spatial evening;
    private final Spatial morning;
    private final Material matMorning;
    private final Material matDay;
    private final Material matEvening;
    private final Material matNight;

    private GlobalLightingControl glc;
    private final PlayerControl playerControl;
    private EnemyControl enemyControl;

    public GameRunningState(SimpleApplication app, Boolean fogEnabled, Boolean bloomEnabled, Boolean lightScatterEnabled, Boolean anisotropyEnabled, Boolean waterPostProcessingEnabled, Boolean shadows, Boolean globalLightningEnabled) {

        System.out.println("Game State is being constructed");

        this.console = mygame.Main.getConsole();
        this.fogEnabled = fogEnabled;
        this.bloomEnabled = bloomEnabled;
        this.lightScatterEnabled = lightScatterEnabled;
        this.anisotropyEnabled = anisotropyEnabled;
        this.waterPostProcessing = waterPostProcessingEnabled;
        this.globalLightningEnabled = globalLightningEnabled;
        this.shadows = shadows;
        this.weatherEnabled = true;
        this.stateManager = app.getStateManager();

        //CONSTRUKTOR
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();

        fps = new ArrayList<>();
        fpp = new FilterPostProcessor(assetManager);

        //PHYSICS STATE
        bulletAppState = new BulletAppState();
        app.getStateManager().attach(bulletAppState);
        bulletAppState.setEnabled(false);
        bulletAppState.setDebugEnabled(false);

        //CAMERA        
        this.viewPort.getCamera().setLocation(new Vector3f(0, 8, -10));

        //TERRAIN
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.275f);
        heightmap.load();
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        Material sphereMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture diff = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        diff.setWrap(Texture.WrapMode.Repeat);
        sphereMat.setTexture("DiffuseMap", diff);
        Texture norm = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        norm.setWrap(Texture.WrapMode.Repeat);
        sphereMat.setTexture("NormalMap", norm);
        terrain.setMaterial(sphereMat);
        TerrainLodControl control = new TerrainLodControl(terrain, viewPort.getCamera());
        terrain.addControl(control);
        terrain.setLocalTranslation(-164.0f, -3.75f, 8.9f);
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        RigidBodyControl rb = new RigidBodyControl(0);
        terrain.addControl(rb);
        bulletAppState.getPhysicsSpace().addAll(terrain);
        localRootNode.attachChild(terrain);
        rb.setFriction(1);

        //FOV
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

        //PLAYER
        playerControl = new PlayerControl(app, bulletAppState, localRootNode, noWide);
        playerControl.getPhysicsCharacter().setEnabled(false);
        Node player = new Node("playerNode");
        player.addControl(playerControl);
        localRootNode.attachChild(player);

        //SUN
        Node sunNode = new Node("sunNode");
        glc = new GlobalLightingControl(viewPort, assetManager, playerControl.getLamp(), localRootNode);
        sunNode.addControl(glc);
        localRootNode.attachChild(sunNode);
        glc.setGlobalLightning(this.globalLightningEnabled);

        //SKY
        Texture west = assetManager.loadTexture("Textures/skybox/Night/FullMoonLeft2048.png");
        Texture east = assetManager.loadTexture("Textures/skybox/Night/FullMoonRight2048.png");
        Texture north = assetManager.loadTexture("Textures/skybox/Night/FullMoonBack2048.png");
        Texture south = assetManager.loadTexture("Textures/skybox/Night/FullMoonFront2048.png");
        Texture up = assetManager.loadTexture("Textures/skybox/Night/FullMoonUp2048.png");
        Texture down = assetManager.loadTexture("Textures/skybox/Night/FullMoonDown2048.png");
        night = SkyFactory.createSky(assetManager, west, east, north, south, up, down);

        Texture west1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterLeft2048.png");
        Texture east1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterRight2048.png");
        Texture north1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterBack2048.png");
        Texture south1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterFront2048.png");
        Texture up1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterUp2048.png");
        Texture down1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterDown2048.png");
        morning = SkyFactory.createSky(assetManager, west1, east1, north1, south1, up1, down1);

        /*Texture west2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyLeft2048.png");
        Texture east2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyRight2048.png");
        Texture north2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyBack2048.png");
        Texture south2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyFront2048.png");
        Texture up2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyUp2048.png");
        Texture down2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyDown2048.png");*/
        evening = morning; //SkyFactory.createSky(assetManager, west2, east2, north2, south2, up2, down2);

        /*Texture west3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetLeft2048.png");
        Texture east3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetRight2048.png");
        Texture north3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetBack2048.png");
        Texture south3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetFront2048.png");
        Texture up3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetUp2048.png");
        Texture down3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetDown2048.png");*/
        day = morning;//SkyFactory.createSky(assetManager, west3, east3, north3, south3, up3, down3);

        morning.setLocalTranslation(0, -1000, 0);
        evening.setLocalTranslation(0, -1000, 0);
        night.setLocalTranslation(0, -1000, 0);
        day.setLocalTranslation(0, -1000, 0);

        Geometry morningGeom = (Geometry) morning;
        matMorning = morningGeom.getMaterial();
        matMorning.setTransparent(true);
        matMorning.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        morningGeom.setQueueBucket(RenderQueue.Bucket.Sky);

        Geometry dayGeom = (Geometry) day;
        matDay = dayGeom.getMaterial();
        matDay.setTransparent(true);
        matDay.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.ModulateX2);
        dayGeom.setQueueBucket(RenderQueue.Bucket.Sky);

        Geometry eveningGeom = (Geometry) evening;
        matEvening = eveningGeom.getMaterial();
        matEvening.setTransparent(true);
        matEvening.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.ModulateX2);
        eveningGeom.setQueueBucket(RenderQueue.Bucket.Sky);

        Geometry nightGeom = (Geometry) night;
        matNight = nightGeom.getMaterial();
        matNight.setTransparent(true);
        matNight.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        nightGeom.setQueueBucket(RenderQueue.Bucket.Sky);

        localRootNode.attachChild(day);
        localRootNode.attachChild(night);
        localRootNode.attachChild(morning);
        localRootNode.attachChild(evening);
        day.setCullHint(Spatial.CullHint.Always);
        evening.setCullHint(Spatial.CullHint.Always);
        morning.setCullHint(Spatial.CullHint.Always);
        night.setCullHint(Spatial.CullHint.Always);

        //LightScatter
        if (lightScatterEnabled) {
            sunlight = new LightScatteringFilter(new Vector3f(.5f, .5f, .5f).multLocal(-3000));
            sunlight.setLightDensity(lightScatterFilterDensity);
            sunlight.setNbSamples(lightScatterFiltersamples);
            fpp.addFilter(sunlight);
        }

        //FOG
        if (fogEnabled) {
            fog = new FogFilter();
            fog.setFogColor(ColorRGBA.Gray);
            fog.setFogDistance(0);
            fog.setFogDensity(0);
            fpp.addFilter(fog);
        }

        //Weather
        if (weatherEnabled) {
            weatherLimit = getLimit();
            flash = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 8);
            Material flash_mat = new Material(
                    assetManager, "Common/MatDefs/Misc/Particle.j3md");
            flash_mat.setTexture("Texture",
                    assetManager.loadTexture("Textures/weatherSprites/lightning/Xz2ctMGg_5c2UF-vqdqT3dMZLvs.png"));
            flash.setMaterial(flash_mat);
            flash.setShape(new EmitterBoxShape(new Vector3f(-256f, -1f, -256f), new Vector3f(256f, 1f, 256f)));
            flash.setParticlesPerSec(0);
            flash.setLocalTranslation(0, 75, 0);
            flash.center();
            localRootNode.attachChild(flash);

            flash.setEndColor(ColorRGBA.Blue);
            flash.setStartColor(ColorRGBA.White);

            flash.setStartSize(3);
            flash.setEndSize(28);

            flash.setHighLife(0.35f);
            flash.setLowLife(0.1f);

            flash.setFacingVelocity(false);
            flash.setInWorldSpace(false);
            flash.setGravity(0, 0, 0);
            flash.setFaceNormal(new Vector3f(0, 0, 1));

            rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, (int) rainThickness);
            Material rainMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            rainMat.setTexture("Texture", assetManager.loadTexture("Textures/weatherSprites/rain/rain.png"));
            rain.setMaterial(rainMat);
            rain.setStartSize(0.225f);
            rain.setEndSize(0.125f);
            rain.setGravity(0, 900, 0);
            rain.setEndColor(ColorRGBA.White);
            rain.setStartColor(ColorRGBA.White);
            rain.setHighLife(1f);
            rain.setLowLife(1f);
            rain.setInWorldSpace(true);
            rain.setShape(new EmitterBoxShape(new Vector3f(-256, 0, -256), new Vector3f(256, 0.1f, 256)));
            rain.setParticlesPerSec(0);
            rain.setFacingVelocity(false);
            rain.setLocalTranslation(0, 50, 0);
            rain.center();
            localRootNode.attachChild(rain);

            clouds = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, cloudThickness);
            Material cloudMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            cloudMat.setTexture("Texture", assetManager.loadTexture("Textures/weatherSprites/clouds/SmallCloud.png"));
            clouds.setMaterial(cloudMat);
            clouds.setStartSize(25);
            clouds.setEndSize(25);
            clouds.setGravity(0.015f, 0, 0.015f);
            clouds.setHighLife(200f);
            clouds.setLowLife(200f);
            clouds.setShape(new EmitterBoxShape(new Vector3f(-256, -1f, -256), new Vector3f(256, 1f, 256)));
            clouds.setParticlesPerSec(0);
            clouds.setFacingVelocity(false);
            clouds.setInWorldSpace(true);
            clouds.setFaceNormal(new Vector3f(0, -1, 0));
            clouds.getParticleInfluencer().setVelocityVariation(3f);
            clouds.setLocalTranslation(0, 200, 0);
            clouds.center();
            clouds.setShadowMode(RenderQueue.ShadowMode.Cast);
            localRootNode.attachChild(clouds);

            debrisEffect = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 128);
            Material debrisMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            debrisMat.setTexture("Texture", assetManager.loadTexture("Textures/weatherSprites/rain/splash.png"));
            debrisEffect.setMaterial(debrisMat);
            debrisEffect.setStartSize(0.1f);
            debrisEffect.setEndSize(0.01f);
            debrisEffect.setEndColor(ColorRGBA.White);
            debrisEffect.setStartColor(ColorRGBA.White);
            debrisEffect.setHighLife(0.5f);
            debrisEffect.setLowLife(0.25f);
            debrisEffect.setInWorldSpace(true);
            debrisEffect.setFacingVelocity(false);
            debrisEffect.setShape(new EmitterSphereShape(Vector3f.ZERO, 64));
            debrisEffect.getWorldTranslation().set(0, 6, 0);
            localRootNode.attachChild(debrisEffect);

            sun = new DirectionalLight();
            sun.setDirection((new Vector3f(0f, -0.5f, 0f)).normalizeLocal());
            sun.setColor(ColorRGBA.White);
        }

        //ANISOTROPY
        if (anisotropyEnabled) {

            asl = new AssetEventListener() {

                @Override
                public void assetLoaded(AssetKey key) {
                    //
                }

                @Override
                public void assetRequested(AssetKey key) {
                    if (key.getExtension().equals("png") || key.getExtension().equals("jpg") || key.getExtension().equals("dds")) {
                        TextureKey tkey = (TextureKey) key;
                        tkey.setAnisotropy(samples);
                    }
                }

                @Override
                public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
                    //
                }

            };
            assetManager.addAssetEventListener(asl);
        }

        //PosterizationFilter
        pf = new PosterizationFilter();
        pf.setNumColors(8);
        fpp.addFilter(pf);

        //Depth of Field
        dofFilter = new DepthOfFieldFilter();
        dofFilter.setFocusRange(range);
        dofFilter.setBlurScale(scale);
        fpp.addFilter(dofFilter);

        //Bloom
        if (bloomEnabled) {
            bloom = new BloomFilter(BloomFilter.GlowMode.SceneAndObjects);
            bloom.setExposureCutOff(cutOff);
            bloom.setBloomIntensity(density);
            bloom.setDownSamplingFactor(sampling);
            bloom.setBlurScale(blurScale);
            bloom.setExposurePower(exposurePower);
            fpp.addFilter(bloom);
        }

        //Screen Space Ambient Occlusion
        if (ssaoEnabled) {
            ssaoFilter = new SSAOFilter(ssaoSampleRadius, ssaoIntensity, ssaoScale, ssaoBias);
            //ssaoFilter.setApproximateNormals(approximateNormals);
            fpp.addFilter(ssaoFilter);
        }

        //Second Camera View
        cam2 = app.getCamera().clone();
        cam2.setViewPort(0f, 0.5f, 0f, 0.5f);
        cam2.setLocation(new Vector3f(-0.10947256f, 25.5760219f, 4.81758f));
        cam2.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));

        view2 = app.getRenderManager().createMainView("Bottom Left", cam2);
        view2.setClearFlags(true, true, true);
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

        pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/cross.png", true);
        pic.setName("crosshair");
        pic.setWidth(app.getContext().getSettings().getWidth() / 24);
        pic.setHeight(app.getContext().getSettings().getHeight() / 24);
        pic.setPosition(app.getContext().getSettings().getWidth() / 2, app.getContext().getSettings().getHeight() / 2);


        /*        CartoonEdgeFilter toon = new CartoonEdgeFilter();
        toon.setEdgeWidth(0.5f);
        toon.setEdgeIntensity(1.0f);
        toon.setNormalThreshold(0.8f);
        fpp.addFilter(toon);*/
    }

    private void setupHudText() {
        localGuiNode.attachChild(pic);
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
        hudText2.setLocalTranslation((viewPort.getCamera().getWidth() / 2) - 60, hudText2.getLineHeight() * 3, 0); // position
        localGuiNode.attachChild(hudText2);

        healthText = new BitmapText(assetManager.loadFont("Interface/Fonts/Lato.fnt"), false);
        healthText.setSize(assetManager.loadFont("Interface/Fonts/Lato.fnt").getCharSet().getRenderedSize() * 2.8f);
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

            WildLifeControl wildlifeControl = new WildLifeControl(glc);

            bird.addControl(wildlifeControl);
            wildlifeControl.getSkeletonControl().setHardwareSkinningPreferred(false);
            bird.setName("bird");
            bird.setShadowMode(RenderQueue.ShadowMode.Cast);
            localRootNode.attachChild(bird);
            wildlifeControl.setAnim("fly", LoopMode.Loop);
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

        //WATER
        water = new WaterFilter((Node) localRootNode, new Vector3f(0, 0, 0));
        water.setWaterHeight(initialWaterHeight);
        water.setUseSpecular(specular);
        water.setUseHQShoreline(hqshore);
        water.setUseCaustics(caustics);
        water.setUseFoam(foam);
        water.setUseRefraction(refraction);
        water.setUseRipples(ripples);
        if (waterPostProcessing) {
            if (!fpp.getFilterList().contains(water)) {
                fpp.addFilter(water);
            }
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

    public void treeoutroot(Node node) {

        int n = node.getQuantity();

        String name = node.getName();

        if (n > 0) {
            System.out.print("+ " + name + " " + n);
            console.output("+ " + name + " " + n);
        } else {
            System.out.println(name);
            console.output(name);
        }

        for (Spatial spat : node.getChildren()) {
            int c = spat.getParent().getChildIndex(spat);
            if (c > 0) {
                System.out.println("-" + name + " " + c);
                console.output("-" + name + " " + c);
            } else {
                System.out.println(name);
                console.output(name);
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
                        console.output("-" + spat.getName() + " " + c);
                    } else {
                        System.out.println(spat.getName());
                        console.output(spat.getName());
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
                                bulletAppState.getPhysicsSpace().addAll(terrain);
                                bulletAppState.getPhysicsSpace().removeAll(teapot);
                                playerControl.getPhysicsCharacter().warp(new Vector3f(0, 3.5f, 0));
                                localRootNode.attachChild(terrain);
                                enemyControl = new EnemyControl(glc, assetManager, localRootNode, bulletAppState, playerControl);
                                localRootNode.addControl(enemyControl);
                                enemyControl.setEnabled(true);
                            } else if (localRootNode.hasChild(terrain)) {
                                terrain.removeFromParent();
                                localRootNode.attachChild(teapot);
                                playerControl.getPhysicsCharacter().warp(new Vector3f(0, 6, 0));
                                bulletAppState.getPhysicsSpace().addAll(teapot);
                                bulletAppState.getPhysicsSpace().removeAll(terrain);
                                enemyControl.remAllEnemys();
                                enemyControl.setEnabled(false);
                                localRootNode.removeControl(enemyControl);
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
                        // write(localRootNode, "localRootNode");
                        for (Spatial kid : localRootNode.getChildren()) {
                            try {
                                Node childNode = (Node) kid;
                                if (childNode != null) {
                                    if (childNode.getName() != null) {
                                        if (childNode.getQuantity() >= 1) {
                                            write(childNode, childNode.getName());
                                        }
                                    }
                                }
                            } catch (ClassCastException classCastException) {
                                System.out.println(classCastException.getLocalizedMessage());
                            }
                        }
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
        File file = new File(userHome + "/" + "j3o" + "/" + name + ".j3o");

        playerControl.removeChaseCam();

        if (teapot.getParent() != null) {
            teapot.removeFromParent();
            bulletAppState.getPhysicsSpace().removeAll(teapot);
            bulletAppState.getPhysicsSpace().addAll(terrain);
            playerControl.getPhysicsCharacter().warp(new Vector3f(0, 3.5f, 0));
            localRootNode.attachChild(terrain);
            enemyControl = new EnemyControl(glc, assetManager, localRootNode, bulletAppState, playerControl);
            localRootNode.addControl(enemyControl);
            enemyControl.setEnabled(true);
        }

        try {
            try {
                exporter.save(node, file);
                System.out.println("Succesfully saved " + node.getName() + " to " + file.getPath());
            } catch (OutOfMemoryError outOfMemoryError) {
                System.out.println("run out of memory while saving root : " + outOfMemoryError.getLocalizedMessage());
            }
        } catch (IOException ex) {
            System.out.println("Failed to save Root : " + ex.getLocalizedMessage());
        }

        playerControl.attachChaseCam();
    }

    @Override
    public void update(float tpf
    ) {

        if (isRunning) {

            super.update(tpf);

            night.rotate(0, glc.getRotation() / 4, 0);
            day.rotate(0, glc.getRotation() / 4, 0);
            evening.rotate(0, glc.getRotation() / 4, 0);
            morning.rotate(0, glc.getRotation() / 4, 0);

            if (glc.isMorning()) {
                day.setCullHint(Spatial.CullHint.Always);
                evening.setCullHint(Spatial.CullHint.Always);
                morning.setCullHint(Spatial.CullHint.Never);
                night.setCullHint(Spatial.CullHint.Never);

            } else if (glc.isDay()) {
                night.setCullHint(Spatial.CullHint.Never);
                morning.setCullHint(Spatial.CullHint.Always);
                evening.setCullHint(Spatial.CullHint.Always);
                day.setCullHint(Spatial.CullHint.Never);

            } else if (glc.isEvening()) {
                morning.setCullHint(Spatial.CullHint.Always);
                day.setCullHint(Spatial.CullHint.Always);
                night.setCullHint(Spatial.CullHint.Never);
                evening.setCullHint(Spatial.CullHint.Never);

            } else if (glc.isNight()) {
                morning.setCullHint(Spatial.CullHint.Always);
                day.setCullHint(Spatial.CullHint.Always);
                evening.setCullHint(Spatial.CullHint.Never);
                night.setCullHint(Spatial.CullHint.Never);
            }

            timeWater += tpf;

            if (waterPostProcessing) {
                if (dynamicLighting) {
                    if (!glc.isNight()) {
                        water.setDeepWaterColor(glc.getBackgroundColor());
                        water.setWaterColor(ColorRGBA.Blue);
                        water.setLightColor(glc.getSun().getColor());
                        water.getLightDirection().set(glc.getSunDirection());
                    } else {
                        water.setDeepWaterColor(ColorRGBA.Black);
                        water.setLightColor(ColorRGBA.Black);
                        water.setLightDirection(new Vector3f(0, 1, 0));
                    }
                }
                if (dynamicWater) {
                    waterHeight = (float) Math.cos(((timeWater * 0.6f) % FastMath.TWO_PI)) * 1.5f;
                    water.setWaterHeight(initialWaterHeight + waterHeight);
                }
            }
            /*            else {
            waterProcessor.setWaterDepth(waterDepth);
            waterProcessor.setDistortionScale(waterStrength);
            waterProcessor.setWaveSpeed(waterSpeed);
            }*/

            if (lightScatterEnabled) {
                if (!glc.isNight()) {
                    sunlight.setEnabled(true);
                    sunlight.setLightPosition(glc.getSunPosition());
                } else {
                    sunlight.setEnabled(false);
                }

            }

            Ray ray = new Ray(viewPort.getCamera().getLocation(), viewPort.getCamera().getDirection());
            CollisionResults results = new CollisionResults();
            int numCollisions = localRootNode.collideWith(ray, results);
            if (numCollisions > 0) {
                CollisionResult hit = results.getClosestCollision();
                dofFilter.setFocusDistance(hit.getDistance() / focusDistance);
            }

            weatherCounter += tpf;

            if (pf.getStrength() > 0) {
                pf.setStrength(pf.getStrength() - tpf);
            }
            if (pf.getStrength() < 0) {
                pf.setStrength(0);
            }

            if (weatherCounter >= weatherLimit) {
                weatherCounter = 0;
                startRandomWeather();

                weatherLimit = getLimit();
            }

            if (clouded) {
                if (clouded_high) {
                    if (clouds.getNumVisibleParticles() < cloudThickness * 2) {
                        clouds.emitParticles(cloudThickness / 5);
                    }
                } else if (clouded_med) {
                    if (clouds.getNumVisibleParticles() < cloudThickness) {
                        clouds.emitParticles(cloudThickness / 10);
                    }
                } else if (clouded_low) {
                    if (clouds.getNumVisibleParticles() < cloudThickness / 2) {
                        clouds.emitParticles(cloudThickness / 20);
                    }
                }
            } else {

                clouded_low = false;
                clouded_med = false;
                clouded_high = false;

                if (clouds.getNumVisibleParticles() > 0) {
                    clouds.killParticle(0);
                }
            }

            if (raining) {

                if (raining_high) {
                    rain.setParticlesPerSec(rainStrength * 2);
                } else if (raining_med) {
                    rain.setParticlesPerSec(rainStrength);
                } else if (raining_low) {
                    rain.setParticlesPerSec(rainStrength / 2);
                }

                if (rain.getNumVisibleParticles() > 0) {

                    debrisEffect.setNumParticles((int) rain.getParticlesPerSec());

                    for (int c = rain.getParticles().length / 2; c >= 0; c--) {
                        try {
                            if (rain.getParticles()[c].life < 0.001f) {//(rain.getParticles()[c].startlife - rain.getParticles()[c].life) >= 2
                                Vector3f position = rain.getParticles()[c].position;
                                debrisEffect.getWorldTranslation().set(position.getX(), position.getY(), position.getZ()); //
                                debrisEffect.emitParticles(1);
                            }
                        } catch (Exception e) {
                        }
                    }
                    for (int c = 0; c <= rain.getParticles().length / 2; c++) {
                        try {
                            if (rain.getParticles()[c].life < 0.001f) {//(rain.getParticles()[c].startlife - rain.getParticles()[c].life) >= 2
                                Vector3f position = rain.getParticles()[c].position;
                                debrisEffect.getWorldTranslation().set(position.getX(), position.getY(), position.getZ()); //
                                debrisEffect.emitParticles(1);
                            }
                        } catch (Exception e) {
                        }
                    }
                }

            } else {

                debrisEffect.setParticlesPerSec(0);
                debrisEffect.killAllParticles();

                raining_low = false;
                raining_med = false;
                raining_high = false;

                if (rain.getParticlesPerSec() > 0) {
                    rain.setParticlesPerSec(0);
                }
            }

            if (lightnungStrikes) {

                flashCounter += tpf;

                setLightningFrequency();

                if (flashCounter >= flashLimit) {

                    flashCounter = 0;

                    if (lightnungStrikes_high) {
                        flash.emitParticles(getRandomNumberInRange(1, 3));
                    } else if (lightnungStrikes_med) {
                        flash.emitParticles(getRandomNumberInRange(1, 2));
                    } else if (lightnungStrikes_low) {
                        flash.emitParticles(getRandomNumberInRange(1, 1));
                    }

                    Particle p = flash.getParticles()[0];
                    Node n = new Node();
                    Spatial spat = (Spatial) n;

                    spat.setLocalTranslation((p.position));
                    spat.lookAt(Vector3f.ZERO, Vector3f.UNIT_XYZ);

                    final DirectionalLight clone = new DirectionalLight();
                    clone.setColor(ColorRGBA.White);
                    clone.setDirection(spat.getLocalRotation().getRotationColumn(2));
                    localRootNode.addLight(clone);

                    float lightDelay = 1;
                    switch (getRandomNumberInRange(0, 2)) {
                        case 0:
                            lightDelay = 0.25f;
                            break;
                        case 1:
                            lightDelay = 0.3f;
                            break;
                        case 2:
                            lightDelay = 0.35f;
                            break;
                    }

                    flash.addControl(new TimedActionControl(lightDelay) {

                        @Override
                        void action() {

                            localRootNode.removeLight(clone);
                        }
                    });
                }

            } else {

                lightnungStrikes_low = false;
                lightnungStrikes_med = false;
                lightnungStrikes_high = false;

                if (flash.getParticlesPerSec() > 0) {
                    flash.killAllParticles();
                    flash.setParticlesPerSec(0);
                }
            }

            if (misty) {
                if (fog != null) {

                    if (misty_high) {
                        if (fog.getFogDistance() <= fogDistance / 1.25f) {
                            fog.setFogDistance(fog.getFogDistance() + (tpf * fogDistance));
                        }

                        if (fog.getFogDensity() < fogDensity / 1.25f) {
                            fog.setFogDensity(fog.getFogDensity() + tpf);
                        }
                    } else if (misty_med) {
                        if (fog.getFogDistance() != fogDistance / 1.5f) {
                            fog.setFogDistance(fog.getFogDistance() + (tpf * fogDistance));
                        }

                        if (fog.getFogDensity() < fogDensity / 1.5f) {
                            fog.setFogDensity(fog.getFogDensity() + (tpf * 4));
                        }
                    } else if (misty_low) {
                        if (fog.getFogDistance() != fogDistance / 1.75f) {
                            fog.setFogDistance(fog.getFogDistance() + (tpf * fogDistance));
                        }

                        if (fog.getFogDensity() < fogDensity / 1.75) {
                            fog.setFogDensity(fog.getFogDensity() + (tpf * 2));
                        }
                    }
                }

            } else {

                misty_low = false;
                misty_med = false;
                misty_high = false;
                if (fog != null) {
                    if (fog.getFogDistance() > 0) {
                        fog.setFogDistance(fog.getFogDistance() - (tpf * fogDistance));
                    }

                    if (fog.getFogDensity() > 0) {
                        fog.setFogDensity(fog.getFogDensity() - (tpf * fogDensity));
                    }
                }
            }
            Ray ray1 = new Ray(viewPort.getCamera().getLocation(), viewPort.getCamera().getDirection());
            CollisionResults results1 = new CollisionResults();
            localRootNode.collideWith(ray1, results1);
            if (results1.size() > 0) {
                if (results1.getClosestCollision().getGeometry().getName().contains("terrain")) {

                    if (results1.getClosestCollision().getDistance() < 16f) {
                        playerControl.getChaseCam().setMaxDistance((playerControl.getChaseCam().getMaxDistance() - (tpf * 115)));
                    }
                } else {
                    playerControl.getChaseCam().setMaxDistance(30);
                }
            } else {
                playerControl.getChaseCam().setMaxDistance(30);
            }

            ray1 = new Ray(viewPort.getCamera().getLocation(), Vector3f.UNIT_X);
            results1 = new CollisionResults();
            localRootNode.collideWith(ray1, results1);
            if (results1.size() > 0) {
                if (results1.getClosestCollision().getGeometry().getName().contains("terrain")) {

                    if (results1.getClosestCollision().getDistance() < 16f) {
                        playerControl.getChaseCam().setMaxDistance((playerControl.getChaseCam().getMaxDistance() - (tpf * 115)));
                    }
                } else {
                    playerControl.getChaseCam().setMaxDistance(30);
                }
            } else {
                playerControl.getChaseCam().setMaxDistance(30);
            }

            ray1 = new Ray(viewPort.getCamera().getLocation(), Vector3f.UNIT_X.negate());
            results1 = new CollisionResults();
            localRootNode.collideWith(ray1, results1);
            if (results1.size() > 0) {
                if (results1.getClosestCollision().getGeometry().getName().contains("terrain")) {

                    if (results1.getClosestCollision().getDistance() < 16f) {
                        playerControl.getChaseCam().setMaxDistance((playerControl.getChaseCam().getMaxDistance() - (tpf * 115)));
                    }
                } else {
                    playerControl.getChaseCam().setMaxDistance(30);
                }
            } else {
                playerControl.getChaseCam().setMaxDistance(30);
            }

            if (playerControl != null) {
                if (!playerControl.isDead()) {

                    if (playerControl.getStamina() >= 0) {
                        if (playerControl.getStamina() >= 99 && playerControl.getStamina() > 75) {
                            hudText2.setText(">>>>>");
                        }
                        if (playerControl.getStamina() < 99 && playerControl.getStamina() > 75) {
                            hudText2.setText(">>>>");
                        }
                        if (playerControl.getStamina() < 75 && playerControl.getStamina() > 50) {
                            hudText2.setText(">>>");
                        }
                        if (playerControl.getStamina() < 50 && playerControl.getStamina() > 25) {
                            hudText2.setText(">>");
                        }
                        if (playerControl.getStamina() < 25 && playerControl.getStamina() > 0) {
                            hudText2.setText(">");
                        }
                        if (playerControl.getStamina() <= 1) {
                            hudText2.setText("");
                        }
                    }

                    if (playerControl.isRotating()) {
                        localGuiNode.setCullHint(Spatial.CullHint.Always);
                    } else {
                        localGuiNode.setCullHint(Spatial.CullHint.Never);
                    }

                    if (playerControl.getChaseCam().getDistanceToTarget() <= playerControl.getChaseCam().getMinDistance()) {

                        if (stateManager.getState(VideoRecorderAppState.class) == null) {
                            if (!playerControl.isRotating()) {
                                if (playerControl.isChaseEnabled()) {
                                    pic.setCullHint(Spatial.CullHint.Never);
                                } else {
                                    pic.setCullHint(Spatial.CullHint.Always);
                                }
                            } else {
                                pic.setCullHint(Spatial.CullHint.Always);
                            }

                        } else {
                            pic.setCullHint(Spatial.CullHint.Always);
                        }
                    } else {
                        pic.setCullHint(Spatial.CullHint.Always);
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
                        pf.setEnabled(true);
                        pf.setStrength(1.25f);
                    }

                    healthText.setText(i.toString());
                } else {
                    healthText.setText("dead");
                    pic.setCullHint(Spatial.CullHint.Always);
                }
            }

            if (isTimeDemo) {
                playerControl.setRotationModifier(12.f);
                playerControl.setIdleCounter(90);
                if (fps.size() < 500) {
                    console.output(" timedemo : " + fps.size() + " - 500");
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
                    console.clear();
                    console.output("min : " + (int) minMaxFps.getX() + " max : " + (int) minMaxFps.getY() + " avg : " + (int) (t / fps.size()));
                    fps = new ArrayList<>();
                }
            }

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
        glc.setEnabled(true);
        playerControl.setEnabled(true);
        pf.setEnabled(true);
        pf.setStrength(1.75f);

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

        attachLocalGuiNode();
        attachLocalRootNode();
        addListener();
        setupKeys();
        playerControl.setupListener();
        playerControl.setupMappings();
        view2.attachScene(localRootNode);
        if (localRootNode.hasChild(terrain)) {
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

        Main.setShowFps(false);

        if (localGuiNode.hasChild(pic)) {
            pic.removeFromParent();
        }

        hudText.removeFromParent();
        hudText2.removeFromParent();
        healthText.removeFromParent();

        amb.stop();
        amb1.stop();
        amb2.stop();

        removeMappings();
        removeListener();

        playerControl.removeMappings();
        playerControl.removeListeners();

        view2.setEnabled(false);
        view2.detachScene(localRootNode);
        pf.setEnabled(false);
        playerControl.setEnabled(false);

        glc.setEnabled(false);

        if (waterPostProcessing) {
            // fpp.removeFilter(water);
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

        enemyControl.setEnabled(false);
        localRootNode.removeControl(enemyControl);

        /*        if (!waterPostProcessing) {
        if (waterProcessor != null) {
        viewPort.removeProcessor(waterProcessor);
        waterGeom.removeFromParent();
        }
        }*/
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

    public final void startRandomWeather() {

        switch (getRandomNumberInRange(0, 6)) {
            case 5:
                makeMisty();
                break;
        }

        switch (getRandomNumberInRange(0, 3)) {

            case 2:
                makeCloudy();
                break;
        }

        switch (getRandomNumberInRange(0, 2)) {

            case 1:
                makeLightning();
                break;
        }

        switch (getRandomNumberInRange(0, 1)) {

            case 0:
                makeRain();
                break;

            case 1:
                makeSuny();
                break;
        }
        weatherLimit = getLimit();
    }

    private void makeMisty() {

        misty = true;
        clouded = false;
        switch (getRandomNumberInRange(0, 2)) {
            case 0:
                misty_low = true;
                misty_med = false;
                misty_high = false;
                System.out.println("misty low");
                break;
            case 1:
                misty_low = false;
                misty_med = true;
                misty_high = false;
                System.out.println("misty med");
                break;
            case 2:
                misty_low = false;
                misty_med = false;
                misty_high = true;
                System.out.println("misty high");
                break;
        }
    }

    public void makeRain() {
        raining = true;
        makeCloudy();
        switch (getRandomNumberInRange(0, 2)) {
            case 0:
                raining_high = true;
                raining_med = false;
                raining_low = false;
                System.out.println("raining_high");
                break;
            case 1:
                raining_high = false;
                raining_med = true;
                raining_low = false;
                System.out.println("raining_med");
                break;
            case 2:
                raining_high = false;
                raining_med = false;
                raining_low = true;
                System.out.println("raining_low");
                break;
        }

        switch (getRandomNumberInRange(0, 1)) {
            case 0:
                makeLightning();
                break;
        }
    }

    public void makeLightning() {

        lightnungStrikes = true;

        switch (getRandomNumberInRange(0, 2)) {
            case 0:
                lightnungStrikes_high = true;
                lightnungStrikes_med = false;
                lightnungStrikes_low = false;
                System.out.println("lightnungStrikes_high");
                break;
            case 1:
                lightnungStrikes_high = false;
                lightnungStrikes_med = true;
                lightnungStrikes_low = false;
                System.out.println("lightnungStrikes_med");
                break;
            case 2:
                lightnungStrikes_high = false;
                lightnungStrikes_med = false;
                lightnungStrikes_low = true;
                System.out.println("lightnungStrikes_low");
                break;
        }
        setLightningFrequency();
    }

    private void setLightningFrequency() {
        if (lightnungStrikes_high) {
            flashLimit = (float) getRandomNumberInRange(4, 8);
        } else if (lightnungStrikes_med) {
            flashLimit = (float) getRandomNumberInRange(6, 12);
        } else if (lightnungStrikes_low) {
            flashLimit = (float) getRandomNumberInRange(8, 16);
        }
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }

    public void makeCloudy() {
        clouded = true;
        switch (getRandomNumberInRange(0, 1)) {
            case 0:
                clouded_high = true;
                clouded_med = false;
                clouded_low = false;
                System.out.println("clouded_high");
                break;
            case 1:
                clouded_high = false;
                clouded_med = true;
                clouded_low = false;
                System.out.println("clouded_med");
                break;
            case 2:
                clouded_high = false;
                clouded_med = false;
                clouded_low = true;
                System.out.println("clouded_low");
                break;
        }
    }

    public void makeSuny() {

        clouded = false;
        raining = false;
        misty = false;
        lightnungStrikes = false;
        System.out.println("sunny");
    }

    private float getLimit() {
        int delayValue = 0;
        switch (glc.getTimeDelay()) {
            case 4:
                delayValue = 2;
                break;
            case 8:
                delayValue = 4;
                break;
            case 16:
                delayValue = 6;
                break;
            case 32:
                delayValue = 8;
                break;
            case 64:
                delayValue = 10;
                break;
            case 128:
                delayValue = 12;
                break;
            case 256:
                delayValue = 14;
                break;
            case 512:
                delayValue = 16;
                break;
            case 1024:
                delayValue = 18;
                break;
            case 2048:
                delayValue = 20;
                break;
            case 4096:
                delayValue = 22;
                break;
            case 8192:
                delayValue = 24;
                break;
            case 16384:
                delayValue = 26;
                break;
            case 32768:
                delayValue = 28;
                break;
            case 65536:
                delayValue = 30;
                break;
            case 131072:
                delayValue = 32;
                break;
        }

        return (float) getRandomNumberInRange(minimumWeatherLength + delayValue, maximumWeatherLength + delayValue);
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

    public AudioNode getAudioNode(String path) {
        return (AudioNode) new AudioNode(assetManager, path, AudioData.DataType.Buffer);
    }

    public AudioNode getLightRain() {
        return lightRain;
    }

    public AudioNode getNormalRain() {
        return normalRain;
    }

    public AudioNode getHeavyRain() {
        return heavyRain;
    }

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
};
