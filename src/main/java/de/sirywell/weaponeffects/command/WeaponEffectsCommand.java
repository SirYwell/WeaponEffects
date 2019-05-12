package de.sirywell.weaponeffects.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import de.sirywell.weaponeffects.Messages;
import de.sirywell.weaponeffects.effect.WeaponEffect;
import de.sirywell.weaponeffects.effect.WeaponEffectType;
import de.sirywell.weaponeffects.handler.EffectHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@CommandAlias("weaponeffects|weffects|weffect")
@CommandPermission("weaponeffects.command")
public class WeaponEffectsCommand extends BaseCommand {
    private EffectHandler<?> effectHandler;
    private Messages messages;

    public WeaponEffectsCommand(EffectHandler effectHandler, Messages messages) {
        this.effectHandler = effectHandler;
        this.messages = messages;
    }

    @HelpCommand
    @CommandPermission("weaponeffects.command.help")
    public void help(Player player, CommandHelp help) {
        player.sendMessage(messages.getFormattedMessage("help"));
        help.showHelp();
    }

    @Subcommand("show")
    @CommandPermission("weaponeffects.command.show")
    @Description("Shows all effects added to the item in your hand.")
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
    @CommandPermission("weaponeffects.command.add")
    @CommandCompletion("@weaponEffectType")
    @Description("Adds the specified effect to the item in your hand.")
    public void addEffect(Player player, WeaponEffectType type, int duration, @Default("1") byte amplifier,
                          @Default("true") boolean ambient, @Default("false") boolean particles,
                          @Default("false") boolean icon) {
        if (!hasValidItem(player)) {
            return;
        }
        ItemStack result = effectHandler.addEffect(type, amplifier, duration, ambient, particles, icon,
                player.getInventory().getItemInMainHand());
        boolean success = player.getInventory().getItemInMainHand() != result;
        if (success) {
            player.getInventory().setItemInMainHand(result);
            player.sendMessage(messages.getFormattedMessage("add.success",
                    type, duration, amplifier, particles, ambient));
        } else {
            player.sendMessage(messages.getFormattedMessage("add.already-added", type));
        }
    }

    @Subcommand("remove")
    @CommandPermission("weaponeffects.command.remove")
    @Description("Removes the specified effect from the item in your hand.")
    public void removeFfect(Player player, WeaponEffectType type) {
        if (!hasValidItem(player)) {
            return;
        }
        ItemStack result = effectHandler.removeEffect(type, player.getInventory().getItemInMainHand());
        boolean success = player.getInventory().getItemInMainHand() != result;
        if (success) {
            player.getInventory().setItemInMainHand(result);
            player.sendMessage(messages.getFormattedMessage("remove.success", type));
        }
    }

    @Subcommand("removeAll")
    @CommandPermission("weaponeffects.command.removeall")
    @Description("Removes all effects from the item in your hand.")
    public void removeAllEffects(Player player) {
        if (!hasValidItem(player)) {
            return;
        }
        ItemStack result = player.getInventory().getItemInMainHand();
        for (WeaponEffect effect : effectHandler.getEffects(result)) {
            result = effectHandler.removeEffect(effect.getType(), result);
        }
        boolean success = player.getInventory().getItemInMainHand() != result;
        if (success) {
            player.getInventory().setItemInMainHand(result);
            player.sendMessage(messages.getFormattedMessage("remove-all.success"));
        }
    }

    private boolean hasValidItem(Player player) {
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(messages.getFormattedMessage("no-item-in-hand"));
            return false;
        }
        return true;
    }

}
