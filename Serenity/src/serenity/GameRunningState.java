package serenity;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public class GameRunningState extends AbstractAppState {

    private boolean fogEnabled;
    private boolean bloomEnabled;
    private boolean lightScatterEnabled;
    private boolean anisotropyEnabled;
    private boolean shadows;
    private boolean waterPostProcessingEnabled;
    private boolean globalLightningEnabled;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        super.initialize(stateManager, app);

        System.out.println("Game State is being initialized");

        SimpleApplication sa = (SimpleApplication) app;

        this.fogEnabled = Main.isFogEnabled();
        this.bloomEnabled = Main.isBloomEnabled();
        this.lightScatterEnabled = Main.isLightScatterEnabled();
        this.anisotropyEnabled = Main.isAnisotropyEnabled();
        this.waterPostProcessingEnabled = Main.isWaterPostProcessing();
        this.shadows = Main.isShadows();
        this.globalLightningEnabled = Main.isGlobalLightningEnabled();
    }

    @Override
    public void update(float tpf) {
        //
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //
    }

}
