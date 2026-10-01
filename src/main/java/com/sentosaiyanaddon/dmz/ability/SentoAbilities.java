package com.sentosaiyanaddon.dmz.ability;

import com.dragonminez.common.init.MainEffects;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.sentosaiyanaddon.dmz.config.SentoConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class SentoAbilities {

    private static final String CD_SOUND_BREAKER = "TechniqueCooldown_sento_sound_breaker";
    private static final String CD_TRIGGER_WAVE = "TechniqueCooldown_sento_trigger_wave";

    public static boolean castSoundBreaker(ServerPlayer player) {
        if (!isSento(player)) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.sound_breaker.not_sento"), true);
            return false;
        }

        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null)
            return false;

        if (data.getCooldowns().hasCooldown(CD_SOUND_BREAKER)) {
            int secs = data.getCooldowns().getCooldown(CD_SOUND_BREAKER) / 20;
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.cooldown", secs), true);
            return false;
        }

        double energyCost = data.getMaxEnergy() * SentoConfig.SOUND_BREAKER_ENERGY_COST_PCT.get();
        if (data.getResources().getCurrentEnergy() < energyCost) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.no_ki"), true);
            return false;
        }

        data.getResources().setCurrentEnergy(
                (float) (data.getResources().getCurrentEnergy() - energyCost));

        playDmzSound(player, "oozaru_growl_player", 2.0F, 0.6F);

        ServerLevel level = player.serverLevel();
        Vec3 pos = player.position();
        level.sendParticles(ParticleTypes.SONIC_BOOM,
                pos.x, pos.y + 1.0D, pos.z, 1, 0, 0, 0, 0);
        for (int i = 0; i < 24; i++) {
            double angle = (Math.PI * 2.0D) * i / 24.0D;
            double dx = Math.cos(angle) * 2.0D;
            double dz = Math.sin(angle) * 2.0D;
            level.sendParticles(ParticleTypes.CLOUD,
                    pos.x + dx, pos.y + 1.0D, pos.z + dz,
                    1, 0, 0, 0, 0);
        }

        double radius = SentoConfig.SOUND_BREAKER_RADIUS.get();
        AABB box = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box);

        int stunTicks = SentoConfig.SOUND_BREAKER_STUN_TICKS.get();
        for (LivingEntity target : targets) {
            if (target == player)
                continue;
            if (!canBeAffected(target))
                continue;
            applyStun(target, stunTicks);
        }

        data.getCooldowns().setCooldown(CD_SOUND_BREAKER,
                SentoConfig.SOUND_BREAKER_COOLDOWN_TICKS.get());

        return true;
    }

    public static boolean castTriggerWave(ServerPlayer player) {
        if (!isSentoMutant(player)) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.trigger_wave.not_sento_mutant"), true);
            return false;
        }

        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null)
            return false;

        if (data.getCooldowns().hasCooldown(CD_TRIGGER_WAVE)) {
            int secs = data.getCooldowns().getCooldown(CD_TRIGGER_WAVE) / 20;
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.cooldown", secs), true);
            return false;
        }

        double energyCost = data.getMaxEnergy() * SentoConfig.TRIGGER_WAVE_ENERGY_COST_PCT.get();
        if (data.getResources().getCurrentEnergy() < energyCost) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.no_ki"), true);
            return false;
        }

        data.getResources().setCurrentEnergy(
                (float) (data.getResources().getCurrentEnergy() - energyCost));

        playDmzSound(player, "oozaru_growl_player", 3.0F, 0.4F);

        ServerLevel level = player.serverLevel();
        Vec3 pos = player.position();
        for (int ring = 0; ring < 5; ring++) {
            double r = (ring + 1) * 3.0D;
            for (int i = 0; i < 32; i++) {
                double angle = (Math.PI * 2.0D) * i / 32.0D;
                double dx = Math.cos(angle) * r;
                double dz = Math.sin(angle) * r;
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        pos.x + dx, pos.y + 0.5D, pos.z + dz,
                        1, 0, 0.05D, 0, 0);
            }
        }

        double radius = SentoConfig.TRIGGER_WAVE_RADIUS.get();
        AABB box = player.getBoundingBox().inflate(radius);
        List<ServerPlayer> targets = level.getEntitiesOfClass(ServerPlayer.class, box);

        for (ServerPlayer target : targets) {
            if (target == player)
                continue;
            if (!isSento(target))
                continue;
            if (isAlreadyOozaru(target))
                continue;
            forceOozaru(target);
        }

        data.getCooldowns().setCooldown(CD_TRIGGER_WAVE,
                SentoConfig.TRIGGER_WAVE_COOLDOWN_TICKS.get());

        return true;
    }

    private static boolean isSento(ServerPlayer player) {
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null || data.getCharacter() == null)
            return false;
        String race = data.getCharacter().getRaceName();
        return race != null && "sentosaiyan".equalsIgnoreCase(race);
    }

    private static boolean isSentoMutant(ServerPlayer player) {
        if (!isSento(player))
            return false;
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        return data != null && data.getEffects() != null && data.getEffects().hasEffect("mutant");
    }

    private static boolean isAlreadyOozaru(ServerPlayer player) {
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null || data.getCharacter() == null)
            return false;
        String form = data.getCharacter().getActiveForm();
        return form != null && (form.equalsIgnoreCase("oozaru") || form.equalsIgnoreCase("goldenoozaru"));
    }

    private static boolean forceOozaru(ServerPlayer player) {
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null || data.getCharacter() == null)
            return false;
        if (data.getCharacter().hasActiveForm() || data.getCharacter().hasActiveStackForm())
            return false;
        if (!data.getCharacter().isHasSaiyanTail())
            return false;

        float[] snapshot = data.snapshotMultiplierResources();
        data.getCharacter().recordPreviousForm();
        data.getCharacter().setActiveForm("sentosaiyanoozaru", "oozaru");
        data.restoreMultiplierGains(player, snapshot);

        MobEffect transformed = MainEffects.TRANSFORMED.get();
        if (!player.hasEffect(transformed)) {
            player.addEffect(new MobEffectInstance(transformed, -1, 0, false, false, true));
        }

        player.refreshDimensions();
        player.displayClientMessage(
                Component.translatable("sentosaiyanaddon.ability.trigger_wave.forced"), true);
        return true;
    }

    private static void applyStun(LivingEntity target, int ticks) {
        MobEffect stun = MainEffects.STUN.get();
        target.addEffect(new MobEffectInstance(stun, ticks, 0, false, false, true));
    }

    private static boolean canBeAffected(LivingEntity target) {
        if (target instanceof ServerPlayer tp) {
            if (isSento(tp))
                return false;
        }
        return true;
    }

    private static void playDmzSound(ServerPlayer player, String soundName, float volume, float pitch) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(
                new ResourceLocation("dragonminez", soundName));
        if (sound == null) {
            sound = net.minecraft.sounds.SoundEvents.ENDER_DRAGON_GROWL;
        }
        player.serverLevel().playSound(null,
                player.getX(), player.getY(), player.getZ(),
                sound, SoundSource.PLAYERS, volume, pitch);
    }

    private SentoAbilities() {
    }
}