package de.sirywell.weaponeffects;

import co.aikar.commands.PaperCommandManager;
import de.sirywell.weaponeffects.command.WeaponEffectsCommand;
import de.sirywell.weaponeffects.effect.EfficientEffect;
import de.sirywell.weaponeffects.handler.EffectHandler;
import de.sirywell.weaponeffects.handler.EfficientEffectHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WeaponEffects extends JavaPlugin {
    private static final int TICKS_PER_SECOND = 20;
    private EffectHandler effectHandler;
    private BukkitTask weaponEffectBukkitTask;

    @Override
    public void onEnable() {
        loadEffectHandler();
        setupCommands();
        startTask();
    }

    @Override
    public void onDisable() {
        EfficientEffect.cleanUpCache();
        stopTask();
    }

    private void setupCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new WeaponEffectsCommand(effectHandler));
    }

    private void loadEffectHandler() {
        List<String> applicable = ConfigConstant.APPLICABLE_ITEMS.fromConfig(getConfig());
        Supplier<EnumSet<Material>> enumSetSupplier = () -> EnumSet.noneOf(Material.class);
        EnumSet<Material> items;
        if (applicable.isEmpty()) {
            Bukkit.getLogger().info("No applicable items were defined in config. Checking all items.");
            items = Arrays.stream(Material.values())
                    .parallel().filter(Material::isItem)
                    .collect(Collectors.toCollection(enumSetSupplier));
        } else {
            items = applicable.stream()
                    .map(matName -> {
                        Material mat = Material.getMaterial(matName);
                        if (mat == null) {
                            Bukkit.getLogger().warning(
                                    String.format("No material with name %s found. Ignoring it.", matName));
                        }
                        return mat;
                    })
                    .filter(Objects::nonNull)
                    .filter(material -> {
                        if (!material.isItem()) {
                            Bukkit.getLogger().warning(
                                    String.format("%s is not an item. Ignoring it.", material.name()));
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toCollection(enumSetSupplier));
        }
        effectHandler = new EfficientEffectHandler(items);
    }

    private void startTask() {
        Bukkit.getLogger().info("Starting weapon effect task...");
        int refreshRate = ConfigConstant.REFRESH_RATE.<Integer>fromConfig(getConfig()) * TICKS_PER_SECOND;
        weaponEffectBukkitTask = Bukkit.getScheduler()
                .runTaskTimer(this, new WeaponEffectTask(effectHandler), 0, refreshRate);
        Bukkit.getLogger().info("Started weapon effect task successfully.");
    }

    private void stopTask() {
        Bukkit.getLogger().info("Stopping weapon effect task...");
        weaponEffectBukkitTask.cancel();
        Bukkit.getLogger().info("Stopped weapon effect task successfully.");
    }

    private void restartTask() {
        Bukkit.getLogger().info("Restarting weapon effect task...");
        stopTask();
        startTask();
        Bukkit.getLogger().info("Restarted weapon effect task successfully.");
    }
}
