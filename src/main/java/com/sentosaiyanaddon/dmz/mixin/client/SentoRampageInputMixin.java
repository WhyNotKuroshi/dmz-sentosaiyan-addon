package com.sentosaiyanaddon.dmz.mixin.client;

import com.sentosaiyanaddon.dmz.ability.SentoStressHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class SentoRampageInputMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void sentosaiyan$lockMovementDuringRampage(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.getPersistentData().getBoolean(SentoStressHandler.NBT_RAMPAGE)) {
            if (player.input != null) {
                player.input.forwardImpulse = 0.0F;
                player.input.leftImpulse = 0.0F;
                player.input.jumping = false;
                player.input.shiftKeyDown = false;
            }
        }
    }
}