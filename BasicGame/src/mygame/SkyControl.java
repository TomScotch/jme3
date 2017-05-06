package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class SkyControl extends AbstractControl {

    private final Spatial night;
    private final Spatial day;
    private final GlobalLightingControl glc;

    public SkyControl(AssetManager assetManager, GlobalLightingControl glc) {

        this.glc = glc;

        Texture west = assetManager.loadTexture("Textures/skybox/6.png");
        Texture east = assetManager.loadTexture("Textures/skybox/5.png");
        Texture north = assetManager.loadTexture("Textures/skybox/4.png");
        Texture south = assetManager.loadTexture("Textures/skybox/3.png");
        Texture up = assetManager.loadTexture("Textures/skybox/2.png");
        Texture down = assetManager.loadTexture("Textures/skybox/1.png");
        night = SkyFactory.createSky(assetManager, west, east, north, south, up, down);

        day = SkyFactory.createSky(
                assetManager, "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);
        night.setLocalTranslation(0, -1000, 0);
        day.setLocalTranslation(0, -1000, 0);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (isEnabled()) {
            Node localRootNode = (Node) this.spatial;
            if (glc.getIsSun()) {
                if (!localRootNode.hasChild(day)) {
                    localRootNode.attachChild(day);
                    if (localRootNode.hasChild(night)) {
                        night.removeFromParent();
                    }
                }
            } else {
                if (!localRootNode.hasChild(night)) {
                    localRootNode.attachChild(night);
                    if (localRootNode.hasChild(day)) {
                        day.removeFromParent();
                    }
                }
            }
            night.rotate(0, tpf / (glc.getTimeDelay() * 20), 0);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

}
