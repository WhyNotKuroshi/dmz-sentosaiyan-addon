package com.sentosaiyanaddon.dmz.ability;

import com.sentosaiyanaddon.dmz.block.SentoFluids;
import com.sentosaiyanaddon.dmz.config.SentoConfig;

import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.ResourceSyncS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SentoHealingLiquidHandler {

    private static final int HEAL_INTERVAL_TICKS = 20; // 1 second

    private static final Map<UUID, Integer> LINGER = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (!(event.player instanceof ServerPlayer player))
            return;

        if (!isInHealingLiquid(player)) {
            LINGER.remove(player.getUUID());
            return;
        }

        // Whenever submerged, HP / stamina / ki
        StatsProvider.get(StatsCapability.INSTANCE, player)
                .ifPresent(data -> applyGeneralHealing(player, data));

        // only when there is nemblock cures the flex
        if (SentoFlexHandler.needsFlexRecovery(player)) {
            tickFlexRecovery(player);
        } else {
            LINGER.remove(player.getUUID());
        }
    }

    private static void applyGeneralHealing(ServerPlayer player, StatsData data) {
        // executes only every 20 ticks (1s) — reduces the cost of setHealth/setResource
        if (player.tickCount % HEAL_INTERVAL_TICKS != 0)
            return;

        boolean needsSync = false;

        // HP
        float hp = player.getHealth();
        float maxHp = player.getMaxHealth();
        if (hp < maxHp) {
            player.setHealth(Math.min(maxHp, hp + (float) (double) SentoConfig.HOT_SPRING_HEAL_HP_PER_SECOND.get()));
        }

        // Stamina
        float stam = data.getResources().getCurrentStamina();
        float maxStam = data.getMaxStamina();
        if (stam < maxStam) {
            data.getResources().setCurrentStamina(
                    Math.min(maxStam,
                            stam + maxStam * (float) (double) SentoConfig.HOT_SPRING_HEAL_HP_PER_SECOND.get()));
            needsSync = true;
        }

        // Ki
        float ene = data.getResources().getCurrentEnergy();
        float maxEne = data.getMaxEnergy();
        if (ene < maxEne) {
            data.getResources().setCurrentEnergy(Math.min(maxEne,
                    ene + maxEne * (float) (double) SentoConfig.HOT_SPRING_HEAL_PERCENT_PER_SECOND.get()));
            needsSync = true;
        }

        // it only synchronizes if something has changed
        if (needsSync) {
            NetworkHandler.sendToTrackingEntityAndSelf(
                    new ResourceSyncS2C(player), player);
        }
    }

    private static void tickFlexRecovery(ServerPlayer player) {
        int remaining = LINGER.getOrDefault(player.getUUID(),
                SentoConfig.FLEX_HOT_SPRING_TICKS.get());
        remaining--;

        if (remaining <= 0) {
            LINGER.remove(player.getUUID());
            SentoFlexHandler.recoverFromHotSpring(player);
            return;
        }

        LINGER.put(player.getUUID(), remaining);

        if (remaining % 100 == 0) {
            int secs = remaining / 20;
            player.displayClientMessage(
                    Component.translatable(
                            "sentosaiyanaddon.ability.flex.hot_spring_progress", secs),
                    true);
        }
    }

    private static boolean isInHealingLiquid(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        if (isHealingLiquidBlock(player.level().getBlockState(pos)))
            return true;
        if (isHealingLiquidBlock(player.level().getBlockState(pos.below())))
            return true;
        return false;
    }

    private static boolean isHealingLiquidBlock(BlockState state) {
        Block block = state.getBlock();

        if (block == SentoFluids.HOT_SPRING_BLOCK.get())
            return true;

        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        return id != null && "dragonminez".equals(id.getNamespace()) && id.getPath().contains("healing_liquid");
    }

    private SentoHealingLiquidHandler() {
    }
}