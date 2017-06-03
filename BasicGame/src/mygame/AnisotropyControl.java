package mygame;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class AnisotropyControl extends AbstractControl {

    private final AssetEventListener asl;

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
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        assetManager.addAssetEventListener(asl);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }
}
