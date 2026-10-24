package com.sentosaiyanaddon.dmz.util;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import net.minecraft.world.entity.player.Player;

public final class SentoUtils {
    
    private SentoUtils() {}

    public static boolean isSento(Player player) {
        if (player == null) return false;
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null || data.getCharacter() == null) return false;
        String race = data.getCharacter().getRaceName();
        return "sentosaiyan".equalsIgnoreCase(race);
    }

    public static boolean isSentoMutant(Player player) {
        if (!isSento(player)) return false;
        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        return data != null && data.getEffects() != null && data.getEffects().hasEffect("mutant");
    }
}