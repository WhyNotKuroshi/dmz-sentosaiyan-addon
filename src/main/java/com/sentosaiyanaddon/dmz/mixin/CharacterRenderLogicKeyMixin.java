package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.common.stats.character.Character;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Character.class, remap = false)
public class CharacterRenderLogicKeyMixin {

    @Inject(method = "getRenderLogicKey", at = @At("RETURN"), cancellable = true, remap = false)
    private void sentosaiyan$forceOozaruLogicKey(CallbackInfoReturnable<String> cir) {
        Character self = (Character) (Object) this;
        if (!sentosaiyan$isSentoOozaru(self)) return;

        String original = cir.getReturnValue();
        if (original != null && original.toLowerCase().startsWith("oozaru")) return;
        cir.setReturnValue("oozaru");
    }

    @Inject(method = "isOozaruCached", at = @At("RETURN"), cancellable = true, remap = false)
    private void sentosaiyan$forceOozaruCached(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue())) return;

        Character self = (Character) (Object) this;
        if (sentosaiyan$isSentoOozaru(self)) {
            cir.setReturnValue(true);
        }
    }

    private static boolean sentosaiyan$isSentoOozaru(Character self) {
        if (self == null) return false;
        String race = self.getRaceName();
        if (race == null || !race.equalsIgnoreCase("sentosaiyan")) return false;
        String form = self.getActiveForm();
        if (form == null) return false;
        return form.equalsIgnoreCase("oozaru") || form.equalsIgnoreCase("goldenoozaru");
    }
}