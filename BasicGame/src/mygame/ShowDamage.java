package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.BillboardControl;

public class ShowDamage extends AbstractControl {

    private final BitmapText txt;

    public ShowDamage(AssetManager assetManager, String val, Node n) {
        BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
        txt = new BitmapText(fnt, false);
        txt.setQueueBucket(RenderQueue.Bucket.Transparent);
        txt.setSize(1f);
        txt.center();
        txt.setText(val);
        txt.setColor(ColorRGBA.Red);
        n.attachChild(txt);
        txt.getLocalTranslation().addLocal(0, n.getWorldTransform().getScale().getY() * 7.5f, 0);
        BillboardControl billboard = new BillboardControl();
        Node node = (Node) txt;
        node.addControl(billboard);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getParent() != null) {
            txt.getLocalTranslation().addLocal(0, tpf * 3.5f, 0);
            txt.setAlpha(txt.getAlpha() - tpf);
            if (txt.getWorldTranslation().y > 15) {
                txt.removeFromParent();
                spatial.removeControl(this);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

}
