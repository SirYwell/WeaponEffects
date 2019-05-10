package de.sirywell.weaponeffects;

import co.aikar.commands.PaperCommandManager;
import de.sirywell.weaponeffects.command.WeaponEffectsCommand;
import de.sirywell.weaponeffects.effect.EfficientEffect;
import de.sirywell.weaponeffects.handler.EffectHandler;
import de.sirywell.weaponeffects.handler.EfficientEffectHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;

public class WeaponEffects extends JavaPlugin {
    private EffectHandler effectHandler;

    @Override
    public void onEnable() {
        effectHandler = new EfficientEffectHandler(EnumSet.allOf(Material.class));
        setupCommands();
        Bukkit.getScheduler().runTaskTimer(this, new WeaponEffectTask(effectHandler), 0, 10 * 20);
    }

    @Override
    public void onDisable() {
        EfficientEffect.cleanUpCache();
    }

    private void setupCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new WeaponEffectsCommand(effectHandler));
    }
}
