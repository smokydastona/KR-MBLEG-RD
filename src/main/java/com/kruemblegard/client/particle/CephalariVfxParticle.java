package com.kruemblegard.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Shared particle implementation for Cephalari cure/zombify VFX.
 */
public class CephalariVfxParticle extends TextureSheetParticle {

    public enum Kind {
        SHELL_DUST,
        SHELL_FRAGMENT,
        SHELL_SPIRAL,
        ZOMBIFY,
        CURE
    }

    private final SpriteSet sprites;

    private CephalariVfxParticle(
        ClientLevel level,
        double x,
        double y,
        double z,
        double dx,
        double dy,
        double dz,
        SpriteSet sprites,
        Kind kind
    ) {
        super(level, x, y, z, dx, dy, dz);

        this.sprites = sprites;
        this.hasPhysics = false;

        this.xd = dx;
        this.yd = dy;
        this.zd = dz;

        switch (kind) {
            case SHELL_DUST -> {
                this.quadSize = 0.05f + (this.random.nextFloat() * 0.05f);
                this.lifetime = 12 + this.random.nextInt(10);
                this.alpha = 0.95f;
            }
            case SHELL_FRAGMENT -> {
                this.quadSize = 0.06f + (this.random.nextFloat() * 0.06f);
                this.lifetime = 14 + this.random.nextInt(12);
                this.alpha = 0.95f;
            }
            case SHELL_SPIRAL -> {
                this.quadSize = 0.08f + (this.random.nextFloat() * 0.06f);
                this.lifetime = 16 + this.random.nextInt(10);
                this.alpha = 0.9f;
            }
            case ZOMBIFY -> {
                this.quadSize = 0.10f + (this.random.nextFloat() * 0.08f);
                this.lifetime = 10 + this.random.nextInt(10);
                this.alpha = 0.95f;
            }
            case CURE -> {
                this.quadSize = 0.10f + (this.random.nextFloat() * 0.08f);
                this.lifetime = 12 + this.random.nextInt(12);
                this.alpha = 0.95f;
            }
        }

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        // Animated sprites.
        this.setSpriteFromAge(this.sprites);

        float life = (float) this.age / (float) this.lifetime;
        this.alpha = this.alpha * (1.0f - (life * 0.85f));

        // Subtle lift.
        this.yd += 0.002;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final Kind kind;

        public Provider(SpriteSet sprites, Kind kind) {
            this.sprites = sprites;
            this.kind = kind;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new CephalariVfxParticle(level, x, y, z, dx, dy, dz, sprites, kind);
        }
    }
}
