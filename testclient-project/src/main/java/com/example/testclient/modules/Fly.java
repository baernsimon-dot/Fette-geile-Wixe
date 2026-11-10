package com.example.testclient.modules;

import com.example.testclient.settings.NumSetting;

public class Fly extends Module {
    private final NumSetting speed = num("Speed", 0.05, 0.02, 0.5, 0.01);
    public Fly() { super("Fly", "Movement", "Creative-Flug (nur Singleplayer / eigener Server)"); }
    @Override public void onTick() {
        if (mc.player == null) return;
        mc.player.getAbilities().mayfly = true;
        mc.player.getAbilities().setFlyingSpeed(speed.f());
    }
    @Override protected void onDisable() {
        if (mc.player == null) return;
        if (!mc.player.isCreative() && !mc.player.isSpectator()) {
            mc.player.getAbilities().mayfly = false;
            mc.player.getAbilities().flying = false;
        }
        mc.player.getAbilities().setFlyingSpeed(0.05f);
    }
}
