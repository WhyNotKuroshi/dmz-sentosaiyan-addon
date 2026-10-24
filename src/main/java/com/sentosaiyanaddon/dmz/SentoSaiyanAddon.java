package com.sentosaiyanaddon.dmz;

import com.mojang.logging.LogUtils;

import com.sentosaiyanaddon.dmz.ability.SentoEffects;
import com.sentosaiyanaddon.dmz.block.SentoFluids;
import com.sentosaiyanaddon.dmz.config.SentoConfig;
import com.sentosaiyanaddon.dmz.strike.SentoStrikeTemplates;
import com.sentosaiyanaddon.dmz.worldgen.ReplaceFluidFeature;
import com.sentosaiyanaddon.dmz.item.SentoItems;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.slf4j.Logger;

@Mod(SentoSaiyanAddon.MOD_ID)
public class SentoSaiyanAddon {

        public static final String MOD_ID = "sentosaiyanaddon";
        private static final Logger LOGGER = LogUtils.getLogger();
        public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES,
                        MOD_ID);
        public static final RegistryObject<ReplaceFluidFeature> REPLACE_FLUID = FEATURES.register("replace_fluid",
                        ReplaceFluidFeature::new);

        public SentoSaiyanAddon() {
                IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

                SentoFluids.FLUID_TYPES.register(bus);
                SentoFluids.FLUIDS.register(bus);
                SentoFluids.BLOCKS.register(bus);
                SentoItems.ITEMS.register(bus);
                FEATURES.register(bus);

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

                LOGGER.info("[Sento] Generations detected: {}",
                                ModList.get().isLoaded("dmzgenerations") ? "yes" : "no");

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