package com.example.testclient.modules;

public class Sprint extends Module {
    public Sprint() { super("Sprint", "Movement", "Sprintet automatisch beim Vorwaertslaufen"); }
    @Override public void onTick() {
        if (mc.player != null && mc.options.keyUp.isDown() && !mc.player.isShiftKeyDown()) mc.player.setSprinting(true);
    }
}
