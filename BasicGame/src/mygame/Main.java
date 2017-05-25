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
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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

    private final static int antiAlias = 0;
    private final static int depthBit = 24;
    private Nifty nifty;

    public static void main(String[] args) {

        app = new Main();
        cfg = new AppSettings(true);
        //cfg.setFrameRate(60);
        cfg.setVSync(false);
        //cfg.setFrequency(60);
        cfg.setResolution(1600, 900);
        cfg.setSamples(antiAlias);
        cfg.setDepthBits(depthBit);
        cfg.setFullscreen(true);
        cfg.setRenderer(AppSettings.LWJGL_OPENGL3);
        cfg.setTitle("Serenity");
        try {
            cfg.load("com.foo.MyCoolGame3");
        } catch (BackingStoreException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        app.setPauseOnLostFocus(true);
        app.setShowSettings(false);
        app.setSettings(cfg);
        app.start();
    }
    private NiftyJmeDisplay niftyDisplay;

    public void toggleToFullscreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();
        int i = 0; // note: there are usually several, let's pick the first
        settings.setResolution(modes[i].getWidth(), modes[i].getHeight());
        settings.setFrequency(modes[i].getRefreshRate());
        settings.setBitsPerPixel(modes[i].getBitDepth());
        settings.setFullscreen(device.isFullScreenSupported());
        app.setSettings(settings);
        app.restart(); // restart the context to apply changes
    }

    @Override
    public void stop() {
        super.stop();
        try {
            cfg.save("com.foo.MyCoolGame3");
        } catch (BackingStoreException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void simpleInitApp() {

        inputManager.setCursorVisible(false);
        flyCam.setEnabled(false);

        videoRecorderAppState = new VideoRecorderAppState();

        setDisplayFps(true);
        setDisplayStatView(false);

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
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.fromXml("Gui/startScreen_Gui.xml", "start", this);
        nifty.gotoScreen("start");

    }

    public void doRestart() {
        app.getGuiViewPort().removeProcessor(niftyDisplay);

        System.out.println("restart");
        app.getContext().restart();

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
        nifty.fromXml("Gui/startScreen_Gui.xml", "start", this);
        nifty.gotoScreen("start");
    }

    public void shutdown() {
        app.stop();
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
                shutdown();
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
            nifty.fromXml("Gui/settingsScreen_Gui.xml", "start", this);
            nifty.gotoScreen("start");
            System.out.println("switching to settings...");
        } else if (stateManager.hasState(settingsScreenState)) {
            stateManager.detach(settingsScreenState);
            stateManager.attach(startScreenState);
            nifty.fromXml("Gui/startScreen_Gui.xml", "start", this);
            nifty.gotoScreen("start");
            System.out.println("switching to startscreen...");
        }
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
            if (stateManager.hasState(startScreenState)) {
                if (gameRunningState != null) {
                    stateManager.detach(startScreenState);
                    stateManager.attach(gameRunningState);
                    if (guiViewPort.getProcessors().contains(niftyDisplay)) {
                        guiViewPort.removeProcessor(niftyDisplay);
                        System.out.println("switching to game...");
                    }
                } else {
                    gameRunningState = new GameRunningState(app);
                    stateManager.detach(startScreenState);
                    stateManager.attach(gameRunningState);
                    if (guiViewPort.getProcessors().contains(niftyDisplay)) {
                        guiViewPort.removeProcessor(niftyDisplay);
                        System.out.println("switching to game...");
                    }
                }
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //
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
}
