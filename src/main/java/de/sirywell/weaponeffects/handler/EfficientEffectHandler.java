package de.sirywell.weaponeffects.handler;

import de.sirywell.weaponeffects.effect.EfficientEffect;
import de.sirywell.weaponeffects.effect.WeaponEffect;
import de.sirywell.weaponeffects.effect.WeaponEffectType;
import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EfficientEffectHandler implements EffectHandler<EfficientEffect> {
    private static final String WEAPON_EFFECTS_ARRAY = "WeaponEffects";
    private EnumSet<Material> materialsToCheck;

    public EfficientEffectHandler(EnumSet<Material> materialsToCheck) {
        this.materialsToCheck = materialsToCheck;
    }

    @Override
    public void updateEffects(Player player) {
        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand != null && materialsToCheck.contains(inHand.getType())) {
            NBTItem item = new NBTItem(inHand);
            if (!item.hasKey(WEAPON_EFFECTS_ARRAY)) {
                return;
            }
            int[] effects = item.getIntArray(WEAPON_EFFECTS_ARRAY);
            for (int effect : effects) {
                EfficientEffect.fromInt(effect)
                        .toPotionEffect()
                        .ifPresent(player::addPotionEffect);
            }
        }
    }

    @Override
    public ItemStack addEffect(EfficientEffect effect, ItemStack stack) {
        NBTItem item = new NBTItem(stack);
        if (!item.hasKey(WEAPON_EFFECTS_ARRAY)) {
            int[] updated = new int[]{effect.getInt()};
            item.setIntArray(WEAPON_EFFECTS_ARRAY, updated);
            return item.getItem();
        }
        int[] old = item.getIntArray(WEAPON_EFFECTS_ARRAY);
        Set<Integer> asSet = Arrays.stream(old).boxed().collect(Collectors.toCollection(HashSet::new));
        int potionId = effect.getInt() & EfficientEffect.POTION_ID_BITMASK;
        if (asSet.stream().noneMatch(integer -> (integer & EfficientEffect.POTION_ID_BITMASK) == potionId)) {
            asSet.add(effect.getInt());
            int[] updated = asSet.stream().mapToInt(Integer::intValue).toArray();
            item.setIntArray(WEAPON_EFFECTS_ARRAY, updated);
            return item.getItem();
        }
        return stack;
    }

    @Override
    public ItemStack addEffect(WeaponEffectType type, byte amplifier, int duration,
                             boolean ambient, boolean particles, boolean icon, ItemStack stack) {
        return addEffect(new EfficientEffect(type, amplifier, duration, ambient, particles, icon), stack);
    }

    @Override
    public ItemStack removeEffect(WeaponEffectType type, ItemStack stack) {
        NBTItem item = new NBTItem(stack);
        if (!item.hasKey(WEAPON_EFFECTS_ARRAY)) {
            return stack;
        }
        int[] old = item.getIntArray(WEAPON_EFFECTS_ARRAY);
        Set<Integer> asSet = Arrays.stream(old).boxed().collect(Collectors.toCollection(HashSet::new));
        int potionId = type.getId();
        boolean removed = asSet.removeIf(integer -> (integer & EfficientEffect.POTION_ID_BITMASK) == potionId);
        if (removed) {
            int[] updated = asSet.stream().mapToInt(Integer::intValue).toArray();
            item.setIntArray(WEAPON_EFFECTS_ARRAY, updated);
        }
        return removed ? item.getItem() : stack;
    }

    @Override
    public Set<EfficientEffect> getEffects(ItemStack stack) {
        NBTItem item = new NBTItem(stack);
        if (!item.hasKey(WEAPON_EFFECTS_ARRAY)) {
            return Collections.emptySet();
        }
        int[] array = item.getIntArray(WEAPON_EFFECTS_ARRAY);
        return Collections.unmodifiableSet(Arrays.stream(array)
                .mapToObj(EfficientEffect::fromInt)
                .collect(Collectors.toSet()));
    }


}
