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

public class Main extends SimpleApplication implements ScreenController {

    private static AppSettings cfg;
    private static Main app;

    private static VideoRecorderAppState videoRecorderAppState;

    private final Trigger pause_trigger = new KeyTrigger(KeyInput.KEY_BACK);
    private final Trigger save_trigger = new KeyTrigger(KeyInput.KEY_RETURN);
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
        app.setPauseOnLostFocus(true);
        app.setShowSettings(false);
        app.setSettings(cfg);
        app.start();
    }
    private boolean loadGameState;

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

        inputManager.addMapping("Toggle Settings", save_trigger);
        inputManager.addListener(actionListener, new String[]{"Toggle Settings"});

        inputManager.addMapping("record", record_trigger);
        inputManager.addListener(actionListener, new String[]{"record"});

        inputManager.addMapping("restart", restart_trigger);
        inputManager.addListener(actionListener, new String[]{"restart"});

        inputManager.addMapping("exit", exit_trigger);
        inputManager.addListener(actionListener, new String[]{"exit"});
        
                NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Gui/startScreen_Gui.xml", "start", this);
        app.getGuiViewPort().addProcessor(niftyDisplay);
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.gotoScreen("start");
    }

    private void loadGame() {
        if (gameRunningState == null) {
            startScreenState.startLoading();
            loadGameState = true;
            inputManager.setCursorVisible(false);
        }
    }

    public void doRestart() {

        System.out.println("restart");

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
    }

    public void shutdown() {
        System.out.println("mygame.Main.shutdown()");
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

            if (name.equals("Toggle Settings") && !isPressed) {
                if (stateManager.hasState(startScreenState)) {
                    stateManager.detach(startScreenState);
                    stateManager.attach(settingsScreenState);
                    System.out.println("switching to settings...");
                } else if (stateManager.hasState(settingsScreenState)) {
                    stateManager.detach(settingsScreenState);
                    stateManager.attach(startScreenState);
                    System.out.println("switching to startscreen...");
                }
            }
        }
    };

    public void switchGameState() {
        if (stateManager.hasState(gameRunningState)) {
            stateManager.detach(gameRunningState);
            stateManager.attach(startScreenState);
            System.out.println("switching to startscreen...");
        } else {
            if (stateManager.hasState(startScreenState)) {
                if (gameRunningState != null) {
                    stateManager.detach(startScreenState);
                    stateManager.attach(gameRunningState);
                    System.out.println("switching to game...");
                } else {
                    loadGame();
                }
            }
        }
    }

    private float c = 0f;

    @Override
    public void simpleUpdate(float tpf) {

        if (loadGameState) {
            c += tpf;
            if (c > 0.6f) {
                loadGameState = false;
                c = 0f;
                gameRunningState = new GameRunningState(app);
                startScreenState.setGameIsLoaded(true);
                stateManager.detach(startScreenState);
                stateManager.attach(gameRunningState);
                System.out.println("switching to game...");
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
}
