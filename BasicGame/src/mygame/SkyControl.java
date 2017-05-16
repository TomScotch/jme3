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
    private final Spatial evening;
    private final Spatial morning;
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

        Texture west2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyLeft2048.png");
        Texture east2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyRight2048.png");
        Texture north2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyBack2048.png");
        Texture south2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyFront2048.png");
        Texture up2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyUp2048.png");
        Texture down2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyDown2048.png");
        evening = SkyFactory.createSky(assetManager, west2, east2, north2, south2, up2, down2);

        Texture west3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetLeft2048.png");
        Texture east3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetRight2048.png");
        Texture north3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetBack2048.png");
        Texture south3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetFront2048.png");
        Texture up3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetUp2048.png");
        Texture down3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetDown2048.png");
        morning = SkyFactory.createSky(assetManager, west3, east3, north3, south3, up3, down3);

        morning.setLocalTranslation(0, -1000, 0);
        evening.setLocalTranslation(0, -1000, 0);
        night.setLocalTranslation(0, -1000, 0);
        day.setLocalTranslation(0, -1000, 0);
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {

            Node localRootNode = (Node) this.spatial;

            Float z = glc.getTimingValue();

            if (glc.getIsSun()) {
                //Morning
                if (z > 0.99f) {
                    if (!localRootNode.hasChild(morning)) {
                        localRootNode.attachChild(morning);
                        if (localRootNode.hasChild(night)) {
                            night.removeFromParent();
                        }
                    }
                }

                //Day
                if (z < -0.f && z > -0.45f) {
                    if (!localRootNode.hasChild(day)) {
                        localRootNode.attachChild(day);
                        if (localRootNode.hasChild(morning)) {
                            morning.removeFromParent();
                        }
                    }
                }

                //Evening
                if (z < -0.45f && z > -0.99f) {
                    if (!localRootNode.hasChild(evening)) {
                        localRootNode.attachChild(evening);
                        if (localRootNode.hasChild(day)) {
                            day.removeFromParent();
                        }
                    }
                }
            } else {
                //Night
                if (z < -0.99f) {
                    if (!localRootNode.hasChild(night)) {
                        localRootNode.attachChild(night);
                        if (localRootNode.hasChild(evening)) {
                            evening.removeFromParent();
                        }
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
