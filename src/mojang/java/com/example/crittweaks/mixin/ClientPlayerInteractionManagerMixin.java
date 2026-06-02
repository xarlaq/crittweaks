package com.example.crittweaks.mixin;

import com.example.crittweaks.CritTweaksClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mojang-mapped (non-obfuscated, 26.1+) variant of ClientPlayerInteractionManagerMixin.
// ClientPlayerInteractionManager -> MultiPlayerGameMode, PlayerEntity -> Player,
// attackEntity -> attack, particleManager -> particleEngine,
// addEmitter -> createTrackingEmitter.
@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackEntity(Player player, Entity target, CallbackInfo ci) {
        // Record the attack so ParticleManagerMixin can attribute particles to the local player.
        CritTweaksClient.lastAttackedEntityId = target.getId();
        CritTweaksClient.lastAttackTime = System.currentTimeMillis();

        var config = CritTweaksClient.config;
        if (config == null || !config.modEnabled) return;

        // "Always particles": emit on every hit you land, regardless of whether it
        // was a real crit or a Sharpness weapon. These flow through the emitter the
        // same as vanilla (and respect the Force/multiplier settings).
        var particleEngine = Minecraft.getInstance().particleEngine;
        if (config.alwaysCritParticles) {
            particleEngine.createTrackingEmitter(target, ParticleTypes.CRIT);
        }
        if (config.alwaysSharpnessParticles) {
            particleEngine.createTrackingEmitter(target, ParticleTypes.ENCHANTED_HIT);
        }
    }
}
