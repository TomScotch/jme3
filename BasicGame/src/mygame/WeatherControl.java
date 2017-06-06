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

    public WeatherControl(AssetManager am, Node localRoot) {

        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 32000);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/Clouds/SmallCloud.png"));
        rain.setMaterial(mat);
        rain.setEndColor(ColorRGBA.Blue);
        rain.setStartColor(ColorRGBA.Blue);
        rain.setStartSize(5f);
        rain.setEndSize(2.5f);
        rain.setGravity(0, 150, 0);
        rain.setLowLife(5);
        rain.setHighLife(10);
        rain.setInWorldSpace(false);
        rain.setShape(new EmitterBoxShape(new Vector3f(-256, -1f, -256), new Vector3f(256, 1f, 256)));
        rain.setLocalTranslation(new Vector3f(0, 100, 0));
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

    private float cloudDensity;
    private int cloudNumbers;
    private RGBColor cloudColor;

    public final void startRandomWeather() {

        switch (getRandomNumberInRange(1, 3)) {

            case 1:

                suny = true;
                clouded = false;
                raining = false;
                rain.setParticlesPerSec(0);
                System.out.println("sunny");

            case 2:

                suny = false;
                clouded = true;
                raining = false;
                rain.setParticlesPerSec(0);
                System.out.println("cloudy");

            case 3:
                if (getRandomNumberInRange(1, 9) > 3) {
                    suny = false;
                    clouded = false;
                    raining = true;
                    rain.setParticlesPerSec(1000);
                    System.out.println("raining");
                }
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (isEnabled()) {
            System.out.println("\n clouded : " + clouded + "\n raining : " + raining + " \n sunny : " + suny);
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

    public float getCloudDensity() {
        return cloudDensity;
    }

    public void setCloudDensity(float cloudDensity) {
        this.cloudDensity = cloudDensity;
    }

    public int getCloudNumbers() {
        return cloudNumbers;
    }

    public void setCloudNumbers(int cloudNumbers) {
        this.cloudNumbers = cloudNumbers;
    }

    public RGBColor getCloudColor() {
        return cloudColor;
    }

    public void setCloudColor(RGBColor cloudColor) {
        this.cloudColor = cloudColor;
    }
}
