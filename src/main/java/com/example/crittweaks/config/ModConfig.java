package com.example.crittweaks.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "crittweaks")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean modEnabled = true;

    // --- Criticals --- (PrefixText renders the section header)
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip
    public boolean forceCritParticles = true;

    @ConfigEntry.Gui.Tooltip
    public boolean alwaysCritParticles = false;

    @ConfigEntry.Gui.Tooltip
    public boolean hideOtherPlayerCrits = true;

    @ConfigEntry.Gui.Tooltip
    public boolean hideIncomingCrits = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 10, max = 1000)
    public int critParticleMultiplier = 100;

    // --- Sharpness (enchanted hit) ---
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip
    public boolean forceSharpnessParticles = false;

    @ConfigEntry.Gui.Tooltip
    public boolean alwaysSharpnessParticles = false;

    @ConfigEntry.Gui.Tooltip
    public boolean hideOtherPlayerSharpness = true;

    @ConfigEntry.Gui.Tooltip
    public boolean hideIncomingSharpness = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 10, max = 1000)
    public int sharpnessParticleMultiplier = 100;
}
