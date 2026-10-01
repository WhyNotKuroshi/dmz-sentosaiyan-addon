package com.sentosaiyanaddon.dmz.ability;

import com.sentosaiyanaddon.dmz.config.SentoConfig;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SentoEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            "sentosaiyanaddon");

    public static final RegistryObject<MobEffect> FLEX = EFFECTS.register("sento_flex",
            () -> new SentoFlexEffect()
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                            "A1B2C3D4-E5F6-7890-ABCD-EF1234567890",
                            SentoConfig.FLEX_ATTACK_BONUS.get(),
                            AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            "B2C3D4E5-F6A7-8901-BCDE-F12345678901",
                            -SentoConfig.FLEX_SPEED_PENALTY.get(),
                            AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
                            "C3D4E5F6-A7B8-9012-CDEF-123456789012",
                            SentoConfig.FLEX_KNOCKBACK_RESIST.get(),
                            AttributeModifier.Operation.ADDITION));

    public static void register(net.minecraftforge.eventbus.api.IEventBus bus) {
        EFFECTS.register(bus);
    }

    private SentoEffects() {
    }
}