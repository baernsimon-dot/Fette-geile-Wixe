package com.example.testclient.modules;

import net.minecraft.client.gui.GuiGraphics;

public class CoordsHud extends Module {
    public CoordsHud() { super("Coords", "Render", "Zeigt Koordinaten und FPS"); }
    @Override public void onHud(GuiGraphics g) {
        if (mc.player == null) return;
        String s = String.format("XYZ %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        g.drawString(mc.font, s, 6, 6, 0xFFFFFFFF);
        g.drawString(mc.font, mc.getFps() + " FPS", 6, 18, 0xFFAAAAAA);
    }
}
