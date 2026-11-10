package com.example.testclient;

import com.example.testclient.gui.ClickGui;
import com.example.testclient.modules.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class TestClient implements ClientModInitializer {
    private static KeyMapping openGui;

    @Override public void onInitializeClient() {
        ModuleManager.init();
        openGui = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.testclient.gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openGui.consumeClick()) mc.setScreen(new ClickGui());
            ModuleManager.tick();
        });

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("testclient", "hud"), (g, delta) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.options.hideGui) return;
            int y = 4;
            for (Module m : ModuleManager.MODULES) {
                if (!m.isEnabled()) continue;
                m.onHud(g);
                int w = mc.font.width(m.name);
                int x = g.guiWidth() - w - 8;
                g.fill(x - 3, y - 1, g.guiWidth() - 2, y + 10, 0x99101018);
                g.fill(g.guiWidth() - 3, y - 1, g.guiWidth() - 2, y + 10, 0xFF6C63FF);
                g.drawString(mc.font, m.name, x, y + 1, 0xFFB4AFFF);
                y += 12;
            }
        });
    }
}
