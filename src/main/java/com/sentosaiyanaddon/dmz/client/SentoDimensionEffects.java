package com.sentosaiyanaddon.dmz.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SentoDimensionEffects extends DimensionSpecialEffects {

    // null = no brightness at all during sunrise and sunset.
    // For a custom color, replace with RGB values ​​from 0 to 1; for example: new float[] {0.3F, 0.8F, 0.7F}
    private static final float[] SUNSET_RGB = null;

    private final float[] glow = new float[4];

    public SentoDimensionEffects() {
        super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor.multiply(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F,
                brightness * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    // The glow of the horizon and the hue of the mist at sunset originate here; the
    // biome's `sky_color` does not affect this.
    @Override
    public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        if (SUNSET_RGB == null) {
            return null;
        }
        float f1 = Mth.cos(timeOfDay * ((float) Math.PI * 2F));
        if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = f1 / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - Mth.sin(f3 * (float) Math.PI)) * 0.99F;
            f4 *= f4;
            glow[0] = SUNSET_RGB[0];
            glow[1] = SUNSET_RGB[1];
            glow[2] = SUNSET_RGB[2];
            glow[3] = f4;
            return glow;
        }
        return null;
    }
}
