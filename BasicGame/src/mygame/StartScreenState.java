package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class StartScreenState extends AbstractAppState {

    /**
     * @return the boxGeo
     */
    public Geometry getBoxGeo() {
        return boxGeo;
    }

    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final Node localRootNode = new Node("Start Screen RootNode");
    private final Node localGuiNode = new Node("Start Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Black;
    private final Geometry boxGeo;

    public StartScreenState(SimpleApplication app) {
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        Box boxMesh = new Box(1, 1, 1);
        boxGeo = new Geometry("Box", boxMesh);
        Material boxMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = app.getAssetManager().loadTexture("Interface/Logo/Monkey.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxMat.setColor("Color", ColorRGBA.White);
        boxGeo.setMaterial(boxMat);
        this.localRootNode.attachChild(boxGeo);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        viewPort.setBackgroundColor(backgroundColor);
        app.getInputManager().setCursorVisible(true);
        localRootNode.attachChild(boxGeo);
        boxGeo.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    @Override
    public void update(float tpf) {
        //boxGeo.rotate(0, -tpf, 0);
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
