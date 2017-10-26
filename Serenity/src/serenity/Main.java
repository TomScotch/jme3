package serenity;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    private static Main app;
    private static AppSettings cfg;

    public static void main(String[] args) {
        app = new Main();
        cfg = new AppSettings(false);
        cfg.setTitle("Serenity");
        app.setShowSettings(true);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
    }

    @Override
    public void simpleInitApp() {
        //
    }

    @Override
    public void simpleUpdate(float tpf) {
        //
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //
    }
}
