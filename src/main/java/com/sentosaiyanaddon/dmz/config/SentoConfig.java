package com.sentosaiyanaddon.dmz.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SentoConfig {

    public static final ForgeConfigSpec SPEC;

    // Habilidade Sound Breaker
    public static final ForgeConfigSpec.DoubleValue SOUND_BREAKER_RADIUS;
    public static final ForgeConfigSpec.IntValue SOUND_BREAKER_STUN_TICKS;
    public static final ForgeConfigSpec.IntValue SOUND_BREAKER_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue SOUND_BREAKER_ENERGY_COST_PCT;

    // Habilidade Trigger Wave
    public static final ForgeConfigSpec.DoubleValue TRIGGER_WAVE_RADIUS;
    public static final ForgeConfigSpec.IntValue TRIGGER_WAVE_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue TRIGGER_WAVE_ENERGY_COST_PCT;

    // As mecanicas mais importantes da habilidade Flex
    public static final ForgeConfigSpec.IntValue FLEX_ACTIVE_WINDOW_TICKS;
    public static final ForgeConfigSpec.IntValue FLEX_CLEAN_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue FLEX_NUMBLOCK_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue FLEX_NUMBLOCK_STUN_TICKS;
    public static final ForgeConfigSpec.IntValue FLEX_SLEEP_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue FLEX_ACTIVATION_STAMINA_PCT;
    public static final ForgeConfigSpec.DoubleValue FLEX_STAMINA_DRAIN_PER_TICK;
    public static final ForgeConfigSpec.IntValue FLEX_ABUSE_BASE_LIMIT;
    public static final ForgeConfigSpec.IntValue FLEX_ABUSE_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue FLEX_ABUSE_DECAY_TICKS;

    // Flex recuperacao
    public static final ForgeConfigSpec.IntValue FLEX_HOT_SPRING_TICKS;
    public static final ForgeConfigSpec.IntValue FLEX_BED_SLEEP_RECOVERY;

    // Flex atributos
    public static final ForgeConfigSpec.DoubleValue FLEX_ATTACK_BONUS;
    public static final ForgeConfigSpec.DoubleValue FLEX_SPEED_PENALTY;
    public static final ForgeConfigSpec.DoubleValue FLEX_KNOCKBACK_RESIST;

    // Race Installer
    public static final ForgeConfigSpec.BooleanValue RACE_INSTALLER_ENABLED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        // Sound Breaker
        builder.comment(
                "Sound Breaker — short roar that stuns nearby targets.",
                "Available to every Sento Saiyan.")
                .push("sound_breaker");

        SOUND_BREAKER_RADIUS = builder
                .comment("Effect radius in blocks.")
                .defineInRange("radius", 10.0D, 1.0D, 100.0D);

        SOUND_BREAKER_STUN_TICKS = builder
                .comment("Stun duration in ticks (20 ticks = 1 second).")
                .defineInRange("stunTicks", 100, 20, 1200);

        SOUND_BREAKER_COOLDOWN_TICKS = builder
                .comment("Cooldown in ticks.")
                .defineInRange("cooldownTicks", 600, 0, 72000);

        SOUND_BREAKER_ENERGY_COST_PCT = builder
                .comment("Ki cost as a fraction of max energy (0.10 = 10%).")
                .defineInRange("energyCostPercent", 0.10D, 0.0D, 1.0D);

        builder.pop();

        // Trigger Wave
        builder.comment(
                "Trigger Wave — long roar that forces nearby Sentos into Oozaru.",
                "Exclusive to Sento Saiyan Mutants.")
                .push("trigger_wave");

        TRIGGER_WAVE_RADIUS = builder
                .comment("Effect radius in blocks.")
                .defineInRange("radius", 60.0D, 5.0D, 200.0D);

        TRIGGER_WAVE_COOLDOWN_TICKS = builder
                .comment("Cooldown in ticks.")
                .defineInRange("cooldownTicks", 2400, 0, 72000);

        TRIGGER_WAVE_ENERGY_COST_PCT = builder
                .comment("Ki cost as a fraction of max energy.")
                .defineInRange("energyCostPercent", 0.30D, 0.0D, 1.0D);

        builder.pop();

        // Flex — mecanicas principais
        builder.comment(
                "Flex — channels ki into the limbs, temporary buff.",
                "Available to every Sento Saiyan.")
                .push("flex");

        FLEX_ACTIVE_WINDOW_TICKS = builder
                .comment("Active duration in ticks (600 = 30 seconds).",
                        "If the window expires without deactivating, numblock triggers.")
                .defineInRange("activeWindowTicks", 600, 100, 72000);

        FLEX_CLEAN_COOLDOWN_TICKS = builder
                .comment("Cooldown after a clean deactivation, in ticks.")
                .defineInRange("cleanCooldownTicks", 600, 0, 72000);

        FLEX_NUMBLOCK_COOLDOWN_TICKS = builder
                .comment("Cooldown after a numblock, in ticks.",
                        "Lore default: 200s = 4000 ticks.")
                .defineInRange("numblockCooldownTicks", 4000, 0, 72000);

        FLEX_NUMBLOCK_STUN_TICKS = builder
                .comment("Stun duration after a numblock, in ticks.")
                .defineInRange("numblockStunTicks", 4000, 20, 72000);

        FLEX_SLEEP_THRESHOLD = builder
                .comment("How many numblocks before entering sleep state (lore: 3).")
                .defineInRange("sleepThreshold", 3, 1, 10);

        FLEX_ACTIVATION_STAMINA_PCT = builder
                .comment("Activation cost as a fraction of max stamina.")
                .defineInRange("activationStaminaPercent", 0.10D, 0.0D, 1.0D);

        FLEX_STAMINA_DRAIN_PER_TICK = builder
                .comment("Stamina drained per tick while active (0.05 = 1/s).")
                .defineInRange("staminaDrainPerTick", 0.05D, 0.0D, 5.0D);

        FLEX_ABUSE_BASE_LIMIT = builder
                .comment("Clean activations allowed before the drawback kicks in (base).")
                .defineInRange("abuseBaseLimit", 3, 1, 20);

        FLEX_ABUSE_PER_LEVEL = builder
                .comment("Extra activations granted per level of the 'superforms' skill.")
                .defineInRange("abusePerLevel", 1, 0, 10);

        FLEX_ABUSE_DECAY_TICKS = builder
                .comment("Idle time (in ticks) before the abuse counter resets.")
                .defineInRange("abuseDecayTicks", 1200, 100, 72000);

        // Recuperacao (cama e futuramente outra opcao ai)
        FLEX_HOT_SPRING_TICKS = builder
                .comment("Time immersed in a hot spring for full recovery.",
                        "6000 = 300 seconds (5 minutes).")
                .defineInRange("hotSpringTicks", 6000, 20, 72000);

        FLEX_BED_SLEEP_RECOVERY = builder
                .comment("How many numblocks are recovered per full night of sleep.",
                        "0 = beds do not help.")
                .defineInRange("bedSleepRecovery", 1, 0, 10);

        builder.pop();

        // Atributos do Flex
        builder.comment(
                "Attributes applied while Flex is active.",
                "Note: changes require a game RESTART — values are frozen at effect registration time.")
                .push("flex_attributes");

        FLEX_ATTACK_BONUS = builder
                .comment("Attack bonus as MULTIPLY_TOTAL (0.5 = +50%).")
                .defineInRange("attackBonus", 0.5D, 0.0D, 5.0D);

        FLEX_SPEED_PENALTY = builder
                .comment("Movement speed penalty (positive value = reduction).")
                .defineInRange("speedPenalty", 0.15D, 0.0D, 1.0D);

        FLEX_KNOCKBACK_RESIST = builder
                .comment("Knockback resistance (0.0 to 1.0).")
                .defineInRange("knockbackResistance", 0.5D, 0.0D, 1.0D);

        builder.pop();

        // Pra desativar ou ativar o Race Installer
        builder.comment(
                "Automatic race-file installer.",
                "Copies character/stats/forms JSON files to config/dragonminez/races/sentosaiyan/.",
                "Disable if you are a modpack author and ship pre-configured JSONs yourself.")
                .push("race_installer");

        RACE_INSTALLER_ENABLED = builder
                .comment("Run auto-install on boot?",
                        "true = addon installs/updates files automatically",
                        "false = modpack author manages files manually")
                .define("enabled", true);

        builder.pop();

        SPEC = builder.build();
    }

    private SentoConfig() {
    }
}