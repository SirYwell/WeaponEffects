package de.sirywell.weaponeffects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigConstant {
    REFRESH_RATE("refresh-rate"),
    APPLICABLE_ITEMS("applicable-items");

    private final String path;

    ConfigConstant(String path) {
        this.path = path;
    }

    public <T> T fromConfig(FileConfiguration configuration) {
        try {
            return (T) configuration.get(path);
        } catch (ClassCastException e) {
            Bukkit.getLogger().warning("Trying to read invalid type from config: " + path);
            return null;
        }
    }
}
