package de.sirywell.weaponeffects.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.sirywell.weaponeffects.Messages;
import de.sirywell.weaponeffects.effect.WeaponEffect;
import de.sirywell.weaponeffects.effect.WeaponEffectType;
import de.sirywell.weaponeffects.handler.EffectHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@CommandAlias("weaponeffects|weffects|weffect")
public class WeaponEffectsCommand extends BaseCommand {
    private EffectHandler<?> effectHandler;
    private Messages messages;

    public WeaponEffectsCommand(EffectHandler effectHandler, Messages messages) {
        this.effectHandler = effectHandler;
        this.messages = messages;
    }

    @Default
    public void help(Player player) {
        player.sendMessage(messages.getFormattedMessage("help"));
    }

    @Subcommand("show")
    public void showEffects(Player player) {
        if (!hasValidItem(player)) {
            return;
        }
        Set<? extends WeaponEffect> effects = effectHandler.getEffects(player.getInventory().getItemInMainHand());
        if (effects.isEmpty()) {
            player.sendMessage(messages.getFormattedMessage("show.no-effects-error"));
            return;
        }
        player.sendMessage(messages.getFormattedMessage("show.header"));
        effects.forEach(effect -> player.sendMessage(messages.getFormattedMessage("show.line", effect.getType(),
                effect.getDuration(), effect.getAmplifier(), effect.hasParticles(), effect.hasAmbient())));
    }

    @Subcommand("add")
    public void addEffect(Player player, WeaponEffectType type, int duration, @Default("1") short amplifier,
                          @Default("true") boolean ambient, @Default("false") boolean particles,
                          @Default("false") boolean icon) {
        if (!hasValidItem(player)) {
            return;
        }
        if (amplifier > Byte.MAX_VALUE) {
            // TODO warn/error
        }
        System.out.println("effect " + type.name());
        ItemStack result = effectHandler.addEffect(type, (byte) amplifier, duration, ambient, particles, icon,
                player.getInventory().getItemInMainHand());
        boolean success = player.getInventory().getItemInMainHand() != result;
        if (success) {
            player.getInventory().setItemInMainHand(result);
            player.sendMessage(messages.getFormattedMessage("add.success",
                    type, duration, amplifier, particles, ambient));
        }
        // TODO send message on fail
    }

    private boolean hasValidItem(Player player) {
        if (player.getInventory().getItemInMainHand() == null) {
            player.sendMessage(messages.getFormattedMessage("no-item-in-hand"));
            return false;
        }
        return true;
    }

}
