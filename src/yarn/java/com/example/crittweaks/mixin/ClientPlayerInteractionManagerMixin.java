package com.example.crittweaks.mixin;

import com.example.crittweaks.CritTweaksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        // Record the attack so ParticleManagerMixin can attribute particles to the local player.
        CritTweaksClient.lastAttackedEntityId = target.getId();
        CritTweaksClient.lastAttackTime = System.currentTimeMillis();

        var config = CritTweaksClient.config;
        if (config == null || !config.modEnabled) return;

        // "Always particles": emit on every hit you land, regardless of whether it
        // was a real crit or a Sharpness weapon. These flow through the emitter the
        // same as vanilla (and respect the Force/multiplier settings).
        var particleManager = MinecraftClient.getInstance().particleManager;
        if (config.alwaysCritParticles) {
            particleManager.addEmitter(target, ParticleTypes.CRIT);
        }
        if (config.alwaysSharpnessParticles) {
            particleManager.addEmitter(target, ParticleTypes.ENCHANTED_HIT);
        }
    }
}
