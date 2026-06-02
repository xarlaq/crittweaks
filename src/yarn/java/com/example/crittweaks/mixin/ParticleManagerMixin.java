package com.example.crittweaks.mixin;

import com.example.crittweaks.CritTweaksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(value = ParticleManager.class, priority = 900)
public abstract class ParticleManagerMixin {

    /** Baseline spawn attempts per override burst at 100% (vanilla EmitterParticle: ~3 ticks × 16 per tick). */
    @Unique
    private static final int BASE_OVERRIDE_PARTICLE_ATTEMPTS = 48;

    /** Maximum time (ms) after an attack for particles to be attributed to the local player. */
    @Unique
    private static final long ATTACK_ATTRIBUTION_WINDOW_MS = 1000L;

    @Shadow
    protected abstract <T extends ParticleEffect> Particle createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

    @Shadow
    private Queue<Particle> newParticles;

    @Inject(method = "addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;)V", at = @At("HEAD"), cancellable = true)
    private void onAddEmitter(Entity entity, ParticleEffect parameters, CallbackInfo ci) {
        handleEmitter(entity, parameters, ci);
    }

    @Inject(method = "addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V", at = @At("HEAD"), cancellable = true)
    private void onAddEmitterWithAge(Entity entity, ParticleEffect parameters, int maxAge, CallbackInfo ci) {
        handleEmitter(entity, parameters, ci);
    }

    @Unique
    private void handleEmitter(Entity entity, ParticleEffect parameters, CallbackInfo ci) {
        var config = CritTweaksClient.config;
        if (config == null || !config.modEnabled) return;

        var type = parameters.getType();
        boolean isCrit = type == ParticleTypes.CRIT;
        boolean isEnchanted = type == ParticleTypes.ENCHANTED_HIT;

        // Early exit if this particle type is not relevant
        if (!isCrit && !isEnchanted) return;

        boolean isSelf = (entity == MinecraftClient.getInstance().player);

        // --- Self-particle filtering ---
        if (isSelf) {
            boolean cancelCrit = isCrit && config.hideIncomingCrits;
            boolean cancelEnchanted = isEnchanted && config.hideIncomingSharpness;

            if (cancelCrit || cancelEnchanted) {
                ci.cancel();
                return;
            }
        }

        // --- Other-player particle filtering ---
        if (!isSelf) {
            boolean isRecentAttackTarget = (entity.getId() == CritTweaksClient.lastAttackedEntityId)
                    && (System.currentTimeMillis() - CritTweaksClient.lastAttackTime < ATTACK_ATTRIBUTION_WINDOW_MS);

            if (!isRecentAttackTarget) {
                boolean hideOtherCrit = isCrit && config.hideOtherPlayerCrits;
                boolean hideOtherEnchanted = isEnchanted && config.hideOtherPlayerSharpness;

                if (hideOtherCrit || hideOtherEnchanted) {
                    ci.cancel();
                    return;
                }
            }
        }

        // --- Take over the spawn to apply the per-type multiplier. This runs
        // whenever the mod is on; Force* only decides whether to also bypass the
        // 'Minimal' particle setting (else vanilla suppresses these particles,
        // leaving nothing to scale). ---
        boolean force = isCrit ? config.forceCritParticles : config.forceSharpnessParticles;
        boolean suppressedByMinimal = !force
                && MinecraftClient.getInstance().options.getParticles().getValue() == ParticlesMode.MINIMAL;

        if (!suppressedByMinimal) {
            ci.cancel();

            int multiplierPercent = isCrit ? config.critParticleMultiplier : config.sharpnessParticleMultiplier;
            int attempts = Math.max(0, Math.round(BASE_OVERRIDE_PARTICLE_ATTEMPTS * multiplierPercent / 100.0f));

            // Entity position/size are invariant for the duration of this call,
            // so hoist them out of the spawn loop (these reads are otherwise
            // repeated for every spawn attempt).
            ThreadLocalRandom random = ThreadLocalRandom.current();
            final double baseX = entity.getX();
            final double baseY = entity.getY();
            final double baseZ = entity.getZ();
            final double quarterWidth = (double) entity.getWidth() / 4.0;
            final double halfHeight = (double) entity.getHeight() * 0.5;
            final double quarterHeight = (double) entity.getHeight() / 4.0;

            for (int i = 0; i < attempts; ++i) {
                double d = random.nextFloat() * 2.0f - 1.0f;
                double e = random.nextFloat() * 2.0f - 1.0f;
                double f = random.nextFloat() * 2.0f - 1.0f;

                if (d * d + e * e + f * f <= 1.0) {
                    double x = baseX + d * quarterWidth;
                    double y = baseY + halfHeight + e * quarterHeight;
                    double z = baseZ + f * quarterWidth;

                    Particle particle = this.createParticle(parameters, x, y, z, d, e + 0.2, f);
                    if (particle != null) {
                        this.newParticles.add(particle);
                    }
                }
            }
        }
    }
}
