package serenity;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class StartScreenState extends AbstractAppState {

    private final Geometry geo = new Geometry("Box", new Box(1, 1, 1));

    private ViewPort viewPort;

    private Node rootNode;

    private final Node localRootNode = new Node("Start Screen RootNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Black;
    private final Geometry boxGeo = new Geometry("Box", new Box(1, 1, 1));

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        super.initialize(stateManager, app);

        SimpleApplication sa = (SimpleApplication) app;

        this.rootNode = sa.getRootNode();
        this.viewPort = sa.getViewPort();

        viewPort.setBackgroundColor(backgroundColor);

        Material boxMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = app.getAssetManager().loadTexture("Textures/gloemtoi.png");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxMat.setColor("Color", ColorRGBA.White);
        boxGeo.setMaterial(boxMat);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = app.getAssetManager().loadTexture("Textures/dragon_logo.png");
        mat.setTexture("ColorMap", tex);
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geo.setMaterial(mat);
        localRootNode.attachChild(geo);

        this.rootNode.attachChild(this.localRootNode);

        app.getInputManager().setCursorVisible(true);
    }

    @Override
    public void update(float tpf) {
        if (localRootNode.hasChild(boxGeo)) {
            viewPort.getCamera().setLocation(new Vector3f(0, 0, -5.5f));
            viewPort.getCamera().lookAt(boxGeo.getLocalTranslation(), Vector3f.UNIT_Y);
        }
        if (localRootNode.hasChild(geo)) {
            viewPort.getCamera().setLocation(new Vector3f(0, 0, -5.5f));
            viewPort.getCamera().lookAt(geo.getLocalTranslation(), Vector3f.UNIT_Y);
        }
    }

    public void attachBox() {
        if (!localRootNode.hasChild(boxGeo)) {
            localRootNode.detachChild(geo);
            localRootNode.attachChild(boxGeo);
        }
    }

    public void detachBox() {
        if (localRootNode.hasChild(boxGeo)) {
            boxGeo.removeFromParent();
            localRootNode.attachChild(geo);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        localRootNode.detachAllChildren();
        this.rootNode.detachChild(this.localRootNode);
    }
}
