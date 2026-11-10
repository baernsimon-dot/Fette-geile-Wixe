package com.example.testclient.gui;

import com.example.testclient.modules.*;
import com.example.testclient.modules.Module;
import com.example.testclient.settings.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.*;

public class ClickGui extends Screen {
    private static final int W = 440, H = 280, SIDE = 96, MROW = 24, SROW = 18;
    private String category = ModuleManager.CATEGORIES.get(0);
    private Module expanded;
    private int scroll;
    private NumSetting dragging;
    private int dragX, dragW;

    private record Row(int y, int h, Module module, Setting setting) {}

    public ClickGui() { super(Component.literal("TestClient")); }

    private int x0() { return (width - W) / 2; }
    private int y0() { return (height - H) / 2; }
    private int listX() { return x0() + SIDE + 8; }
    private int listW() { return W - SIDE - 16; }
    private int listTop() { return y0() + 34; }
    private int listBottom() { return y0() + H - 8; }

    private List<Row> rows() {
        List<Row> out = new ArrayList<>();
        int y = listTop() - scroll;
        for (Module m : ModuleManager.MODULES) {
            if (!m.category.equals(category)) continue;
            out.add(new Row(y, MROW, m, null)); y += MROW + 2;
            if (m == expanded) for (Setting s : m.settings) { out.add(new Row(y, SROW, m, s)); y += SROW + 1; }
        }
        return out;
    }

    private int contentHeight() {
        int h = 0;
        for (Module m : ModuleManager.MODULES) if (m.category.equals(category)) {
            h += MROW + 2; if (m == expanded) h += m.settings.size() * (SROW + 1);
        }
        return h;
    }

    @Override public void render(GuiGraphics g, int mx, int my, float delta) {
        g.fill(0, 0, width, height, 0x88000000);
        int x0 = x0(), y0 = y0();
        g.fill(x0, y0, x0 + W, y0 + H, 0xEE12121C);
        g.fill(x0, y0, x0 + SIDE, y0 + H, 0xEE0D0D15);
        g.drawString(font, "TestClient", x0 + 10, y0 + 10, 0xFFB4AFFF);
        g.drawString(font, "v2.0", x0 + 10, y0 + 21, 0xFF666680);

        int cy = y0 + 40;
        for (String c : ModuleManager.CATEGORIES) {
            boolean sel = c.equals(category), hov = mx >= x0 + 6 && mx < x0 + SIDE - 6 && my >= cy && my < cy + 22;
            if (sel) g.fill(x0 + 6, cy, x0 + SIDE - 6, cy + 22, 0xFF2A2760);
            else if (hov) g.fill(x0 + 6, cy, x0 + SIDE - 6, cy + 22, 0xFF1C1C2C);
            g.drawString(font, c, x0 + 14, cy + 7, sel ? 0xFFFFFFFF : 0xFF9090B0);
            cy += 24;
        }

        g.drawString(font, category + " Modules", listX(), y0 + 12, 0xFFE0E0FF);
        long on = ModuleManager.MODULES.stream().filter(m -> m.category.equals(category) && m.isEnabled()).count();
        g.drawString(font, on + " aktiv  |  Rechtsklick = Einstellungen", listX(), y0 + 22, 0xFF666680);

        g.enableScissor(listX(), listTop(), listX() + listW(), listBottom());
        for (Row r : rows()) {
            int rx = listX(), rw = listW();
            if (r.setting == null) {
                boolean hov = mx >= rx && mx < rx + rw && my >= r.y && my < r.y + r.h;
                g.fill(rx, r.y, rx + rw, r.y + r.h, hov ? 0xFF22223A : 0xFF191926);
                g.drawString(font, r.module.name, rx + 8, r.y + 4, 0xFFFFFFFF);
                g.drawString(font, r.module.description, rx + 8, r.y + 14, 0xFF6A6A88);
                int sx = rx + rw - 30, sy = r.y + 7;
                g.fill(sx, sy, sx + 20, sy + 10, r.module.isEnabled() ? 0xFF6C63FF : 0xFF3A3A50);
                int kx = r.module.isEnabled() ? sx + 11 : sx + 1;
                g.fill(kx, sy + 1, kx + 8, sy + 9, 0xFFFFFFFF);
            } else {
                int ix = rx + 12, iw = rw - 12;
                g.fill(ix, r.y, ix + iw, r.y + r.h, 0xFF14141F);
                if (r.setting instanceof BoolSetting b) {
                    g.drawString(font, b.name, ix + 6, r.y + 5, 0xFFC0C0D8);
                    g.fill(ix + iw - 22, r.y + 4, ix + iw - 8, r.y + 14, b.value ? 0xFF6C63FF : 0xFF3A3A50);
                } else if (r.setting instanceof NumSetting n) {
                    double f = (n.value - n.min) / (n.max - n.min);
                    g.fill(ix, r.y, ix + (int) (iw * f), r.y + r.h, 0xFF2A2760);
                    g.drawString(font, n.name, ix + 6, r.y + 5, 0xFFC0C0D8);
                    String v = n.step >= 1 ? String.valueOf((int) n.value) : String.format("%.2f", n.value);
                    g.drawString(font, v, ix + iw - 6 - font.width(v), r.y + 5, 0xFFFFFFFF);
                }
            }
        }
        g.disableScissor();
    }

    private void setFromMouse(NumSetting n, double mx) {
        double f = (mx - dragX) / (double) dragW;
        n.set(n.min + Math.max(0, Math.min(1, f)) * (n.max - n.min));
    }

    @Override public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
        double mx = e.x(), my = e.y(); int btn = e.button();
        int cy = y0() + 40;
        for (String c : ModuleManager.CATEGORIES) {
            if (mx >= x0() + 6 && mx < x0() + SIDE - 6 && my >= cy && my < cy + 22) {
                category = c; scroll = 0; expanded = null; return true;
            }
            cy += 24;
        }
        if (mx < listX() || mx >= listX() + listW() || my < listTop() || my >= listBottom()) return super.mouseClicked(e, dbl);
        for (Row r : rows()) {
            if (my < r.y || my >= r.y + r.h) continue;
            if (r.setting == null) {
                if (btn == 0) r.module.toggle();
                else if (btn == 1) { expanded = expanded == r.module ? null : r.module; }
                ModuleManager.save();
            } else if (r.setting instanceof BoolSetting b) {
                b.value = !b.value; ModuleManager.save();
            } else if (r.setting instanceof NumSetting n) {
                dragging = n; dragX = listX() + 12; dragW = listW() - 12; setFromMouse(n, mx);
            }
            return true;
        }
        return super.mouseClicked(e, dbl);
    }

    @Override public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (dragging != null) { setFromMouse(dragging, e.x()); return true; }
        return super.mouseDragged(e, dx, dy);
    }

    @Override public boolean mouseReleased(MouseButtonEvent e) {
        if (dragging != null) { dragging = null; ModuleManager.save(); return true; }
        return super.mouseReleased(e);
    }

    @Override public boolean mouseScrolled(double mx, double my, double h, double v) {
        int max = Math.max(0, contentHeight() - (listBottom() - listTop()));
        scroll = Math.max(0, Math.min(max, scroll - (int) (v * 14)));
        return true;
    }

    @Override public boolean isPauseScreen() { return false; }
}
