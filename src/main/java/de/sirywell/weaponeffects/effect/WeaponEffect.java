package de.sirywell.weaponeffects.effect;

import org.bukkit.potion.PotionEffect;

import java.util.Optional;

public interface WeaponEffect {

    Optional<PotionEffect> toPotionEffect();
}
