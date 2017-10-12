package mygame;

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ElementBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.controls.slider.builder.SliderBuilder;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import com.jme3.opencl.*;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleExecuteCommandEvent;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.mapping.DefaultInputMapping;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.tools.Color;
import java.util.Collections;
import java.util.List;

/**
 * Application Main Method
 *
 * @version 0.21
 * @author tomscotch
 */
@SuppressWarnings("null")
public class Main extends SimpleApplication implements ScreenController, KeyInputHandler {

    public GameRunningState getGameRunningState() {
        return gameRunningState;
    }

    /**
     * get the nifty gui console
     *
     * @return console
     */
    public static Console getConsole() {
        return console;
    }

    private static AppSettings cfg;
    private static Main app;

    private static final boolean opencl = false;
    private static final boolean openAl = true;

    private VideoRecorderAppState videoRecorderAppState;
    private static DisplayMode[] modes;

    private final Trigger rain_trigger = new KeyTrigger(KeyInput.KEY_R);
    private final Trigger pause_trigger = new KeyTrigger(KeyInput.KEY_BACK);
    private final Trigger record_trigger = new KeyTrigger(KeyInput.KEY_F6);
    private final Trigger restart_trigger = new KeyTrigger(KeyInput.KEY_F11);
    private final Trigger exit_trigger = new KeyTrigger(KeyInput.KEY_F12);
    private final Trigger superDebug_trigger = new KeyTrigger(KeyInput.KEY_F1);
    private final Trigger fpsSwitch_trigger = new KeyTrigger(KeyInput.KEY_F2);
    private final Trigger statsViewTrigger = new KeyTrigger(KeyInput.KEY_F3);
    private final Trigger helpTrigger = new KeyTrigger(KeyInput.KEY_H);
    private final Trigger consoleTrigger = new KeyTrigger(KeyInput.KEY_F4);
    private GameRunningState gameRunningState;
    private StartScreenState startScreenState;
    private SettingsScreenState settingsScreenState;

    private Future loadFuture = null;
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;

    private boolean bloomEnabled = false;
    private boolean fogEnabled = false;
    private boolean lightScatterEnabled = false;
    private boolean anisotropyEnabled = false;
    private boolean waterPostProcessing = true;
    private boolean globalLightningEnabled = false;
    private boolean isGl3 = false;
    private boolean shadows = false;
    private static boolean showFps = false;
    private boolean displayStatView = false;
    private boolean wireframe = false;
    private static Platform selectedPlatform;
    private Node settingsNode;

    private static final Object sync = new Object();
    private static List<? extends Device> availableDevices;
    private static int currentDeviceIndex;
    private BitmapText helloText;
    private float deathCounter = 0;
    private BitmapText deathText;
    private boolean showConsole = false;
    private static Console console;

    /**
     * nifty gui switch procedure to turn shadows on/off
     */
    public void switchShadows() {
        shadows = !shadows;
        settingsNode.setUserData("shadows", shadows);
        nifty.getScreen("settings").findNiftyControl("shadowsButton", Button.class).setText("Shadows : " + shadows);
    }

    /**
     * nifty gui switch procedure nifty gui switch procedure to turn bloom
     * on/off
     */
    public void switchBloom() {
        bloomEnabled = !bloomEnabled;
        settingsNode.setUserData("bloomEnabled", bloomEnabled);
        nifty.getScreen("settings").findNiftyControl("BloomButton", Button.class).setText("Bloom : " + bloomEnabled);
    }

    /**
     * nifty gui switch procedure to turn OpenGl on/off
     */
    public void switchOpenGl() {

        isGl3 = !isGl3;

        if (isGl3) {
            cfg.setRenderer(AppSettings.LWJGL_OPENGL3);
        } else {
            cfg.setRenderer(AppSettings.LWJGL_OPENGL2);
        }

        settingsNode.setUserData("isGl3", isGl3);
        nifty.getScreen("settings").findNiftyControl("openglButton", Button.class).setText("OpenGL = " + isGl3);
    }

    /**
     * nifty gui switch procedure nifty gui switch procedure to turn water post
     * processing on/off
     */
    public void switchPostProcessWater() {
        waterPostProcessing = !waterPostProcessing;
        settingsNode.setUserData("waterPostProcessing", waterPostProcessing);
        nifty.getScreen("settings").findNiftyControl("waterButton", Button.class).setText("WaterPP = " + waterPostProcessing);
    }

    /**
     * nifty gui switch procedure nifty gui switch procedure to turn anisotropy
     * on/off
     */
    public void switchAnisotropy() {
        anisotropyEnabled = !anisotropyEnabled;
        settingsNode.setUserData("anisotropyEnabled", anisotropyEnabled);
        nifty.getScreen("settings").findNiftyControl("anisotropyButton", Button.class).setText("anisotropy = " + anisotropyEnabled);
    }

    /**
     * nifty gui switch procedure nifty gui switch procedure to turn
     * LightScatter on/off
     */
    public void switchLightScatter() {

        lightScatterEnabled = !lightScatterEnabled;
        settingsNode.setUserData("lightScatterEnabled", lightScatterEnabled);
        nifty.getScreen("settings").findNiftyControl("lightScatterButton", Button.class).setText("lightScatter = " + lightScatterEnabled);
    }

    /**
     * nifty gui switch procedure nifty gui switch procedure to turn fog on/off
     */
    public void switchFog() {
        fogEnabled = !fogEnabled;
        settingsNode.setUserData("fogEnabled", fogEnabled);
        nifty.getScreen("settings").findNiftyControl("fogButton", Button.class).setText("fog = " + fogEnabled);
    }

    /**
     * nifty gui switch procedure nifty gui switch procedure to turn global
     * lighting on/off
     */
    public void switchGlobalLightning() {
        globalLightningEnabled = !globalLightningEnabled;
        settingsNode.setUserData("globalLightningEnabled", globalLightningEnabled);
        nifty.getScreen("settings").findNiftyControl("globalLightningButton", Button.class).setText("globalLightning = " + globalLightningEnabled);
    }

    /**
     * build new game state multithreading
     */
    @SuppressWarnings("Convert2Lambda")
    Callable<Void> loadingCallable = new Callable<Void>() {
        @Override
        public Void call() {
            gameRunningState = new GameRunningState(app, fogEnabled, bloomEnabled, lightScatterEnabled, anisotropyEnabled, waterPostProcessing, shadows, globalLightningEnabled);
            return null;
        }
    };

    /**
     * save a node as j3o
     *
     * @param node the node to write to disk
     * @return true if file could be saved / false if not
     */
    public boolean saveNode(Node node) {
        String userHome = System.getProperty("user.home");
        BinaryExporter exporter = BinaryExporter.getInstance();
        File file = new File(userHome + "/" + node.getName() + ".j3o");
        boolean x;
        try {
            exporter.save(node, file);
            x = true;
        } catch (IOException ex) {
            // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to save node!", ex);
            x = false;
        }
        return x;
    }

    /**
     * load a j3o from disk
     *
     * @param fileName the name of the j3o to load
     * @return node
     */
    public Node loadNode(String fileName) {

        Node loadedNode = null;
        String userHome = System.getProperty("user.home");
        BinaryImporter importer = BinaryImporter.getInstance();
        importer.setAssetManager(assetManager);
        File file = new File(userHome + "/" + fileName + ".j3o");
        if (file.exists()) {
            try {
                loadedNode = (Node) importer.load(file);
                loadedNode.setName(fileName);
                System.out.println("Successfully Loaded Node " + fileName);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "No saved node loaded.", ex);
            }
        }
        return loadedNode;
    }

    /**
     * sort a list with display modes
     *
     * @param modes display modes list to be sorted
     * @return modes2 the sorted list with modes
     */
    private static DisplayMode[] sortModes(DisplayMode[] modes) {
        DisplayMode[] modes2;
        for (int i = 1; i < modes.length; i++) {
            for (int j = 0; j < modes.length; j++) {
                if (modes[j].getWidth() < modes[i].getWidth()) {
                    DisplayMode temp = modes[i];
                    modes[i] = modes[j];
                    modes[j] = temp;
                }
            }
        }

        int dupes = 0;
        for (int i = 0; i < modes.length; i++) {
            int j = i + 1;
            if (j < modes.length) {
                if (modes[i].equals(modes[j])) {
                    dupes += 1;
                    System.out.println(dupes);
                }
            }
        }

        int l = modes.length - dupes;
        modes2 = new DisplayMode[l];
        for (int i = 0; i < modes2.length; i++) {
            int j = i + 1;
            if (j < modes2.length) {
                if (!modes[i].equals(modes[j])) {
                    modes2[i] = modes[i];
                }
            }
        }
        return modes2;
    }

    public static void main(String[] args) throws BackingStoreException {

        app = new Main();
        cfg = new AppSettings(true);
        cfg.setTitle("Serenity");
        app.setShowSettings(false);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        modes = sortModes(device.getDisplayModes());

        cfg.setRenderer(AppSettings.LWJGL_OPENGL2);
        cfg.setResolution(modes[0].getWidth(), modes[0].getHeight());
        cfg.setFullscreen(device.isFullScreenSupported());
        cfg.setVSync(false);
        cfg.setSamples(0);
        cfg.setDepthBits(-1);
        cfg.setFrameRate(60);
        cfg.setFrequency(cfg.getFrameRate());
        app.setDisplayFps(showFps);
        app.setDisplayStatView(false);

        cfg.load(cfg.getTitle());

        if (cfg.getRenderer().equals(AppSettings.LWJGL_OPENGL3)) {
            cfg.setGammaCorrection(true);
        } else {
            cfg.setGammaCorrection(false);
        }

        System.out.println("isGamaCorrection : " + cfg.isGammaCorrection());

        if (openAl) {
            cfg.setAudioRenderer(AppSettings.LWJGL_OPENAL);
        }

        if (opencl) {
            cfg.setOpenCLSupport(true);
            cfg.setOpenCLPlatformChooser(CustomPlatformChooser.class);
        }

        app.setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        app.setPauseOnLostFocus(true);

        app.setSettings(cfg);

        app.start();
    }

    /**
     * tries to save the cfg to disk ( Logs error) and to stop zombie tasks
     */
    @Override
    public void stop() {

        exec.shutdown();

        try {
            cfg.save(cfg.getTitle());
        } catch (BackingStoreException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            saveNode(settingsNode);
        } catch (Exception ex) {
            // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.stop();
    }
    protected String s;

    @Override
    public void simpleInitApp() {

        inputManager.setMouseCursor((JmeCursor) assetManager.loadAsset("cursor.ani"));

        settingsNode = loadNode("settings");

        if (settingsNode != null) {
            bloomEnabled = settingsNode.getUserData("bloomEnabled");
            fogEnabled = settingsNode.getUserData("fogEnabled");
            lightScatterEnabled = settingsNode.getUserData("lightScatterEnabled");
            anisotropyEnabled = settingsNode.getUserData("anisotropyEnabled");
            waterPostProcessing = settingsNode.getUserData("waterPostProcessing");
            globalLightningEnabled = settingsNode.getUserData("globalLightningEnabled");
            isGl3 = settingsNode.getUserData("isGl3");
            shadows = settingsNode.getUserData("shadows");
        } else {
            settingsNode = new Node("settings");
            settingsNode.setUserData("bloomEnabled", true);
            settingsNode.setUserData("fogEnabled", true);
            settingsNode.setUserData("lightScatterEnabled", true);
            settingsNode.setUserData("anisotropyEnabled", true);
            settingsNode.setUserData("waterPostProcessing", true);
            settingsNode.setUserData("globalLightningEnabled", true);
            settingsNode.setUserData("isGl3", false);
            settingsNode.setUserData("shadows", true);

            saveNode(settingsNode);
        }

        inputManager.setCursorVisible(false);
        flyCam.setEnabled(false);

        videoRecorderAppState = new VideoRecorderAppState();

        startScreenState = new StartScreenState(this);
        settingsScreenState = new SettingsScreenState(this);

        stateManager.attach(startScreenState);

        niftyDisplay = new NiftyJmeDisplay(
                assetManager, app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();

        app.getGuiViewPort().addProcessor(niftyDisplay);

        s
                = "F1          - Super Vision\n"
                + "F2          - show FPS\n"
                + "F3          - show Statistics\n"
                + "F6          - Record Video\n"
                + "F10         - Timedemo \n"
                + "F11         - Restart\n"
                + "F12         - Quit\n"
                + "Q           - Debug Vision\n"
                + "Up          - Speed up Time\n"
                + "Down        - Speed down Time\n"
                + "W           - forward\n"
                + "a           - strafe left\n"
                + "d           - strafe right \n"
                + "s           - backwards\n"
                + "space       - jump\n"
                + "l           - Change FOV\n"
                + "r           - change weather randomly\n"
                + "f           - flashlight\n"
                + "e           - switch to rotation mode\n"
                + "t           - disable fps restriction 30 / 60\n"
                + "o           - print nodes in rootnode to terminal\n"
                + "p           - open second viewport - ie the sun\n"
                + "back        - open menu\n"
                + "leftmouse   - attack\n"
                + "rightmouse  - rotate view ( rotation mode only  ) \n"
                + "mouse wheel - zoom in or out \n"
                + "b           - show health bar";
        System.out.println(s);
        initStartGui();
        console = nifty.getScreen("console").findNiftyControl("console", Console.class);

        addListener();
        add_mapping();

        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText(s);
        helloText.setLocalTranslation(75, (cam.getHeight()) - (cam.getHeight() / 6), 0);
        guiNode.attachChild(helloText);
        helloText.setCullHint(Spatial.CullHint.Always);

        deathText = new BitmapText(guiFont, false);
        deathText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        deathText.setColor(ColorRGBA.Red);
        deathText.setText("");
        deathText.setLocalTranslation((cam.getWidth()) - (cam.getWidth() / 2), (cam.getHeight()) - (cam.getHeight() / 2), 0);
        guiNode.attachChild(deathText);
        deathText.setCullHint(Spatial.CullHint.Always);
        app.getRenderManager().setAlphaToCoverage(true);
    }

    /**
     * add all action listener at once
     */
    private void addListener() {
        inputManager.addListener(actionListener, new String[]{"record"});
        inputManager.addListener(actionListener, new String[]{"restart"});
        inputManager.addListener(actionListener, new String[]{"exit"});
        inputManager.addListener(actionListener, new String[]{"switchStats"});
        inputManager.addListener(actionListener, new String[]{"rain_trigger"});
        inputManager.addListener(actionListener, new String[]{"Game Pause Unpause"});
        inputManager.addListener(actionListener, new String[]{"superDebug"});
        inputManager.addListener(actionListener, new String[]{"fpsSwitch_trigger"});
        inputManager.addListener(actionListener, new String[]{"help"});
        inputManager.addListener(actionListener, new String[]{"console"});
    }

    /**
     * add all keyboard mappings at once
     */
    public void add_mapping() {
        inputManager.addMapping("rain_trigger", rain_trigger);
        inputManager.addMapping("fpsSwitch_trigger", fpsSwitch_trigger);
        inputManager.addMapping("superDebug", superDebug_trigger);
        inputManager.addMapping("Game Pause Unpause", pause_trigger);
        inputManager.addMapping("record", record_trigger);
        inputManager.addMapping("restart", restart_trigger);
        inputManager.addMapping("exit", exit_trigger);
        inputManager.addMapping("switchStats", statsViewTrigger);
        inputManager.addMapping("help", helpTrigger);
        inputManager.addMapping("console", consoleTrigger);
    }

    /**
     * remove local keyboard trigger
     */
    public void remove_trigger() {
        inputManager.deleteTrigger("rain_trigger", rain_trigger);
        inputManager.deleteTrigger("fpsSwitch_trigger", fpsSwitch_trigger);
        inputManager.deleteTrigger("superDebug", superDebug_trigger);
        inputManager.deleteTrigger("Game Pause Unpause", pause_trigger);
        inputManager.deleteTrigger("record", record_trigger);
        inputManager.deleteTrigger("restart", restart_trigger);
        inputManager.deleteTrigger("exit", exit_trigger);
        inputManager.deleteTrigger("switchStats", statsViewTrigger);
        inputManager.deleteTrigger("help", helpTrigger);
    }

    /**
     * @return number of available Display Modes
     */
    public int getDisplayMode() {
        int c = 0;
        for (DisplayMode dm : modes) {

            if (dm.getHeight() == cfg.getHeight() && dm.getWidth() == cfg.getWidth()) {
                break;
            } else {
                c += 1;
            }
        }
        return c;
    }

    /**
     * full deconstruction of the game state and fresh start of the game
     */
    public void doRestart() {

        System.out.println("restart");

        if (stateManager.hasState(gameRunningState)) {
            if (loadFuture == null) {

                inputEnabled = false;
                rootNode.detachAllChildren();
                stateManager.attach(startScreenState);
                app.getStateManager().detach(gameRunningState.getBulletAppState());
                stateManager.detach(gameRunningState);
                viewPort.clearProcessors();
                gameRunningState = null;
                deathCounter = 0;
                app.restart();
                switchGameState();
            }
        }
    }

    protected boolean isRecording;

    private final ActionListener actionListener = new ActionListener() {

        @Override
        @SuppressWarnings("Convert2Lambda")
        public void onAction(String name, boolean isPressed, float tpf) {

            if (stateManager.hasState(getGameRunningState())) {
                getGameRunningState().getPlayerCOntrol().setIdleCounter(0);
            }

            /**
             * display or hide the onscreem console
             */
            if (name.equals("console") && !isPressed) {

                showConsole = !showConsole;

                if (stateManager.hasState(getGameRunningState()) && getGameRunningState() != null) {
                    if (showConsole) {
                        remove_trigger();
                        nifty.gotoScreen("console");
                        console.clear();
                    } else {
                        add_mapping();
                        nifty.gotoScreen("game");
                        showConsole = false;
                    }
                }
                /*else if (stateManager.hasState(startScreenState) && gameRunningState != null) {
                    nifty.gotoScreen("start");
                    showConsole = false;
                } else if (stateManager.hasState(settingsScreenState) && gameRunningState != null) {
                    nifty.gotoScreen("settings");
                    showConsole = false;
                }*/

            }

            /**
             * display keyboard mapping
             */
            if (name.equals("help") && !isPressed) {

                if (!isRecording) {
                    if (helloText.getCullHint() == Spatial.CullHint.Always) {
                        helloText.setCullHint(Spatial.CullHint.Never);
                    } else if (helloText.getCullHint() == Spatial.CullHint.Never) {
                        helloText.setCullHint(Spatial.CullHint.Always);
                    }
                }

            }

            /**
             * change to random weather settings
             */
            if (name.equals("rain_trigger") && !isPressed) {
                if (stateManager.hasState(getGameRunningState())) {
                    getGameRunningState().getLocalRoot().getControl(WeatherControl.class).startRandomWeather();
                }
            }

            /**
             * show or hide statistics in lower left gui
             */
            if (name.equals("switchStats") && !isPressed) {
                displayStatView = !displayStatView;
                app.setDisplayStatView(displayStatView);
                app.getContext().restart();
            }

            /**
             * show or hide framesPerSecond in lower left gui
             */
            if (name.equals("fpsSwitch_trigger") && !isPressed) {

                switchFps();
            }

            /**
             * switch between wireframe view
             */
            if (name.equals("superDebug") && !isPressed) {
                wireframe = !wireframe;
                rootNode.depthFirstTraversal(new SceneGraphVisitor() {
                    @Override
                    public void visit(Spatial spatial) {
                        if (spatial instanceof Geometry) {
                            ((Geometry) spatial).getMaterial().getAdditionalRenderState().setWireframe(wireframe);
                        }
                    }
                });
            }

            /**
             * pause the game and jump to startscreen , return to the game on
             * continue
             */
            if (name.equals("Game Pause Unpause") && !isPressed) {
                if (getGameRunningState() != null) {
                    switchGameState();
                }
            }

            if (name.equals("restart") && !isPressed) {
                doRestart();
            }

            /**
             * full shutdown of the application
             */
            if (name.equals("exit") && !isPressed) {
                app.stop();
            }

            /**
             * create video recording of your game
             */
            if (name.equals("record") && !isPressed) {

                if (stateManager.hasState(getGameRunningState())) {
                    if (stateManager.hasState(videoRecorderAppState)) {
                        stateManager.detach(videoRecorderAppState);
                        isRecording = false;
                        if (helloText.getCullHint() == Spatial.CullHint.Always) {
                            helloText.setCullHint(Spatial.CullHint.Never);
                        }
                        System.out.println("finished recording");
                    } else if (!stateManager.hasState(videoRecorderAppState)) {
                        if (!gameRunningState.isTimeDemo) {
                            stateManager.attach(videoRecorderAppState);
                            helloText.setCullHint(Spatial.CullHint.Never);
                            isRecording = true;
                            System.out.println("start record");
                        }
                    }
                } else {
                    System.out.println("start game to begin recording");
                }
            }
        }
    };

    /**
     * show or hide framesPerSecond in lower left gui
     */
    private void switchFps() {
        showFps = !showFps;
        app.setDisplayFps(showFps);
    }

    /**
     * open settings screen
     */
    public void switchOptionsState() {
        if (stateManager.hasState(startScreenState)) {
            stateManager.detach(startScreenState);
            stateManager.attach(settingsScreenState);
            nifty.gotoScreen("settings");
            System.out.println("switching to settings...");
        } else if (stateManager.hasState(settingsScreenState)) {
            stateManager.detach(settingsScreenState);
            stateManager.attach(startScreenState);
            nifty.gotoScreen("start");
            System.out.println("switching to startscreen...");
        }
    }

    /**
     * create nifty gui screens and controls
     */
    public void initStartGui() {

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        nifty.addScreen("game", new ScreenBuilder("Game Screen") {
            {
                controller(app);

                layer(new LayerBuilder("Layer_ID") {
                    {
                        childLayoutOverlay();
                    }
                });
            }
        }.build(nifty));

        nifty.addScreen("console", new ScreenBuilder("Console") {
            {
                controller(app);

                layer(new LayerBuilder("Layer_ID") {
                    {
                        childLayoutHorizontal();

                        control(new ConsoleBuilder("console") {
                            {
                                width("30%");
                                lines(10);
                                alignLeft();
                                valignBottom();
                                onStartScreenEffect(new EffectBuilder("move") {
                                    {
                                        length(75);
                                        inherit();
                                        neverStopRendering(true);
                                        effectParameter("mode", "in");
                                        effectParameter("direction", "top");
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }.build(nifty));

        nifty.getScreen("console").addKeyboardInputHandler(new DefaultInputMapping(), this);

        nifty.addScreen("start", new ScreenBuilder("Start Screen") {
            {
                controller(app);

                layer(new LayerBuilder("Layer_ID") {
                    {
                        childLayoutHorizontal();

                        control(new ButtonBuilder("StartButton", "Start Continue") {
                            {
                                align(ElementBuilder.Align.Left);
                                height("5%");
                                width("10%");
                                visibleToMouse(true);
                                interactOnClick("switchGameState()");
                            }
                        });
                        control(new ButtonBuilder("SettingsButton", "Settings") {
                            {
                                align(ElementBuilder.Align.Center);
                                height("5%");
                                width("10%");
                                visibleToMouse(true);
                                interactOnClick("switchOptionsState()");
                            }
                        });
                        control(new ButtonBuilder("QuitButton", "Quit") {
                            {
                                align(ElementBuilder.Align.Right);
                                height("5%");
                                width("10%");
                                visibleToMouse(true);
                                interactOnClick("doShutdown()");
                            }
                        });
                    }
                });
            }
        }.build(nifty));

        nifty.addScreen("settings", new ScreenBuilder("Settings Screen") {
            {
                controller(app);

                layer(new LayerBuilder("Layer_ID") {
                    {
                        childLayoutVertical();
                        control(new ButtonBuilder("BackButton", "Back") {
                            {
                                align(ElementBuilder.Align.Left);
                                height("5%");
                                width("5%");
                                visibleToMouse(true);
                                interactOnClick("switchOptionsState()");
                            }
                        });
                        control(new ButtonBuilder("ApplyButton", "Apply") {
                            {
                                align(ElementBuilder.Align.Right);
                                height("5%");
                                width("5%");
                                visibleToMouse(true);
                                interactOnClick("applyResolution()");
                            }
                        });
                        // panel added
                        panel(new PanelBuilder("panel_top") {
                            {
                                childLayoutHorizontal();
                                alignCenter();
                                backgroundColor("#f008");
                                height("25%");
                                width("75%");

                                control(new LabelBuilder("keyEventLabelIdFullScreen", "FullScreen:"));
                                control(new CheckboxBuilder("fullscreenCheckbox") {
                                    {
                                        checked(cfg.isFullscreen());
                                        interactOnClick("switchFullScreen()");
                                    }
                                });
                                control(new LabelBuilder("keyEventLabelIdVSync", "VSync:"));
                                control(new CheckboxBuilder("vSyncCheckbox") {
                                    {
                                        checked(cfg.isVSync());
                                        interactOnClick("switchVsync()");
                                    }
                                });

                                SliderBuilder sliderBuilderA = new SliderBuilder("sliderA", false);
                                sliderBuilderA.max(modes.length - 1);
                                sliderBuilderA.stepSize(1);
                                sliderBuilderA.initial(getDisplayMode());
                                sliderBuilderA.buttonStepSize(1);
                                control(sliderBuilderA);
                                control(new LabelBuilder("resolutionLabel", cfg.getWidth() + " x " + cfg.getHeight()));

                                SliderBuilder sliderBuilderB = new SliderBuilder("sliderB", false);
                                sliderBuilderB.max(8);
                                sliderBuilderB.stepSize(2);
                                sliderBuilderB.initial(cfg.getSamples());
                                sliderBuilderB.buttonStepSize(2);
                                control(sliderBuilderB);
                                control(new LabelBuilder("sampleLabel", "AAx" + Integer.toString(cfg.getSamples())));
                            }
                        });

                        panel(new PanelBuilder("panel_bottom") {

                            {
                                childLayoutHorizontal();
                                alignCenter();
                                backgroundColor("#00f8");
                                height("25%");
                                width("75%");

                                control(new ButtonBuilder("BloomButton", "Bloom : " + bloomEnabled) {
                                    {
                                        align(ElementBuilder.Align.Right);
                                        height("5%");
                                        width("10%");
                                        visibleToMouse(true);
                                        interactOnClick("switchBloom()");
                                    }
                                });

                                control(new ButtonBuilder("openglButton", "OpenGL = " + isGl3) {
                                    {
                                        align(ElementBuilder.Align.Right);
                                        height("5%");
                                        width("10%");
                                        visibleToMouse(true);
                                        interactOnClick("switchOpenGl()");
                                    }
                                });

                                control(new ButtonBuilder("lightScatterButton", "LightScatter = " + isGl3) {
                                    {
                                        align(ElementBuilder.Align.Right);
                                        height("5%");
                                        width("10%");
                                        visibleToMouse(true);
                                        interactOnClick("switchLightScatter()");
                                    }
                                });

                                control(new ButtonBuilder("globalLightningButton", "globalLighting = " + globalLightningEnabled) {
                                    {
                                        align(ElementBuilder.Align.Right);
                                        height("5%");
                                        width("10%");
                                        visibleToMouse(true);
                                        interactOnClick("switchGlobalLightning()");
                                    }
                                });

                                control(new ButtonBuilder("shadowsButton", "shadows = " + shadows) {
                                    {
                                        align(ElementBuilder.Align.Right);
                                        height("5%");
                                        width("10%");
                                        visibleToMouse(true);
                                        interactOnClick("switchShadows()");
                                    }
                                });

                                control(new ButtonBuilder("anisotropyButton", "Anisotropy = " + anisotropyEnabled) {
                                    {
                                        align(ElementBuilder.Align.Right);
                                        height("5%");
                                        width("10%");
                                        visibleToMouse(true);
                                        interactOnClick("switchAnisotropy()");
                                    }
                                });

                                control(new ButtonBuilder("waterButton", "WaterPP = " + waterPostProcessing) {
                                    {
                                        align(ElementBuilder.Align.Right);
                                        height("5%");
                                        width("10%");
                                        visibleToMouse(true);
                                        interactOnClick("switchPostProcessWater()");
                                    }
                                });
                            }
                        });

                    }
                });
            }
        }.build(nifty));
        nifty.gotoScreen("start");
    }

    /**
     * applies a new video resolution , in case of multisamples the application
     * will exit
     */
    public void applyResolution() {
        int value = (int) nifty.getScreen("settings").findNiftyControl("sliderA", Slider.class).getValue();
        int samples = (int) nifty.getScreen("settings").findNiftyControl("sliderB", Slider.class).getValue();

        nifty.getScreen("settings").findNiftyControl("resolutionLabel", Label.class).setText(modes[value].getWidth() + " x " + modes[value].getHeight());
        cfg.setResolution(modes[value].getWidth(), modes[value].getHeight());

        if (cfg.getSamples() != samples) {
            System.out.println("will Stop because samples");
            cfg.setSamples(samples);
            app.setSettings(cfg);
            app.stop();
        } else {
            app.setSettings(cfg);
            doRestart();
        }
        nifty.resolutionChanged();
    }

    @NiftyEventSubscriber(pattern = "slider*.")
    public void onSliderChangedEvent(final String id, final SliderChangedEvent event) {
        if (id.equals("sliderA")) {
            int height = (int) modes[(int) event.getValue()].getHeight();
            int width = (int) modes[(int) event.getValue()].getWidth();
            nifty.getScreen("settings").findNiftyControl("resolutionLabel", Label.class).setText(width + " x " + height);
        }
        if (id.equals("sliderB")) {
            String val = Float.toString(event.getValue());
            nifty.getScreen("settings").findNiftyControl("sampleLabel", Label.class).setText("AAx" + val);
        }
    }

    /**
     * apply nifty control changes , update vsync app settings and restart game
     */
    public void switchVsync() {
        nifty.getScreen("settings").findNiftyControl("vSyncCheckbox", CheckBox.class).setChecked(!nifty.getScreen("settings").findNiftyControl("vSyncCheckbox", CheckBox.class).isChecked());
        cfg.setVSync(nifty.getScreen("settings").findNiftyControl("vSyncCheckbox", CheckBox.class).isChecked());
        app.setSettings(cfg);
        doRestart();
    }

    /**
     * apply nifty control changes , apply fullscreen settings and restart
     */
    public void switchFullScreen() {

        nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).setChecked(!nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).isChecked());
        cfg.setFullscreen(nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).isChecked());

        if (nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).isChecked()) {
            if (!GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported()) {
                nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).setChecked(false);
                cfg.setFullscreen(false);
            }
        }
        nifty.resolutionChanged();
        nifty.update();
        app.setSettings(cfg);
        doRestart();
    }

    /**
     * switch between game and startscreen or start game if none existing
     */
    public void switchGameState() {

        System.out.println("switchting Game State");

        if (stateManager.hasState(gameRunningState)) {
            stateManager.detach(gameRunningState);
            stateManager.attach(startScreenState);
            nifty.gotoScreen("start");
            System.out.println("switching to startscreen...");

        } else {

            if (gameRunningState == null) {

                inputManager.setCursorVisible(false);

                if (guiViewPort.getProcessors().contains(niftyDisplay)) {
                    nifty.gotoScreen("game");
                    showConsole = false;
                }

                loadFuture = exec.submit(loadingCallable);

            } else {

                if (stateManager.hasState(startScreenState)) {

                    stateManager.detach(startScreenState);
                    stateManager.attach(gameRunningState);

                    if (guiViewPort.getProcessors().contains(niftyDisplay)) {
                        nifty.gotoScreen("game");
                        showConsole = false;
                    }
                    System.out.println("switching to game...");
                }
            }
        }
    }

    /**
     * in case game lose focus pause to avoid uncontrolled game progress
     */
    @Override
    public void loseFocus() {
        System.out.println("lostFocus");
        if (gameRunningState != null) {
            if (gameRunningState.getIsRunning()) {
                stateManager.detach(gameRunningState);
                stateManager.attach(startScreenState);
                if (!guiViewPort.getProcessors().contains(niftyDisplay)) {
                    guiViewPort.addProcessor(niftyDisplay);
                }
                nifty.gotoScreen("start");
            }
        }
        super.loseFocus();
    }

    /**
     * Main Game Loop
     *
     * @param tpf times per frame
     */
    @Override
    @SuppressWarnings("Convert2Lambda")
    public void simpleUpdate(final float tpf) {

        /**
         * if player dies display message count down and restart applicatiom
         */
        if (gameRunningState != null) {
            if (gameRunningState.getHealth() <= 0) {
                deathCounter += tpf;
                deathText.setCullHint(Spatial.CullHint.Never);
                int dt = (int) (9 - deathCounter);
                deathText.setText("restart in " + dt);
                nifty.gotoScreen("game");
                showConsole = false;
                if (deathCounter >= 9 && deathCounter < 10) {
                    deathText.setCullHint(Spatial.CullHint.Always);
                    deathText.setText("");
                    deathCounter = 11;
                    inputEnabled = false;
                    showConsole = false;
                    addListener();
                    add_mapping();
                    doRestart();
                }
            }
        }

        /**
         * in case the game screen is being recorded, blinking REC message is
         * being displayed
         */
        if (isRecording) {
            helloText.setColor(ColorRGBA.Red);
            helloText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
            helloText.setText(" REC ");
            if (helloText.getControl(FlipFlopControl.class) == null) {
                helloText.addControl(new FlipFlopControl(0.3f) {
                    @Override
                    void action() {
                        if (helloText.getCullHint() == Spatial.CullHint.Always) {
                            helloText.setCullHint(Spatial.CullHint.Never);
                        } else {
                            helloText.setCullHint(Spatial.CullHint.Always);
                        }
                    }
                });
            }
        } else {
            if (helloText.getControl(FlipFlopControl.class) != null) {
                helloText.setCullHint(Spatial.CullHint.Always);
                helloText.removeControl(FlipFlopControl.class);
            }
            helloText.setColor(ColorRGBA.White);
            helloText.setSize(guiFont.getCharSet().getRenderedSize());
            helloText.setText(s);
        }

        /**
         * rotate Box while Game is being loaded
         */
        if (loadFuture != null) {

            if (stateManager.hasState(startScreenState)) {
                startScreenState.attachBox();
                //inputManager.clearMappings();
                enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {

                        startScreenState.getBoxGeo().rotate(0, 0, tpf);
                        return null;
                    }
                });
            }

            /**
             * remove Rotating Box detach StartScreen State attach Game State
             * set multithreading to null enable user input
             */
            if (gameRunningState != null) {
                startScreenState.detachBox();
                stateManager.detach(startScreenState);
                stateManager.attach(gameRunningState);
                loadFuture = null;
                inputEnabled = true;
                //add_mapping();
            }
        }
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        // System.out.println("bind( " + screen.getScreenId() + ")");
    }

    @Override
    public void onStartScreen() {
        // System.out.println("onStartScreen");
    }

    @Override
    public void onEndScreen() {
        // System.out.println("onEndScreen");
    }

    public void quit() {
        nifty.gotoScreen("end");
    }

    /**
     * stop application
     */
    public void doShutdown() {
        app.stop();
    }

    /**
     * @return true if fps is shown in gui
     */
    public static boolean isShowFps() {
        return showFps;
    }

    /**
     *
     * @param aShowFps true to show fps
     */
    public static void setShowFps(boolean aShowFps) {
        showFps = aShowFps;
    }

    @Override
    public boolean keyEvent(NiftyInputEvent nie) {
        return false;
        //
    }

    @NiftyEventSubscriber(id = "console")
    public void onConsoleExecuteCommandEvent(final String id, final ConsoleExecuteCommandEvent cEvent) {

        console.clear();

        switch (cEvent.getCommand()) {
            case "show_root":
                if (stateManager.hasState(gameRunningState)) {
                    gameRunningState.treeoutroot(getRootNode());
                }
                break;
            case "restart":
                console.output("restarting");
                doRestart();
                break;
            case "shutdown":
                doShutdown();
                break;
        }

        //TODO: collect meaningful list of commands for the console
    }

    public static class CustomPlatformChooser implements PlatformChooser {

        public CustomPlatformChooser() {
        }

        @Override
        public List<? extends Device> chooseDevices(List<? extends Platform> platforms) {
            synchronized (sync) {
                if (currentDeviceIndex == -1) {
                    return Collections.emptyList();
                }

                Platform platform = platforms.get(0);
                availableDevices = platform.getDevices();
                selectedPlatform = platform;

                Device device = platform.getDevices().get(currentDeviceIndex);
                currentDeviceIndex++;
                if (currentDeviceIndex >= availableDevices.size()) {
                    currentDeviceIndex = -1;
                }

                return Collections.singletonList(device);
            }
        }

    }
}
