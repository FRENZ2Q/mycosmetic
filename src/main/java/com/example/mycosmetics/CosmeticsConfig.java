package com.example.mycosmetics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class CosmeticsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("mycosmetics.json");
    private static CosmeticsConfig instance = new CosmeticsConfig();

    public boolean enabled = true;
    public boolean showWhenInvisible = false;
    /** категория (CAPE, HAT...) -> id косметики; нет записи = ничего не надето */
    public Map<String, String> selected = new LinkedHashMap<>();

    public static CosmeticsConfig get() {
        return instance;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                CosmeticsConfig loaded = GSON.fromJson(Files.readString(FILE), CosmeticsConfig.class);
                if (loaded != null) {
                    if (loaded.selected == null) loaded.selected = new LinkedHashMap<>();
                    instance = loaded;
                }
            } else {
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Files.writeString(FILE, GSON.toJson(instance));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
