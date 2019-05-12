package de.sirywell.weaponeffects.handler;

import de.sirywell.weaponeffects.effect.WeaponEffect;
import de.sirywell.weaponeffects.effect.WeaponEffectType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface EffectHandler<W extends WeaponEffect> {

    /**
     * Update the effects for a specific player. This will check the item
     * currently held by the player for valid weapon effects. If no effects are found,
     * nothing will happen.
     *
     * @param player the player to update effects for.
     */
    void updateEffects(Player player);

    /**
     * Add an effect to a weapon. Since each effect should be added only once,
     * calling this method may have no impact. If nothing was changed, the returned stack
     * will be the same instance. However, implementations need to verify the uniqueness
     * by checking the {@link WeaponEffectType} of the effects.
     *
     * @param effect the effect to add.
     * @param stack  the item stack (weapon) to add the effect to.
     * @return the new item stack if something was changed, the old on otherwise.
     */
    ItemStack addEffect(W effect, ItemStack stack);

    /**
     * Add an effect to a weapon. The effect is built by the implementation with the given data. If the
     * same effect type is already attached to the item, no actions will be performed.
     * If nothing was changed, the returned stack will be the same instance.
     *
     * @param type      the type of effect.
     * @param amplifier the amplifier of the effect.
     * @param duration  the duration of the effect.
     * @param ambient   whether the effect has ambient.
     * @param particles whether the effect has particles.
     * @param icon      whether the effect has an icon.
     * @param stack     the item stack to add the effect to.
     * @return the new item stack if something was changed, the old on otherwise.
     * @see #addEffect(WeaponEffect, ItemStack)
     */
    ItemStack addEffect(WeaponEffectType type, byte amplifier, int duration,
                        boolean ambient, boolean particles, boolean icon, ItemStack stack);

    /**
     * Remove an effect of a weapon. Since the uniqueness is determined by
     * the effect type, no specified effect is required. If the effect with that
     * effect type is not attached to the item, calling this method may have no impact.
     * If nothing was changed, the returned stack will be the same instance.
     *
     * @param type  the type of the effect to remove.
     * @param stack the stack to remove the effect with the specified type from.
     * @return the new item stack if something was changed, the old on otherwise.
     */
    ItemStack removeEffect(WeaponEffectType type, ItemStack stack);

    /**
     * Get all effects attached to an item stack. The returned set may be empty, if
     * no effect is attached yet. Also, it's a snapshot of the current state and will
     * not change if an effect is added to the item stack. Likewise, updating the set
     * will not change the effects attached to the item. The returned set may be unmodifiable
     * depending on implementation.
     *
     * @param stack the stack to get all effects from.
     * @return a set of all attached effects. May be empty.
     */
    Set<W> getEffects(ItemStack stack);
}
