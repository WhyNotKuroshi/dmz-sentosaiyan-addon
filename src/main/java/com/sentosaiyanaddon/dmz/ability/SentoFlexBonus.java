package com.sentosaiyanaddon.dmz.ability;

import com.sentosaiyanaddon.dmz.config.SentoConfig;

import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.StatsSyncS2C;
import com.dragonminez.common.stats.StatsData;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/* Mirrors Flex bonuses into the DMZ BonusStats system.
 * StatsFlexMixin already multiplies the value returned by getStrength/Skp/Pwr.
 *
 * JUST A REMINDER: call apply() BEFORE player.addEffect(FLEX); otherwise,
 * getStrength() will return the already-buffed value, and the line will show double the amount.
 */
public final class SentoFlexBonus {

    private static final String BONUS_NAME = "Flex";

    private SentoFlexBonus() {}

    public static void apply(ServerPlayer player, StatsData data) {
        if (data == null || data.getStats() == null) return;

        double mult = SentoConfig.FLEX_ATTACK_BONUS.get();

        int str = data.getStats().getStrength();
        int skp = data.getStats().getStrikePower();
        int pwr = data.getStats().getKiPower();

        add(data, "STR", (int) Math.floor(str * mult));
        add(data, "SKP", (int) Math.floor(skp * mult));
        add(data, "PWR", (int) Math.floor(pwr * mult));

        sync(player);
    }

    public static void remove(ServerPlayer player, StatsData data) {
        if (data == null || data.getBonusStats() == null) return;

        data.getBonusStats().removeBonus("STR", BONUS_NAME);
        data.getBonusStats().removeBonus("SKP", BONUS_NAME);
        data.getBonusStats().removeBonus("PWR", BONUS_NAME);

        sync(player);
    }

    private static void add(StatsData data, String stat, int value) {
        if (value <= 0) return;
        // Remove it beforehand to prevent accumulation if something calls apply() twice.
        data.getBonusStats().removeBonus(stat, BONUS_NAME);
        data.getBonusStats().addBonus(stat, BONUS_NAME, "+", value, true);
    }

    private static void sync(ServerPlayer player) {
        NetworkHandler.sendToTrackingEntityAndSelf(
                new StatsSyncS2C(player), (Entity) player);
    }
}