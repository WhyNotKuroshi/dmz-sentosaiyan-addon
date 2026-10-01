package com.sentosaiyanaddon.dmz.ability;

import com.dragonminez.common.init.MainEffects;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.sentosaiyanaddon.dmz.config.SentoConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SentoFlexHandler {

    private static final String NBT_NUMBLOCK_COUNT = "sento_flex_numblock";
    private static final String NBT_SLEEPING = "sento_flex_sleeping";
    private static final String NBT_ABUSE_COUNT = "sento_flex_abuse_count";
    private static final String NBT_ABUSE_LAST_TICK = "sento_flex_abuse_last";
    private static final String NBT_FLEX_KNOCKDOWN = "sento_flex_knockdown";

    private static final String CD_KEY = "TechniqueCooldown_sento_flex";
    private static final String CD_KNOCKDOWN = "KnockdownDuration";

    private static final Map<UUID, Integer> ACTIVE_TICK = new HashMap<>();

    private SentoFlexHandler() {
    }

    public static void toggle(ServerPlayer player) {
        if (player.hasEffect(SentoEffects.FLEX.get())) {
            deactivateClean(player);
        } else {
            activate(player);
        }
    }

    public static void recoverFromHotSpring(ServerPlayer player) {
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data != null) {
            data.getStatus().setKnockedDown(false);
            data.getCooldowns().setCooldown(CD_KEY, SentoConfig.FLEX_CLEAN_COOLDOWN_TICKS.get() / 2);
            SentoFlexBonus.remove(player, data);
        }

        CompoundTag nbt = player.getPersistentData();
        nbt.putBoolean(NBT_SLEEPING, false);
        nbt.putInt(NBT_NUMBLOCK_COUNT, 0);
        nbt.putInt(NBT_ABUSE_COUNT, 0);
        nbt.putBoolean(NBT_FLEX_KNOCKDOWN, false);

        player.removeEffect(MainEffects.STUN.get());
    }

    public static boolean isSleeping(ServerPlayer player) {
        return player.getPersistentData().getBoolean(NBT_SLEEPING);
    }

    private static void activate(ServerPlayer player) {
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null)
            return;

        if (!isSento(player)) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.not_sento"), true);
            return;
        }

        if (data.getCooldowns().hasCooldown(CD_KEY)) {
            int secs = data.getCooldowns().getCooldown(CD_KEY) / 20;
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.cooldown", secs), true);
            return;
        }

        if (isSleeping(player)) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.sleeping"), true);
            return;
        }

        CompoundTag nbt = player.getPersistentData();
        long now = player.level().getGameTime();
        long lastUse = nbt.getLong(NBT_ABUSE_LAST_TICK);

        if (now - lastUse > SentoConfig.FLEX_ABUSE_DECAY_TICKS.get()) {
            nbt.putInt(NBT_ABUSE_COUNT, 0);
        }

        int abuseCount = nbt.getInt(NBT_ABUSE_COUNT);
        int superformsLevel = data.getSkills().getSkillLevel("superforms");
        int abuseLimit = SentoConfig.FLEX_ABUSE_BASE_LIMIT.get()
                + superformsLevel * SentoConfig.FLEX_ABUSE_PER_LEVEL.get();

        if (abuseCount >= abuseLimit) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.abuse_ko"), true);
            numblock(player);
            return;
        }

        if (abuseCount == abuseLimit - 1) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.abuse_warning"), false);
        }

        nbt.putInt(NBT_ABUSE_COUNT, abuseCount + 1);
        nbt.putLong(NBT_ABUSE_LAST_TICK, now);

        double cost = data.getMaxStamina() * SentoConfig.FLEX_ACTIVATION_STAMINA_PCT.get();
        if (data.getResources().getCurrentStamina() < cost) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.no_stamina"), true);
            return;
        }

        data.getResources().setCurrentStamina(
                (float) (data.getResources().getCurrentStamina() - cost));

        SentoFlexBonus.apply(player, data);

        int window = SentoConfig.FLEX_ACTIVE_WINDOW_TICKS.get();
        player.addEffect(new MobEffectInstance(SentoEffects.FLEX.get(),
                window, 0, false, false, true));

        ACTIVE_TICK.put(player.getUUID(), 0);

        int remaining = abuseLimit - (abuseCount + 1);
        if (remaining > 0) {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.activate_uses", remaining),
                    true);
        } else {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.activate"), true);
        }
    }

    private static void deactivateClean(ServerPlayer player) {
        ACTIVE_TICK.remove(player.getUUID());
        player.removeEffect(SentoEffects.FLEX.get());

        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data != null) {
            data.getCooldowns().setCooldown(CD_KEY, SentoConfig.FLEX_CLEAN_COOLDOWN_TICKS.get());
            SentoFlexBonus.remove(player, data);
        }

        player.displayClientMessage(
                Component.translatable("sentosaiyanaddon.ability.flex.deactivate"), true);
    }

    private static void numblock(ServerPlayer player) {
        ACTIVE_TICK.remove(player.getUUID());
        player.removeEffect(SentoEffects.FLEX.get());

        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null)
            return;

        SentoFlexBonus.remove(player, data);

        int stunTicks = SentoConfig.FLEX_NUMBLOCK_STUN_TICKS.get();

        player.addEffect(new MobEffectInstance(MainEffects.STUN.get(),
                stunTicks, 0, false, false, true));

        // KnockdownDuration NAO MEXE NESSE E NEM NO isKnockedDown PFV
        data.getCooldowns().setCooldown(CD_KNOCKDOWN, stunTicks);
        data.getStatus().setKnockedDown(true);

        data.getCooldowns().setCooldown(CD_KEY, SentoConfig.FLEX_NUMBLOCK_COOLDOWN_TICKS.get());

        CompoundTag nbt = player.getPersistentData();
        nbt.putBoolean(NBT_FLEX_KNOCKDOWN, true);

        int count = nbt.getInt(NBT_NUMBLOCK_COUNT) + 1;
        nbt.putInt(NBT_NUMBLOCK_COUNT, count);

        int threshold = SentoConfig.FLEX_SLEEP_THRESHOLD.get();
        if (count >= threshold) {
            nbt.putBoolean(NBT_SLEEPING, true);
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.sleep"), true);
        } else {
            player.displayClientMessage(
                    Component.translatable("sentosaiyanaddon.ability.flex.numblock",
                            threshold - count),
                    true);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (!(event.player instanceof ServerPlayer player))
            return;

        CompoundTag nbt = player.getPersistentData();
        if (nbt.getBoolean(NBT_FLEX_KNOCKDOWN)) {
            if (!player.hasEffect(MainEffects.STUN.get())) {
                StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
                if (data != null) {
                    data.getStatus().setKnockedDown(false);
                }
                nbt.putBoolean(NBT_FLEX_KNOCKDOWN, false);
            }
        }

        Integer tick = ACTIVE_TICK.get(player.getUUID());
        if (tick == null)
            return;

        if (!player.hasEffect(SentoEffects.FLEX.get())) {
            numblock(player);
            return;
        }

        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null)
            return;

        // pra impedir de zoar o nível do cara quando o stat mudar
        if (tick % 20 == 0) {
            SentoFlexBonus.apply(player, data);
        }

        double drain = SentoConfig.FLEX_STAMINA_DRAIN_PER_TICK.get();
        float cur = data.getResources().getCurrentStamina();
        if (cur <= drain) {
            numblock(player);
            return;
        }
        data.getResources().setCurrentStamina(cur - (float) drain);

        int next = tick + 1;
        if (next >= SentoConfig.FLEX_ACTIVE_WINDOW_TICKS.get()) {
            numblock(player);
            return;
        }
        ACTIVE_TICK.put(player.getUUID(), next);
    }

    public static boolean needsFlexRecovery(ServerPlayer player) {
        if (isSleeping(player))
            return true;
        CompoundTag nbt = player.getPersistentData();
        if (nbt.getInt(NBT_NUMBLOCK_COUNT) > 0)
            return true;
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        return data != null && data.getStatus().isKnockedDown();
    }

    @SubscribeEvent
    public static void onWakeUp(net.minecraftforge.event.entity.player.PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if (event.wakeImmediately() || event.updateLevel())
            return;

        CompoundTag nbt = player.getPersistentData();
        int count = nbt.getInt(NBT_NUMBLOCK_COUNT);
        if (count <= 0)
            return;

        int recovery = SentoConfig.FLEX_BED_SLEEP_RECOVERY.get();
        if (recovery <= 0)
            return;

        int newCount = Math.max(0, count - recovery);
        nbt.putInt(NBT_NUMBLOCK_COUNT, newCount);

        player.displayClientMessage(
                Component.translatable("sentosaiyanaddon.ability.flex.slept",
                        count - newCount),
                false);
    }

    private static boolean isSento(ServerPlayer player) {
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null || data.getCharacter() == null)
            return false;
        String race = data.getCharacter().getRaceName();
        return race != null && "sentosaiyan".equalsIgnoreCase(race);
    }
}