package com.sentosaiyanaddon.dmz.event;

import com.mojang.logging.LogUtils;
import com.sentosaiyanaddon.dmz.SentoSaiyanAddon;
import com.sentosaiyanaddon.dmz.block.SentoFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = SentoSaiyanAddon.MOD_ID, value = Dist.CLIENT)
public class HotSpringUnderwaterSounds {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final long BLOOP_COOLDOWN_TICKS = 20L;

    private static boolean wasInHotSpring = false;
    private static boolean submergedNow = false;
    private static long lastBloopTick = -1000L;
    private static LoopSound loop = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            wasInHotSpring = false;
            submergedNow = false;
            return;
        }
        if (mc.isPaused()) {
            return;
        }

        boolean inHotSpring = isEyeInHotSpring(mc.level, player);
        boolean vanillaHandles = player.isUnderWater();

        if (inHotSpring != wasInHotSpring) {
            wasInHotSpring = inHotSpring;
            long now = mc.level.getGameTime();
            if (!vanillaHandles && now - lastBloopTick >= BLOOP_COOLDOWN_TICKS) {
                lastBloopTick = now;
                LOGGER.info("[Sento] som {} (vanilla submerso: {})", inHotSpring ? "ENTRAR" : "SAIR", vanillaHandles);
                mc.level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                        inHotSpring ? SoundEvents.AMBIENT_UNDERWATER_ENTER : SoundEvents.AMBIENT_UNDERWATER_EXIT,
                        SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }
        }

        submergedNow = inHotSpring && !vanillaHandles;

        if (submergedNow) {
            if (loop == null || loop.isStopped()) {
                loop = new LoopSound(player);
                LOGGER.info("[Sento] zumbido de fundo iniciado");
                mc.getSoundManager().play(loop);
            }

            float f = player.level().random.nextFloat();
            SoundEvent addition = null;
            if (f < 1.0E-4F) {
                addition = SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE;
            } else if (f < 0.001F) {
                addition = SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE;
            } else if (f < 0.01F) {
                addition = SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS;
            }
            if (addition != null) {
                LOGGER.info("[Sento] som ocasional: {}", addition.getLocation());
                mc.getSoundManager().play(SimpleSoundInstance.forAmbientAddition(addition));
            }
        }
    }

    private static boolean isEyeInHotSpring(Level level, LocalPlayer player) {
        double eyeY = player.getEyeY();
        BlockPos pos = BlockPos.containing(player.getX(), eyeY, player.getZ());
        FluidState fluid = level.getFluidState(pos);
        if (!fluid.getType().isSame(SentoFluids.HOT_SPRING.get())) {
            return false;
        }
        return pos.getY() + fluid.getHeight(level, pos) > eyeY;
    }

    private static class LoopSound extends AbstractTickableSoundInstance {
        private final LocalPlayer player;
        private int fade = 0;

        LoopSound(LocalPlayer player) {
            super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.AMBIENT,
                    SoundInstance.createUnseededRandom());
            this.player = player;
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0F;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (player.isRemoved() || fade < 0) {
                stop();
                return;
            }
            fade += submergedNow ? 1 : -2;
            fade = Math.min(fade, 40);
            volume = Math.max(0.0F, Math.min(1.0F, fade / 40.0F));
        }
    }
}