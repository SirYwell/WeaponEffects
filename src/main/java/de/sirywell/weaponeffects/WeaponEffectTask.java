package de.sirywell.weaponeffects;

import de.sirywell.weaponeffects.handler.EffectHandler;
import org.bukkit.Bukkit;

public class WeaponEffectTask implements Runnable {
    private EffectHandler effectHandler;

    public WeaponEffectTask(EffectHandler effectHandler) {
        this.effectHandler = effectHandler;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(effectHandler::updateEffects);
    }
}
