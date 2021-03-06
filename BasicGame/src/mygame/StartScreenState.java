package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
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

    private final Geometry geo;
    private final BitmapFont guiFont;
    private final BitmapText helloText;

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

        guiNode.detachAllChildren();
        guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setLocalTranslation(app.getCamera().getWidth() / 2, helloText.getLineHeight(), 0);
        helloText.setColor(ColorRGBA.Blue);

        Box boxMesh = new Box(1, 1, 1);
        boxGeo = new Geometry("Box", boxMesh);
        Material boxMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = app.getAssetManager().loadTexture("Textures/gloemtoi.png");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxMat.setColor("Color", ColorRGBA.White);
        boxGeo.setMaterial(boxMat);

        Box box = new Box(1, 1, 1);
        geo = new Geometry("Box", box);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = app.getAssetManager().loadTexture("Textures/dragon_logo.png");
        mat.setTexture("ColorMap", tex);
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geo.setMaterial(mat);
        localRootNode.attachChild(geo);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        viewPort.setBackgroundColor(backgroundColor);
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

    public BitmapText getText() {
        return helloText;
    }

    public void attachBox() {
        localGuiNode.attachChild(helloText);
        if (!localRootNode.hasChild(boxGeo)) {
            localRootNode.detachChild(geo);
            localRootNode.attachChild(boxGeo);
        }
    }

    public void detachBox() {
        localGuiNode.detachChild(helloText);
        if (localRootNode.hasChild(boxGeo)) {
            boxGeo.removeFromParent();
            localRootNode.attachChild(geo);
        }
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
