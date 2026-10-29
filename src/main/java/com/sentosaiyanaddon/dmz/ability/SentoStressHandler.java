package com.sentosaiyanaddon.dmz.ability;

import com.sentosaiyanaddon.dmz.block.SentoFluids;
import com.sentosaiyanaddon.dmz.server.SentoWeatherState;
import com.sentosaiyanaddon.dmz.util.SentoUtils;

import com.dragonminez.common.init.MainEffects;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SentoStressHandler {

    public static final String NBT_STRESS = "sento_stress";
    public static final String NBT_RAMPAGE = "sento_oozaru_rampage";
    public static final float MAX_STRESS = 100.0F;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!SentoUtils.isSento(player)) return;
        if (!player.level().dimension().equals(SentoWeatherState.SENTO_KEY)) return;
        if (player.tickCount % 20 != 0) return; 

        CompoundTag nbt = player.getPersistentData();
        float stress = nbt.getFloat(NBT_STRESS);
        boolean inRampage = nbt.getBoolean(NBT_RAMPAGE);

        if (player.level().getFluidState(player.blockPosition()).getType().isSame(SentoFluids.HOT_SPRING.get())) {
            stress = Math.max(0.0F, stress - 5.0F);
            nbt.putFloat(NBT_STRESS, stress);

            if (inRampage && stress <= 10.0F) {
                pacifyOozaru(player, nbt);
            }
            return;
        }

        if (inRampage) return; 

        if (player.getFoodData().getFoodLevel() <= 6) { 
            stress += 0.8F;
        }
        if (player.getHealth() < player.getMaxHealth() * 0.35F) {
            stress += 1.5F;
        }
        int ticksSinceRest = player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        if (ticksSinceRest > 48000) { 
            stress += 0.5F;
        }

        stress = Math.min(MAX_STRESS, Math.max(0.0F, stress));
        nbt.putFloat(NBT_STRESS, stress);

        if (stress >= MAX_STRESS) {
            triggerRampageOozaru(player, nbt);
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!SentoUtils.isSento(player)) return;
        if (!player.level().dimension().equals(SentoWeatherState.SENTO_KEY)) return;

        CompoundTag nbt = player.getPersistentData();
        float stress = nbt.getFloat(NBT_STRESS);
        
        stress = Math.min(MAX_STRESS, stress + (event.getAmount() * 0.5F));
        nbt.putFloat(NBT_STRESS, stress);

        if (stress >= MAX_STRESS && !nbt.getBoolean(NBT_RAMPAGE)) {
            triggerRampageOozaru(player, nbt);
        }
    }

    private static void triggerRampageOozaru(ServerPlayer player, CompoundTag nbt) {
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null || data.getCharacter() == null || !data.getCharacter().isHasSaiyanTail()) return;

        nbt.putBoolean(NBT_RAMPAGE, true);
        data.getCharacter().recordPreviousForm();
        data.getCharacter().setActiveForm("sentosaiyanoozaru", "oozaru");

        if (!player.hasEffect(MainEffects.TRANSFORMED.get())) {
            player.addEffect(new MobEffectInstance(MainEffects.TRANSFORMED.get(), -1, 0, false, false, true));
        }
    }

    private static void pacifyOozaru(ServerPlayer player, CompoundTag nbt) {
        nbt.putBoolean(NBT_RAMPAGE, false);
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data != null && data.getCharacter() != null) {
            data.getCharacter().clearActiveForm();
        }
        player.removeEffect(MainEffects.TRANSFORMED.get());
    }
}