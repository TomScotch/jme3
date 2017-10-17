package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

public class Terrain extends AbstractControl {

    public AbstractHeightMap getHeightmap() {
        return heightmap;
    }

    public TerrainQuad getTerrain() {
        return terrain;
    }

    private final boolean shadowsEnabled;
    private final TerrainQuad terrain;
    private final AbstractHeightMap heightmap;

    public Terrain(AssetManager assetManager, BulletAppState bulletAppState, Node localRootNode, ViewPort vp) {

        shadowsEnabled = true;

        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");

        heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.275f);
        heightmap.load();

        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        Material sphereMat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

        Texture diff = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        diff.setWrap(Texture.WrapMode.Repeat);
        sphereMat.setTexture("DiffuseMap", diff);

        Texture norm = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        norm.setWrap(Texture.WrapMode.Repeat);
        sphereMat.setTexture("NormalMap", norm);

        terrain.setMaterial(sphereMat);
        TerrainLodControl control = new TerrainLodControl(terrain, vp.getCamera());
        terrain.addControl(control);
        terrain.setLocalTranslation(-164.0f, -3.75f, 8.9f);
        if (shadowsEnabled) {
            terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        }

        RigidBodyControl rb = new RigidBodyControl(0);
        terrain.addControl(rb);
        bulletAppState.getPhysicsSpace().addAll(terrain);
        localRootNode.attachChild(terrain);
        rb.setFriction(1);
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
