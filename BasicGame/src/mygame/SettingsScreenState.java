package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

public class SettingsScreenState extends AbstractAppState {

    private final Node rootNode;
    private final Node guiNode;
    private final Node localRootNode = new Node("Settings Screen RootNode");
    private final Node localGuiNode = new Node("Settings Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Black;

    public SettingsScreenState(SimpleApplication app) {

        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        super.initialize(stateManager, app);

        app.getViewPort().setBackgroundColor(backgroundColor);
        app.getInputManager().setCursorVisible(true);
    }

    @Override
    public void update(float tpf) {
        //
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        this.rootNode.attachChild(this.localRootNode);
        this.guiNode.attachChild(this.localGuiNode);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {

        this.rootNode.detachChild(this.localRootNode);
        this.guiNode.detachChild(this.localGuiNode);
    }
}
