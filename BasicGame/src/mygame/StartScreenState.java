package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class StartScreenState extends AbstractAppState {

    private final ViewPort viewPort;
    private final Node rootNode;
    private final Node guiNode;
    private final AssetManager assetManager;
    private final Node localRootNode = new Node("Start Screen RootNode");
    private final Node localGuiNode = new Node("Start Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Black;
    private final Geometry boxGeo;
    private final BitmapText startGame;
    private boolean gameIsLoaded = false;

    public StartScreenState(SimpleApplication app) {
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();

        BitmapFont guiFont = assetManager.loadFont(
                "Interface/Fonts/Default.fnt");
        BitmapText displaytext = new BitmapText(guiFont);
        displaytext.setSize(guiFont.getCharSet().getRenderedSize());
        displaytext.move(10, displaytext.getLineHeight() + 20, 0);
        displaytext.setText("Start screen. Press BACKSPACE to resume the game, "
                + "press RETURN to edit Settings.");
        localGuiNode.attachChild(displaytext);

        BitmapText options = new BitmapText(guiFont);
        options.setSize(guiFont.getCharSet().getRenderedSize() * 3);
        options.move(options.getLineWidth() + viewPort.getCamera().getWidth() / 10, options.getLineHeight() + viewPort.getCamera().getHeight() / 3, 0);
        options.setText("Options");
        options.setColor(ColorRGBA.Green);
        localGuiNode.attachChild(options);

        startGame = new BitmapText(guiFont);
        startGame.setSize(guiFont.getCharSet().getRenderedSize() * 3);
        startGame.setColor(ColorRGBA.Green);
        startGame.move(startGame.getLineWidth() + viewPort.getCamera().getWidth() / 10, startGame.getLineHeight() + viewPort.getCamera().getHeight() / 2, 0);

        localGuiNode.attachChild(startGame);

        app.getInputManager().setCursorVisible(false);

        Box boxMesh = new Box(1f, 1f, 1f);
        boxGeo = new Geometry("A Textured Box", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Interface/Logo/Monkey.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxMat.setColor("Color", ColorRGBA.Blue);
        boxGeo.setMaterial(boxMat);
        this.localRootNode.attachChild(boxGeo);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        viewPort.setBackgroundColor(backgroundColor);
        app.getInputManager().setCursorVisible(false);

        if (gameIsLoaded) {
            startGame.setText("Continue");
        } else {
            startGame.setText("Start Game");
        }

        boxGeo.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    @Override
    public void update(float tpf) {

        boxGeo.rotate(0, tpf, 0);
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

    public boolean isGameIsLoaded() {
        return gameIsLoaded;
    }

    public void setGameIsLoaded(boolean gameIsLoaded) {
        this.gameIsLoaded = gameIsLoaded;
    }
}
