package com.example.crittweaks.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

/**
 * Registers a custom Cloth Config slider for the *ParticleMultiplier fields so the
 * slider label reads "x1.2", "x2.0", ... instead of a raw percentage. Yarn variant.
 */
public final class GuiProviders {
    private GuiProviders() {}

    public static void register() {
        GuiRegistry registry = AutoConfig.getGuiRegistry(ModConfig.class);
        registry.registerPredicateProvider(
                (i13n, field, config, defaults, guiAccess) -> buildSlider(i13n, field, config, defaults),
                field -> field.getName().endsWith("ParticleMultiplier"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<AbstractConfigListEntry> buildSlider(String i13n, java.lang.reflect.Field field, Object config, Object defaults) {
        int value;
        int def;
        try {
            value = field.getInt(config);
            def = field.getInt(defaults);
        } catch (IllegalAccessException e) {
            return Collections.emptyList();
        }
        AbstractConfigListEntry entry = ConfigEntryBuilder.create()
                .startIntSlider(Text.translatable(i13n), value, 10, 1000)
                .setDefaultValue(def)
                .setTextGetter(v -> Text.literal(String.format("x%.1f", v / 100.0)))
                .setTooltip(Text.translatable(i13n + ".tooltip"))
                .setSaveConsumer(v -> {
                    try {
                        field.setInt(config, v);
                    } catch (IllegalAccessException ignored) {
                    }
                })
                .build();
        return Collections.singletonList(entry);
    }
}
