package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.w3c.dom.css.RGBColor;

public class WeatherControl extends AbstractControl {

    //Weather Types
    //
    // Sunny
    private boolean suny;
    // Clouded
    private boolean clouded;
    // Storming    
    private boolean storming;
    // Rainy
    private boolean raining;

    //Rain
    private boolean isRaining;
    private float rainStrength;
    private float rainThickness;
    private RGBColor rainColor;
    private float randomRain;

    //Lightning
    private boolean isLightning;
    private float lightningIntervall;
    private int maxLightningStrikes;
    private float lightningDistance;
    private RGBColor lightningColor;
    private float randomLightning;

    //Clouds
    private boolean isCloudy;
    private float cloudDensity;
    private int cloudNumbers;
    private RGBColor cloudColor;
    private float randomCouds;

    @SuppressWarnings("FieldMayBeFinal")
    private ParticleEmitter rainEmitter;

    public WeatherControl(SimpleApplication app, Node localRootNode) {
        rainEmitter = (ParticleEmitter) app.getAssetManager().loadAsset("/ParticleEmitter/rain.j30");
        localRootNode.attachChild(rainEmitter);
        rainEmitter.getWorldTranslation().set(new Vector3f(0, 0, 0));
    }

    @Override
    protected void controlUpdate(float tpf) {

        // no weather effects at all 
        if (suny) {
            isRaining = false;
            isLightning = false;
            isCloudy = false;
        }

        // some white clouds 
        if (clouded) {
            isRaining = false;
            isLightning = false;
            isCloudy = true;
        }

        // dark clouds with heavy rain thunder and lightning 
        if (storming) {
            isRaining = true;
            isLightning = true;
            isCloudy = true;
        }

        // white clouds with normal rain fall
        if (raining) {
            isRaining = true;
            isLightning = false;
            isCloudy = true;
        }
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

    public boolean isStorming() {
        return storming;
    }

    public void setStorming(boolean storming) {
        this.storming = storming;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public boolean isIsRaining() {
        return isRaining;
    }

    public void setIsRaining(boolean isRaining) {
        this.isRaining = isRaining;
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

    public float getRandomRain() {
        return randomRain;
    }

    public void setRandomRain(float randomRain) {
        this.randomRain = randomRain;
    }

    public boolean isIsLightning() {
        return isLightning;
    }

    public void setIsLightning(boolean isLightning) {
        this.isLightning = isLightning;
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

    public float getRandomLightning() {
        return randomLightning;
    }

    public void setRandomLightning(float randomLightning) {
        this.randomLightning = randomLightning;
    }

    public boolean isIsCloudy() {
        return isCloudy;
    }

    public void setIsCloudy(boolean isCloudy) {
        this.isCloudy = isCloudy;
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

    public float getRandomCouds() {
        return randomCouds;
    }

    public void setRandomCouds(float randomCouds) {
        this.randomCouds = randomCouds;
    }

    public ParticleEmitter getRainEmitter() {
        return rainEmitter;
    }

    public void setRainEmitter(ParticleEmitter rainEmitter) {
        this.rainEmitter = rainEmitter;
    }
}
