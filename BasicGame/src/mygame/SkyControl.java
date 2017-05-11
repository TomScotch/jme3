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

        Texture west = assetManager.loadTexture("Textures/skybox/Night/FullMoonLeft2048.png");
        Texture east = assetManager.loadTexture("Textures/skybox/Night/FullMoonRight2048.png");
        Texture north = assetManager.loadTexture("Textures/skybox/Night/FullMoonBack2048.png");
        Texture south = assetManager.loadTexture("Textures/skybox/Night/FullMoonFront2048.png");
        Texture up = assetManager.loadTexture("Textures/skybox/Night/FullMoonUp2048.png");
        Texture down = assetManager.loadTexture("Textures/skybox/Night/FullMoonDown2048.png");
        night = SkyFactory.createSky(assetManager, west, east, north, south, up, down);

        Texture west1 = assetManager.loadTexture("Textures/skybox/Midday/TropicalSunnyDayLeft2048.png");
        Texture east1 = assetManager.loadTexture("Textures/skybox/Midday/TropicalSunnyDayRight2048.png");
        Texture north1 = assetManager.loadTexture("Textures/skybox/Midday/TropicalSunnyDayBack2048.png");
        Texture south1 = assetManager.loadTexture("Textures/skybox/Midday/TropicalSunnyDayFront2048.png");
        Texture up1 = assetManager.loadTexture("Textures/skybox/Midday/TropicalSunnyDayUp2048.png");
        Texture down1 = assetManager.loadTexture("Textures/skybox/Midday/TropicalSunnyDayDown2048.png");
        day = SkyFactory.createSky(assetManager, west1, east1, north1, south1, up1, down1);

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
            night.rotate(0, tpf / (glc.getTimeDelay() * 5), 0);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

}
