package com.example.testclient.modules;

import com.example.testclient.settings.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name, category, description;
    public final List<Setting> settings = new ArrayList<>();
    protected final Minecraft mc = Minecraft.getInstance();
    private boolean enabled;

    protected Module(String name, String category, String description) {
        this.name = name; this.category = category; this.description = description;
    }

    protected BoolSetting bool(String n, boolean d) { BoolSetting s = new BoolSetting(n, d); settings.add(s); return s; }
    protected NumSetting num(String n, double d, double min, double max, double step) {
        NumSetting s = new NumSetting(n, d, min, max, step); settings.add(s); return s;
    }

    public boolean isEnabled() { return enabled; }
    public void toggle() { setEnabled(!enabled); }
    public void setEnabled(boolean e) {
        if (e == enabled) return;
        enabled = e;
        if (e) onEnable(); else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
    /** Optionales HUD-Rendering (nur wenn aktiv). */
    public void onHud(GuiGraphics g) {}
}
