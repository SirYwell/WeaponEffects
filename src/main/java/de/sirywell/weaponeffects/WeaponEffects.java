package de.sirywell.weaponeffects;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.PaperCommandManager;
import de.sirywell.weaponeffects.command.WeaponEffectsCommand;
import de.sirywell.weaponeffects.effect.EfficientEffect;
import de.sirywell.weaponeffects.effect.WeaponEffectType;
import de.sirywell.weaponeffects.handler.EffectHandler;
import de.sirywell.weaponeffects.handler.EfficientEffectHandler;
import de.sirywell.weaponeffects.listener.ItemChangeListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WeaponEffects extends JavaPlugin {
    private static final int TICKS_PER_SECOND = 20;
    private static final String MESSAGES_FILE_NAME = "messages.yml";
    private static Logger LOGGER;
    private EffectHandler effectHandler;
    private BukkitTask weaponEffectBukkitTask;
    private Messages messages;
    private Settings settings;

    public static Logger getPluginLogger() {
        return LOGGER;
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        saveDefaultConfig();
        loadSettings();
        saveResource(MESSAGES_FILE_NAME, false);
        loadEffectHandler();
        startTask();
        loadMessages();
        setupCommands();

        Bukkit.getPluginManager().registerEvents(new ItemChangeListener(effectHandler), this);
    }

    @Override
    public void onDisable() {
        EfficientEffect.cleanUpCache();
        stopTask();
    }

    private void loadSettings() {
        ConfigurationSerialization.registerClass(Settings.class, "Settings");
        settings = (Settings) getConfig().get("settings");
    }

    private void loadMessages() {
        File file = new File(getDataFolder(), MESSAGES_FILE_NAME);
        messages = new Messages(YamlConfiguration.loadConfiguration(file));
    }

    private void setupCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.enableUnstableAPI("help");

        manager.getCommandContexts().registerContext(byte.class, c -> {
            try {
                return parseAndValidateNumber(c, Byte.MIN_VALUE, Byte.MAX_VALUE).byteValue();
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgument(MessageKeys.MUST_BE_A_NUMBER, "{num}", c.getFirstArg());
            }
        });

        manager.getCommandCompletions().registerAsyncCompletion("weaponEffectType",
                c -> Arrays.stream(WeaponEffectType.values())
                        .map(WeaponEffectType::name)
                        .collect(Collectors.toList()));

        manager.registerCommand(new WeaponEffectsCommand(effectHandler, messages));
    }

    private void loadEffectHandler() {
        effectHandler = new EfficientEffectHandler(settings.getApplicableItems(), settings.getAdditionalSlots());
    }

    private void startTask() {
        WeaponEffects.getPluginLogger().info("Starting weapon effect task...");
        int refreshRate = settings.getRefreshRate() * TICKS_PER_SECOND;
        weaponEffectBukkitTask = Bukkit.getScheduler()
                .runTaskTimer(this, new WeaponEffectTask(effectHandler), 0, refreshRate);
        WeaponEffects.getPluginLogger().info("Started weapon effect task successfully.");
    }

    private void stopTask() {
        WeaponEffects.getPluginLogger().info("Stopping weapon effect task...");
        weaponEffectBukkitTask.cancel();
        LOGGER.info("Stopped weapon effect task successfully.");
    }

    private void restartTask() {
        LOGGER.info("Restarting weapon effect task...");
        stopTask();
        startTask();
        LOGGER.info("Restarted weapon effect task successfully.");
    }


    // see https://github.com/aikar/commands/blob/master/core/src/main/java/co/aikar/commands/CommandContexts.java


    private Number parseAndValidateNumber(BukkitCommandExecutionContext c, Number minValue, Number maxValue)
            throws InvalidCommandArgument {
        final Number val = ACFUtil.parseNumber(c.popFirstArg(), c.hasFlag("suffixes"));
        validateMinMax(c, val, minValue, maxValue);
        return val;
    }

    private void validateMinMax(BukkitCommandExecutionContext c, Number val, Number minValue, Number maxValue)
            throws InvalidCommandArgument {
        minValue = c.getFlagValue("min", minValue);
        maxValue = c.getFlagValue("max", maxValue);
        if (maxValue != null && val.doubleValue() > maxValue.doubleValue()) {
            throw new InvalidCommandArgument(MessageKeys.PLEASE_SPECIFY_AT_MOST, "{max}", String.valueOf(maxValue));
        }
        if (minValue != null && val.doubleValue() < minValue.doubleValue()) {
            throw new InvalidCommandArgument(MessageKeys.PLEASE_SPECIFY_AT_LEAST, "{min}", String.valueOf(minValue));
        }
    }
}
