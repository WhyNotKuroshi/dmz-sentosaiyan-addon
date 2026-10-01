package com.sentosaiyanaddon.dmz.ability;

import com.sentosaiyanaddon.dmz.config.SentoConfig;
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

    private static final String DMZ_NAMESPACE = "dragonminez";
    private static final String LIQUID_PATH_KEY = "healing_liquid";

    private static final Map<UUID, Integer> LINGER = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!SentoFlexHandler.needsFlexRecovery(player)) {
            LINGER.remove(player.getUUID());
            return;
        }

        if (!isInHealingLiquid(player)) {
            LINGER.remove(player.getUUID());
            return;
        }

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
        for (int dy = -1; dy <= 0; dy++) {
            BlockPos pos = player.blockPosition().offset(0, dy, 0);
            BlockState state = player.level().getBlockState(pos);
            if (isHealingLiquidBlock(state)) return true;
        }
        return false;
    }

    private static boolean isHealingLiquidBlock(BlockState state) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return id != null
                && DMZ_NAMESPACE.equals(id.getNamespace())
                && id.getPath().contains(LIQUID_PATH_KEY);
    }

    private SentoHealingLiquidHandler() {}
}