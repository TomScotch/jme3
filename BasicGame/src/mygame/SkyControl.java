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
    private final Node localRootNode;
    private final GlobalLightingControl lightingControl;

    public SkyControl(AssetManager assetManager) {

        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
        night = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        day = SkyFactory.createSky(
                assetManager, "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);
        night.setLocalTranslation(0, -1000, 0);
        day.setLocalTranslation(0, -1000, 0);
        localRootNode = (Node) spatial;
        localRootNode.attachChild(day);
        lightingControl = localRootNode.getControl(GlobalLightingControl.class);
    }

    @Override
    protected void controlUpdate(float tpf) {

        day.rotate(0, tpf / (lightingControl.getTimeDelay() * 24), 0);
        night.rotate(0, tpf / (lightingControl.getTimeDelay() * 24), 0);

        if (lightingControl.getTimingValue() > 0.99f) {

            if (lightingControl.getIsSun() == false) {
                night.removeFromParent();
                localRootNode.attachChild(day);
            }
        }

        if (lightingControl.getTimingValue() < -0.999f) {

            if (lightingControl.getIsSun() == true) {
                day.removeFromParent();
                localRootNode.attachChild(night);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

}
