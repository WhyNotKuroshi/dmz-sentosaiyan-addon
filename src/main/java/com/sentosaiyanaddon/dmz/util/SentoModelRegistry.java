package com.sentosaiyanaddon.dmz.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class SentoModelRegistry {

    private SentoModelRegistry() {
    }

    @Nullable
    public static ResourceLocation resolveGeoModel(String customModel, boolean isMale, int bodyType) {
        if (customModel == null || customModel.isEmpty())
            return null;

        switch (customModel.toLowerCase()) {
            case "sentosaiyan_oozaru":
                return geo("sentosaiyan_oozaru.geo.json");

            case "sentobuffed":
                return geo("sentobuf_" + (isMale ? "m" : "f") + ".geo.json");

            default:
                return null;
        }
    }

    public static String resolveFaceKey(String faceKey) {
        if (faceKey == null)
            return null;

        switch (faceKey.toLowerCase()) {
            case "sentobuffed":
                return "sentosaiyan";

            default:
                return faceKey;
        }
    }

    private static ResourceLocation geo(String fileName) {
        return new ResourceLocation("dragonminez", "geo/entity/races/" + fileName);
    }
}