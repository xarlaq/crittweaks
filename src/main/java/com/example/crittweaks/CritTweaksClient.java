package com.example.crittweaks;

import com.example.crittweaks.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CritTweaksClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("crittweaks");

    public static volatile ModConfig config;
    public static volatile int lastAttackedEntityId = -1;
    public static volatile long lastAttackTime = 0;

    @Override
    public void onInitializeClient() {
        try {
            // Attempt to load config via Cloth Config (optional dependency)
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
            ConfigLoader.load();
            LOGGER.info("CritTweaks config loaded via Cloth Config");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Cloth Config not present — use defaults
            config = new ModConfig();
            LOGGER.info("CritTweaks running with default config (Cloth Config not found)");
        } catch (Exception e) {
            config = new ModConfig();
            LOGGER.warn("CritTweaks config failed to load, using defaults", e);
        }
    }

    /**
     * Separated into its own class so that class-loading of CritTweaksClient
     * does not trigger loading of AutoConfig classes when Cloth Config is absent.
     */
    private static final class ConfigLoader {
        static void load() {
            me.shedaniel.autoconfig.AutoConfig.register(
                    ModConfig.class,
                    me.shedaniel.autoconfig.serializer.JanksonConfigSerializer::new
            );
            // Custom "x1.2"-style slider for the multiplier fields (era-specific).
            com.example.crittweaks.config.GuiProviders.register();
            config = me.shedaniel.autoconfig.AutoConfig
                    .getConfigHolder(ModConfig.class)
                    .getConfig();
        }
    }
}
