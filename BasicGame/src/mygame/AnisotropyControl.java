package mygame;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Use to enable anisotropic filtering
 *
 * @version 0.3
 * @author tomscotch
 */
public class AnisotropyControl extends AbstractControl {

    private final AssetEventListener asl;

    /**
     *
     * @param assetManager must be supplied
     * @param samples used for anistropic filtering ( 4 to 16 )
     */
    public AnisotropyControl(AssetManager assetManager, final int samples) {

        asl = new AssetEventListener() {

            @Override
            public void assetRequested(AssetKey key) {
                if (key.getExtension().equals("png") || key.getExtension().equals("jpg") || key.getExtension().equals("dds")) {
                    TextureKey tkey = (TextureKey) key;
                    tkey.setAnisotropy(samples);
                }
            }

            @Override
            public void assetLoaded(AssetKey key) {
                //
            }

            @Override
            public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
                //
            }
        };
        assetManager.addAssetEventListener(asl);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }
}
