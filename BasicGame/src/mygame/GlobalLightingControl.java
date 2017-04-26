package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class GlobalLightingControl extends AbstractControl {

    public boolean isGlobalLightning() {
        return globalLightning;
    }

    public void setGlobalLightning(boolean globalLightning) {
        this.globalLightning = globalLightning;
    }

    public int getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    private final Node localRootNode;
    private final static Node pivot = new Node();
    private final DirectionalLightShadowRenderer dlsr;
    private final int shadowmapSize = 512;
    private final DirectionalLight sun;
    private boolean globalLightning = true;
    private int timeDelay = 12;
    private final ViewPort vp;
    private boolean isSun = true;

    public GlobalLightingControl(ViewPort vp, AssetManager assetManager, Node localRootNode) {

        this.localRootNode = localRootNode;
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0, 0, 0));
        sun.setColor(ColorRGBA.White);
        this.localRootNode.addLight(sun);
        dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmapSize, 1);
        dlsr.setLight(sun);
        this.vp = vp;
        this.vp.addProcessor(dlsr);
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {

            if (globalLightning) {

                pivot.rotate((FastMath.QUARTER_PI * tpf) / timeDelay, 0, 0);

                sun.setDirection(pivot.getLocalRotation().getRotationColumn(2));

                float z = pivot.getLocalRotation().getRotationColumn(2).getZ();

                if (z > 0.55f) {
                    if (isSun == false) {
                        localRootNode.addLight(sun);
                        //this.vp.addProcessor(dlsr);
                        isSun = true;
                    }
                }
                System.out.println(z);
                if (z < -0.99f) {
                    if (isSun == true) {
                        localRootNode.removeLight(sun);
                        //this.vp.removeProcessor(dlsr);
                        isSun = false;
                    }
                }
            } else {
                sun.setDirection(new Vector3f(-5, -5, -5));
                sun.setColor(ColorRGBA.White);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
