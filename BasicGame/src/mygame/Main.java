package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ElementBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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

    @SuppressWarnings("Convert2Lambda")
    Callable<Void> loadingCallable = new Callable<Void>() {
        @Override
        public Void call() {
            gameRunningState = new GameRunningState(app);
            return null;
        }
    };

    public static void main(String[] args) {

        app = new Main();
        cfg = new AppSettings(true);
        cfg.setTitle("Serenity");
        app.setShowSettings(false);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();
        cfg.setResolution(modes[0].getWidth(), modes[0].getHeight());
        cfg.setFullscreen(device.isFullScreenSupported());
        cfg.setVSync(false);
        cfg.setSamples(0);

        try {
            cfg.setRenderer(AppSettings.LWJGL_OPENGL3);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            cfg.setRenderer(AppSettings.LWJGL_OPENGL2);
        }

        try {
            cfg.load(cfg.getTitle());
        } catch (BackingStoreException ex) {
            // config load is always true 
        }

        app.setDisplayFps(false);
        app.setDisplayStatView(false);
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

    public void doRestart() {
        app.getGuiViewPort().removeProcessor(niftyDisplay);
        System.out.println("restart");
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
        gameRunningState = null;
        stateManager.attach(startScreenState);
        app.getGuiViewPort().addProcessor(niftyDisplay);
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.gotoScreen("start");
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
                        childLayoutHorizontal();

                        control(new ButtonBuilder("BackButton", "Back") {
                            {
                                align(ElementBuilder.Align.Left);
                                height("5%");
                                width("10%");
                                visibleToMouse(true);
                                interactOnClick("switchOptionsState()");
                            }
                        });
                        control(new LabelBuilder("keyEventLabelId", "FullScreen:"));
                        control(new CheckboxBuilder("fullscreenCheckbox") {
                            {
                                checked(cfg.isFullscreen());
                                interactOnClick("switchFullScreen()");
                            }
                        });
                    }
                });
            }
        }.build(nifty));
        nifty.gotoScreen("start");
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
    public void simpleUpdate(float tpf) {

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
