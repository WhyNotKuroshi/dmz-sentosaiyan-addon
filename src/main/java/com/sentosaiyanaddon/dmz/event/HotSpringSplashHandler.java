package com.sentosaiyanaddon.dmz.event;

import com.sentosaiyanaddon.dmz.SentoSaiyanAddon;
import com.sentosaiyanaddon.dmz.block.SentoFluids;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SentoSaiyanAddon.MOD_ID)
public class HotSpringSplashHandler {

    private static final String KEY_INSIDE = "SentoInHotSpring";
    private static final String KEY_LAST = "SentoHotSpringLastSound";
    private static final int COOLDOWN_TICKS = 10;
    private static final int SWIM_INTERVAL_TICKS = 8;
    private static final double SWIM_MIN_SPEED = 0.04D;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        boolean inside = level.getFluidState(entity.blockPosition())
                .getType().isSame(SentoFluids.HOT_SPRING.get());
        CompoundTag data = entity.getPersistentData();
        boolean wasInside = data.getBoolean(KEY_INSIDE);

        if (inside && !wasInside) {
            data.putBoolean(KEY_INSIDE, true);
            if (offCooldown(entity, data)) {
                splash(level, entity);
            }
        } else if (!inside && wasInside) {
            data.remove(KEY_INSIDE);
            if (offCooldown(entity, data)) {
                exitSplash(level, entity);
            }
        } else if (inside && entity.tickCount % SWIM_INTERVAL_TICKS == 0) {
            swim(level, entity);
        }
    }

    private static boolean offCooldown(LivingEntity entity, CompoundTag data) {
        int now = entity.tickCount;
        if (now - data.getInt(KEY_LAST) < COOLDOWN_TICKS && now >= data.getInt(KEY_LAST)) {
            return false;
        }
        data.putInt(KEY_LAST, now);
        return true;
    }

    private static void splash(ServerLevel level, LivingEntity entity) {
        Vec3 v = entity.getDeltaMovement();
        float volume = Math.min(1.0F,
                (float) Math.sqrt(v.x * v.x * 0.2D + v.y * v.y + v.z * v.z * 0.2D) * 0.2F);

        boolean player = entity instanceof Player;
        SoundEvent sound;
        if (volume < 0.25F) {
            sound = player ? SoundEvents.PLAYER_SPLASH : SoundEvents.GENERIC_SPLASH;
        } else {
            sound = player ? SoundEvents.PLAYER_SPLASH_HIGH_SPEED : SoundEvents.GENERIC_SPLASH;
        }

        float pitch = 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F;
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                sound, entity.getSoundSource(), volume, pitch);

        double surfaceY = Mth.floor(entity.getY()) + 1.0D;
        int count = 1 + (int) (entity.getBbWidth() * 20.0F);
        level.sendParticles(ParticleTypes.BUBBLE, entity.getX(), surfaceY, entity.getZ(),
                count, entity.getBbWidth() * 0.5D, 0.0D, entity.getBbWidth() * 0.5D, 0.1D);
        level.sendParticles(ParticleTypes.SPLASH, entity.getX(), surfaceY, entity.getZ(),
                count, entity.getBbWidth() * 0.5D, 0.0D, entity.getBbWidth() * 0.5D, 0.1D);
    }

    private static void exitSplash(ServerLevel level, LivingEntity entity) {
        boolean player = entity instanceof Player;
        SoundEvent sound = player ? SoundEvents.PLAYER_SPLASH : SoundEvents.GENERIC_SPLASH;
        float pitch = 1.2F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F;
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                sound, entity.getSoundSource(), 0.4F, pitch);

        double surfaceY = Mth.floor(entity.getY()) + 1.0D;
        level.sendParticles(ParticleTypes.SPLASH, entity.getX(), surfaceY, entity.getZ(),
                1 + (int) (entity.getBbWidth() * 10.0F),
                entity.getBbWidth() * 0.5D, 0.0D, entity.getBbWidth() * 0.5D, 0.1D);
    }

    private static void swim(ServerLevel level, LivingEntity entity) {
        Vec3 v = entity.getDeltaMovement();
        double speed = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        if (speed < SWIM_MIN_SPEED) {
            return;
        }
        float volume = Math.min(1.0F,
                (float) Math.sqrt(v.x * v.x * 0.2D + v.y * v.y + v.z * v.z * 0.2D) * 0.35F);
        SoundEvent sound = entity instanceof Player ? SoundEvents.PLAYER_SWIM : SoundEvents.GENERIC_SWIM;
        float pitch = 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F;
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                sound, entity.getSoundSource(), volume, pitch);
    }
}