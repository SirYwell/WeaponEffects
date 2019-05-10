package de.sirywell.weaponeffects.effect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.potion.PotionEffect;
import org.graalvm.compiler.nodes.calc.IntegerDivRemNode;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EfficientEffect implements WeaponEffect {
    public static final int POTION_ID_BITMASK = 0xFF;
    private static final Cache<Integer, PotionEffect> cache;
    private int bitEffect;

    static {
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .maximumSize(200)
                .build();
    }

    public EfficientEffect(WeaponEffectType type, byte amplifier, int duration, boolean ambient, boolean particles, boolean icon) {
        bitEffect = (byte) type.getId(); // bits [0, 7]
        bitEffect += ((int) amplifier) << 8; // bits [8, 15]
        bitEffect += intFromBoolean(ambient) << 16;
        bitEffect += intFromBoolean(particles) << 17;
        bitEffect += intFromBoolean(icon) << 18;
        bitEffect += duration << 20;
    }

    private EfficientEffect(int bitEffect) {
        this.bitEffect = bitEffect;
    }

    public static void cleanUpCache() {
        cache.invalidateAll();
    }

    public static EfficientEffect fromInt(int bits) {
        return new EfficientEffect(bits);
    }

    public int getInt() {
        return bitEffect;
    }

    @Override
    public Optional<PotionEffect> toPotionEffect() {
        try {
            return Optional.of(cache.get(bitEffect, () -> {
                Optional<WeaponEffectType> type = WeaponEffectType.getById(bitEffect & POTION_ID_BITMASK);
                if (!type.isPresent()) {
                    return null;
                }
                byte amplifier = (byte) (bitEffect >> 8);
                boolean ambient = booleanFromInt(bitEffect >> 16 & 1);
                boolean particles = booleanFromInt(bitEffect >> 17 & 1);
                boolean icon = booleanFromInt(bitEffect >> 18 & 1);
                int duration = bitEffect >>> 20;
                return new PotionEffect(type.get().getBukkitType(), duration, amplifier,ambient, particles);
            }));
        } catch (ExecutionException ignore) {
            return Optional.empty();
        }
    }

    private int intFromBoolean(boolean b) {
        return b ? 1 : 0;
    }

    private boolean booleanFromInt(int i) {
        return i == 1;
    }

    @Override
    public String toString() {
        return "EfficientEffect{" +
                "bitEffect=" + bitEffect +
                ", effect=" + toPotionEffect().toString() + "}";
    }
}
