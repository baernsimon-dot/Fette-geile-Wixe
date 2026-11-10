package com.example.testclient.modules;

import com.example.testclient.settings.*;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.*;
import java.util.*;

public class ModuleManager {
    public static final List<Module> MODULES = new ArrayList<>();
    public static final List<String> CATEGORIES = List.of("Combat", "Movement", "Render", "World");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("testclient.json"); }

    public static void init() {
        MODULES.add(new KillAura());
        MODULES.add(new Fly());
        MODULES.add(new Sprint());
        MODULES.add(new EntityEsp());
        MODULES.add(new Fullbright());
        MODULES.add(new CoordsHud());
        MODULES.add(new BlockScan());
        load();
    }

    public static void tick() { for (Module m : MODULES) if (m.isEnabled()) m.onTick(); }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : MODULES) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                JsonObject s = new JsonObject();
                for (Setting st : m.settings) {
                    if (st instanceof BoolSetting b) s.addProperty(b.name, b.value);
                    else if (st instanceof NumSetting n) s.addProperty(n.name, n.value);
                }
                o.add("settings", s);
                root.add(m.name, o);
            }
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception ignored) {}
    }

    private static void load() {
        try {
            if (!Files.exists(file())) return;
            JsonObject root = JsonParser.parseString(Files.readString(file())).getAsJsonObject();
            for (Module m : MODULES) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                JsonObject s = o.has("settings") ? o.getAsJsonObject("settings") : new JsonObject();
                for (Setting st : m.settings) {
                    if (!s.has(st.name)) continue;
                    if (st instanceof BoolSetting b) b.value = s.get(st.name).getAsBoolean();
                    else if (st instanceof NumSetting n) n.set(s.get(st.name).getAsDouble());
                }
                if (o.has("enabled") && o.get("enabled").getAsBoolean()) m.setEnabled(true);
            }
        } catch (Exception ignored) {}
    }
}
