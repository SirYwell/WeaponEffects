package de.sirywell.weaponeffects;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private final Map<String, String> cache;

    public Messages(FileConfiguration messages) {
        this.cache = new HashMap<>();
        loadMessages(messages);
    }

    public String getFormattedMessage(String key, Object... replacements) {
        return MessageFormat.format(cache.get(key), replacements);
    }

    public String getFormattedMessage(String key) {
        return cache.get(key);
    }

    private void loadMessages(FileConfiguration messages) {
        for (String key : messages.getKeys(true)) {
            if (messages.isString(key)) {
                cache.put(key, ChatColor.translateAlternateColorCodes('&', messages.getString(key)));
            }
        }
    }
}
