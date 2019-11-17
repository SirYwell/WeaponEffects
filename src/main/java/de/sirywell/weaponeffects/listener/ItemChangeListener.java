package de.sirywell.weaponeffects.listener;

import de.sirywell.weaponeffects.handler.EffectHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class ItemChangeListener implements Listener {
    private EffectHandler effectHandler;

    public ItemChangeListener(EffectHandler effectHandler) {
        this.effectHandler = effectHandler;
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent event) {
        effectHandler.updateEffects(event.getPlayer());
    }
}
