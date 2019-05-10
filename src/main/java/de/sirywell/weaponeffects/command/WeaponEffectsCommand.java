package de.sirywell.weaponeffects.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.sirywell.weaponeffects.effect.WeaponEffectType;
import de.sirywell.weaponeffects.handler.EffectHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("weaponeffects|weffect")
public class WeaponEffectsCommand extends BaseCommand {
    private EffectHandler effectHandler;

    public WeaponEffectsCommand(EffectHandler effectHandler) {
        this.effectHandler = effectHandler;
    }

    @Default
    public void help(Player player) {
        player.sendMessage("HELP");
    }

    @Subcommand("show")
    public void showEffects(Player player) {
        // TODO list all effects of current item
    }

    @Subcommand("add")
    public void addEffect(Player player, WeaponEffectType type, int duration, @Default("1") short amplifier,
                          @Default("true") boolean ambient, @Default("false") boolean particles,
                          @Default("false") boolean icon) {
        if (amplifier > Byte.MAX_VALUE) {
            // TODO warn/error
        }
        System.out.println("effect " + type.name());
        ItemStack result = effectHandler.addEffect(type, (byte) amplifier, duration, ambient, particles, icon,
                player.getInventory().getItemInMainHand());
        boolean success = player.getInventory().getItemInMainHand() != result;
        if (success)
            player.getInventory().setItemInMainHand(result);
        // TODO send message
        player.sendMessage("Effect added: " + success);
    }

}
