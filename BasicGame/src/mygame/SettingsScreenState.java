package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class SettingsScreenState extends AbstractAppState {

    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final AssetManager assetManager;
    private final Node localRootNode = new Node("Settings Screen RootNode");
    private final Node localGuiNode = new Node("Settings Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Black;
    private final Geometry boxGeo;

    public SettingsScreenState(SimpleApplication app) {
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();

        Box boxMesh = new Box(1f, 1f, 1f);
        boxGeo = new Geometry("A Textured Box", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Interface/Logo/Monkey.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxMat.setColor("Color", ColorRGBA.White);
        boxGeo.setMaterial(boxMat);
        localRootNode.attachChild(boxGeo);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        super.initialize(stateManager, app);
        viewPort.setBackgroundColor(backgroundColor);
        app.getInputManager().setCursorVisible(true);
        boxGeo.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    @Override
    public void update(float tpf) {

        boxGeo.rotate(0, -tpf, 0);
        viewPort.getCamera().setLocation(new Vector3f(0, 0, -5.5f));
        viewPort.getCamera().lookAt(boxGeo.getLocalTranslation(), Vector3f.UNIT_Y);
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
