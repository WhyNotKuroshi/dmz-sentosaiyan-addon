package com.sentosaiyanaddon.dmz.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.sentosaiyanaddon.dmz.RaceInstaller;
import com.sentosaiyanaddon.dmz.ability.SentoAbilities;
import com.sentosaiyanaddon.dmz.ability.SentoFlexHandler;
import com.sentosaiyanaddon.dmz.server.SentoWeatherState;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SentoCommands {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
                LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("dmzsento")
                                .then(Commands.literal("reset")
                                                .then(Commands.literal("config")
                                                                .requires(src -> src.hasPermission(2))
                                                                .executes(ctx -> {
                                                                        String source = RaceInstaller.desiredSource();
                                                                        RaceInstaller.forceInstall();
                                                                        String sourceLabel = source.equals("revamp")
                                                                                        ? "revamp"
                                                                                        : "base";
                                                                        final String finalSource = sourceLabel;
                                                                        ctx.getSource()
                                                                                        .sendSuccess(() -> Component
                                                                                                        .translatable(
                                                                                                                        "sentosaiyanaddon.command.reset_config.success",
                                                                                                                        finalSource),
                                                                                                        true);
                                                                        return 1;
                                                                }))
                                                // TODO: This resets the Flex state on the player that is executing it:
                                                // sleep, numblock count, abuse count, STUN, isKnockedDown.
                                                // maybe I’ll remove it after adding the dimension of the planet Sento
                                                .then(Commands.literal("flex")
                                                                .requires(src -> src.hasPermission(2))
                                                                .executes(ctx -> {
                                                                        ServerPlayer player = ctx.getSource()
                                                                                        .getPlayerOrException();
                                                                        SentoFlexHandler.recoverFromHotSpring(player);
                                                                        ctx.getSource().sendSuccess(
                                                                                        () -> Component.translatable(
                                                                                                        "sentosaiyanaddon.command.reset_flex.success"),
                                                                                        false);
                                                                        return 1;
                                                                })))
                                .then(Commands.literal("test")
                                                .requires(src -> src.hasPermission(2))
                                                .then(Commands.literal("soundbreaker")
                                                                .executes(ctx -> {
                                                                        ServerPlayer player = ctx.getSource()
                                                                                        .getPlayerOrException();
                                                                        return SentoAbilities.castSoundBreaker(player)
                                                                                        ? 1
                                                                                        : 0;
                                                                }))
                                                .then(Commands.literal("triggerwave")
                                                                .executes(ctx -> {
                                                                        ServerPlayer player = ctx.getSource()
                                                                                        .getPlayerOrException();
                                                                        return SentoAbilities.castTriggerWave(player)
                                                                                        ? 1
                                                                                        : 0;
                                                                })));
                event.getDispatcher().register(
                                Commands.literal("sentoweather")
                                                .requires(src -> src.hasPermission(2))
                                                .then(Commands.literal("rain")
                                                                .executes(ctx -> SentoWeatherState.set(ctx.getSource(),
                                                                                true, false)))
                                                .then(Commands.literal("thunder")
                                                                .executes(ctx -> SentoWeatherState.set(ctx.getSource(),
                                                                                true, true)))
                                                .then(Commands.literal("clear")
                                                                .executes(ctx -> SentoWeatherState.set(ctx.getSource(),
                                                                                false, false))));

                event.getDispatcher().register(root);
        }

        private SentoCommands() {
        }
}