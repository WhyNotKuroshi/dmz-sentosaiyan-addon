package com.sentosaiyanaddon.dmz.strike;

import com.dragonminez.common.stats.techniques.PredefinedTechniques;
import com.dragonminez.common.stats.techniques.StrikeAttackData;
import com.sentosaiyanaddon.dmz.config.SentoConfig;

public final class SentoStrikeTemplates {

    public static final String SOUND_BREAKER_ID = "sento_sound_breaker";
    public static final String TRIGGER_WAVE_ID = "sento_trigger_wave";
    public static final String FLEX_ID = "sento_flex";

    private SentoStrikeTemplates() {
    }

    public static void register() {
        StrikeAttackData sb = new StrikeAttackData();
        sb.setId(SOUND_BREAKER_ID);
        sb.setName("technique.sentosaiyanaddon.sound_breaker");
        sb.setAuthor("Orran/Sheeri");
        sb.setDamageMultiplier(0.0F);
        sb.setAnimationId("animation.technique.regeneration");
        sb.setDurationTicks(20);
        sb.applyConfigDefaults();
        sb.setCooldown(SentoConfig.SOUND_BREAKER_COOLDOWN_TICKS.get());
        PredefinedTechniques.STRIKE_REGISTRY.put(SOUND_BREAKER_ID, sb);

        StrikeAttackData tw = new StrikeAttackData();
        tw.setId(TRIGGER_WAVE_ID);
        tw.setName("technique.sentosaiyanaddon.trigger_wave");
        tw.setAuthor("Orran");
        tw.setDamageMultiplier(0.0F);
        tw.setAnimationId("animation.technique.regeneration");
        tw.setDurationTicks(60);
        tw.applyConfigDefaults();
        tw.setCooldown(SentoConfig.TRIGGER_WAVE_COOLDOWN_TICKS.get());
        PredefinedTechniques.STRIKE_REGISTRY.put(TRIGGER_WAVE_ID, tw);

        StrikeAttackData flex = new StrikeAttackData();
        flex.setId(FLEX_ID);
        flex.setName("technique.sentosaiyanaddon.flex");
        flex.setAuthor("WhyNotKuroshi");
        flex.setDamageMultiplier(0.0F);
        flex.setAnimationId("animation.technique.regeneration");
        flex.setDurationTicks(SentoConfig.FLEX_ACTIVE_WINDOW_TICKS.get());
        flex.applyConfigDefaults();
        flex.setCooldown(SentoConfig.FLEX_CLEAN_COOLDOWN_TICKS.get());
        PredefinedTechniques.STRIKE_REGISTRY.put(FLEX_ID, flex);
    }

    public static StrikeAttackData copy(String id) {
        StrikeAttackData source = PredefinedTechniques.STRIKE_REGISTRY.get(id);
        if (source == null)
            return null;
        StrikeAttackData copy = new StrikeAttackData();
        copy.load(source.save());
        return copy;
    }
}