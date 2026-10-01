package com.sentosaiyanaddon.dmz.ability;

import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.StatsSyncS2C;
import com.dragonminez.common.stats.StatsData;
import com.sentosaiyanaddon.dmz.config.SentoConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Espelha os bônus do Flex no sistema BonusStats do DMZ.
 * O StatsFlexMixin já multiplica o VALOR retornado por getStrength/Skp/Pwr.
 *
 * SÓ LEMBRANDO SEU BETINHA: chamar apply() ANTES de player.addEffect(FLEX), senão
 * getStrength() já retorna o valor buffado e a linha mostraria o dobro.
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
        // remove antes pra não acumular se algo chamar apply() duas vezes
        data.getBonusStats().removeBonus(stat, BONUS_NAME);
        data.getBonusStats().addBonus(stat, BONUS_NAME, "+", value, true);
    }

    private static void sync(ServerPlayer player) {
        NetworkHandler.sendToTrackingEntityAndSelf(
                new StatsSyncS2C(player), (Entity) player);
    }
}