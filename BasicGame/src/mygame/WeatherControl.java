package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;

public class WeatherControl extends AbstractControl {

    private final ParticleEmitter rain;
    private final ParticleEmitter flash;
    private final ParticleEmitter clouds;

    //private boolean suny = false;
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

    private final float fogDensity = 0.60f; // 1.3f
    private final int fogDistance = 40; // 50
    private final int cloudThickness = 325; // 400
    private final int lightningFrequency = 220; // 360
    private final int lightningVoloume = 12; // 180
    private float rainStrength = 1700; // 3000
    private float rainThickness = 2700; // 6000

    private float counter = 0f;
    private float limit = 0f;

    private ColorRGBA rainColorStart = new ColorRGBA(ColorRGBA.Blue);
    private ColorRGBA rainColorEnd = new ColorRGBA(ColorRGBA.LightGray);

    private final int maximumWeatherLength = 38;
    private final int minimumWeatherLength = 8;

    public WeatherControl(AssetManager am, Node localRoot) {

        limit = (float) getRandomNumberInRange(minimumWeatherLength, maximumWeatherLength);

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

        flash.addLight(new PointLight(Vector3f.ZERO, ColorRGBA.White, 256));

        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, (int) rainThickness);
        Material rainMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        rainMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/rain/raindrop.png"));
        rain.setMaterial(rainMat);
        rain.setEndColor(rainColorEnd);
        rain.setStartColor(rainColorStart);
        rain.setStartSize(0.28f);
        rain.setEndSize(0.075f);
        //rain.setImagesX(1);
        //rain.setImagesY(3);
        rain.setGravity(0, 1000, 0);
        rain.setHighLife(2f);
        rain.setLowLife(1f);
        rain.setInWorldSpace(true);
        rain.setShape(new EmitterSphereShape(Vector3f.ZERO, 256));
        rain.setParticlesPerSec(0);
        rain.setFacingVelocity(true);
        //rain.getParticleInfluencer().setVelocityVariation(3f);
        rain.setLocalTranslation(0, 40, 0);
        rain.center();
        rain.setQueueBucket(RenderQueue.Bucket.Transparent);
        localRoot.attachChild(rain);

        clouds = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, cloudThickness);
        Material cloudMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        cloudMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/clouds/SmallCloud.png"));
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
        clouds.setShadowMode(RenderQueue.ShadowMode.Cast);
        localRoot.attachChild(clouds);
    }

    public final void startRandomWeather() {

        switch (getRandomNumberInRange(0, 4)) {
            case 4:
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
            n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor().interpolateLocal(n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor(), ColorRGBA.LightGray, 0.25f);
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
        //suny = false;
    }

    public void makeRain() {

        /*        Node n = (Node) spatial;
        
        if (n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
        n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor().interpolateLocal(n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor(), ColorRGBA.Gray, 0.25f);
        }*/
        makeCloudy();
        raining = true;
        //suny = false;

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
        /*        Node n = (Node) spatial;
        if (n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
        n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor().interpolateLocal(n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor(), ColorRGBA.DarkGray, 0.25f);
        }*/
        //suny = false;
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

        clouded = false;
        raining = false;
        misty = false;
        lightnungStrikes = false;

        if (!n.getChild("sunNode").getControl(GlobalLightingControl.class).isEvening()) {
            if (!n.getChild("sunNode").getControl(GlobalLightingControl.class).isNight()) {

                /*                if (n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
                n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor().interpolateLocal(n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor(), ColorRGBA.White, 0.75f);
                n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor().interpolateLocal(n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor(), ColorRGBA.Orange, 0.75f);
                n.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().getColor().clamp();
                }*/
                //suny = true;
                System.out.println("sunny");
            } else {
                System.out.println("too late to get Sunny");
            }
        } else {
            System.out.println("sunny");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (this.isEnabled()) {

            counter += tpf;

            if (counter >= limit) {
                counter = 0;
                startRandomWeather();
                limit = (float) getRandomNumberInRange(minimumWeatherLength, maximumWeatherLength);
            }

            /*            if (suny) {
            
            if (clouds.getNumVisibleParticles() > 0) {
            clouds.killParticle(0);
            }
            if (rain.getParticlesPerSec() > 0) {
            rain.setParticlesPerSec(0);
            }
            if (flash.getParticlesPerSec() > 0) {
            flash.setParticlesPerSec(0);
            }
            }*/
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

                clouded_low = false;
                clouded_med = false;
                clouded_high = false;

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

                raining_low = false;
                raining_med = false;
                raining_high = false;

                if (rain.getParticlesPerSec() > 0) {
                    rain.setParticlesPerSec(0);
                }
            }

            if (lightnungStrikes) {

                if (lightnungStrikes_high) {
                    flash.setParticlesPerSec(lightningFrequency / 1.2f);
                } else if (lightnungStrikes_med) {
                    flash.setParticlesPerSec(lightningFrequency / 1.4f);
                } else if (lightnungStrikes_low) {
                    flash.setParticlesPerSec(lightningFrequency / 1.6f);
                }
            } else {

                lightnungStrikes_low = false;
                lightnungStrikes_med = false;
                lightnungStrikes_high = false;

                if (flash.getParticlesPerSec() > 0) {
                    flash.setParticlesPerSec(0);
                }
            }

            if (misty) {
                if (spatial.getControl(FogPostFilter.class).getFog() != null) {
                    Node n = (Node) spatial;
                    if (!n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
                        spatial.getControl(FogPostFilter.class).getFog().setFogColor(ColorRGBA.DarkGray);
                    } else {
                        spatial.getControl(FogPostFilter.class).getFog().setFogColor(ColorRGBA.Gray);
                    }

                    if (misty_high) {
                        if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() < fogDistance) {
                            spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() + tpf * 4);
                        }

                        if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity) {
                            spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf * 4);
                        }
                    } else if (misty_med) {
                        if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() < fogDistance / 1.25f) {
                            spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() + tpf * 2);
                        }

                        if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity / 1.25) {
                            spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf * 2);
                        }
                    } else if (misty_low) {
                        if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() < fogDistance / 1.5) {
                            spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() + tpf * 2);
                        }

                        if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity / 1.5) {
                            spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf * 2);
                        }
                    }
                } else {

                    misty_low = false;
                    misty_med = false;
                    misty_high = false;

                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() > 0) {
                        spatial.getControl(FogPostFilter.class).setFogDistance(spatial.getControl(FogPostFilter.class).getFog().getFogDistance() - tpf * 2);
                    }

                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() > 0) {
                        spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() - tpf * 2);
                    }
                }
            }
        }
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

    /*    public boolean isSuny() {
    return suny;
    }
    
    public void setSuny(boolean suny) {
    this.suny = suny;
    }*/
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
