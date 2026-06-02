package com.example.crittweaks.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;

// Mojang-mapped (non-obfuscated, 26.1+) variant of ModMenuIntegration.
// Cloth Config 26.1 moved screen generation out of AutoConfig into the new
// client-only AutoConfigClient class (same getConfigScreen signature).
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfigClient.getConfigScreen(ModConfig.class, parent).get();
    }
}
