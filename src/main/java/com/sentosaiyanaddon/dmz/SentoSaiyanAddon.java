package com.sentosaiyanaddon.dmz;

import com.mojang.logging.LogUtils;
import com.sentosaiyanaddon.dmz.ability.SentoEffects;
import com.sentosaiyanaddon.dmz.config.SentoConfig;
import com.sentosaiyanaddon.dmz.strike.SentoStrikeTemplates;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("sentosaiyanaddon")
public class SentoSaiyanAddon {

        private static final Logger LOGGER = LogUtils.getLogger();

        public SentoSaiyanAddon() {
                IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

                LOGGER.info("[Sento] DragonMineZ: Sento Saiyan Race booting");
                LOGGER.info("[Sento] DMZ version: {}",
                                ModList.get().getModContainerById("dragonminez")
                                                .map(c -> c.getModInfo().getVersion().toString())
                                                .orElse("NOT LOADED — check your mods folder"));
                LOGGER.info("[Sento] Revamp detected: {}",
                                ModList.get().isLoaded("dmzrevamp") ? "yes" : "no");
                LOGGER.info("[Sento] Race installer source: {}",
                                RaceInstaller.desiredSource());
                LOGGER.info("[Sento] Addon version: {}",
                                ModList.get().getModContainerById("sentosaiyanaddon")
                                                .map(c -> c.getModInfo().getVersion().toString())
                                                .orElse("?"));

                LOGGER.info("[Sento] Oculus detected: {}",
                                ModList.get().isLoaded("oculus") ? "yes — polygon offset active" : "no");

                LOGGER.info("[Sento] NOEA detected: {}",
                                ModList.get().isLoaded("NoeaBosses") ? "yes — Be careful, I haven't created a compatibility patch for this mod yet, so issues may occur." : "no");

                LOGGER.info("[Sento] Generations detected: {}",
                                ModList.get().isLoaded("dmzgenerations") ? "yes — known z-fighting bug" : "no");

                SentoEffects.EFFECTS.register(bus);

                ModLoadingContext.get().registerConfig(
                                ModConfig.Type.COMMON,
                                SentoConfig.SPEC,
                                "dmzsentosaiyanaddon-common.toml");

                RaceInstaller.install();
                SentoStrikeTemplates.register();

                LOGGER.info("[Sento] Boot complete");
        }
}