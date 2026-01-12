package com.kruemblegard.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class ArcaneSparkParticle extends TextureSheetParticle {

    private ArcaneSparkParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, dx, dy, dz);

        this.hasPhysics = false;

        this.xd = dx;
        this.yd = dy;
        this.zd = dz;

        this.rCol = 0.72f;
        this.gCol = 0.35f;
        this.bCol = 1.0f;
        this.alpha = 0.9f;

        this.quadSize = 0.07f + (this.random.nextFloat() * 0.03f);
        this.lifetime = 10 + this.random.nextInt(12);

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        // Soft fade-out near end of life.
        float life = (float) this.age / (float) this.lifetime;
        this.alpha = 0.9f * (1.0f - life);

        // Slight floaty drift.
        this.yd += 0.002;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new ArcaneSparkParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
