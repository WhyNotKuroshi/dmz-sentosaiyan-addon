package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.model.DMZPlayerModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DMZPlayerModel.class, remap = false)
public class DMZPlayerModelMixin {

    @Inject(method = "resolveCustomModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void sentosaiyan$resolveCustomModels(
            String modelName, boolean isSlimSkin, boolean isMale,
            int bodyType, String customRaceGender,
            CallbackInfoReturnable<ResourceLocation> cir) {

        if (cir.isCancelled()) return;
        if (modelName == null) return;

        // Oozaru Sento
        if ("sentosaiyan_oozaru".equalsIgnoreCase(modelName)) {
            cir.setReturnValue(new ResourceLocation(
                    "dragonminez", "geo/entity/races/sentosaiyan_oozaru.geo.json"));
            return;
        }

        // Legendary forms — modelo buffed Sento
        if ("sentobuffed".equalsIgnoreCase(modelName)) {
            String gender = isMale ? "m" : "f";
            cir.setReturnValue(new ResourceLocation(
                    "dragonminez", "geo/entity/races/sentobuf_" + gender + ".geo.json"));
        }
    }
}