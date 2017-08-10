package mygame;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.asset.AssetManager;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
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

    private final Material matMorning;
    private final Material matDay;
    private final Material matEvening;
    private final Material matNight;

    public SkyControl(AssetManager assetManager, GlobalLightingControl glc, Node localRootNode) {

        this.glc = glc;

        Texture west = assetManager.loadTexture("Textures/skybox/Night/FullMoonLeft2048.png");
        Texture east = assetManager.loadTexture("Textures/skybox/Night/FullMoonRight2048.png");
        Texture north = assetManager.loadTexture("Textures/skybox/Night/FullMoonBack2048.png");
        Texture south = assetManager.loadTexture("Textures/skybox/Night/FullMoonFront2048.png");
        Texture up = assetManager.loadTexture("Textures/skybox/Night/FullMoonUp2048.png");
        Texture down = assetManager.loadTexture("Textures/skybox/Night/FullMoonDown2048.png");
        night = SkyFactory.createSky(assetManager, west, east, north, south, up, down);

        Texture west1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterLeft2048.png");
        Texture east1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterRight2048.png");
        Texture north1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterBack2048.png");
        Texture south1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterFront2048.png");
        Texture up1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterUp2048.png");
        Texture down1 = assetManager.loadTexture("Textures/skybox/ThickCloudsWater/ThickCloudsWaterDown2048.png");
        morning = SkyFactory.createSky(assetManager, west1, east1, north1, south1, up1, down1);

        /*Texture west2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyLeft2048.png");
        Texture east2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyRight2048.png");
        Texture north2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyBack2048.png");
        Texture south2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyFront2048.png");
        Texture up2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyUp2048.png");
        Texture down2 = assetManager.loadTexture("Textures/skybox/Evening/DarkStormyDown2048.png");*/
        evening = morning; //SkyFactory.createSky(assetManager, west2, east2, north2, south2, up2, down2);

        /*Texture west3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetLeft2048.png");
        Texture east3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetRight2048.png");
        Texture north3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetBack2048.png");
        Texture south3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetFront2048.png");
        Texture up3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetUp2048.png");
        Texture down3 = assetManager.loadTexture("Textures/skybox/Morning/SunSetDown2048.png");*/
        day = morning;//SkyFactory.createSky(assetManager, west3, east3, north3, south3, up3, down3);

        morning.setLocalTranslation(0, -1000, 0);
        evening.setLocalTranslation(0, -1000, 0);
        night.setLocalTranslation(0, -1000, 0);
        day.setLocalTranslation(0, -1000, 0);

        Geometry morningGeom = (Geometry) morning;
        matMorning = morningGeom.getMaterial();
        matMorning.setTransparent(true);
        //matMorning.getAdditionalRenderState().setBlendMode(BlendMode.Additive);
        morningGeom.setQueueBucket(Bucket.Sky);

        Geometry dayGeom = (Geometry) day;
        matDay = dayGeom.getMaterial();
        matDay.setTransparent(true);
        //   matDay.getAdditionalRenderState().setBlendMode(BlendMode.Additive);
        dayGeom.setQueueBucket(Bucket.Sky);

        Geometry eveningGeom = (Geometry) evening;
        matEvening = eveningGeom.getMaterial();
        matEvening.setTransparent(true);
        // matEvening.getAdditionalRenderState().setBlendMode(BlendMode.Additive);
        eveningGeom.setQueueBucket(Bucket.Sky);

        Geometry nightGeom = (Geometry) night;
        matNight = nightGeom.getMaterial();
        matNight.setTransparent(true);
        // matNight.getAdditionalRenderState().setBlendMode(BlendMode.Additive);
        nightGeom.setQueueBucket(Bucket.Sky);

        localRootNode.attachChild(day);
        localRootNode.attachChild(night);
        localRootNode.attachChild(morning);
        localRootNode.attachChild(evening);
        day.setCullHint(Spatial.CullHint.Always);
        evening.setCullHint(Spatial.CullHint.Always);
        morning.setCullHint(Spatial.CullHint.Always);
        night.setCullHint(Spatial.CullHint.Never);

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (this.isEnabled()) {

            night.rotate(0, glc.getRotation() / 4, 0);
            day.rotate(0, glc.getRotation() / 4, 0);
            evening.rotate(0, glc.getRotation() / 4, 0);
            morning.rotate(0, glc.getRotation() / 4, 0);

            if (glc.isMorning()) {
                day.setCullHint(Spatial.CullHint.Always);
                night.setCullHint(Spatial.CullHint.Always);
                evening.setCullHint(Spatial.CullHint.Always);
                morning.setCullHint(Spatial.CullHint.Never);

            } else if (glc.isDay()) {
                night.setCullHint(Spatial.CullHint.Always);
                morning.setCullHint(Spatial.CullHint.Always);
                evening.setCullHint(Spatial.CullHint.Always);
                day.setCullHint(Spatial.CullHint.Never);

            } else if (glc.isEvening()) {
                morning.setCullHint(Spatial.CullHint.Always);
                day.setCullHint(Spatial.CullHint.Always);
                night.setCullHint(Spatial.CullHint.Always);
                evening.setCullHint(Spatial.CullHint.Never);

            } else if (glc.isNight()) {
                morning.setCullHint(Spatial.CullHint.Always);
                day.setCullHint(Spatial.CullHint.Always);
                evening.setCullHint(Spatial.CullHint.Always);
                night.setCullHint(Spatial.CullHint.Never);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

}
