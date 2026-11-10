package com.example.testclient.modules;

import com.example.testclient.settings.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/** ESP ueber den eingebauten Glow-Effekt (clientseitig, keine eigene Render-Pipeline noetig). */
public class EntityEsp extends Module {
    private final BoolSetting players = bool("Spieler", true);
    private final BoolSetting mobs = bool("Mobs", true);
    private final NumSetting range = num("Range", 64, 8, 128, 4);
    private final List<Entity> glowing = new ArrayList<>();

    public EntityEsp() { super("ESP", "Render", "Hebt Entities durch Waende hervor (Glow)"); }

    @Override public void onTick() {
        if (mc.level == null || mc.player == null) return;
        glowing.removeIf(e -> {
            boolean drop = e.isRemoved() || mc.player.distanceTo(e) > range.value;
            if (drop) e.setGlowingTag(false);
            return drop;
        });
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e == mc.player || !(e instanceof LivingEntity)) continue;
            boolean ok = (players.value && e instanceof Player) || (mobs.value && !(e instanceof Player));
            if (ok && mc.player.distanceTo(e) <= range.value) { e.setGlowingTag(true); if (!glowing.contains(e)) glowing.add(e); }
        }
    }
    @Override protected void onDisable() { glowing.forEach(e -> e.setGlowingTag(false)); glowing.clear(); }
}
