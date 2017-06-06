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
import org.w3c.dom.css.RGBColor;

public class WeatherControl extends AbstractControl {

    private final ParticleEmitter rain;
    private final ParticleEmitter lightning;

    public WeatherControl(AssetManager am, Node localRoot) {

        lightning = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 50000);
        Material lightningMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        lightningMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/lightning/1.png"));
        lightning.setMaterial(lightningMat);
        lightning.setEndColor(ColorRGBA.White);
        lightning.setStartColor(ColorRGBA.Gray);
        lightning.setStartSize(1f);
        lightning.setEndSize(0.75f);
        lightning.setGravity(0, 500, 0);
        lightning.setLowLife(1);
        lightning.setHighLife(2);
        lightning.setInWorldSpace(false);
        lightning.setShape(new EmitterBoxShape(new Vector3f(-256, -1f, -256), new Vector3f(256, 1f, 256)));
        lightning.setLocalTranslation(new Vector3f(0, 300, 0));
        lightning.setNumParticles(50000);
        lightning.setParticlesPerSec(0);
        localRoot.attachChild(lightning);

        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 32000);
        Material rainMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        rainMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/Clouds/SmallCloud.png"));
        rain.setMaterial(rainMat);

        rain.setEndColor(ColorRGBA.Blue);
        rain.setStartColor(ColorRGBA.Blue);
        rain.setStartSize(0.5f);
        rain.setEndSize(0.1f);
        rain.setGravity(0, 400, 0);
        rain.setLowLife(3);
        rain.setHighLife(10);
        rain.setInWorldSpace(false);
        rain.setShape(new EmitterBoxShape(new Vector3f(-256, -1f, -256), new Vector3f(256, 1f, 256)));
        rain.setLocalTranslation(new Vector3f(0, 300, 0));
        rain.setNumParticles(32000);
        rain.setParticlesPerSec(0);
        localRoot.attachChild(rain);
    }

    private boolean suny;
    private boolean clouded;
    private boolean raining;

    private float rainStrength;
    private float rainThickness;
    private RGBColor rainColor;

    private float lightningIntervall;
    private int maxLightningStrikes;
    private float lightningDistance;
    private RGBColor lightningColor;

    public final void startRandomWeather() {

        switch (getRandomNumberInRange(1, 3)) {

            case 1:

                suny = true;
                clouded = false;
                raining = false;
                rain.setParticlesPerSec(0);
                lightning.setParticlesPerSec(0);
                System.out.println("sunny");

            case 2:

                suny = false;
                clouded = true;
                raining = false;
                rain.setParticlesPerSec(0);
                lightning.setParticlesPerSec(0);
                System.out.println("cloudy");

            case 3:
                if (getRandomNumberInRange(1, 9) > 3) {
                    suny = false;
                    clouded = false;
                    raining = true;
                    rain.setParticlesPerSec(5000);
                    lightning.setParticlesPerSec(0);
                    System.out.println("raining");
                }
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (raining) {
            //
        }
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
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

    public RGBColor getRainColor() {
        return rainColor;
    }

    public void setRainColor(RGBColor rainColor) {
        this.rainColor = rainColor;
    }

    public float getLightningIntervall() {
        return lightningIntervall;
    }

    public void setLightningIntervall(float lightningIntervall) {
        this.lightningIntervall = lightningIntervall;
    }

    public int getMaxLightningStrikes() {
        return maxLightningStrikes;
    }

    public void setMaxLightningStrikes(int maxLightningStrikes) {
        this.maxLightningStrikes = maxLightningStrikes;
    }

    public float getLightningDistance() {
        return lightningDistance;
    }

    public void setLightningDistance(float lightningDistance) {
        this.lightningDistance = lightningDistance;
    }

    public RGBColor getLightningColor() {
        return lightningColor;
    }

    public void setLightningColor(RGBColor lightningColor) {
        this.lightningColor = lightningColor;
    }
}
