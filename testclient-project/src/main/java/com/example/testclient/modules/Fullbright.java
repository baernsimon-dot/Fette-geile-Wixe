package com.example.testclient.modules;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Fullbright extends Module {
    public Fullbright() { super("Fullbright", "Render", "Nachtsicht (nur clientseitig)"); }
    @Override public void onTick() {
        if (mc.player != null) mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
    }
    @Override protected void onDisable() {
        if (mc.player != null) mc.player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
