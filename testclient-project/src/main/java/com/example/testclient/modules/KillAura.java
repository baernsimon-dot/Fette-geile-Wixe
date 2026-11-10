package com.example.testclient.modules;

import com.example.testclient.settings.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public class KillAura extends Module {
    private final NumSetting range = num("Range", 4.0, 2.0, 6.0, 0.1);
    private final NumSetting cooldown = num("Cooldown", 0.9, 0.0, 1.0, 0.05);
    private final BoolSetting mobs = bool("Monster", true);
    private final BoolSetting players = bool("Spieler", false);

    public KillAura() { super("KillAura", "Combat", "Greift Ziele in Reichweite an (Singleplayer / Testserver)"); }

    @Override public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;
        if (mc.player.getAttackStrengthScale(0.5f) < cooldown.f()) return;
        Entity best = null; double bd = range.value * range.value;
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e == mc.player || !(e instanceof LivingEntity le) || !le.isAlive()) continue;
            boolean ok = (mobs.value && e instanceof Enemy) || (players.value && e instanceof Player);
            if (!ok) continue;
            double d = mc.player.distanceToSqr(e);
            if (d <= bd) { bd = d; best = e; }
        }
        if (best != null) {
            mc.gameMode.attack(mc.player, best);
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
