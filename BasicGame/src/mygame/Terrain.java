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
import com.jme3.texture.Texture.WrapMode;

public class Terrain extends AbstractControl {

    public Terrain(AssetManager assetManager, BulletAppState bulletAppState, Node localRootNode, ViewPort vp) {
        // Create material from Terrain Material Definition
        Material matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        // Load alpha map (for splat textures)
        matRock.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
        // load heightmap image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        // load grass texture
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", 64f);
        // load dirt texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex2", dirt);
        matRock.setFloat("Tex2Scale", 32f);
        // load rock texture
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex3", rock);
        matRock.setFloat("Tex3Scale", 128f);
        TerrainQuad terrain;

        AbstractHeightMap heightmap;
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.275f);
        heightmap.load();
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        terrain.setMaterial(matRock);
        terrain.setMaterial(new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md"));
        //terrain.setLocalScale(2f, 1f, 2f); // scale to make it less steep
        TerrainLodControl control = new TerrainLodControl(terrain, vp.getCamera());
        terrain.addControl(control);
        terrain.setLocalTranslation(-164.0f, -9f, 8.9f);
        terrain.addControl(new RigidBodyControl(0));
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        bulletAppState.getPhysicsSpace().addAll(terrain);
        localRootNode.attachChild(terrain);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations
    }

}
