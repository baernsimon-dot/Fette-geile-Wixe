package com.example.testclient.modules;

import com.example.testclient.settings.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

/** Scannt geladene Bloecke um dich und zeigt die naechsten Treffer als HUD-Liste (Koordinaten). */
public class BlockScan extends Module {
    private final NumSetting radius = num("Radius", 24, 8, 48, 4);
    private final BoolSetting diamond = bool("Diamant", true);
    private final BoolSetting debris = bool("Ancient Debris", true);
    private final BoolSetting spawner = bool("Spawner", true);
    private final BoolSetting chest = bool("Kisten", false);
    private final List<String> lines = new ArrayList<>();
    private int tick;

    public BlockScan() { super("BlockScan", "World", "Listet Erze/Spawner in der Naehe mit Koordinaten"); }

    private boolean wanted(Block b) {
        return (diamond.value && (b == Blocks.DIAMOND_ORE || b == Blocks.DEEPSLATE_DIAMOND_ORE))
            || (debris.value && b == Blocks.ANCIENT_DEBRIS)
            || (spawner.value && b == Blocks.SPAWNER)
            || (chest.value && (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.BARREL));
    }

    @Override public void onTick() {
        if (mc.level == null || mc.player == null || ++tick % 40 != 0) return;
        int r = (int) radius.value; BlockPos c = mc.player.blockPosition();
        List<BlockPos> hits = new ArrayList<>(); List<String> names = new ArrayList<>();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) for (int y = -r; y <= r; y++) for (int z = -r; z <= r; z++) {
            p.set(c.getX() + x, c.getY() + y, c.getZ() + z);
            if (!mc.level.isLoaded(p)) continue;
            Block b = mc.level.getBlockState(p).getBlock();
            if (wanted(b)) { hits.add(p.immutable()); names.add(b.getName().getString()); }
        }
        Integer[] idx = new Integer[hits.size()];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingDouble(i -> hits.get(i).distSqr(c)));
        lines.clear();
        for (int i = 0; i < Math.min(8, idx.length); i++) {
            BlockPos h = hits.get(idx[i]);
            lines.add(names.get(idx[i]) + "  " + h.getX() + " " + h.getY() + " " + h.getZ());
        }
    }

    @Override public void onHud(GuiGraphics g) {
        int y = 40;
        for (String s : lines) { g.drawString(mc.font, s, 6, y, 0xFFFFE066); y += 10; }
    }
}
