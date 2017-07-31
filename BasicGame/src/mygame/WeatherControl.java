package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.effect.Particle;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
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

    private final float fogDensity = 0.40f; // 1.3f
    private final int fogDistance = 30; // 50

    private final int cloudThickness = 350; // 400

    //private final int lightningFrequency = 100; // 360
    //private final int lightningVoloume = 10; // 180
    private float rainStrength = 800; // 3000
    private float rainThickness = 4000; // 6000

    private float counter = 0f;
    private float limit = 0f;

    private float flashCounter = 0f;
    private float flashLimit = 0f;

    private ColorRGBA rainColorStart = new ColorRGBA(ColorRGBA.Blue);
    private ColorRGBA rainColorEnd = new ColorRGBA(ColorRGBA.DarkGray);

    private final int maximumWeatherLength = 38;
    private final int minimumWeatherLength = 8;
    private final Camera cam;
    private final AbstractHeightMap hm;
    private final AssetManager am;
    private final ParticleEmitter debrisEffect;
    private final Node localRoot;
    private final DirectionalLight sun;

    public WeatherControl(AssetManager am, Node localRoot, Camera cam, AbstractHeightMap hm) {

        this.cam = cam;
        this.hm = hm;
        this.am = am;
        this.localRoot = localRoot;

        limit = (float) getRandomNumberInRange(minimumWeatherLength, maximumWeatherLength);

        flash = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 8);
        Material flash_mat = new Material(
                am, "Common/MatDefs/Misc/Particle.j3md");
        flash_mat.setTexture("Texture",
                am.loadTexture("Textures/weatherSprites/lightning/Xz2ctMGg_5c2UF-vqdqT3dMZLvs.png"));
        flash.setMaterial(flash_mat);
        flash.setShape(new EmitterBoxShape(new Vector3f(-256f, -1f, -256f), new Vector3f(256f, 1f, 256f)));
        flash.setParticlesPerSec(0);
        flash.setLocalTranslation(0, 75, 0);
        flash.center();
        this.localRoot.attachChild(flash);

        flash.setEndColor(ColorRGBA.Blue);
        flash.setStartColor(ColorRGBA.White);

        flash.setStartSize(3);
        flash.setEndSize(28);

        flash.setHighLife(0.35f);
        flash.setLowLife(0.1f);

        flash.setFacingVelocity(false);
        flash.setInWorldSpace(false);
        flash.setGravity(0, 0, 0);
        flash.setFaceNormal(new Vector3f(0, 0, 1));

        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, (int) rainThickness);
        Material rainMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        rainMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/rain/rain.png"));
        rain.setMaterial(rainMat);
        rain.setStartSize(0.6f);
        rain.setEndSize(0.4f);
        rain.setGravity(0, 1750, 0);
        rain.setEndColor(ColorRGBA.White);
        rain.setStartColor(ColorRGBA.LightGray);
        rain.setHighLife(2.5f);
        rain.setLowLife(1.25f);
        rain.setInWorldSpace(true);
        rain.setShape(new EmitterBoxShape(new Vector3f(-256, -1f, -256), new Vector3f(256, 1f, 256)));
        rain.setParticlesPerSec(0);
        rain.setFacingVelocity(false);
        rain.setLocalTranslation(0, 50, 0);
        //  rain.setQueueBucket(RenderQueue.Bucket.Translucent);
        rain.center();
        this.localRoot.attachChild(rain);

        clouds = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, cloudThickness);
        Material cloudMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        cloudMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/clouds/SmallCloud.png"));
        clouds.setMaterial(cloudMat);
        clouds.setStartSize(25);
        clouds.setEndSize(25);
        clouds.setGravity(0.015f, 0, 0.015f);
        clouds.setHighLife(200f);
        clouds.setLowLife(200f);
        clouds.setShape(new EmitterBoxShape(new Vector3f(-256, -1f, -256), new Vector3f(256, 1f, 256)));
        clouds.setParticlesPerSec(0);
        clouds.setFacingVelocity(false);
        clouds.setInWorldSpace(true);
        clouds.setFaceNormal(new Vector3f(0, -1, 0));
        clouds.getParticleInfluencer().setVelocityVariation(3f);
        clouds.setLocalTranslation(0, 120, 0);
        clouds.center();
        clouds.setShadowMode(RenderQueue.ShadowMode.Cast);
        this.localRoot.attachChild(clouds);

        debrisEffect = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 128);
        Material debrisMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        debrisMat.setTexture("Texture", am.loadTexture("Textures/weatherSprites/rain/splash.png"));
        debrisEffect.setMaterial(debrisMat);
        debrisEffect.setStartSize(0.25f);
        debrisEffect.setEndSize(0.05f);
        debrisEffect.setEndColor(ColorRGBA.White);
        debrisEffect.setStartColor(ColorRGBA.LightGray);

        debrisEffect.setHighLife(0.5f);
        debrisEffect.setLowLife(0.25f);
        debrisEffect.setInWorldSpace(true);
        debrisEffect.setFacingVelocity(false);
        debrisEffect.setShape(new EmitterSphereShape(Vector3f.ZERO, 64));
        //debrisEffect.setQueueBucket(RenderQueue.Bucket.Opaque);
        debrisEffect.getWorldTranslation().set(0, 6, 0);
        this.localRoot.attachChild(debrisEffect);

        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(0f, -0.5f, 0f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
    }

    public final void startRandomWeather() {

        misty = false;

        switch (getRandomNumberInRange(0, 8)) {
            case 8:
                makeMisty();
                break;
        }

        switch (getRandomNumberInRange(0, 3)) {

            case 2:
                makeCloudy();
                break;
        }

        switch (getRandomNumberInRange(0, 2)) {

            case 1:
                makeLightning();
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
    }

    public void makeRain() {

        raining = true;

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
                makeLightning();
                break;
        }
    }

    public void makeLightning() {
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
        if (lightnungStrikes_high) {
            flashLimit = (float) getRandomNumberInRange(3, 6);
        } else if (lightnungStrikes_med) {
            flashLimit = (float) getRandomNumberInRange(4, 8);
        } else if (lightnungStrikes_low) {
            flashLimit = (float) getRandomNumberInRange(5, 10);
        }
    }

    public void makeCloudy() {

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

        clouded = false;
        raining = false;
        misty = false;
        lightnungStrikes = false;
        System.out.println("sunny");
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

                if (rain.getNumVisibleParticles() > 0) {

                    debrisEffect.setNumParticles((int) rain.getParticlesPerSec());

                    for (int c = rain.getParticles().length; c >= 0; c--) {

                        try {
                            Vector3f position = rain.getParticles()[c].position;
                            float trueHeightAtPoint = hm.getScaledHeightAtPoint((int) position.getX(), (int) position.getZ());
                            debrisEffect.getWorldTranslation().set(position.getX(), trueHeightAtPoint + 0.00001f, position.getZ()); //
                            //float distance = cam.getLocation().distance(position);

                            //    if (distance < 20) {
                            //  debrisEffect.setStartSize(0f);
                            //  debrisEffect.setEndSize(0.0f);
                            // }
                            debrisEffect.emitParticles(1);
                        } catch (Exception e) {
                        }
                    }
                }

            } else {

                debrisEffect.setParticlesPerSec(0);
                debrisEffect.killAllParticles();

                raining_low = false;
                raining_med = false;
                raining_high = false;

                if (rain.getParticlesPerSec() > 0) {
                    rain.setParticlesPerSec(0);
                }
            }

            if (lightnungStrikes) {

                flashCounter += tpf;

                if (lightnungStrikes_high) {
                    flashLimit = (float) getRandomNumberInRange(3, 6);
                } else if (lightnungStrikes_med) {
                    flashLimit = (float) getRandomNumberInRange(4, 8);
                } else if (lightnungStrikes_low) {
                    flashLimit = (float) getRandomNumberInRange(5, 10);
                }

                if (flashCounter >= flashLimit) {

                    flashCounter = 0;

                    if (lightnungStrikes_high) {
                        flash.emitParticles(getRandomNumberInRange(1, 3));
                    } else if (lightnungStrikes_med) {
                        flash.emitParticles(getRandomNumberInRange(1, 2));
                    } else if (lightnungStrikes_low) {
                        flash.emitParticles(getRandomNumberInRange(1, 1));
                    }

                    for (Particle p : flash.getParticles()) {

                        Node n = new Node();
                        Spatial spat = (Spatial) n;

                        spat.setLocalTranslation((p.position));
                        spat.lookAt(Vector3f.ZERO, Vector3f.UNIT_XYZ);

                        DirectionalLight clone = localRoot.getChild("sunNode").getControl(GlobalLightingControl.class).getSun().clone();
                        clone.setColor(ColorRGBA.White);
                        clone.setDirection(spat.getLocalRotation().getRotationColumn(2));
                        clone.setEnabled(enabled);
                        localRoot.addLight(clone);

                        flash.addControl(new TimedActionControl(getRandomNumberInRange(1, 2) - 0.75f) {

                            @Override
                            void action() {

                                clone.setEnabled(false);
                                localRoot.removeLight(clone);
                            }
                        });
                    }

                }

            } else {

                lightnungStrikes_low = false;
                lightnungStrikes_med = false;
                lightnungStrikes_high = false;

                if (flash.getParticlesPerSec() > 0) {
                    flash.killAllParticles();
                    flash.setParticlesPerSec(0);
                }
            }

            if (misty) {
                if (spatial != null) {
                    if (spatial.getControl(FogPostFilter.class) != null) {
                        Node n = (Node) spatial;
                        if (!n.getChild("sunNode").getControl(GlobalLightingControl.class).getIsSun()) {
                            spatial.getControl(FogPostFilter.class).getFog().setFogColor(ColorRGBA.DarkGray);
                        } else {
                            spatial.getControl(FogPostFilter.class).getFog().setFogColor(ColorRGBA.Gray);
                        }

                        if (misty_high) {
                            if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() != fogDistance) {
                                spatial.getControl(FogPostFilter.class).setFogDistance(fogDistance);
                            }

                            if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity) {
                                spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf);
                            }
                        } else if (misty_med) {
                            if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() != fogDistance / 1.5) {
                                spatial.getControl(FogPostFilter.class).setFogDistance(fogDistance);
                            }

                            if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity / 1.5) {
                                spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf);
                            }
                        } else if (misty_low) {
                            if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() != fogDistance / 1.75) {
                                spatial.getControl(FogPostFilter.class).setFogDistance(fogDistance / 1.75f);
                            }

                            if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() < fogDensity / 1.75) {
                                spatial.getControl(FogPostFilter.class).setFogDensity(spatial.getControl(FogPostFilter.class).getFog().getFogDensity() + tpf);
                            }
                        }
                    }
                } else {

                    misty_low = false;
                    misty_med = false;
                    misty_high = false;

                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDistance() != 1000) {
                        spatial.getControl(FogPostFilter.class).setFogDistance(1000);//spatial.getControl(FogPostFilter.class).getFog().getFogDistance() - (tpf * 20)
                    }

                    if (spatial.getControl(FogPostFilter.class).getFog().getFogDensity() > 0) {
                        spatial.getControl(FogPostFilter.class).setFogDensity(0);//spatial.getControl(FogPostFilter.class).getFog().getFogDensity() - (tpf * 20)
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
