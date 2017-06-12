package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;

public class WeatherControl extends AbstractControl {

    private final ParticleEmitter rain;
    private final ParticleEmitter flash;

    public WeatherControl(AssetManager am, Node localRoot) {

        flash = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, (int) lightningVoloume / 10);
        Material flash_mat = new Material(
                am, "Common/MatDefs/Misc/Particle.j3md");
        flash_mat.setTexture("Texture",
                am.loadTexture("Textures/weatherSprites/lightning/Xz2ctMGg_5c2UF-vqdqT3dMZLvs.png"));
        flash.setMaterial(flash_mat);
        flash.setShape(new EmitterBoxShape(new Vector3f(-496, -1.5f, -496), new Vector3f(496, 1.5f, 496)));
        flash.setParticlesPerSec(0);
        flash.setLocalTranslation(0, 35, 0);
        flash.center();
        localRoot.attachChild(flash);

        flash.setEndColor(ColorRGBA.Blue);
        flash.setStartColor(ColorRGBA.White);

        flash.setStartSize(3);
        flash.setEndSize(35);

        flash.setHighLife(0.5f);
        flash.setLowLife(0.1f);

        flash.setFacingVelocity(false);
        flash.setInWorldSpace(false);
        flash.setGravity(0, 0, 0);

        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, (int) rainThickness);
        Material rainMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        rainMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/raindrop-icon.png"));
        rain.setMaterial(rainMat);
        rain.setEndColor(rainColorEnd);
        rain.setStartColor(rainColorStart);
        rain.setStartSize(0.18f);
        rain.setEndSize(0.075f);
        //rain.setImagesX(1);
        rain.setImagesY(3);
        rain.setGravity(0, 160, 0);
        rain.setHighLife(2f);
        rain.setLowLife(1f);
        rain.setInWorldSpace(true);
        rain.setShape(new EmitterBoxShape(new Vector3f(-128, -15f, -128), new Vector3f(128, 15f, 128)));
        rain.setParticlesPerSec(0);
        rain.setFacingVelocity(true);
        rain.getParticleInfluencer().setVelocityVariation(3f);
        rain.setLocalTranslation(0, 40, 0);
        rain.center();
        localRoot.attachChild(rain);
    }
    int lightningFrequency = 18;
    int lightningVoloume = 80000;
    private boolean suny = false;
    private boolean clouded = false;
    private boolean raining = false;

    private float rainStrength = 3500;
    private float rainThickness = 80000;
    private ColorRGBA rainColorStart = new ColorRGBA(0.85f, 0.85f, 0.85f, 2f);
    private ColorRGBA rainColorEnd = new ColorRGBA(0f, 0f, 0, 1f);

    public final void startRandomWeather() {

        switch (getRandomNumberInRange(1, 3)) {

            case 1:

                makeSuny();
                break;

            case 2:

                makeCloudy();
                break;

            case 3:

                makeRain();
                break;
        }

    }

    public void makeRain() {
        suny = false;
        clouded = false;
        raining = true;
        System.out.println("raining");
    }

    public void makeCloudy() {
        suny = false;
        clouded = true;
        raining = false;
        System.out.println("cloudy");
    }

    public void makeSuny() {
        suny = true;
        clouded = false;
        raining = false;
        System.out.println("sunny");
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (this.isEnabled()) {
            if (raining) {
                rain.setParticlesPerSec(rainStrength);
                flash.setParticlesPerSec(lightningFrequency);
            } else {
                rain.setParticlesPerSec(0);
                flash.setParticlesPerSec(0);
            }
        }
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //rain.getWorldTranslation().set(vp.getCamera().getLocation()).addLocal(0, 45, 0);
    }

    public boolean isSuny() {
        return suny;
    }

    public void setSuny(boolean suny) {
        this.suny = suny;
    }

    public boolean isClouded() {
        return clouded;
    }

    public void setClouded(boolean clouded) {
        this.clouded = clouded;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public float getRainStrength() {
        return rainStrength;
    }

    public void setRainStrength(float rainStrength) {
        this.rainStrength = rainStrength;
    }

    public float getRainThickness() {
        return rainThickness;
    }

    public void setRainThickness(float rainThickness) {
        this.rainThickness = rainThickness;
    }

    public ColorRGBA getRainColorStart() {
        return rainColorStart;
    }

    public void setRainColorStart(ColorRGBA rainColorStart) {
        this.rainColorStart = rainColorStart;
    }

    public ColorRGBA getRainColorEnd() {
        return rainColorEnd;
    }

    public void setRainColorEnd(ColorRGBA rainColorEnd) {
        this.rainColorEnd = rainColorEnd;
    }
}
