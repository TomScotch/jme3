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
    private final ParticleEmitter clouds;

    private boolean suny = false;
    private boolean misty = false;
    private boolean clouded = false;
    private boolean raining = false;
    private boolean lightnungStrikes = false;

    private boolean clouded_low = false;
    private boolean clouded_med = false;
    private boolean clouded_high = false;

    private boolean raining_low = false;
    private boolean raining_med = false;
    private boolean raining_high = false;

    private boolean misty_low = false;
    private boolean misty_med = false;
    private boolean misty_high = false;

    private boolean lightnungStrikes_low = false;
    private boolean lightnungStrikes_med = false;
    private boolean lightnungStrikes_high = false;

    private final float fogDensity = 0.65f; // 1.3f
    private final int fogDistance = 35; // 50
    private final int cloudThickness = 325; // 400
    private final int lightningFrequency = 27; // 27
    private final int lightningVoloume = 3000; // 5850
    private float rainStrength = 1750; // 3000
    private float rainThickness = 4500; // 6000

    private ColorRGBA rainColorStart = new ColorRGBA(0.85f, 0.85f, 0.85f, 2f);
    private ColorRGBA rainColorEnd = new ColorRGBA(0f, 0f, 0, 1f);

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

        clouds = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, cloudThickness);
        Material cloudMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        cloudMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/Clouds/SmallCloud.png"));
        clouds.setMaterial(cloudMat);
        clouds.setStartSize(25);
        clouds.setEndSize(25);
        clouds.setGravity(0.015f, 0, 0.015f);
        clouds.setHighLife(200f);
        clouds.setLowLife(200f);
        clouds.setInWorldSpace(true);
        clouds.setShape(new EmitterBoxShape(new Vector3f(-256, -10f, -256), new Vector3f(256, 10f, 256)));
        clouds.setParticlesPerSec(0);
        clouds.setFacingVelocity(false);
        clouds.getParticleInfluencer().setVelocityVariation(3f);
        clouds.setLocalTranslation(0, 150, 0);
        clouds.center();
        localRoot.attachChild(clouds);
    }

    public final void startRandomWeather() {

        switch (getRandomNumberInRange(0, 2)) {
            case 2:
                makeMisty();
                break;
        }

        switch (getRandomNumberInRange(0, 3)) {

            case 2:
                makeCloudy();
                break;
        }
        switch (getRandomNumberInRange(0, 1)) {

            case 0:
                makeRain();
                break;

            case 1:
                makeSuny();
                break;
        }
    }

    private void makeMisty() {

        Node n = (Node) spatial;

        if (n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
            n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().setColor(ColorRGBA.LightGray);
        }

        misty = true;

        switch (getRandomNumberInRange(0, 2)) {
            case 0:
                misty_low = true;
                misty_med = false;
                misty_high = false;
                System.out.println("misty low");
                break;
            case 1:
                misty_low = false;
                misty_med = true;
                misty_high = false;
                System.out.println("misty med");
                break;
            case 2:
                misty_low = false;
                misty_med = false;
                misty_high = true;
                System.out.println("misty high");
                break;
        }
        suny = false;
    }

    public void makeRain() {

        Node n = (Node) spatial;

        if (n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
            n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().setColor(ColorRGBA.Gray);
        }

        makeCloudy();
        raining = true;
        suny = false;

        switch (getRandomNumberInRange(0, 2)) {
            case 0:
                raining_high = true;
                raining_med = false;
                raining_low = false;
                System.out.println("raining_high");
                break;
            case 1:
                raining_high = false;
                raining_med = true;
                raining_low = false;
                System.out.println("raining_med");
                break;
            case 2:
                raining_high = false;
                raining_med = false;
                raining_low = true;
                System.out.println("raining_low");
                break;
        }

        switch (getRandomNumberInRange(0, 1)) {
            case 0:
                lightnungStrikes = true;
                switch (getRandomNumberInRange(0, 2)) {
                    case 0:
                        lightnungStrikes_high = true;
                        lightnungStrikes_med = false;
                        lightnungStrikes_low = false;
                        System.out.println("lightnungStrikes_high");
                        break;
                    case 1:
                        lightnungStrikes_high = false;
                        lightnungStrikes_med = true;
                        lightnungStrikes_low = false;
                        System.out.println("lightnungStrikes_med");
                        break;
                    case 2:
                        lightnungStrikes_high = false;
                        lightnungStrikes_med = false;
                        lightnungStrikes_low = true;
                        System.out.println("lightnungStrikes_low");
                        break;
                }
                break;
            case 1:
                lightnungStrikes = false;
                lightnungStrikes_high = false;
                lightnungStrikes_med = false;
                lightnungStrikes_low = false;
                System.out.println("noLightning");
                break;
        }
    }

    public void makeCloudy() {
        Node n = (Node) spatial;
        if (n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
            n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().setColor(ColorRGBA.DarkGray);
        }
        suny = false;
        clouded = true;
        switch (getRandomNumberInRange(0, 1)) {
            case 0:
                clouded_high = true;
                clouded_med = false;
                clouded_low = false;
                System.out.println("clouded_high");
                break;
            case 1:
                clouded_high = false;
                clouded_med = true;
                clouded_low = false;
                System.out.println("clouded_med");
                break;
            case 2:
                clouded_high = false;
                clouded_med = false;
                clouded_low = true;
                System.out.println("clouded_low");
                break;
        }
    }

    public void makeSuny() {

        Node n = (Node) spatial;

        if (n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
            n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().setColor(ColorRGBA.Orange);
        }

        suny = true;
        clouded = false;
        raining = false;
        misty = false;
        lightnungStrikes = false;

        System.out.println("sunny");
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (this.isEnabled()) {

            if (suny) {

                clouded_low = false;
                clouded_med = false;
                clouded_high = false;

                raining_low = false;
                raining_med = false;
                raining_high = false;

                misty_low = false;
                misty_med = false;
                misty_high = false;

                lightnungStrikes_low = false;
                lightnungStrikes_med = false;
                lightnungStrikes_high = false;

                if (clouds.getNumVisibleParticles() > 0) {
                    clouds.killParticle(0);
                }
                if (rain.getParticlesPerSec() > 0) {
                    rain.setParticlesPerSec(0);
                }
                if (flash.getParticlesPerSec() > 0) {
                    flash.setParticlesPerSec(0);
                }
                if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() > 0) {
                    spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() - tpf * 10);
                }

                if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() > 0) {
                    spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() - tpf * 10);
                }
            }

            if (clouded) {
                if (clouded_high) {
                    if (clouds.getNumVisibleParticles() < cloudThickness * 2) {
                        clouds.emitParticles(cloudThickness / 5);
                    }
                } else if (clouded_med) {
                    if (clouds.getNumVisibleParticles() < cloudThickness) {
                        clouds.emitParticles(cloudThickness / 10);
                    }
                } else if (clouded_low) {
                    if (clouds.getNumVisibleParticles() < cloudThickness / 2) {
                        clouds.emitParticles(cloudThickness / 20);
                    }
                }
            } else {
                if (clouds.getNumVisibleParticles() > 0) {
                    clouds.killParticle(0);
                }
            }

            if (raining) {
                if (raining_high) {
                    rain.setParticlesPerSec(rainStrength * 2);
                } else if (raining_med) {
                    rain.setParticlesPerSec(rainStrength);
                } else if (raining_low) {
                    rain.setParticlesPerSec(rainStrength / 2);
                }
            } else {
                if (rain.getParticlesPerSec() > 0) {
                    rain.setParticlesPerSec(0);
                }
            }

            if (lightnungStrikes) {

                if (lightnungStrikes_high) {
                    flash.setParticlesPerSec(lightningFrequency * 2);
                } else if (lightnungStrikes_med) {
                    flash.setParticlesPerSec(lightningFrequency);
                } else if (lightnungStrikes_low) {
                    flash.setParticlesPerSec(lightningFrequency / 2);
                }
            } else {
                if (flash.getParticlesPerSec() > 0) {
                    flash.setParticlesPerSec(0);
                }
            }

            if (misty) {

                Node n = (Node) spatial;
                if (!n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
                    spatial.getControl(FogPostFilter.class).getFog().setFogColor(ColorRGBA.DarkGray);
                } else {
                    spatial.getControl(FogPostFilter.class).getFog().setFogColor(ColorRGBA.Gray);
                }

                if (misty_high) {
                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() < fogDistance * 2) {
                        spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() + tpf * 5);
                    }

                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity * 2) {
                        spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf * 5);
                    }
                } else if (misty_med) {
                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() < fogDistance) {
                        spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() + tpf * 5);
                    }

                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity / 2) {
                        spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf * 5);
                    }
                } else if (misty_low) {
                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() < fogDistance / 2) {
                        spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() + tpf * 5);
                    }

                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity / 2) {
                        spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf * 5);
                    }
                }
            } else {
                if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() > 0) {
                    spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() - tpf * 10);
                }

                if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() > 0) {
                    spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() - tpf * 10);
                }
            }

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
