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

    public WeatherControl(AssetManager am, Node localRoot) {

        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, (int) rainThickness);
        Material rainMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        rainMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/raindrop-icon.png"));
        rain.setMaterial(rainMat);
        //rain.setEndColor(rainColorEnd);
        //rain.setStartColor(rainColorStart);
        rain.setStartSize(0.18f);
        rain.setEndSize(0.054f);
        rain.setImagesX(1);
        rain.setImagesY(3);
        rain.setGravity(0, 120, 0);
        rain.setHighLife(2.5f);
        rain.setLowLife(1.25f);
        rain.setInWorldSpace(true);
        rain.setShape(new EmitterBoxShape(new Vector3f(-128, -15f, -128), new Vector3f(128, 15f, 128)));
        rain.setParticlesPerSec(0);
        rain.setFacingVelocity(true);
        rain.getParticleInfluencer().setVelocityVariation(3f);
        rain.setLocalTranslation(0, 32f, 0);
        localRoot.attachChild(rain);
        rain.center();
    }

    private boolean suny = false;
    private boolean clouded = false;
    private boolean raining = false;

    private float rainStrength = 2000;
    private float rainThickness = 80000;
    private ColorRGBA rainColorStart = new ColorRGBA(0.85f, 0.85f, 0.85f, 1f);
    private ColorRGBA rainColorEnd = new ColorRGBA(0f, 0f, 0, 0.5f);

    public final void startRandomWeather() {

        switch (getRandomNumberInRange(1, 3)) {

            case 1:

                makeSuny();
                break;

            case 2:

                makeCloudy();
                break;

            case 3:

                if (getRandomNumberInRange(1, 6) > 3) {

                    makeRain();
                }
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

        if (raining) {
            rain.setParticlesPerSec(rainStrength);
        } else {
            rain.setParticlesPerSec(0);
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
