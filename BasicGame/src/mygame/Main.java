package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    private VideoRecorderAppState videoRecorderAppState;
    private final Trigger pause_trigger = new KeyTrigger(KeyInput.KEY_BACK);
    private final Trigger save_trigger = new KeyTrigger(KeyInput.KEY_RETURN);
    private final Trigger record_trigger = new KeyTrigger(KeyInput.KEY_F6);
    private boolean isRunning = false;
    private static GameRunningState gameRunningState;
    private static StartScreenState startScreenState;
    private static SettingsScreenState settingsScreenState;

    private final static int antiAlias = 0;
    private final static int depthBit = 24;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings cfg = new AppSettings(true);
        cfg.setFrameRate(60);
        cfg.setVSync(false);
        cfg.setFrequency(60);
        cfg.setResolution(1360, 768);
        cfg.setSamples(antiAlias);
        cfg.setDepthBits(depthBit);
        cfg.setFullscreen(false);
        cfg.setRenderer(AppSettings.LWJGL_OPENGL2);
        cfg.setTitle("Serenity");
        app.setPauseOnLostFocus(true);
        app.setShowSettings(false);
        app.setSettings(cfg);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        inputManager.setCursorVisible(false);
        flyCam.setEnabled(false);

        videoRecorderAppState = new VideoRecorderAppState();

        setDisplayFps(true);
        setDisplayStatView(false);

        gameRunningState = new GameRunningState(this);
        startScreenState = new StartScreenState(this);
        settingsScreenState = new SettingsScreenState(this);

        stateManager.attach(startScreenState);

        inputManager.addMapping("Game Pause Unpause", pause_trigger);
        inputManager.addListener(actionListener, new String[]{"Game Pause Unpause"});
        inputManager.addMapping("Toggle Settings", save_trigger);
        inputManager.addListener(actionListener, new String[]{"Toggle Settings"});
        inputManager.addMapping("record", record_trigger);
        inputManager.addListener(actionListener, new String[]{"record"});
    }
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {

            isRunning = gameRunningState.getIsRunning();

            if (name.equals("Game Pause Unpause") && !isPressed) {

                if (isRunning) {
                    stateManager.detach(gameRunningState);
                    stateManager.attach(startScreenState);
                    System.out.println("switching to startscreen...");

                } else {
                    if (stateManager.hasState(startScreenState)) {
                        stateManager.detach(startScreenState);
                        stateManager.attach(gameRunningState);
                        System.out.println("switching to game...");
                    }
                }
            }

            if (name.equals("record") && !isPressed) {

                if (stateManager.hasState(videoRecorderAppState)) {
                    stateManager.detach(videoRecorderAppState);
                    System.out.println("finished recording");
                } else if (!stateManager.hasState(videoRecorderAppState)) {
                    stateManager.attach(videoRecorderAppState);
                    System.out.println("start record");
                }
            }

            if (name.equals("Toggle Settings") && !isPressed && !isRunning) {
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

    @Override
    public void simpleUpdate(float tpf) {
        //super.update();
    }
}
