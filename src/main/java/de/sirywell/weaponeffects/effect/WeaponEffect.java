package de.sirywell.weaponeffects.effect;

import org.bukkit.potion.PotionEffect;

import java.util.Optional;

public interface WeaponEffect {

    Optional<PotionEffect> toPotionEffect();

    WeaponEffectType getType();

    int getDuration();

    byte getAmplifier();

    boolean hasAmbient();

    boolean hasParticles();

    boolean hasIcon();
}
