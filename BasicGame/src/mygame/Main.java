package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
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

public class Main extends SimpleApplication implements ScreenController {

    private static AppSettings cfg;
    private static Main app;

    private static VideoRecorderAppState videoRecorderAppState;
    private static DisplayMode[] modes;

    private final Trigger pause_trigger = new KeyTrigger(KeyInput.KEY_BACK);
    private final Trigger record_trigger = new KeyTrigger(KeyInput.KEY_F6);
    private final Trigger restart_trigger = new KeyTrigger(KeyInput.KEY_F11);
    private final Trigger exit_trigger = new KeyTrigger(KeyInput.KEY_F12);

    private static GameRunningState gameRunningState;
    private static StartScreenState startScreenState;
    private static SettingsScreenState settingsScreenState;

    private Future loadFuture = null;
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;

    private boolean bloomEnabled;
    private boolean fogEnabled;
    private boolean lightScatterEnabled;
    private boolean anisotropyEnabled;
    private boolean waterPostProcessing;
    private boolean globalLightningEnabled;
    private boolean isGl3;
    private boolean shadows;

    public void switchShadows() {
        this.shadows = !this.shadows;
        nifty.getScreen("settings").findNiftyControl("shadowsButton", Button.class).setText("Shadows : " + shadows);
    }

    public void switchBloom() {
        this.bloomEnabled = !this.bloomEnabled;
        nifty.getScreen("settings").findNiftyControl("BloomButton", Button.class).setText("Bloom : " + bloomEnabled);
    }

    public void switchOpenGl() {

        isGl3 = !isGl3;

        if (isGl3) {
            cfg.setRenderer(AppSettings.LWJGL_OPENGL3);
        } else {
            cfg.setRenderer(AppSettings.LWJGL_OPENGL2);
        }
        nifty.getScreen("settings").findNiftyControl("openglButton", Button.class).setText("OpenGL = " + isGl3);
    }

    public void switchPostProcessWater() {
        waterPostProcessing = !waterPostProcessing;
        nifty.getScreen("settings").findNiftyControl("waterButton", Button.class).setText("WaterPP = " + waterPostProcessing);
    }

    public void switchAnisotropy() {
        anisotropyEnabled = !anisotropyEnabled;
        nifty.getScreen("settings").findNiftyControl("anisotropyButton", Button.class).setText("anisotropy = " + anisotropyEnabled);
    }

    public void switchLightScatter() {
        lightScatterEnabled = !lightScatterEnabled;
        nifty.getScreen("settings").findNiftyControl("lightScatterButton", Button.class).setText("lightScatter = " + lightScatterEnabled);
    }

    public void switchFog() {
        fogEnabled = !fogEnabled;
        nifty.getScreen("settings").findNiftyControl("fogButton", Button.class).setText("fog = " + fogEnabled);
    }

    public void switchGlobalLightning() {
        globalLightningEnabled = !globalLightningEnabled;
        nifty.getScreen("settings").findNiftyControl("globalLightningButton", Button.class).setText("globalLightning = " + globalLightningEnabled);
    }

    @SuppressWarnings("Convert2Lambda")
    Callable<Void> loadingCallable = new Callable<Void>() {
        @Override
        public Void call() {
            gameRunningState = new GameRunningState(app);
            return null;
        }
    };

    public boolean saveNode(Node node) {
        String userHome = System.getProperty("user.home");
        BinaryExporter exporter = BinaryExporter.getInstance();
        File file = new File(userHome + "/somefile.j3o");
        boolean x;
        try {
            exporter.save(rootNode, file);
            x = true;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to save node!", ex);
            x = false;
        }
        return x;
    }

    public Node loadNode(String fileName) {

        Node loadedNode = new Node(fileName);
        String userHome = System.getProperty("user.home");
        BinaryImporter importer = BinaryImporter.getInstance();
        importer.setAssetManager(assetManager);
        File file = new File(userHome + "/somefile.j3o");

        try {
            loadedNode = (Node) importer.load(file);
            loadedNode.setName(fileName);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "No saved node loaded.", ex);
        }
        return loadedNode;
    }

    public static void main(String[] args) throws BackingStoreException {

        app = new Main();
        cfg = new AppSettings(true);
        cfg.setTitle("Serenity");
        app.setShowSettings(false);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        modes = device.getDisplayModes();

        cfg.setRenderer(AppSettings.LWJGL_OPENGL2);
        cfg.setResolution(modes[0].getWidth(), modes[0].getHeight());
        cfg.setFullscreen(device.isFullScreenSupported());
        cfg.setVSync(false);
        cfg.setFrameRate(30);
        cfg.setSamples(0);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        //
        cfg.load(cfg.getTitle());
        //
        app.setSettings(cfg);
        app.start();
    }

    @Override
    public void stop() {

        exec.shutdown();

        try {
            cfg.save(cfg.getTitle());
        } catch (BackingStoreException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.stop();
    }

    @Override
    public void simpleInitApp() {
        inputManager.setCursorVisible(false);
        flyCam.setEnabled(false);

        videoRecorderAppState = new VideoRecorderAppState();

        startScreenState = new StartScreenState(this);
        settingsScreenState = new SettingsScreenState(this);

        stateManager.attach(startScreenState);

        inputManager.addMapping("Game Pause Unpause", pause_trigger);
        inputManager.addListener(actionListener, new String[]{"Game Pause Unpause"});

        inputManager.addMapping("record", record_trigger);
        inputManager.addListener(actionListener, new String[]{"record"});

        inputManager.addMapping("restart", restart_trigger);
        inputManager.addListener(actionListener, new String[]{"restart"});

        inputManager.addMapping("exit", exit_trigger);
        inputManager.addListener(actionListener, new String[]{"exit"});

        niftyDisplay = new NiftyJmeDisplay(
                assetManager, app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();

        app.getGuiViewPort().addProcessor(niftyDisplay);

        initStartGui();
    }

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

    public void doRestart() {

        System.out.println("restart");

        app.getContext().restart();
        app.restart();

        if (stateManager.hasState(gameRunningState)) {
            stateManager.detach(gameRunningState);
        }
        if (stateManager.hasState(startScreenState)) {
            stateManager.detach(startScreenState);
        }
        if (stateManager.hasState(settingsScreenState)) {
            stateManager.detach(settingsScreenState);
        }

        startScreenState = new StartScreenState(this);
        settingsScreenState = new SettingsScreenState(this);

        stateManager.attach(startScreenState);
        nifty.gotoScreen("settings");
        nifty.resolutionChanged();
        nifty.update();
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {

            if (name.equals("Game Pause Unpause") && !isPressed) {

                switchGameState();
            }

            if (name.equals("restart") && !isPressed) {
                doRestart();
            }

            if (name.equals("exit") && !isPressed) {
                app.stop();
            }

            if (name.equals("record") && !isPressed) {

                if (stateManager.hasState(gameRunningState)) {
                    if (stateManager.hasState(videoRecorderAppState)) {
                        stateManager.detach(videoRecorderAppState);
                        System.out.println("finished recording");
                    } else if (!stateManager.hasState(videoRecorderAppState)) {
                        stateManager.attach(videoRecorderAppState);
                        System.out.println("start record");
                    }
                } else {
                    System.out.println("start game to begin recording");
                }
            }
        }
    };

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

    public void initStartGui() {

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

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
                                sliderBuilderA.max(modes.length);
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

    }

    @NiftyEventSubscriber(pattern = "slider*.")
    public void onSliderChangedEvent(final String id, final SliderChangedEvent event) {
        System.out.println(id);
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

    public void switchVsync() {
        nifty.getScreen("settings").findNiftyControl("vSyncCheckbox", CheckBox.class).setChecked(!nifty.getScreen("settings").findNiftyControl("vSyncCheckbox", CheckBox.class).isChecked());
        cfg.setVSync(nifty.getScreen("settings").findNiftyControl("vSyncCheckbox", CheckBox.class).isChecked());
        app.setSettings(cfg);
        doRestart();
    }

    public void switchFullScreen() {

        nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).setChecked(!nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).isChecked());
        cfg.setFullscreen(nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).isChecked());

        if (nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).isChecked()) {
            if (!GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported()) {
                nifty.getScreen("settings").findNiftyControl("fullscreenCheckbox", CheckBox.class).setChecked(false);
                cfg.setFullscreen(false);
            }
        }

        app.setSettings(cfg);
        doRestart();
    }

    public void switchGameState() {
        System.out.println("switchting Game State");
        if (stateManager.hasState(gameRunningState)) {
            stateManager.detach(gameRunningState);
            stateManager.attach(startScreenState);
            if (!guiViewPort.getProcessors().contains(niftyDisplay)) {
                guiViewPort.addProcessor(niftyDisplay);
            }
            System.out.println("switching to startscreen...");
        } else {

            if (gameRunningState == null) {

                inputManager.setCursorVisible(false);

                if (guiViewPort.getProcessors().contains(niftyDisplay)) {
                    guiViewPort.removeProcessor(niftyDisplay);
                }

                loadFuture = exec.submit(loadingCallable);
            } else {
                if (stateManager.hasState(startScreenState)) {
                    app.getRenderManager().preloadScene(gameRunningState.getLocalRoot());
                    stateManager.detach(startScreenState);
                    stateManager.attach(gameRunningState);
                    if (guiViewPort.getProcessors().contains(niftyDisplay)) {
                        guiViewPort.removeProcessor(niftyDisplay);
                    }
                    System.out.println("switching to game...");
                }
            }
        }
    }

    @Override
    public void simpleUpdate(final float tpf) {

        if (loadFuture != null) {

            if (stateManager.hasState(startScreenState)) {
                startScreenState.attachBox();
                enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {

                        startScreenState.getBoxGeo().rotate(0, -tpf, 0);
                        return null;
                    }
                });
            }

            if (gameRunningState != null) {
                app.getRenderManager().preloadScene(gameRunningState.getLocalRoot());
                startScreenState.detachBox();
                stateManager.detach(startScreenState);
                stateManager.attach(gameRunningState);
                loadFuture = null;
            }
        }
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        System.out.println("bind( " + screen.getScreenId() + ")");
    }

    @Override
    public void onStartScreen() {
        System.out.println("onStartScreen");
    }

    @Override
    public void onEndScreen() {
        System.out.println("onEndScreen");
    }

    public void quit() {
        nifty.gotoScreen("end");
    }

    public void doShutdown() {
        app.stop();
    }
}
