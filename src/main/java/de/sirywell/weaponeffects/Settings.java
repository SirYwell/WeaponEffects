package de.sirywell.weaponeffects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SerializableAs("Settings")
public class Settings implements ConfigurationSerializable {
    private static final Function<String, Material> STRING_TO_MATERIAL_CONVERTER;
    private static final Predicate<Material> ITEM_FILTER;
    private static final Collector<Material, ?, EnumSet<Material>> MATERIAL_COLLECTOR;
    private static boolean warned = false;
    private final int refreshRate;
    private final EnumSet<Material> applicableItems;
    private final List<Integer> additionalSlots;

    static {
        STRING_TO_MATERIAL_CONVERTER = matName -> {
            Material mat = Material.getMaterial(matName);
            if (mat == null) {
                Bukkit.getLogger().warning(
                        String.format("No material with name %s found. Ignoring it.", matName));
            }
            return mat;
        };
        ITEM_FILTER = material -> {
            if (!material.isItem()) {
                Bukkit.getLogger().warning(
                        String.format("%s is not an item. Ignoring it.", material.name()));
                return false;
            }
            return true;
        };
        MATERIAL_COLLECTOR = Collectors.toCollection(() -> EnumSet.noneOf(Material.class));
    }

    private Settings(int refreshRate, EnumSet<Material> applicableItems, List<Integer> additionalSlots) {
        this.refreshRate = refreshRate;
        this.applicableItems = applicableItems;
        this.additionalSlots = additionalSlots;
    }

    public static Settings deserialize(Map<String, Object> result) {
        int refreshRate = (int) result.get("refresh-rate");
        List<String> applicableItems = (List<String>) result.get("applicableItems");
        List<Integer> additionalSlots = (List<Integer>) result.get("additional-slots");

        EnumSet<Material> applicableMaterials;
        if (applicableItems == null || applicableItems.isEmpty()) {
            if (!warned) { // why is this method even called twice...
                Bukkit.getLogger().info("No applicable items were defined in config. Checking all items.");
                warned = true;
            }
            applicableMaterials = getAllItems();
        } else {
            applicableMaterials = getItems(applicableItems);
        }

        return new Settings(refreshRate, applicableMaterials, additionalSlots);
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public EnumSet<Material> getApplicableItems() {
        return applicableItems;
    }

    public List<Integer> getAdditionalSlots() {
        return additionalSlots;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("refresh-rate", refreshRate);
        result.put("applicable-items", new ArrayList<>(applicableItems));
        result.put("additional-slots", additionalSlots);
        return result;
    }

    private static EnumSet<Material> getItems(List<String> itemStringList) {
        return itemStringList.stream()
                .map(STRING_TO_MATERIAL_CONVERTER)
                .filter(Objects::nonNull)
                .filter(ITEM_FILTER)
                .collect(MATERIAL_COLLECTOR);
    }

    private static EnumSet<Material> getAllItems() {
        return Arrays.stream(Material.values())
                .parallel().filter(Material::isItem)
                .collect(MATERIAL_COLLECTOR);
    }
}
