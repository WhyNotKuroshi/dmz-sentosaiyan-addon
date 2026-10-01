package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.render.layer.DMZHairLayer;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.character.Character;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DMZHairLayer.class, remap = false)
public class DMZHairLayerOozaruMixin {

    @Inject(method = "renderHair", at = @At("HEAD"), cancellable = true, remap = false)
    private void sentosaiyan$skipOozaruHair(PoseStack poseStack, AbstractClientPlayer animatable,
            MultiBufferSource bufferSource, float partialTick,
            int packedLight, int packedOverlay, CallbackInfo ci) {
        if (animatable == null)
            return;
        LazyOptional<StatsData> statsCap = StatsProvider.get(StatsCapability.INSTANCE, (Entity) animatable);
        StatsData stats = statsCap.orElse(new StatsData((Player) animatable));
        Character character = stats.getCharacter();
        if (character == null)
            return;
        if (!"sentosaiyan".equalsIgnoreCase(character.getRaceName()))
            return;

        if (character.hasActiveForm()) {
            String form = character.getActiveForm();
            if (form != null && (form.equalsIgnoreCase("oozaru")
                    || form.equalsIgnoreCase("goldenoozaru"))) {
                ci.cancel();
            }
        }
    }
}